package org.example.controllers;

import org.example.dto.DirectorDTO;
import org.example.services.interfaces.DirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/director")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public ResponseEntity<List<DirectorDTO>> getDirectors() {
        return ResponseEntity
                .status(OK)
                .contentType(APPLICATION_JSON)
                .body(directorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorDTO> getDirector(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(OK)
                .contentType(APPLICATION_JSON)
                .body(directorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody DirectorDTO directorDTO) {
        directorService.create(directorDTO);
        return ResponseEntity
                .ok()
                .build();
    }

    @PatchMapping
    public ResponseEntity<HttpStatus> update(@RequestBody DirectorDTO directorDTO) {
        directorService.update(directorDTO);
        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        directorService.delete(id);
        return ResponseEntity
                .ok()
                .build();
    }
}
