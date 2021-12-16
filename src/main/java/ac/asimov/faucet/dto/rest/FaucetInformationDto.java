package ac.asimov.faucet.dto.rest;

import java.math.BigDecimal;

public class FaucetInformationDto {

    private String address;

    private BigDecimal mtvPoolBalance;
    private BigDecimal isaacPoolBalance;

    private Long totalClaims;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getMtvPoolBalance() {
        return mtvPoolBalance;
    }

    public void setMtvPoolBalance(BigDecimal mtvPoolBalance) {
        this.mtvPoolBalance = mtvPoolBalance;
    }

    public BigDecimal getIsaacPoolBalance() {
        return isaacPoolBalance;
    }

    public void setIsaacPoolBalance(BigDecimal isaacPoolBalance) {
        this.isaacPoolBalance = isaacPoolBalance;
    }

    public Long getTotalClaims() {
        return totalClaims;
    }

    public void setTotalClaims(Long totalClaims) {
        this.totalClaims = totalClaims;
    }
}
