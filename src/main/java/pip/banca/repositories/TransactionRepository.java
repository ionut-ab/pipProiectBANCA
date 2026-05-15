package pip.banca.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pip.banca.entities.Transaction;
import pip.banca.entities.User;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Derived query methods
    Optional<Transaction> findById(UUID id);

    List<Transaction> findBySenderId(UUID sender_id);
    List<Transaction> findByReceiverId(UUID receiver_id);

}