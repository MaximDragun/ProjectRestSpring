package org.example.services.interfaces;

import org.example.dto.MovieDTO;

import java.util.List;

public interface MovieService {
    MovieDTO findById(Long id);
    List<MovieDTO> findAll();
    void create(MovieDTO movieDTO);
    void update(MovieDTO movieDTO);
    void addLinkMovieActor(Long movieId, Long actorId);
    void delete(Long id);
}
