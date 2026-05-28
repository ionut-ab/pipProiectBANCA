package pip.banca.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pip.banca.entities.Transaction;
import pip.banca.entities.User;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

/**
 * Repository for persisting and querying money transfer transactions.
 */
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Derived query methods
    /**
     * Finds a transaction by identifier.
     *
     * @param id transaction identifier
     * @return transaction with the supplied identifier, when present
     */
    Optional<Transaction> findById(UUID id);

    /**
     * Finds transactions sent by the supplied user identifier.
     *
     * @param sender_id sender user identifier
     * @return outgoing transactions for the sender
     */
    ArrayList<Transaction> findBySenderId(UUID sender_id);

    /**
     * Finds transactions received by the supplied user identifier.
     *
     * @param receiver_id receiver user identifier
     * @return incoming transactions for the receiver
     */
    ArrayList<Transaction> findByReceiverId(UUID receiver_id);

}
