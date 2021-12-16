package ac.asimov.faucet.dto.rest;

import java.math.BigDecimal;

public class WalletInformationDto {

    private String address;

    private WalletFaucetInformationDto mtvInformation;
    private WalletFaucetInformationDto isaacInformation;

    public WalletInformationDto() {
    }

    public WalletInformationDto(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public WalletFaucetInformationDto getMtvInformation() {
        return mtvInformation;
    }

    public void setMtvInformation(WalletFaucetInformationDto mtvInformation) {
        this.mtvInformation = mtvInformation;
    }

    public WalletFaucetInformationDto getIsaacInformation() {
        return isaacInformation;
    }

    public void setIsaacInformation(WalletFaucetInformationDto isaacInformation) {
        this.isaacInformation = isaacInformation;
    }
}