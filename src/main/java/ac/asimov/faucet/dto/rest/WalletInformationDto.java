package ac.asimov.faucet.dto.rest;

import java.math.BigDecimal;

public class WalletInformationDto {

    private String address;

    private BigDecimal totalMTVClaimed;
    private BigDecimal totalISAACClaimed;

    private Integer totalCountOfMTVClaims;
    private Integer totalCountOfISAAClaims;

    private Integer consecutiveDaysMTV;
    private Integer consecutiveDaysISAAC;

    private BigDecimal nextClaimAmountMTV;
    private BigDecimal nextClaimAmountISAAC;

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

    public BigDecimal getTotalMTVClaimed() {
        return totalMTVClaimed;
    }

    public void setTotalMTVClaimed(BigDecimal totalMTVClaimed) {
        this.totalMTVClaimed = totalMTVClaimed;
    }

    public BigDecimal getTotalISAACClaimed() {
        return totalISAACClaimed;
    }

    public void setTotalISAACClaimed(BigDecimal totalISAACClaimed) {
        this.totalISAACClaimed = totalISAACClaimed;
    }

    public Integer getTotalCountOfMTVClaims() {
        return totalCountOfMTVClaims;
    }

    public void setTotalCountOfMTVClaims(Integer totalCountOfMTVClaims) {
        this.totalCountOfMTVClaims = totalCountOfMTVClaims;
    }

    public Integer getTotalCountOfISAAClaims() {
        return totalCountOfISAAClaims;
    }

    public void setTotalCountOfISAAClaims(Integer totalCountOfISAAClaims) {
        this.totalCountOfISAAClaims = totalCountOfISAAClaims;
    }

    public Integer getConsecutiveDaysMTV() {
        return consecutiveDaysMTV;
    }

    public void setConsecutiveDaysMTV(Integer consecutiveDaysMTV) {
        this.consecutiveDaysMTV = consecutiveDaysMTV;
    }

    public Integer getConsecutiveDaysISAAC() {
        return consecutiveDaysISAAC;
    }

    public void setConsecutiveDaysISAAC(Integer consecutiveDaysISAAC) {
        this.consecutiveDaysISAAC = consecutiveDaysISAAC;
    }

    public BigDecimal getNextClaimAmountMTV() {
        return nextClaimAmountMTV;
    }

    public void setNextClaimAmountMTV(BigDecimal nextClaimAmountMTV) {
        this.nextClaimAmountMTV = nextClaimAmountMTV;
    }

    public BigDecimal getNextClaimAmountISAAC() {
        return nextClaimAmountISAAC;
    }

    public void setNextClaimAmountISAAC(BigDecimal nextClaimAmountISAAC) {
        this.nextClaimAmountISAAC = nextClaimAmountISAAC;
    }
}