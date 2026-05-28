package pip.banca.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import pip.banca.entities.Transaction;
import pip.banca.entities.User;
import pip.banca.entities.UserIbanMapping;
import pip.banca.repositories.TransactionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final UserIbanService ibanService = mock(UserIbanService.class);
    private final TransactionService service = new TransactionService(transactionRepository);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "ibanServ", ibanService);
    }

    @Test
    void startTransactionTransfersMoneyAndStoresTransaction() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        User sender = userWithId(senderId);
        User receiver = userWithId(receiverId);
        UserIbanMapping senderAccount = new UserIbanMapping("RO01SENDER", sender);
        senderAccount.setBalance(500.0);
        UserIbanMapping receiverAccount = new UserIbanMapping("RO02RECEIVER", receiver);
        receiverAccount.setBalance(100.0);
        when(ibanService.GetUserIban("RO01SENDER")).thenReturn(Optional.of(senderAccount));
        when(ibanService.GetUserIban("RO02RECEIVER")).thenReturn(Optional.of(receiverAccount));

        boolean result = service.StartTransaction(senderId, receiverId, "RO01SENDER", "RO02RECEIVER", 150.0, "gift");

        assertThat(result).isTrue();
        assertThat(senderAccount.getBalance()).isEqualTo(350.0);
        assertThat(receiverAccount.getBalance()).isEqualTo(250.0);
        verify(ibanService).SaveAccountState(senderAccount);
        verify(ibanService).SaveAccountState(receiverAccount);
        org.mockito.ArgumentCaptor<Transaction> captor = org.mockito.ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertThat(captor.getValue().getSender()).isSameAs(sender);
        assertThat(captor.getValue().getReceiver()).isSameAs(receiver);
        assertThat(captor.getValue().getAmountSent()).isEqualTo(150.0f);
        assertThat(captor.getValue().getDescription()).isEqualTo("gift");
    }

    @Test
    void startTransactionReturnsFalseWhenAccountsAreMissing() {
        when(ibanService.GetUserIban("MISSING")).thenReturn(Optional.empty());

        boolean missingSender = service.StartTransaction(UUID.randomUUID(), UUID.randomUUID(), "MISSING", "RO02", 10.0, "x");

        assertThat(missingSender).isFalse();
        verify(transactionRepository, never()).save(any());

        UserIbanMapping senderAccount = new UserIbanMapping("RO01", new User());
        when(ibanService.GetUserIban("RO01")).thenReturn(Optional.of(senderAccount));
        when(ibanService.GetUserIban("MISSING_RECEIVER")).thenReturn(Optional.empty());

        boolean missingReceiver = service.StartTransaction(UUID.randomUUID(), UUID.randomUUID(), "RO01", "MISSING_RECEIVER", 10.0, "x");

        assertThat(missingReceiver).isFalse();
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void startTransactionReturnsFalseWhenTransferThrows() {
        UserIbanMapping senderAccount = new UserIbanMapping("RO01", new User());
        senderAccount.setBalance(5.0);
        UserIbanMapping receiverAccount = new UserIbanMapping("RO02", new User());
        when(ibanService.GetUserIban("RO01")).thenReturn(Optional.of(senderAccount));
        when(ibanService.GetUserIban("RO02")).thenReturn(Optional.of(receiverAccount));

        boolean result = service.StartTransaction(UUID.randomUUID(), UUID.randomUUID(), "RO01", "RO02", 10.0, "too much");

        assertThat(result).isFalse();
        verify(transactionRepository, never()).save(any());
    }

    private static User userWithId(UUID id) {
        User user = new User("Test", "User", id + "@example.com", "0700000000", "City", id.toString());
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
