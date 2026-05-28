package pip.banca.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {

    @Test
    void constructorExposesTransactionData() {
        User sender = new User("Ana", "Ionescu", "ana@example.com", "0712345678", "Iasi", "123");
        User receiver = new User("Ion", "Pop", "ion@example.com", "0788888888", "Cluj", "456");
        UserIbanMapping senderAccount = new UserIbanMapping("RO01BANK", sender);
        UserIbanMapping receiverAccount = new UserIbanMapping("RO02BANK", receiver);
        LocalDateTime timestamp = LocalDateTime.of(2026, 5, 28, 10, 15);

        Transaction transaction = new Transaction(sender, receiver, senderAccount, receiverAccount, 99.5f, timestamp, "invoice");

        assertThat(transaction.getId()).isNull();
        assertThat(transaction.getSender()).isSameAs(sender);
        assertThat(transaction.getReceiver()).isSameAs(receiver);
        assertThat(transaction.getSender_iban()).isSameAs(senderAccount);
        assertThat(transaction.getReceiver_iban()).isSameAs(receiverAccount);
        assertThat(transaction.getAmountSent()).isEqualTo(99.5f);
        assertThat(transaction.getTransactionTimestamp()).isEqualTo(timestamp);
        assertThat(transaction.getDescription()).isEqualTo("invoice");
    }
}
