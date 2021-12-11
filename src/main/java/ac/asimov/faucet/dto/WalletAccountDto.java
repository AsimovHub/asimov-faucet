package ac.asimov.faucet.dto;

import javax.persistence.Embeddable;

@Embeddable
public class WalletAccountDto {

    private String privateKey;

    private String receiverAddress;

    public WalletAccountDto() {}

    public WalletAccountDto(String privateKey, String receiverAddress) {
        this.privateKey = privateKey;
        this.receiverAddress = receiverAddress;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
}
