package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Transaction;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountDTO {
    private long id;
    private String number;
    private LocalDateTime creationDate;
    private double balance;
    private AccountType accountType;
    private boolean accountActive;

    private Set<TransactionDTO> transactions;

    public AccountDTO(Account account) {

        this.id = account.getId();

        this.number = account.getNumber();

        this.creationDate = account.getCreationDate();

        this.balance = account.getBalance();

        this.transactions = account.getTransactions()
                .stream()
                .filter(transaction -> transaction.isTransactionActive())
                .map(transaction -> new TransactionDTO(transaction))
                .collect(Collectors.toSet());
        this.accountType = account.getAccountType();


        this.accountType = account.getAccountType();

        this.accountActive = account.isAccountActive();

    }

    public long getId() {
        return id;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Set<TransactionDTO> getTransactions() {
        return transactions;
    }

    public boolean isAccountActive() {
        return accountActive;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}

