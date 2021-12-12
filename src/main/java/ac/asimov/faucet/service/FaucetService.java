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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            case MTV -> claimMTV(request.getReceivingAddress(), request.getIpAddress());
            case ISAAC -> claimISAAC(request.getReceivingAddress(), request.getIpAddress());
        };
    }

    private ResponseWrapperDto<Void> validateIpAddress(String ipAddress) {
        if (StringUtils.equals(ipAddress, "0:0:0:0:0:0:0:1")) {
            return new ResponseWrapperDto<>();
        }

        // TAKE A LOOK TO BANNED IP ADDRESS
        // TODO
        return new ResponseWrapperDto<>("Not valid for now");
    }

    private ResponseWrapperDto<FaucetClaimResponseDto> claimMTV(String receivingAddress, String ipAddress) {
        Currency currency = Currency.MTV;

        Integer consecutiveDays = getConsecutiveDaysOfCurrency(receivingAddress, currency);
        ResponseWrapperDto<BigDecimal> receivingAmountResponse = getReceivingAmountForCurrency(currency, consecutiveDays);

        BigDecimal receivingAmount = CLAIM_MTV_AMOUNT;

        if (!receivingAmountResponse.hasErrors()) {
            receivingAmount = receivingAmountResponse.getResponse();
        }
        ResponseWrapperDto<TransactionResponseDto> sendResponse = blockchainGateway.sendMTVFunds(new TransferRequestDto(getFaucetPrivateWalletAccount(), new WalletAccountDto(null, receivingAddress), receivingAmount));
        if (sendResponse.hasErrors()) {
            logger.error(sendResponse.getErrorMessage());
            return new ResponseWrapperDto<>("Blockchain error: Cannot send MTV");
        }


        String transactionHash = sendResponse.getResponse().getTransactionHash();

        FaucetClaim faucetClaim = new FaucetClaim();
        faucetClaim.setClaimedAt(LocalDateTime.now());
        faucetClaim.setClaimedCurrency(currency);
        faucetClaim.setReceivingAddress(receivingAddress);
        faucetClaim.setReceivingAmount(receivingAmount);
        faucetClaim.setTransactionHash(transactionHash);
        faucetClaim.setReceivingIpAddress(ipAddress);

        faucetClaim = faucetClaimDao.save(faucetClaim);

        FaucetClaimResponseDto responseDto = mapper.mapFaucetClaimToFaucetClaimResponseDto(faucetClaim);
        return new ResponseWrapperDto<>(responseDto);
    }

    private ResponseWrapperDto<FaucetClaimResponseDto> claimISAAC(String receivingAddress, String ipAddress) {
        Currency currency = Currency.ISAAC;
        Integer consecutiveDays = getConsecutiveDaysOfCurrency(receivingAddress, currency);
        ResponseWrapperDto<BigDecimal> receivingAmountResponse = getReceivingAmountForCurrency(currency, consecutiveDays);

        BigDecimal receivingAmount = CLAIM_ISAAC_AMOUNT;

        if (!receivingAmountResponse.hasErrors()) {
            receivingAmount = receivingAmountResponse.getResponse();
        }

        ResponseWrapperDto<TransactionResponseDto> sendResponse = blockchainGateway.sendISAACTokenFunds(new TransferRequestDto(getFaucetPrivateWalletAccount(), new WalletAccountDto(null, receivingAddress), receivingAmount));
        if (sendResponse.hasErrors()) {
            logger.error(sendResponse.getErrorMessage());
            return new ResponseWrapperDto<>("Blockchain error: Cannot send ISAAC");
        }

        String transactionHash = sendResponse.getResponse().getTransactionHash();

        FaucetClaim faucetClaim = new FaucetClaim();
        faucetClaim.setClaimedAt(LocalDateTime.now());
        faucetClaim.setClaimedCurrency(currency);
        faucetClaim.setReceivingAddress(receivingAddress);
        faucetClaim.setReceivingAmount(receivingAmount);
        faucetClaim.setTransactionHash(transactionHash);
        faucetClaim.setReceivingIpAddress(ipAddress);

        faucetClaim = faucetClaimDao.save(faucetClaim);

        FaucetClaimResponseDto responseDto = mapper.mapFaucetClaimToFaucetClaimResponseDto(faucetClaim);
        return new ResponseWrapperDto<>(responseDto);
    }

    public Integer getConsecutiveDaysOfCurrency(String walletAddress, Currency currency) {
        List<FaucetClaim> faucetClaims = faucetClaimDao.findAllByReceivingAddressAndClaimedCurrency(walletAddress, currency);
        Integer consecutiveISAAC = getConsecutiveDaysOfCurrency(currency, faucetClaims);
        return consecutiveISAAC;
    }

    public Integer getConsecutiveDaysOfCurrency(Currency currency, List<FaucetClaim> faucetClaimList) {
        List<FaucetClaim> consecutiveFaucetClaims = new ArrayList<>();

        faucetClaimList = faucetClaimList.stream().sorted((o1, o2) -> o2.getClaimedAt().compareTo(o1.getClaimedAt())).collect(Collectors.toList());
        // TODO: Check earliest is at [0]
        for (FaucetClaim faucetClaim : faucetClaimList) {
            if (consecutiveFaucetClaims.size() == 0
                    && Duration.between(faucetClaim.getClaimedAt(), LocalDateTime.now()).minusSeconds(getWaitingTimeForCurrency(currency)).getSeconds() <= Duration.ofHours(24).getSeconds()) {
                consecutiveFaucetClaims.add(faucetClaim);
             } else if (consecutiveFaucetClaims.size() > 0
                    && Duration.between(faucetClaim.getClaimedAt(), consecutiveFaucetClaims.get(consecutiveFaucetClaims.size() - 1).getClaimedAt()).minusSeconds(getWaitingTimeForCurrency(currency)).getSeconds() <= Duration.ofHours(24).getSeconds()) {
                // CHECK LAST 'consecutibeFaucetClaim' WAS LESS THAN EXPIRATION AMOUNT FROM CURRENT 'faucetClaim'
                consecutiveFaucetClaims.add(faucetClaim);
            }
        }

        return consecutiveFaucetClaims.size();
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
        List<FaucetClaim> requestInLastPeriod = faucetClaimDao.findAllByClaimedAtBetweenAndReceivingAddressIsAndClaimedCurrencyIs(LocalDateTime.now().minusSeconds(waitingTime), LocalDateTime.now(), request.getReceivingAddress(), request.getCurrency());
        if (!requestInLastPeriod.isEmpty()) {
            if (requestInLastPeriod.size() > 1) {
                return new ResponseWrapperDto<>("You can only use the faucet every " + durationToString(waitingTimeDuration));
            } else {
                Duration leftWaitingTime = Duration.between(LocalDateTime.now(), requestInLastPeriod.get(0).getClaimedAt().plusSeconds(waitingTime));
                String leftWaitingTimeString = durationToString(leftWaitingTime);
                return new ResponseWrapperDto<>("You can only use the faucet every " + durationToString(waitingTimeDuration) + "<br />Try again in " + leftWaitingTimeString );
            }
        }

        if (request.getCurrency() == Currency.MTV) {
            List<FaucetClaim> allISAACClaims = faucetClaimDao.findAllByReceivingAddressAndClaimedCurrency(request.getReceivingAddress(), Currency.ISAAC);
            if (allISAACClaims.size() < 5) {
                return new ResponseWrapperDto<>("To prevent spam you need a total count of 5 ISAAC claims to be able to claim MTV");
            }
        }

        return new ResponseWrapperDto<>();
    }

    private String durationToString(Duration duration) {
        long effectiveTotalSecs = duration.getSeconds();
        if (duration.getSeconds() < 0 && duration.getNano() > 0) {
            effectiveTotalSecs++;
        }

        final int MINUTES_PER_HOUR = 60;
        final int SECONDS_PER_MINUTE = 60;
        final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

        long hours = effectiveTotalSecs / SECONDS_PER_HOUR;
        int minutes = (int) ((effectiveTotalSecs % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
        int secs = (int) (effectiveTotalSecs % SECONDS_PER_MINUTE);
        return (hours != 0 ? hours + " hours " : "") + (minutes != 0 ? minutes + " minutes " : "") + (secs != 0 ? secs + " secs " : "");
    }

    private ResponseWrapperDto<Void> validateCaptcha(String captchaCode) {
        if (StringUtils.equals(captchaCode, "itsvalidnow")) {
            return new ResponseWrapperDto<>();
        } else {
            return new ResponseWrapperDto<>("Invalid captcha code");
        }
    }

    public Integer getWaitingTimeForCurrency(Currency currency) {
        return switch (currency) {
            case MTV -> WAITING_TIME_BETWEEN_MTV_IN_SECONDS;
            case ISAAC -> WAITING_TIME_BETWEEN_ISAAC_IN_SECONDS;
        };
    }

    public ResponseWrapperDto<BigDecimal> getReceivingAmountForCurrency(Currency currency, int consecutiveDays) {
        return switch (currency) {
            case MTV -> getMTVClaimAmount(consecutiveDays);
            case ISAAC -> getISAACClaimAmount(consecutiveDays);
        };
    }

    public ResponseWrapperDto<BigDecimal> getMTVClaimAmount(int consecutiveDays) {
        // Currently no more than the reward on the seventh day
        if (consecutiveDays > 6) {
            consecutiveDays = 6;
        }
        return new ResponseWrapperDto<>(BigDecimal.valueOf(consecutiveDays + 1).multiply(CLAIM_MTV_AMOUNT));
    }

    public ResponseWrapperDto<BigDecimal> getISAACClaimAmount(int consecutiveDays) {
        // Currently no more than the reward on the seventh day
        if (consecutiveDays > 6) {
            consecutiveDays = 6;
        }
        return new ResponseWrapperDto<>(BigDecimal.valueOf(consecutiveDays + 1).multiply(CLAIM_ISAAC_AMOUNT));
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
