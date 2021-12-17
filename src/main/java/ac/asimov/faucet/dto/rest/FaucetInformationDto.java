package ac.asimov.faucet.dto.rest;

import java.math.BigDecimal;

public class FaucetInformationDto {

    private String address;

    private BigDecimal mtvPoolBalance;
    private BigDecimal isaacPoolBalance;

    private BigDecimal totalMTVClaimValue = BigDecimal.ZERO;
    private BigDecimal totalISAACClaimValue = BigDecimal.ZERO;

    private Long totalMTVClaimCount = 0L;
    private Long totalISAACClaimCount = 0L;

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

    public BigDecimal getTotalMTVClaimValue() {
        return totalMTVClaimValue;
    }

    public void setTotalMTVClaimValue(BigDecimal totalMTVClaimValue) {
        this.totalMTVClaimValue = totalMTVClaimValue;
    }

    public BigDecimal getTotalISAACClaimValue() {
        return totalISAACClaimValue;
    }

    public void setTotalISAACClaimValue(BigDecimal totalISAACClaimValue) {
        this.totalISAACClaimValue = totalISAACClaimValue;
    }

    public Long getTotalMTVClaimCount() {
        return totalMTVClaimCount;
    }

    public void setTotalMTVClaimCount(Long totalMTVClaimCount) {
        this.totalMTVClaimCount = totalMTVClaimCount;
    }

    public Long getTotalISAACClaimCount() {
        return totalISAACClaimCount;
    }

    public void setTotalISAACClaimCount(Long totalISAACClaimCount) {
        this.totalISAACClaimCount = totalISAACClaimCount;
    }
}
