package com.mindhub.homebanking.Controllers;
import com.mindhub.homebanking.dto.AccountDTO;
import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


import static java.util.stream.Collectors.toList;


@RestController
public class AccountController {

        @Autowired
        private ClientService clientService;
        @Autowired
        private AccountService accountService;
        @Autowired
        private AccountRepository accountRepository;
        @Autowired
        private TransactionRepository transactionRepository;

        public String randomNumber(){
                String randomNumber;
                do {
                        int number = (int) (Math.random() * 899999 + 100000);
                        randomNumber = "VIN-" + number;
                } while (accountService.findByNumber(randomNumber) != null);
                return randomNumber;
        }

        @RequestMapping("/api/clients/current/accounts")
        public List<AccountDTO> getAccount(Authentication authentication) {
                return accountService.getAccount(authentication) ;
        }

        @RequestMapping("/api/clients/current/accounts/{id}")
        public AccountDTO getAccount (@PathVariable Long id){
                return accountService.getAccountDT0(id);
        }


        @PostMapping("/api/clients/current/accounts")
        public ResponseEntity<Object> createAccount(Authentication authentication, @RequestParam String accountType) {
                Client client = clientService.findByEmail(authentication.getName());
                if (client == null) {
                        return new ResponseEntity<>("You can't create an account because you're not a client.", HttpStatus.NOT_FOUND);
                }

                if (!accountType.equalsIgnoreCase("SAVING") && !accountType.equalsIgnoreCase("CURRENT")) {
                        return new ResponseEntity<>("Please select a correct type of account.", HttpStatus.FORBIDDEN);
                }

                int totalAccounts = client.getAccounts().size();
                if (totalAccounts >= 15) {
                        return new ResponseEntity<>("Client already has the maximum number of accounts allowed.", HttpStatus.FORBIDDEN);
                }

                int activeAccounts = client.getAccounts().stream().filter(Account::isAccountActive).collect(Collectors.toList()).size();
                if (activeAccounts >= 3) {
                        return new ResponseEntity<>("Client already has the maximum number of active accounts allowed.", HttpStatus.FORBIDDEN);
                }

                String accountNumber = randomNumber();
                Account newAccount = new Account(accountNumber, LocalDateTime.now(), 0.0, true, AccountType.valueOf(accountType.toUpperCase()));
                client.addAccount(newAccount);
                accountService.saveAccount(newAccount);

                return new ResponseEntity<>(HttpStatus.CREATED);
        }


        @PutMapping ("/api/accounts/{id}")
        public ResponseEntity<String> deleteAccount(Authentication authentication,@PathVariable long id) {
                Client client = clientService.findByEmail(authentication.getName());
                if (client == null) {
                        return new ResponseEntity<>("You can't delete an account because you're not a client.", HttpStatus.FORBIDDEN);
                }
                Account account = accountRepository.findById(id);
                if (account == null) {
                        return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
                }
                if (account.getBalance() != 0.0) {
                        return new ResponseEntity<>("The account can't be deleted because it has a balance different from 0.", HttpStatus.FORBIDDEN);
                }

                account.setAccountActive(false);
                accountRepository.save(account);

                List<Transaction> transactions = transactionRepository.findByAccountId(id);
                transactions.forEach(transaction -> {
                        transaction.setTransactionActive(false);
                        transactionRepository.save(transaction);
                });
                return new ResponseEntity<>(HttpStatus.OK);
        }
}




