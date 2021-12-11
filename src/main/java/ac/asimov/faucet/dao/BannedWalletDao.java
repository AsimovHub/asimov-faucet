package ac.asimov.faucet.dao;

import ac.asimov.faucet.model.BannedWallet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BannedWalletDao extends CrudRepository<BannedWallet, Long> {

    List<BannedWallet> findAll();

    Optional<BannedWallet> findById(Long id);
    Optional<BannedWallet> findByExternalUUID(String externalUUID);
    Optional<BannedWallet> findByWalletAddress(String walletAddress);
}
