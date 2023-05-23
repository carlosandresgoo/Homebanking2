package com.mindhub.homebanking.Controllers;

import com.mindhub.homebanking.dto.CardDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;


@RestController
public class CardController {
    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/api/clients/current/cards")
    public List<CardDTO> getAccount(Authentication authentication) {
        return cardService.getCardDTO(authentication);
    }
    @PostMapping( "/api/clients/current/cards")
    public ResponseEntity<Object> createCards(Authentication authentication, @RequestParam String type, @RequestParam String color) {
        if (!type.equalsIgnoreCase("DEBIT") && !type.equalsIgnoreCase("CREDIT")) {
            return new ResponseEntity<>("Please enter a valid type. Only 'DEBIT' and 'CREDIT' are allowed.", HttpStatus.FORBIDDEN);
        }

        if (!color.matches("^(GOLD|SILVER|TITANIUM)$")) {
            return new ResponseEntity<>("Please enter a valid color. Only 'GOLD', 'SILVER' and 'TITANIUM' are allowed in uppercase letters.", HttpStatus.FORBIDDEN);
        }

        Client client = clientService.findByEmail(authentication.getName());

        if(client == null) {
            return new ResponseEntity<>("you can't create a card because you're not a client.", HttpStatus.NOT_FOUND);
        }

        int totalCards = client.getCards().size();
        int activeCards = (int) client.getCards().stream().filter(Card::isCardActive).count();

        if (totalCards >= 15 || activeCards >= 15) {
            return new ResponseEntity<>("Client already has the maximum number of cards allowed.", HttpStatus.FORBIDDEN);
        }

        for (Card card : client.getCards()) {
            if (card.getType().equals(CardType.valueOf(type)) && card.getColor().equals(CardColor.valueOf(color)) && card.isCardActive()) {
                return new ResponseEntity<>("you already have this type of card", HttpStatus.FORBIDDEN);
            }
        }

        int cvvnumber = cardService.randomNumbercvv();
        String cardNumber = cardService.generateCardNumber();
        Card newCard = new Card(client.getFirtsName() + " " + client.getLastName(), CardType.valueOf(type), CardColor.valueOf(color), cardNumber, cvvnumber, LocalDate.now().plusYears(5), LocalDate.now(),true);
        client.addCard(newCard);
        cardService.saveCard(newCard);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PutMapping("/api/cards/{id}")
    public ResponseEntity<Object> deleteCard(Authentication authentication, @PathVariable long id) {
        Client client = clientService.findByEmail(authentication.getName());
        if (client == null) {
            return new ResponseEntity<>("You can't delete an account because you're not a client.", HttpStatus.FORBIDDEN);
        }
        Card card = cardService.findById(id);
        if (card == null) {
            return new ResponseEntity<>("Card not found", HttpStatus.NOT_FOUND);
        }

        card.setCardActive(false);
        cardService.saveCard(card);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}

