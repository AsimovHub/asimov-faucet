package ac.asimov.faucet.model;

import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "banned_wallets")
@RestResource(exported = false)
public class BannedWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "external_uuid")
    private String externalUUID = UUID.randomUUID().toString();

    @Column(name = "banned_at", nullable = false)
    private LocalDateTime bannedAt;

    @Column(name = "wallet_address")
    private String walletAddress;

    @Column(name = "ban_reason")
    private String banReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalUUID() {
        return externalUUID;
    }

    public void setExternalUUID(String externalUUID) {
        this.externalUUID = externalUUID;
    }

    public LocalDateTime getBannedAt() {
        return bannedAt;
    }

    public void setBannedAt(LocalDateTime bannedAt) {
        this.bannedAt = bannedAt;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }
}
