package ac.asimov.faucet.dto.rest;

import ac.asimov.faucet.dto.WalletAccountDto;

import java.math.BigDecimal;

public class TransferRequestDto {

    private WalletAccountDto sender;

    private WalletAccountDto receiver;

    private BigDecimal amount;

    public TransferRequestDto(WalletAccountDto sender, WalletAccountDto receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public TransferRequestDto(WalletAccountDto sender, WalletAccountDto receiver, BigDecimal amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public WalletAccountDto getSender() {
        return sender;
    }

    public void setSender(WalletAccountDto sender) {
        this.sender = sender;
    }

    public WalletAccountDto getReceiver() {
        return receiver;
    }

    public void setReceiver(WalletAccountDto receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TransferRequestDto)) {
            return false;
        }

        if (getSender() == null) {
            if (((TransferRequestDto) obj).getSender() != null) {
                return false;
            }
        } else {
            if (((TransferRequestDto) obj).getSender() == null) {
                return false;
            }
            if (!getSender().equals(((TransferRequestDto) obj).getSender())) {
                return false;
            }
        }

        if (getReceiver() == null) {
            if (((TransferRequestDto) obj).getReceiver() != null) {
                return false;
            }
        } else {
            if (((TransferRequestDto) obj).getReceiver() == null) {
                return false;
            }

            if (!getReceiver().equals(((TransferRequestDto) obj).getReceiver())) {
                return false;
            }
        }
        return true;
    }
}
