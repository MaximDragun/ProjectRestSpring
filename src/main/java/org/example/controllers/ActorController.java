package org.example.controllers;

import org.example.dto.ActorDTO;
import org.example.services.interfaces.ActorService;
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
@RequestMapping("/actor")
public class ActorController {
    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping
    public ResponseEntity<List<ActorDTO>> getActors() {
        return ResponseEntity
                .status(OK)
                .contentType(APPLICATION_JSON)
                .body(actorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorDTO> getActor(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(OK)
                .contentType(APPLICATION_JSON)
                .body(actorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody ActorDTO actorDTO) {
        actorService.create(actorDTO);
        return ResponseEntity
                .ok()
                .build();
    }

    @PatchMapping
    public ResponseEntity<HttpStatus> update(@RequestBody ActorDTO actorDTO) {
        actorService.update(actorDTO);
        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        actorService.delete(id);
        return ResponseEntity
                .ok()
                .build();
    }
}
