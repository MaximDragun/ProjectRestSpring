package controllers;

import org.example.controllers.MovieController;
import org.example.dto.MovieDTO;
import org.example.services.interfaces.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class MovieControllerTest {
    @InjectMocks
    private MovieController movieController;
    @Mock
    private MovieService movieService;

    @Test
    void testGetMovies() {
        MovieDTO movieDTO1 = new MovieDTO();
        MovieDTO movieDTO2 = new MovieDTO();

        List<MovieDTO> movieList = new ArrayList<>(List.of(movieDTO1, movieDTO2));
        when(movieService.findAll()).thenReturn(movieList);

        ResponseEntity<List<MovieDTO>> response = movieController.getMovies();

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(movieList, response.getBody());

        verify(movieService, times(1)).findAll();
    }

    @Test
    void testGetMovie() {
        MovieDTO movie = new MovieDTO();
        Long movieId = 1L;
        when(movieService.findById(movieId)).thenReturn(movie);

        ResponseEntity<MovieDTO> response = movieController.getMovie(movieId);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(movie, response.getBody());

        verify(movieService, times(1)).findById(movieId);
    }

    @Test
    void testCreate() {
        MovieDTO movie = new MovieDTO();

        ResponseEntity<HttpStatus> response = movieController.create(movie);

        assertSame(HttpStatus.OK, response.getStatusCode());

        verify(movieService, times(1)).create(movie);
    }

    @Test
    void testUpdate() {
        MovieDTO movie = new MovieDTO();

        ResponseEntity<HttpStatus> response = movieController.update(movie);

        assertSame(HttpStatus.OK, response.getStatusCode());

        verify(movieService, times(1)).update(movie);
    }

    @Test
    void testDelete() {
        Long movieId = 1L;

        ResponseEntity<HttpStatus> response = movieController.delete(movieId);

        assertSame(HttpStatus.OK, response.getStatusCode());

        verify(movieService, times(1)).delete(movieId);
    }
    @Test
    void testAddLink() {
        Long movieId = 1L;
        Long actorId = 1L;

        ResponseEntity<HttpStatus> response = movieController.addLink(movieId,actorId);

        assertSame(HttpStatus.OK, response.getStatusCode());

        verify(movieService, times(1)).addLinkMovieActor(movieId,actorId);
    }
}
