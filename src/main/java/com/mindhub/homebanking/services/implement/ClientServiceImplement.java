package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ClientServiceImplement implements ClientService {

    @Autowired
    private ClientRepository repository;
    @Override
    public List<ClientDTO> getClient() {
        return repository.findAll().stream().map(client -> new ClientDTO(client)).collect(toList());
    }

    @Override
    public ClientDTO getClients(Authentication authentication) {
        return  new ClientDTO(repository.findByEmail(authentication.getName()));
    }

    @Override
    public Client findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public void saveClient(Client client) {
        repository.save(client);
    }

    @Override
    public Client getClientAuthenticated(Authentication authentication) {
        return repository.findByEmail(authentication.getName());
    }



}
