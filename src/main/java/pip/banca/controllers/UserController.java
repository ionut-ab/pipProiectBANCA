package pip.banca.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import pip.banca.dtos.TransactionInit;
import pip.banca.entities.Transaction;
import pip.banca.entities.User;
import pip.banca.entities.UserIbanMapping;
import pip.banca.repositories.TransactionRepository;
import pip.banca.repositories.UserIbanRepository;
import pip.banca.repositories.UserRepository;
import pip.banca.services.TransactionService;
import pip.banca.services.UserIbanService;

import java.util.*;

/**
 * REST controller for user, account, and transfer operations.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * Repository used for user lookups and persistence.
     */
    private final UserRepository userRepository;

    /**
     * Service that creates and updates IBAN accounts.
     */
    @Autowired
    private UserIbanService accountService;

    /**
     * Repository used to query user IBAN mappings.
     */
    @Autowired
    private UserIbanRepository userIbanRepository;

    /**
     * Repository used to query transaction history.
     */
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Service that performs money transfers.
     */
    @Autowired
    private TransactionService transactionService;

    /**
     * Creates a controller backed by the user repository.
     *
     * @param userRepository repository used by user endpoints
     */
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Persists a new user sent in the request body.
     *
     * @param user user data to store
     * @return saved user entity
     */
    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        System.out.println("CREATED USER-");

        return userRepository.save(user);
    }

    /**
     * Authenticates a user by finding the account associated with the supplied email address.
     *
     * @param credentials map containing the email credential
     * @return user matching the supplied email
     * @throws RuntimeException when no user exists for the supplied email
     */
    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit în baza de date"));
    }

    /**
     * Retrieves a user by identifier.
     *
     * @param id user identifier
     * @return matching user, or null when no user exists
     */
    @GetMapping("/id")
    public User getUserById(@RequestParam UUID id){return userRepository.findById(id).orElse(null);};

    /**
     * Creates a bank account for an existing user.
     *
     * @param IBAN account IBAN to create
     * @param userID owner identifier
     * @return true when the account is created successfully
     * @throws RuntimeException when the owner does not exist
     */
    @PostMapping("/create_account")
    public boolean createUserAccount(@RequestBody String IBAN, @RequestParam UUID userID){
        var user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found"));
        return accountService.CreateAccount(IBAN, user);
    }

    /**
     * Returns every account owned by a user.
     *
     * @param userID owner identifier
     * @return account mappings owned by the user
     * @throws RuntimeException when the user does not exist
     */
    @GetMapping("/get_accounts")
    public List<UserIbanMapping> getUserAccounts(@RequestParam UUID userID){
        var user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found"));
        return userIbanRepository.findByAccountOwner(user);
    }

    /**
     * Returns all transactions where the user is either sender or receiver.
     *
     * @param userID user identifier
     * @return combined outgoing and incoming transaction list
     */
    @GetMapping("/get_transactions")
    public ArrayList<Transaction> getUserTransactions(@RequestParam UUID userID){
        var outgoingTransactions = transactionRepository.findBySenderId(userID);
        var incomingTransactions = transactionRepository.findByReceiverId(userID);
        
        ArrayList<Transaction> allTransactions = new ArrayList<>(outgoingTransactions);
        allTransactions.addAll(incomingTransactions);
        
        return allTransactions;
    }

    /**
     * Transfers money from one account to another after checking sender account ownership.
     *
     * @param userID identifier of the user initiating the transfer
     * @param data transfer request data
     * @return true when the transfer succeeds
     * @throws RuntimeException when the initiating user does not exist
     */
    @PostMapping("/transfer_money")
    public boolean sentMoneyToAccount(
            @RequestParam UUID userID,
            @RequestBody TransactionInit data)
    {
        var user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found"));
        var accounts = userIbanRepository.findByAccountOwner(user);
        boolean accountBelongsToCurrentUser = false;
        for(var account : accounts){
            if (Objects.equals(account.getIBAN(), data.senderIBAN)) {
                accountBelongsToCurrentUser = true;
                break;
            }
        }
        if(!accountBelongsToCurrentUser)
        {
            return false;
        }

        UUID receiverID = null;
        var receiverAccount = userIbanRepository.findByIBAN(data.receiverIBAN);
        if (receiverAccount.isPresent()) {
            receiverID = receiverAccount.get().getAccountOwner().getId();
        }

        var result = transactionService.StartTransaction(userID, receiverID, data.senderIBAN, data.receiverIBAN, data.amount, data.description);
        return result;
    }
}
