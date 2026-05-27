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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    private UserIbanService accountService;
    @Autowired
    private UserIbanRepository userIbanRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/create")

    public User createUser(@RequestBody User user) {
        System.out.println("CREATED USER-");

        return userRepository.save(user);
    }

    @GetMapping("/id")
    public User getUserById(@RequestParam UUID id){return userRepository.findById(id).orElse(null);};

    @PostMapping("/create_account")
    public boolean createUserAccount(@RequestBody String IBAN, @RequestParam UUID userID){
        var user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found"));
        return accountService.CreateAccount(IBAN, user);
    }

    @GetMapping("/get_accounts")
    public List<UserIbanMapping> getUserAccounts(@RequestParam UUID userID){
        var user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found"));
        return userIbanRepository.findByAccountOwner(user);
    }

    @GetMapping("/get_transactions")
    public List<Transaction> getUserTransactions(@RequestParam UUID userID){
        var outgoingTransactions = transactionRepository.findBySenderId(userID);
        var incomingTransactions = transactionRepository.findByReceiverId(userID);
        
        List<Transaction> allTransactions = new ArrayList<>(outgoingTransactions);
        allTransactions.addAll(incomingTransactions);
        
        return allTransactions;
    }

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

        var result = transactionService.StartTransaction(userID, receiverID, data.senderIBAN, data.receiverIBAN, data.amount);
        return result;
    }
}