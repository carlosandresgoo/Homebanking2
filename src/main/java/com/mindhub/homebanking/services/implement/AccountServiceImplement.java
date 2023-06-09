package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.Controllers.AccountController;
import com.mindhub.homebanking.dto.AccountDTO;
import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AccountServiceImplement implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientService clientService;

    @Override
    public List<AccountDTO> getAccount(Authentication authentication) {
        return new ClientDTO(clientService.findByEmail(authentication.getName())).getAccounts().stream().collect(toList()) ;
    }



    @Override
    public Account findByNumber(String number) {
        return accountRepository.findByNumber(number);
    }

    @Override
    public String randomNumber() {
            String randomNumber;
            do {
                int number = (int) (Math.random() * 899999 + 100000);
                randomNumber = "VIN-" + number;
            } while (accountRepository.findByNumber(randomNumber) != null);
            return randomNumber;
        }


    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }


    @Override
    public AccountDTO getAccountDT0(Long id) {
        return accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }



}
