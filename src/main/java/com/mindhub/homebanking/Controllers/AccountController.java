package com.mindhub.homebanking.Controllers;
import com.mindhub.homebanking.dto.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class AccountController {

        @Autowired
        private ClientService clientService;
        @Autowired
        private AccountService accountService;
        @Autowired
        private TransactionService transactionService;




        @GetMapping("/api/clients/current/accounts")
        public List<AccountDTO> getAccount(Authentication authentication) {
                return accountService.getAccount(authentication) ;
        }

        @GetMapping("/api/clients/current/accounts/{id}")
        public ResponseEntity<AccountDTO> getAccount(@PathVariable Long id, Authentication authentication) {
                String authenticatedUsername = authentication.getName();
                Client client = clientService.findByEmail(authenticatedUsername);
                if (client == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }
                Account account = accountService.findById(id);
                if (account == null) {
                        return ResponseEntity.notFound().build();
                }
                if (account.getClient().getId() != client.getId()) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                AccountDTO accountDTO = new AccountDTO(account);
                return ResponseEntity.ok(accountDTO);
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

                String accountNumber = accountService.randomNumber();
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
                Account account = accountService.findById(id);
                if (account == null) {
                        return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
                }
                if (account.getBalance() != 0.0) {
                        return new ResponseEntity<>("The account can't be deleted because it has a balance different from 0.", HttpStatus.FORBIDDEN);
                }

                account.setAccountActive(false);
                accountService.saveAccount(account);

                List<Transaction> transactions = transactionService.findById(id);
                transactions.forEach(transaction -> {
                        transaction.setTransactionActive(false);
                        transactionService.saveTransaction(transaction);
                });
                return new ResponseEntity<>(HttpStatus.OK);
        }
}




