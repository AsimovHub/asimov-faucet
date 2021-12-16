package ac.asimov.faucet.dto.rest;

import java.math.BigDecimal;

public class WalletFaucetInformationDto {

    private BigDecimal totalClaimedAmount;
    private Integer totalClaims;
    private Integer consecutiveUsedDays;
    private BigDecimal nextClaimAmount;

    public BigDecimal getTotalClaimedAmount() {
        return totalClaimedAmount;
    }

    public void setTotalClaimedAmount(BigDecimal totalClaimedAmount) {
        this.totalClaimedAmount = totalClaimedAmount;
    }

    public Integer getTotalClaims() {
        return totalClaims;
    }

    public void setTotalClaims(Integer totalClaims) {
        this.totalClaims = totalClaims;
    }

    public Integer getConsecutiveUsedDays() {
        return consecutiveUsedDays;
    }

    public void setConsecutiveUsedDays(Integer consecutiveUsedDays) {
        this.consecutiveUsedDays = consecutiveUsedDays;
    }

    public BigDecimal getNextClaimAmount() {
        return nextClaimAmount;
    }

    public void setNextClaimAmount(BigDecimal nextClaimAmount) {
        this.nextClaimAmount = nextClaimAmount;
    }
}
