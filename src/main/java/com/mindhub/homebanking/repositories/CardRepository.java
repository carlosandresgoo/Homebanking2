package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.ClientLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource

public interface CardRepository extends JpaRepository<Card, Long> {
    Card findByNumber (String number);
    Card findByCvv (int cvv);
    Card findById(long id);

}
