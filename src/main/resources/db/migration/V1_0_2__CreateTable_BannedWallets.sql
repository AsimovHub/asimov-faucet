CREATE TABLE IF NOT EXISTS banned_wallets (
                        id BIGINT(20) NOT NULL AUTO_INCREMENT,
                        external_uuid VARCHAR(255) NOT NULL,
                        banned_at DATETIME NOT NULL,
                        ban_reason VARCHAR(255) DEFAULT NULL,

                        wallet_address VARCHAR(255) NOT NULL,
                        PRIMARY KEY(id)
);