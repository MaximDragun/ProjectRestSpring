package org.example.services;

import org.example.dto.MovieDTO;
import org.example.mapping.MapperDTO;
import org.example.models.Movie;
import org.example.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;
    private final MapperDTO mapperDTO;

    @Autowired
    public MovieService(MovieRepository movieRepository, MapperDTO mapperDTO) {
        this.movieRepository = movieRepository;
        this.mapperDTO = mapperDTO;
    }
    public MovieDTO findById(Long id){
        if (id == null)
            throw new ResponseStatusException(BAD_REQUEST, "Введите id фильма");
        return mapperDTO.toMovieDTO(checkOptionalMovie(id));
    }

    public List<MovieDTO> findAll(){
        return   movieRepository.findAll().stream()
                .map(mapperDTO::toMovieDTO)
                .toList();
    }

    @Transactional
    public void create(MovieDTO movieDTO) {
        Movie movie = movieRepository.save(mapperDTO.toMovie(movieDTO));
        movie.setActorList(new ArrayList<>());
    }

    @Transactional
    public void update(MovieDTO movieDTO) {
        Movie checkMovie = checkOptionalMovie(movieDTO.getId());
        Movie movie = mapperDTO.toMovie(movieDTO);
        movie.setActorList(checkMovie.getActorList());
        movieRepository.save(movie);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null)
            throw new ResponseStatusException(BAD_REQUEST, "Введите id фильма");
        checkOptionalMovie(id);
        movieRepository.deleteById(id);
    }

    public Movie checkOptionalMovie(Long id) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent())
            return optionalMovie.get();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Фильма с таким id в базе нет");
    }
}
