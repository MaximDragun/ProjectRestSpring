package org.example.services.interfaces;

import org.example.dto.ActorDTO;

import java.util.List;

public interface ActorService {
    ActorDTO findById(Long id);
    List<ActorDTO> findAll();
    void create(ActorDTO actorDTO);
    void update(ActorDTO actorDTO);
    void delete(Long id);
}
