package org.example.services.interfaces;

import org.example.dto.DirectorDTO;

import java.util.List;

public interface DirectorService {
    DirectorDTO findById(Long id);
    List<DirectorDTO> findAll();
    void create(DirectorDTO directorDTO);
    void update(DirectorDTO directorDTO);
    void delete(Long id);
}
