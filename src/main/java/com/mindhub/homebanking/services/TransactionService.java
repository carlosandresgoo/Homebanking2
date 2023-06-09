package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dto.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    void saveTransaction (Transaction transaction);

    List<TransactionDTO> getTransactionsByIdAndDateBetween(Account accountRequest, LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime);

    List<Transaction> findById(long id);
}
