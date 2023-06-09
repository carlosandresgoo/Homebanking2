package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dto.CardDTO;
import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;


import static java.util.stream.Collectors.toList;
@Service
public class CardServiceImplement implements CardService {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientService clientService;


    @Override
    public List<CardDTO> getCardDTO(Authentication authentication) {
        return new ClientDTO(clientService.findByEmail(authentication.getName())).getCards().stream().collect(toList());
    }

    @Override
    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    @Override
    public Card findById(long id) {
        return cardRepository.findById(id);
    }

    @Override
    public int randomNumbercvv() {
            int cardnumber;
            cardnumber = (int) (Math.random() * 899 + 100);
            return cardnumber;
        }

    @Override
    public String generateCardNumber() {
            String cardNumber;
            do {
                int firstGroup = (int) (Math.random() * 8999 + 1000);
                int secondGroup = (int) (Math.random() * 8999 + 1000);
                int thirdGroup = (int) (Math.random() * 8999 + 1000);
                int fourthGroup = (int) (Math.random() * 8999 + 1000);
                cardNumber = firstGroup + "-" + secondGroup + "-" + thirdGroup + "-" + fourthGroup;
            } while (cardRepository.findByNumber(cardNumber) != null);
            return cardNumber;
        }



    }











