CREATE TABLE IF NOT EXISTS faucet_claims (
                        id BIGINT(20) NOT NULL AUTO_INCREMENT,
                        external_uuid VARCHAR(255) NOT NULL,
                        claimed_at DATETIME NOT NULL,

                        receiving_address VARCHAR(255) NOT NULL,
                        claimed_currency VARCHAR(255) NOT NULL,
                        receiving_amount DECIMAL(19,4) DEFAULT NULL,
                        transaction_hash VARCHAR(255) NOT NULL,
                        receiving_ip_address VARCHAR(255) NOT NULL,

                        PRIMARY KEY(id)
);