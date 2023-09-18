package org.example.controllers;

import org.example.dto.DirectorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/people")
public class TestController {

    @GetMapping()
    public ResponseEntity<DirectorDTO> getPeople() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setName("Максим");
        directorDTO.setAge(18);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(directorDTO);
    }
}
