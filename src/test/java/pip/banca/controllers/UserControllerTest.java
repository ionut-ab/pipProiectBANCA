package pip.banca.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import pip.banca.dtos.TransactionInit;
import pip.banca.entities.Transaction;
import pip.banca.entities.User;
import pip.banca.entities.UserIbanMapping;
import pip.banca.repositories.TransactionRepository;
import pip.banca.repositories.UserIbanRepository;
import pip.banca.repositories.UserRepository;
import pip.banca.services.TransactionService;
import pip.banca.services.UserIbanService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserIbanService accountService = mock(UserIbanService.class);
    private final UserIbanRepository userIbanRepository = mock(UserIbanRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final TransactionService transactionService = mock(TransactionService.class);
    private final UserController controller = new UserController(userRepository);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "accountService", accountService);
        ReflectionTestUtils.setField(controller, "userIbanRepository", userIbanRepository);
        ReflectionTestUtils.setField(controller, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(controller, "transactionService", transactionService);
    }

    @Test
    void createUserSavesRequestBody() {
        User user = new User("Ana", "Ionescu", "ana@example.com", "0712345678", "Iasi", "123");
        when(userRepository.save(user)).thenReturn(user);

        assertThat(controller.createUser(user)).isSameAs(user);
        verify(userRepository).save(user);
    }

    @Test
    void loginFindsUserByEmailOrThrows() {
        User user = new User("Ana", "Ionescu", "ana@example.com", "0712345678", "Iasi", "123");
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThat(controller.login(Map.of("email", "ana@example.com"))).isSameAs(user);
        assertThatThrownBy(() -> controller.login(Map.of("email", "missing@example.com")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Utilizatorul nu a fost găsit în baza de date");
    }

    @Test
    void getUserByIdReturnsUserOrNull() {
        UUID id = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).thenReturn(Optional.empty());

        assertThat(controller.getUserById(id)).isSameAs(user);
        assertThat(controller.getUserById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).isNull();
    }

    @Test
    void createUserAccountUsesLocatedUser() {
        UUID userId = UUID.randomUUID();
        User user = userWithId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountService.CreateAccount("RO01BANK", user)).thenReturn(true);

        assertThat(controller.createUserAccount("RO01BANK", userId)).isTrue();
    }

    @Test
    void getUserAccountsReturnsAccountsForOwner() {
        UUID userId = UUID.randomUUID();
        User user = userWithId(userId);
        List<UserIbanMapping> accounts = List.of(new UserIbanMapping("RO01BANK", user));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userIbanRepository.findByAccountOwner(user)).thenReturn(accounts);

        assertThat(controller.getUserAccounts(userId)).isSameAs(accounts);
    }

    @Test
    void getUserTransactionsCombinesOutgoingAndIncomingTransactions() {
        UUID userId = UUID.randomUUID();
        Transaction outgoing = transaction("outgoing");
        Transaction incoming = transaction("incoming");
        when(transactionRepository.findBySenderId(userId)).thenReturn(new ArrayList<>(List.of(outgoing)));
        when(transactionRepository.findByReceiverId(userId)).thenReturn(new ArrayList<>(List.of(incoming)));

        assertThat(controller.getUserTransactions(userId)).containsExactly(outgoing, incoming);
    }

    @Test
    void transferMoneyRejectsSenderIbanThatDoesNotBelongToUser() {
        UUID userId = UUID.randomUUID();
        User sender = userWithId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
        when(userIbanRepository.findByAccountOwner(sender)).thenReturn(List.of(new UserIbanMapping("RO01OWNED", sender)));
        TransactionInit request = transferRequest("RO99FOREIGN", "RO02RECEIVER", 10.0);

        assertThat(controller.sentMoneyToAccount(userId, request)).isFalse();
    }

    @Test
    void transferMoneyStartsTransactionForOwnedAccount() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        User sender = userWithId(senderId);
        User receiver = userWithId(receiverId);
        UserIbanMapping senderAccount = new UserIbanMapping("RO01OWNED", sender);
        UserIbanMapping receiverAccount = new UserIbanMapping("RO02RECEIVER", receiver);
        TransactionInit request = transferRequest("RO01OWNED", "RO02RECEIVER", 25.0);
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userIbanRepository.findByAccountOwner(sender)).thenReturn(List.of(senderAccount));
        when(userIbanRepository.findByIBAN("RO02RECEIVER")).thenReturn(Optional.of(receiverAccount));
        when(transactionService.StartTransaction(senderId, receiverId, "RO01OWNED", "RO02RECEIVER", 25.0, "payment"))
                .thenReturn(true);

        assertThat(controller.sentMoneyToAccount(senderId, request)).isTrue();
    }

    @Test
    void userDependentEndpointsThrowWhenUserDoesNotExist() {
        UUID missing = UUID.randomUUID();
        when(userRepository.findById(missing)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.createUserAccount("RO01BANK", missing))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
        assertThatThrownBy(() -> controller.getUserAccounts(missing))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
        assertThatThrownBy(() -> controller.sentMoneyToAccount(missing, transferRequest("RO01", "RO02", 10.0)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    private static Transaction transaction(String description) {
        User sender = new User();
        User receiver = new User();
        return new Transaction(sender, receiver, new UserIbanMapping("S" + description, sender),
                new UserIbanMapping("R" + description, receiver), 10.0f, LocalDateTime.now(), description);
    }

    private static TransactionInit transferRequest(String senderIban, String receiverIban, Double amount) {
        TransactionInit request = new TransactionInit();
        request.senderIBAN = senderIban;
        request.receiverIBAN = receiverIban;
        request.amount = amount;
        request.description = "payment";
        return request;
    }

    private static User userWithId(UUID id) {
        User user = new User("Test", "User", id + "@example.com", "0700000000", "City", id.toString());
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
