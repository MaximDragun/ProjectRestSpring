package org.example.controllers;

import org.example.dto.MovieDTO;
import org.example.services.interfaces.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {
    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }


    @GetMapping
    public ResponseEntity<List<MovieDTO>> getMovies() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(movieService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovie(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(movieService.findById(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody MovieDTO movieDTO) {
        movieService.create(movieDTO);
        return ResponseEntity
                .ok()
                .build();
    }

    @PatchMapping
    public ResponseEntity<HttpStatus> update(@RequestBody MovieDTO movieDTO) {
        movieService.update(movieDTO);
        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        movieService.delete(id);
        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/addLink")
    public ResponseEntity<HttpStatus> addLink(@RequestParam("movieId") Long movieId,
                                              @RequestParam("actorId") Long actorId) {
        movieService.addLinkMovieActor(movieId, actorId);
        return ResponseEntity
                .ok()
                .build();
    }
}
