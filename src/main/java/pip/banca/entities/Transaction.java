package pip.banca.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a money transfer between two IBAN accounts.
 */
@Entity
@Table(name = "transaction")
public class Transaction {
    /**
     * Database-generated transaction identifier.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * User who sends the money.
     */
    @ManyToOne
    @JoinColumn(name="sender_id")
    private User sender;

    /**
     * Account that sends the money.
     */
    @ManyToOne
    @JoinColumn(name="sender_iban")
    private UserIbanMapping sender_iban;

    /**
     * User who receives the money.
     */
    @ManyToOne
    @JoinColumn(name="receiver_id")
    private User receiver;

    /**
     * Account that receives the money.
     */
    @ManyToOne
    @JoinColumn(name="receiver_iban")
    private UserIbanMapping receiver_iban;

    /**
     * Amount transferred.
     */
    @Column
    private float amountSent;

    /**
     * Timestamp when the transfer was created.
     */
    @Column
    private LocalDateTime transactionTimestamp;

    /**
     * Human-readable transfer description.
     */
    @Column
    private String description;

    /**
     * Creates an empty transaction for JPA.
     */
    public Transaction() {}

    /**
     * Creates a transaction with sender, receiver, amount, timestamp, and description data.
     *
     * @param sender user sending money
     * @param receiver user receiving money
     * @param sender_iban account sending money
     * @param receiver_iban account receiving money
     * @param amountSent amount transferred
     * @param transactionTimestamp transfer timestamp
     * @param description transfer description
     */
    public Transaction(User sender, User receiver, UserIbanMapping sender_iban, UserIbanMapping receiver_iban, float amountSent, LocalDateTime transactionTimestamp,String description) {
        this.sender = sender;
        this.receiver = receiver;
        this.sender_iban = sender_iban;
        this.receiver_iban = receiver_iban;
        this.amountSent = amountSent;
        this.transactionTimestamp = transactionTimestamp;
        this.description = description;
    }

    /**
     * Gets the database-generated transaction identifier.
     *
     * @return transaction identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the user who sent money.
     *
     * @return sender user
     */
    public User getSender() {
        return sender;
    }

    /**
     * Gets the account that sent money.
     *
     * @return sender account
     */
    public UserIbanMapping getSender_iban() {
        return sender_iban;
    }

    /**
     * Gets the user who received money.
     *
     * @return receiver user
     */
    public User getReceiver() {
        return receiver;
    }

    /**
     * Gets the account that received money.
     *
     * @return receiver account
     */
    public UserIbanMapping getReceiver_iban() {
        return receiver_iban;
    }

    /**
     * Gets the transferred amount.
     *
     * @return transferred amount
     */
    public float getAmountSent() {
        return amountSent;
    }

    /**
     * Gets the transfer timestamp.
     *
     * @return transaction timestamp
     */
    public LocalDateTime getTransactionTimestamp() {
        return transactionTimestamp;
    }

    /**
     * Gets the transfer description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }
}
