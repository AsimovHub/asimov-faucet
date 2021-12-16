package ac.asimov.faucet.service;


import ac.asimov.faucet.blockchain.MultiVACBlockchainGateway;
import ac.asimov.faucet.dao.BannedWalletDao;
import ac.asimov.faucet.dao.FaucetClaimDao;
import ac.asimov.faucet.dto.AccountBalanceDto;
import ac.asimov.faucet.dto.WalletAccountDto;
import ac.asimov.faucet.dto.rest.FaucetInformationDto;
import ac.asimov.faucet.dto.rest.ResponseWrapperDto;
import ac.asimov.faucet.dto.rest.WalletFaucetInformationDto;
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
import java.math.RoundingMode;
import java.util.List;

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

        walletInformation.setMtvInformation(fillFaucetInformation(walletInformation, Currency.MTV));
        walletInformation.setIsaacInformation(fillFaucetInformation(walletInformation, Currency.ISAAC));

        return new ResponseWrapperDto<>(walletInformation);
    }



    private WalletFaucetInformationDto fillFaucetInformation(WalletInformationDto walletInformation, Currency currency) {
        if (StringUtils.isBlank(walletInformation.getAddress())) {
            return null;
        }

        WalletFaucetInformationDto faucetInformation = new WalletFaucetInformationDto();

        List<FaucetClaim> faucetClaims = faucetClaimDao.findAllByReceivingAddressAndClaimedCurrency(walletInformation.getAddress(), currency);
        Integer consecutiveDays = faucetService.getConsecutiveDaysOfCurrency(currency, faucetClaims);
        faucetInformation.setConsecutiveUsedDays(consecutiveDays);

        BigDecimal totalClaimed = faucetClaims.stream().map(FaucetClaim::getReceivingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        faucetInformation.setTotalClaimedAmount(totalClaimed);

        faucetInformation.setTotalClaims(faucetClaims.size());

        ResponseWrapperDto<BigDecimal> nextClaimableAmountResponse = faucetService.getReceivingAmountForCurrency(currency, consecutiveDays);
        if (!nextClaimableAmountResponse.hasErrors()) {
            faucetInformation.setNextClaimAmount(nextClaimableAmountResponse.getResponse());
        }
        
        // TODO:

        return faucetInformation;
    }

    public ResponseWrapperDto<FaucetInformationDto> getFaucetInformation() {
        try {
            FaucetInformationDto faucetInformation = new FaucetInformationDto();

            WalletAccountDto publicFaucetWallet = faucetService.getFaucetPublicWalletAccount();

            faucetInformation.setAddress(publicFaucetWallet.getReceiverAddress());

            ResponseWrapperDto<AccountBalanceDto> mtvBalanceResponse = blockchainGateway.getMTVAccountBalance(publicFaucetWallet);
            if (!mtvBalanceResponse.hasErrors()) {
                faucetInformation.setMtvPoolBalance(mtvBalanceResponse.getResponse().getAmount().setScale(2, RoundingMode.HALF_UP));
            }

            ResponseWrapperDto<AccountBalanceDto> isaacBalanceResponse = blockchainGateway.getISAACAccountBalance(publicFaucetWallet);
            if (!isaacBalanceResponse.hasErrors()) {
                faucetInformation.setIsaacPoolBalance(isaacBalanceResponse.getResponse().getAmount().setScale(2, RoundingMode.HALF_UP));
            }

            faucetInformation.setTotalClaims(faucetClaimDao.countClaims());

            // TODO: Maybe add other information
            return new ResponseWrapperDto<>(faucetInformation);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseWrapperDto<>("Cannot get faucet data");
        }
    }
}
