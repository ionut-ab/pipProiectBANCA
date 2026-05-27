package pip.banca.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name="sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name="sender_iban")
    private UserIbanMapping sender_iban;

    @ManyToOne
    @JoinColumn(name="receiver_id")
    private User receiver;

    @ManyToOne
    @JoinColumn(name="receiver_iban")
    private UserIbanMapping receiver_iban;

    @Column
    private float amountSent;

    @Column
    private LocalDateTime transactionTimestamp;

    @Column
    private String description;

    public Transaction() {}

    public Transaction(User sender, User receiver, UserIbanMapping sender_iban, UserIbanMapping receiver_iban, float amountSent, LocalDateTime transactionTimestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.sender_iban = sender_iban;
        this.receiver_iban = receiver_iban;
        this.amountSent = amountSent;
        this.transactionTimestamp = transactionTimestamp;
    }
}
