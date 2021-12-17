package ac.asimov.faucet.dao;

import ac.asimov.faucet.model.Currency;
import ac.asimov.faucet.model.FaucetClaim;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FaucetClaimDao extends CrudRepository<FaucetClaim, Long> {

    List<FaucetClaim> findAll();

    Optional<FaucetClaim> findById(Long id);
    Optional<FaucetClaim> findByExternalUUID(String externalUUID);

    List<FaucetClaim> findAllByClaimedAtBetween(LocalDateTime a, LocalDateTime b);
    List<FaucetClaim> findAllByClaimedAtBetweenAndReceivingAddress(LocalDateTime a, LocalDateTime b, String receivingAddress);
    List<FaucetClaim> findAllByClaimedAtBetweenAndReceivingAddressIsAndClaimedCurrencyIs(LocalDateTime a, LocalDateTime b, String receivingAddress, Currency currency);

    List<FaucetClaim> findAllByReceivingAddressAndClaimedCurrency(String receivingAddress, Currency currency);
}
