package ac.asimov.faucet.service;


import ac.asimov.faucet.blockchain.MultiVACBlockchainGateway;
import ac.asimov.faucet.dao.BannedWalletDao;
import ac.asimov.faucet.dao.FaucetClaimDao;
import ac.asimov.faucet.dto.WalletAccountDto;
import ac.asimov.faucet.dto.rest.*;
import ac.asimov.faucet.mapper.FaucetMapper;
import ac.asimov.faucet.model.BannedWallet;
import ac.asimov.faucet.model.Currency;
import ac.asimov.faucet.model.FaucetClaim;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.K;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class FaucetService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${faucetPrivateKey}")
    private String faucetWalletPrivateKey;

    @Autowired
    private FaucetClaimDao faucetClaimDao;

    @Autowired
    private BannedWalletDao bannedWalletDao;

    @Autowired
    private FaucetMapper mapper;

    private final Integer WAITING_TIME_BETWEEN_MTV_IN_SECONDS = 60 * 60 * 24;
    private final Integer WAITING_TIME_BETWEEN_ISAAC_IN_SECONDS = 60 * 60 * 24;

    private final BigDecimal CLAIM_MTV_AMOUNT = BigDecimal.valueOf(1);
    private final BigDecimal CLAIM_ISAAC_AMOUNT = BigDecimal.valueOf(10);

    @Autowired
    private MultiVACBlockchainGateway blockchainGateway;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseWrapperDto<FaucetClaimResponseDto> claimFaucet(FaucetClaimRequestDto request) {

        ResponseWrapperDto<Void> validateRequestResponse = validateClaimFaucetRequest(request);
        if (validateRequestResponse.hasErrors()) {
            return new ResponseWrapperDto<>(validateRequestResponse.getErrorMessage());
        }

        ResponseWrapperDto<Void> validateCaptchaResponse = validateCaptcha(request.getCaptchaCode());
        if (validateCaptchaResponse.hasErrors()) {
            return new ResponseWrapperDto<>(validateCaptchaResponse.getErrorMessage());
        }

        ResponseWrapperDto<Void> validateIpAddressResponse = validateIpAddress(request.getIpAddress());

        return switch (request.getCurrency()) {
            case MTV -> claimMTV(request.getReceivingAddress());
            case ISAAC -> claimISAAC(request.getReceivingAddress());
        };
    }

    private ResponseWrapperDto<Void> validateIpAddress(String ipAddress) {

        // TODO

        return new ResponseWrapperDto<>("Not implemented yet");
    }

    private ResponseWrapperDto<FaucetClaimResponseDto> claimMTV(String receivingAddress) {
        BigDecimal receivingAmount = getReceivingAmountForCurrency(Currency.MTV);
        ResponseWrapperDto<TransactionResponseDto> sendResponse = blockchainGateway.sendMTVFunds(new TransferRequestDto(getFaucetPrivateWalletAccount(), new WalletAccountDto(null, receivingAddress), receivingAmount));
        if (sendResponse.hasErrors()) {
            logger.error(sendResponse.getErrorMessage());
            return new ResponseWrapperDto<>("Blockchain error: Cannot send MTV");
        }


        String transactionHash = sendResponse.getResponse().getTransactionHash();

        FaucetClaim faucetClaim = new FaucetClaim();
        faucetClaim.setClaimedAt(LocalDateTime.now());
        faucetClaim.setClaimedCurrency(Currency.MTV);
        faucetClaim.setReceivingAddress(receivingAddress);
        faucetClaim.setReceivingAmount(receivingAmount);
        faucetClaim.setTransactionHash(transactionHash);

        // TODO
        // faucetClaim.setReceivingIpAddress();

        faucetClaim = faucetClaimDao.save(faucetClaim);

        FaucetClaimResponseDto responseDto = mapper.mapFaucetClaimToFaucetClaimResponseDto(faucetClaim);
        return new ResponseWrapperDto<>(responseDto);
    }

    private ResponseWrapperDto<FaucetClaimResponseDto> claimISAAC(String receivingAddress) {
        BigDecimal receivingAmount = getReceivingAmountForCurrency(Currency.MTV);
        ResponseWrapperDto<TransactionResponseDto> sendResponse = blockchainGateway.sendISAACTokenFunds(new TransferRequestDto(getFaucetPrivateWalletAccount(), new WalletAccountDto(null, receivingAddress), receivingAmount));
        if (sendResponse.hasErrors()) {
            logger.error(sendResponse.getErrorMessage());
            return new ResponseWrapperDto<>("Blockchain error: Cannot send ISAAC");
        }

        String transactionHash = sendResponse.getResponse().getTransactionHash();

        FaucetClaim faucetClaim = new FaucetClaim();
        faucetClaim.setClaimedAt(LocalDateTime.now());
        faucetClaim.setClaimedCurrency(Currency.ISAAC);
        faucetClaim.setReceivingAddress(receivingAddress);
        faucetClaim.setReceivingAmount(receivingAmount);
        faucetClaim.setTransactionHash(transactionHash);

        // TODO
        // faucetClaim.setReceivingIpAddress();

        faucetClaim = faucetClaimDao.save(faucetClaim);

        FaucetClaimResponseDto responseDto = mapper.mapFaucetClaimToFaucetClaimResponseDto(faucetClaim);
        return new ResponseWrapperDto<>(responseDto);
    }

    private ResponseWrapperDto<Void> validateClaimFaucetRequest(FaucetClaimRequestDto request) {

        if (StringUtils.isBlank(request.getCaptchaCode())) {
            return new ResponseWrapperDto<>("Missing captcha code");
        }

        if (StringUtils.isBlank(request.getReceivingAddress())) {
            return new ResponseWrapperDto<>("Missing receiving address");
        }
        Optional<BannedWallet> bannedEntryOptional = bannedWalletDao.findByWalletAddress(request.getReceivingAddress());
        if (bannedEntryOptional.isPresent()) {
            return new ResponseWrapperDto<>("Your address is banned because \"" + bannedEntryOptional.get().getBanReason() + "\"");
        }

        if (StringUtils.isBlank(request.getIpAddress())) {
            return new ResponseWrapperDto<>("Missing ip address");
        }

        if (request.getCurrency() == null) {
            return new ResponseWrapperDto<>("Missing currency");
        }


        Integer waitingTime = getWaitingTimeForCurrency(request.getCurrency());
        Duration waitingTimeDuration = Duration.ofSeconds(getWaitingTimeForCurrency(request.getCurrency()));
        List<FaucetClaim> requestInLastPeriod = faucetClaimDao.findAllByClaimedAtBetweenAAndReceivingAddressIsAndClaimedCurrencyIs(LocalDateTime.now().minusSeconds(waitingTime), LocalDateTime.now(), request.getReceivingAddress(), request.getCurrency());
        if (!requestInLastPeriod.isEmpty()) {
            if (requestInLastPeriod.size() > 1) {
                return new ResponseWrapperDto<>("You only can use the faucet every " + waitingTimeDuration.toString());
            } else {
                Duration leftWaitingTime = Duration.between(requestInLastPeriod.get(0).getClaimedAt(), LocalDateTime.now());
                String leftWaitingTimeString = leftWaitingTime.toString();
                return new ResponseWrapperDto<>("You only can use the faucet every " + waitingTimeDuration.toString() + "\nTry again in " + leftWaitingTimeString );
            }
        }

        return new ResponseWrapperDto<>();
    }

    private ResponseWrapperDto<Void> validateCaptcha(String captchaCode) {

        // TODO:

        return new ResponseWrapperDto<>("Not implemented yet");
    }

    public Integer getWaitingTimeForCurrency(Currency currency) {
        return switch (currency) {
            case MTV -> WAITING_TIME_BETWEEN_MTV_IN_SECONDS;
            case ISAAC -> WAITING_TIME_BETWEEN_ISAAC_IN_SECONDS;
        };
    }

    private BigDecimal getReceivingAmountForCurrency(Currency currency) {
        return switch (currency) {
            case MTV -> CLAIM_MTV_AMOUNT;
            case ISAAC -> CLAIM_ISAAC_AMOUNT;
        };
    }

    private WalletAccountDto getFaucetPrivateWalletAccount() {
        if (StringUtils.isBlank(faucetWalletPrivateKey)) {
            return null;
        }
        Credentials credentials = Credentials.create(faucetWalletPrivateKey);
        return new WalletAccountDto(faucetWalletPrivateKey, credentials.getAddress());
    }

    public WalletAccountDto getFaucetPublicWalletAccount() {
        return new WalletAccountDto(null, getFaucetPublicAddress());
    }

    public String getFaucetPublicAddress() {
        if (StringUtils.isBlank(faucetWalletPrivateKey)) {
            return null;
        }
        Credentials credentials = Credentials.create(faucetWalletPrivateKey);
        return credentials.getAddress();
    }

}
