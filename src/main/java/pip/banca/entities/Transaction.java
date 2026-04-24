package pip.banca.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
    private UUID trans_id;

    @ManyToOne
    @JoinColumn(name="sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name="receiver_id")
    private User receiver;

    @Column
    private float amountSent;

    @Column
    private LocalDateTime transactionTimestamp;

    @Column
    private String description;


}
