package com.mindhub.homebanking.Controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


@RestController
public class TransactionController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;


    @Transactional
    @PostMapping("/api/clients/current/transactions")
    public ResponseEntity<Object> newTransaction (
            Authentication authentication , @RequestParam double amount, @RequestParam String description,
            @RequestParam String initialAccount, @RequestParam String destinateAccount) {

        Client client = clientService.findByEmail(authentication.getName());
        Account accountAuthenticated = accountService.findByNumber(initialAccount.toUpperCase());
        Account destinateAccountAuthenticated = accountService.findByNumber(destinateAccount.toUpperCase());

        if ( String.valueOf(amount) == null ) {
            return new ResponseEntity<>("Please enter an amount.", HttpStatus.FORBIDDEN);
        } else if( amount < 1 ){
            return new ResponseEntity<>("Please enter an amount bigger than 0.", HttpStatus.FORBIDDEN);
        }  else if (!accountAuthenticated.isAccountActive()) {
            return new ResponseEntity<>("This account is not active.", HttpStatus.FORBIDDEN);}
        else if ( accountAuthenticated.getBalance() < amount ){
            return new ResponseEntity<>("You don't have the founds for this transaction", HttpStatus.FORBIDDEN);
        }

        if ( description.isBlank() ) {
            description = "Transaction to " + destinateAccount;
        }

        if ( initialAccount.isBlank()){
            return new ResponseEntity<>("Please enter one of your accounts", HttpStatus.FORBIDDEN);
        } else if ( accountAuthenticated == null) {
            return new ResponseEntity<>("This account " + initialAccount + " doesn't exist", HttpStatus.FORBIDDEN);
        } else if ( client.getAccounts().stream().filter(account -> account.getNumber().equalsIgnoreCase(initialAccount)).collect(Collectors.toList()).size() == 0 ){
            return new ResponseEntity<>("This account is not yours.", HttpStatus.FORBIDDEN);
        }

        if ( destinateAccount.isBlank() ){
            return new ResponseEntity<>("Please enter an account that you want to transfer the money", HttpStatus.FORBIDDEN);
        } else if ( destinateAccountAuthenticated == null ){
            return new ResponseEntity<>("This account " + destinateAccount + " doesn't exist", HttpStatus.FORBIDDEN);
        } else if (destinateAccount.equalsIgnoreCase(initialAccount)) {
            return new ResponseEntity<>("You can't send money to the same account number", HttpStatus.FORBIDDEN);
        } else if (!destinateAccountAuthenticated.isAccountActive()) {
        return new ResponseEntity<>("The destination account is not active.", HttpStatus.FORBIDDEN);
         }

        double initialBalance = accountAuthenticated.getBalance();
        double destinateBalance = destinateAccountAuthenticated.getBalance();

        Transaction newTransaction = new Transaction(TransactionType.DEBIT, amount,destinateAccount +" : "+ description, LocalDateTime.now(), true,initialBalance);
        accountAuthenticated.addTransaction(newTransaction);
        newTransaction.setBalanceAfterTransaction(initialBalance - amount);
        transactionService.saveTransaction(newTransaction);

        Transaction newTransaction2 = new Transaction(TransactionType.CREDIT, amount, initialAccount+" : "+ description, LocalDateTime.now(),true, destinateBalance);
        destinateAccountAuthenticated.addTransaction(newTransaction2);
        newTransaction2.setBalanceAfterTransaction(destinateBalance + amount);
        transactionService.saveTransaction(newTransaction2);

        accountAuthenticated.setBalance(initialBalance - amount);
        destinateAccountAuthenticated.setBalance(destinateBalance + amount);

        return new ResponseEntity<>(HttpStatus.CREATED);
    };

}
