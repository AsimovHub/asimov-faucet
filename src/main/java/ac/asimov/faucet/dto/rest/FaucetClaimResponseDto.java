package ac.asimov.faucet.dto.rest;

import ac.asimov.faucet.model.Currency;

import java.math.BigDecimal;

public class FaucetClaimResponseDto {

    private String receivingAddress;

    private BigDecimal receivingAmount;
    private Currency receivingCurrency;


    public String getReceivingAddress() {
        return receivingAddress;
    }

    public void setReceivingAddress(String receivingAddress) {
        this.receivingAddress = receivingAddress;
    }

    public BigDecimal getReceivingAmount() {
        return receivingAmount;
    }

    public void setReceivingAmount(BigDecimal receivingAmount) {
        this.receivingAmount = receivingAmount;
    }

    public Currency getReceivingCurrency() {
        return receivingCurrency;
    }

    public void setReceivingCurrency(Currency receivingCurrency) {
        this.receivingCurrency = receivingCurrency;
    }
}
