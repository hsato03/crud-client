package com.crud.project.services;

import com.crud.project.dto.ClientDTO;
import com.crud.project.entities.Client;
import com.crud.project.exceptions.DatabaseException;
import com.crud.project.exceptions.ResourceNotFoundException;
import com.crud.project.repositories.ClientRepository;
import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    ClientRepository repository;

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
        Page<Client> list = repository.findAll(pageRequest);
        return list.map(ClientDTO::new);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        Optional<Client> obj = repository.findById(id);
        return new ClientDTO(obj.orElseThrow(() -> new ResourceNotFoundException("Id not found: " + id)));
    }

    @Transactional
    public ClientDTO insert(ClientDTO dto) {
        Client entity = new Client();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new ClientDTO(entity);
    }

    @Transactional
    public ClientDTO update(Long id, ClientDTO dto) {
        try {
            Client entity = repository.getReferenceById(id);
            dto.setId(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new ClientDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }

    }

    public void delete(Long id) {
        try{
            repository.deleteById(id);
        } catch (ResourceNotFoundException e) {
            System.out.println("Id not found: " + id);
        } catch (DatabaseException e) {
            System.out.println("Integrity violation");
        }
    }

    public void copyDtoToEntity(ClientDTO dto, Client entity) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCpf(dto.getCpf());
        entity.setIncome(dto.getIncome());
        entity.setBirthDate(dto.getBirthDate());
        entity.setChildren(dto.getChildren());
    }
}
