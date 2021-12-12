package ac.asimov.faucet.service;


import ac.asimov.faucet.dao.BannedWalletDao;
import ac.asimov.faucet.dao.FaucetClaimDao;
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


    public ResponseWrapperDto<WalletInformationDto> getWalletInformation(String walletAddress) {
        WalletInformationDto walletInformation = new WalletInformationDto(walletAddress);

        walletInformation = fillMTVInformation(walletInformation);
        walletInformation = fillISAACInformation(walletInformation);

        return new ResponseWrapperDto<>("Not implemented yet");
    }

    private WalletInformationDto fillMTVInformation(WalletInformationDto walletInformation) {
        if (StringUtils.isBlank(walletInformation.getAddress())) {
            return walletInformation;
        }

        Currency currency = Currency.MTV;

        List<FaucetClaim> faucetClaims = faucetClaimDao.findAllByReceivingAddressAndClaimedCurrency(walletInformation.getAddress(), currency);
        Integer consecutiveMTV = getConsecutiveDaysOfCurrency(walletInformation.getAddress(), currency, faucetClaims);
        walletInformation.setConsecutiveDaysMTV(consecutiveMTV);

        BigDecimal totalClaimed = faucetClaims.stream().map(FaucetClaim::getReceivingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        walletInformation.setTotalMTVClaimed(totalClaimed);

        // TODO:

        return walletInformation;
    }

    private WalletInformationDto fillISAACInformation(WalletInformationDto walletInformation) {
        if (StringUtils.isBlank(walletInformation.getAddress())) {
            return walletInformation;
        }
        Currency currency = Currency.ISAAC;
        List<FaucetClaim> faucetClaims = faucetClaimDao.findAllByReceivingAddressAndClaimedCurrency(walletInformation.getAddress(), currency);
        Integer consecutiveISAAC = getConsecutiveDaysOfCurrency(walletInformation.getAddress(), currency, faucetClaims);
        walletInformation.setConsecutiveDaysISAAC(consecutiveISAAC);

        BigDecimal totalClaimed = faucetClaims.stream().map(FaucetClaim::getReceivingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        walletInformation.setTotalISAACClaimed(totalClaimed);

        // TODO:


        return walletInformation;
    }

    private Integer getConsecutiveDaysOfCurrency(String walletAddress, Currency currency, List<FaucetClaim> faucetClaimList) {
        List<FaucetClaim> consecutiveFaucetClaims = new ArrayList<>();

        faucetClaimList = faucetClaimList.stream().sorted((o1, o2) -> o1.getClaimedAt().compareTo(o2.getClaimedAt())).collect(Collectors.toList());
        // TODO: Check earliest is at [0]
        for (FaucetClaim faucetClaim : faucetClaimList) {
            if (consecutiveFaucetClaims.size() == 0
                    && Duration.between(faucetClaim.getClaimedAt(), LocalDateTime.now()).minusSeconds(faucetService.getWaitingTimeForCurrency(currency)).getSeconds() <= 0) {
                consecutiveFaucetClaims.add(faucetClaim);
            }

            if (consecutiveFaucetClaims.size() > 0
                    && Duration.between(consecutiveFaucetClaims.get(consecutiveFaucetClaims.size() - 1).getClaimedAt(), faucetClaim.getClaimedAt()).minusSeconds(faucetService.getWaitingTimeForCurrency(currency)).getSeconds() <= 0) {
                // CHECK LAST 'consecutibeFaucetClaim' WAS LESS THAN EXPIRATION AMOUNT FROM CURRENT 'faucetClaim'
                consecutiveFaucetClaims.add(faucetClaim);
            }
        }

        return consecutiveFaucetClaims.size();
    }
}
