package ac.asimov.faucet.model;

import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "faucet_claims")
@RestResource(exported = false)
public class FaucetClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "external_uuid")
    private String externalUUID = UUID.randomUUID().toString();

    @Column(name = "claimed_at", nullable = false)
    private LocalDateTime claimedAt;

    @Column(name = "receiving_address")
    private String receivingAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "claimed_currency")
    private Currency claimedCurrency;

    @Column(name = "receiving_ip_address")
    private String receivingIpAddress;

    @Column(name = "receiving_amount", precision = 19, scale = 4)
    private BigDecimal receivingAmount;

    @Column(name = "transaction_hash")
    private String transactionHash;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getExternalUUID() {
        return externalUUID;
    }

    public void setExternalUUID(String externalUUID) {
        this.externalUUID = externalUUID;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }

    public String getReceivingAddress() {
        return receivingAddress;
    }

    public void setReceivingAddress(String receivingAddress) {
        this.receivingAddress = receivingAddress;
    }

    public Currency getClaimedCurrency() {
        return claimedCurrency;
    }

    public void setClaimedCurrency(Currency claimedCurrency) {
        this.claimedCurrency = claimedCurrency;
    }

    public String getReceivingIpAddress() {
        return receivingIpAddress;
    }

    public void setReceivingIpAddress(String receivingIpAddress) {
        this.receivingIpAddress = receivingIpAddress;
    }

    public BigDecimal getReceivingAmount() {
        return receivingAmount;
    }

    public void setReceivingAmount(BigDecimal receivingAmount) {
        this.receivingAmount = receivingAmount;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
}
