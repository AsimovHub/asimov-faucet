package ac.asimov.faucet.dto.rest;

import java.math.BigDecimal;

public class WalletInformationDto {

    private String address;

    private Integer consecutiveDaysMTV;
    private Integer consecutiveDaysISAAC;

    private BigDecimal totalMTVClaimed;
    private BigDecimal totalISAACClaimed;

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
}