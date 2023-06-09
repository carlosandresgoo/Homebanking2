package com.mindhub.homebanking.Controllers;

import com.mindhub.homebanking.dto.LoanApplicationDTO;
import com.mindhub.homebanking.dto.LoanDTO;
import com.mindhub.homebanking.models.*;

import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class LoanController {
    @Autowired
    ClientService clientService;
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    LoanService loanService;
    @Autowired
    ClientLoanService clientLoanService;


    @GetMapping("/api/loans")
    public List<LoanDTO> getLoans() {
        return loanService.getLoanDTO();
    }
    @Transactional
    @PostMapping("/api/loans")
    public ResponseEntity<Object> createLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication) {
        Client clientAuthenticated = clientService.findByEmail(authentication.getName());

        if (clientAuthenticated == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authenticated client not found");
        }

        Loan loan = loanService.loanById(loanApplicationDTO);

        if (loan == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Loan doesn't exist");
        }
        if (!(loan.getName().equalsIgnoreCase("MORTGAGE") || loan.getName().equalsIgnoreCase("PERSONAL") || loan.getName().equalsIgnoreCase("AUTOMOTIVE"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid loan");
        }

        Account accountReceiver = accountService.findByNumber(loanApplicationDTO.getAccountNumber().toUpperCase());

        if (accountReceiver == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account doesn't exist");
        }

        if (String.valueOf(loanApplicationDTO.getAmount()).isBlank()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Amount can't be blank");
        }
        if (String.valueOf(loanApplicationDTO.getPayments()).isBlank()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Payment can't be blank");
        }
        if (loanApplicationDTO.getAmount() < 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Amount can't be negative");
        }
        if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Amount can't be greater than max amount permitted");
        }
        if (!loan.getPayments().contains(loanApplicationDTO.getPayments())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("That payment isn't permitted");
        }
        if (!clientAuthenticated.getAccounts().contains(accountReceiver)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The Account doesn't belong to you");
        }
        if (!accountReceiver.isAccountActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Inactive accounts cannot apply for a loan");
        }

        // Validar que el cliente solo tenga un tipo de préstamo de cada uno de los tres tipos disponibles (mortgage, personal y automobile)
        for (ClientLoan clientLoan : clientAuthenticated.getClientLoans()) {
            if (clientLoan.getLoan().getName().equalsIgnoreCase(loan.getName()) && clientLoan.getFinalAmount() > 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You already have a " + loan.getName() + " loan in your account");
            }
        }

        ClientLoan clientLoan = new ClientLoan(loanApplicationDTO.getAmount(), loanApplicationDTO.getPayments(), loanApplicationDTO.getAmount() * loan.getInterest());
        clientLoanService.saveClientLoan(clientLoan);
        loan.addClientLoan(clientLoan);
        clientAuthenticated.addClientLoan(clientLoan);
        clientService.saveClient(clientAuthenticated);

        Transaction transaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName() + " loan approved", LocalDateTime.now(), true, accountReceiver.getBalance() + loanApplicationDTO.getAmount());
        // ADD BALANCE TO ACCOUNT RECEIVER
        accountReceiver.setBalance(accountReceiver.getBalance() + loanApplicationDTO.getAmount());
        transactionService.saveTransaction(transaction);
        accountReceiver.addTransaction(transaction);

        return ResponseEntity.status(HttpStatus.CREATED).body("Loan approved successfully");
    }

    @Transactional
    @PostMapping("/api/current/loans")
    public ResponseEntity<Object> payLoan(Authentication authentication , @RequestParam Long idLoan , @RequestParam String account, @RequestParam Double amount) {

        Client client = clientService.getClientAuthenticated(authentication);
        ClientLoan clientLoan = clientLoanService.getClientLoan(idLoan);
        Account accountAuthenticated = accountService.findByNumber(account);
        String description = "Pay " + clientLoan.getLoan().getName() + " loan";
//      id parameter
        if( clientLoan == null ){
            return new ResponseEntity<>("This loan doesn't exist", HttpStatus.FORBIDDEN);
        } else if( client == null){
            return new ResponseEntity<>("You are not registered as a client", HttpStatus.FORBIDDEN);
        }
//        account parameter
        if ( account.isBlank() ){
            return new ResponseEntity<>("PLease enter an account", HttpStatus.FORBIDDEN);
        } else if ( client.getAccounts().stream().filter(accounts -> accounts.getNumber().equalsIgnoreCase(account)).collect(toList()).size() == 0 ){
            return new ResponseEntity<>("This account is not yours.", HttpStatus.FORBIDDEN);}
//      amount parameter
        if (clientLoan.getFinalAmount() <= 0) {
            return new ResponseEntity<>("This loan has already been fully paid", HttpStatus.FORBIDDEN);
        }
        if ( amount < 1 ){
            return new ResponseEntity<>("PLease enter an amount bigger than 0", HttpStatus.FORBIDDEN);
        }  else if ( accountAuthenticated.getBalance() < amount ){
            return new ResponseEntity<>("Insufficient balance in your account " + accountAuthenticated.getNumber(), HttpStatus.FORBIDDEN);
        }

        double initialBalance = accountAuthenticated.getBalance();

        Transaction newTransaction = new Transaction(TransactionType.DEBIT, amount, description , LocalDateTime.now(),true, initialBalance);
        accountAuthenticated.addTransaction(newTransaction);
        newTransaction.setBalanceAfterTransaction(initialBalance - amount);
        transactionService.saveTransaction(newTransaction);

        accountAuthenticated.setBalance( accountAuthenticated.getBalance() - amount );
        clientLoan.setPayments(clientLoan.getPayments()-1);
        clientLoan.setFinalAmount( clientLoan.getFinalAmount() - amount);

        if (clientLoan.getPayments() == 0) {
            clientLoan.setFinalAmount(0.0);
        } else {
            clientLoan.setFinalAmount(clientLoan.getFinalAmount() - amount);
        }


        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/api/loans/manager")
    public ResponseEntity<Object> newLoanAdmin(@RequestBody Loan loan) {

        Loan loan1 = new Loan(loan.getName(), loan.getMaxAmount() , loan.getPayments(),loan.getInterest());
        loanService.saveLoan(loan1);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}





