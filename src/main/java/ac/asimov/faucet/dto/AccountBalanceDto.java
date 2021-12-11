package ac.asimov.faucet.dto;


import java.math.BigDecimal;

public class AccountBalanceDto {

    public AccountBalanceDto(BigDecimal amount) {
        this.amount = amount;
    }

    private BigDecimal amount = BigDecimal.ZERO;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
