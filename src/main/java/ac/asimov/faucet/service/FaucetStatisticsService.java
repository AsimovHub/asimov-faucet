package ac.asimov.faucet.service;


import ac.asimov.faucet.blockchain.MultiVACBlockchainGateway;
import ac.asimov.faucet.dao.BannedWalletDao;
import ac.asimov.faucet.dao.FaucetClaimDao;
import ac.asimov.faucet.dto.WalletAccountDto;
import ac.asimov.faucet.dto.rest.ResponseWrapperDto;
import ac.asimov.faucet.dto.rest.WalletInformationDto;
import ac.asimov.faucet.model.Currency;
import ac.asimov.faucet.model.FaucetClaim;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FaucetStatisticsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FaucetClaimDao faucetClaimDao;

    @Autowired
    private BannedWalletDao bannedWalletDao;


    @Autowired
    private FaucetService faucetService;

    @Autowired
    private MultiVACBlockchainGateway blockchainGateway;


    public ResponseWrapperDto<WalletInformationDto> getWalletInformation(String walletAddress) {
        if (!blockchainGateway.isWalletValid(new WalletAccountDto(null, walletAddress))) {
            return new ResponseWrapperDto<>("Your wallet is not valid");
        }
        WalletInformationDto walletInformation = new WalletInformationDto(walletAddress);

        walletInformation = fillMTVInformation(walletInformation);
        walletInformation = fillISAACInformation(walletInformation);

        return new ResponseWrapperDto<>(walletInformation);
    }



    private WalletInformationDto fillMTVInformation(WalletInformationDto walletInformation) {
        if (StringUtils.isBlank(walletInformation.getAddress())) {
            return walletInformation;
        }

        Currency currency = Currency.MTV;

        List<FaucetClaim> faucetClaims = faucetClaimDao.findAllByReceivingAddressAndClaimedCurrency(walletInformation.getAddress(), currency);
        Integer consecutiveMTV = faucetService.getConsecutiveDaysOfCurrency(currency, faucetClaims);
        walletInformation.setConsecutiveDaysMTV(consecutiveMTV);

        BigDecimal totalClaimed = faucetClaims.stream().map(FaucetClaim::getReceivingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        walletInformation.setTotalMTVClaimed(totalClaimed);

        walletInformation.setTotalCountOfMTVClaims(faucetClaims.size());

        // TODO Calculate
        ResponseWrapperDto<BigDecimal> nextMTVAmount = faucetService.getMTVClaimAmount(consecutiveMTV);
        if (!nextMTVAmount.hasErrors()) {
            walletInformation.setNextClaimAmountMTV(nextMTVAmount.getResponse());
        }

        // TODO:

        return walletInformation;
    }

    private WalletInformationDto fillISAACInformation(WalletInformationDto walletInformation) {
        if (StringUtils.isBlank(walletInformation.getAddress())) {
            return walletInformation;
        }
        Currency currency = Currency.ISAAC;
        List<FaucetClaim> faucetClaims = faucetClaimDao.findAllByReceivingAddressAndClaimedCurrency(walletInformation.getAddress(), currency);
        Integer consecutiveISAAC = faucetService.getConsecutiveDaysOfCurrency(currency, faucetClaims);
        walletInformation.setConsecutiveDaysISAAC(consecutiveISAAC);

        BigDecimal totalClaimed = faucetClaims.stream().map(FaucetClaim::getReceivingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        walletInformation.setTotalISAACClaimed(totalClaimed);

        walletInformation.setTotalCountOfISAAClaims(faucetClaims.size());

        // TODO Calculate
        ResponseWrapperDto<BigDecimal> nextISAACAmount = faucetService.getISAACClaimAmount(consecutiveISAAC);
        if (!nextISAACAmount.hasErrors()) {
            walletInformation.setNextClaimAmountISAAC(nextISAACAmount.getResponse());
        }

        // TODO:


        return walletInformation;
    }

}
