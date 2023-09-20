package services;

import org.example.dto.ActorDTO;
import org.example.dto.DirectorDTO;
import org.example.dto.MovieDTO;
import org.example.mapping.MapperDTO;
import org.example.models.Director;
import org.example.models.Movie;
import org.example.repositories.DirectorRepository;
import org.example.repositories.MovieRepository;
import org.example.services.impl.DirectorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectorServiceTest {
    @InjectMocks
    private DirectorServiceImpl directorService;
    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MapperDTO mapperDTO;
    @Test
    void testFindById() {
        DirectorDTO directorDTO = new DirectorDTO();
        Director director = new Director();

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        when(mapperDTO.toDirectorDTOWithMoviesWithoutActors(director)).thenReturn(directorDTO);

        DirectorDTO result = directorService.findById(1L);

        assertEquals(directorDTO, result);

        verify(directorRepository, times(1)).findById(1L);
        verify(mapperDTO, times(1)).toDirectorDTOWithMoviesWithoutActors(director);

        when(directorRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> directorService.findById(2L));
        assertThrows(ResponseStatusException.class, () -> directorService.findById(null));
    }

    @Test
    void testFindAll() {
        DirectorDTO directorDTO1 = new DirectorDTO();
        DirectorDTO directorDTO2 = new DirectorDTO();
        Director director1 = new Director();
        Director director2 = new Director();

        List<Director> directors = new ArrayList<>(List.of(director1, director2));

        when(directorRepository.findAll()).thenReturn(directors);

        when(mapperDTO.toDirectorDTOWithMoviesWithoutActors(directors.get(0))).thenReturn(directorDTO1);
        when(mapperDTO.toDirectorDTOWithMoviesWithoutActors(directors.get(1))).thenReturn(directorDTO2);

        List<DirectorDTO> result = directorService.findAll();

        assertEquals(2, result.size());
        assertEquals(directorDTO1, result.get(0));
        assertEquals(directorDTO2, result.get(1));

        verify(directorRepository, times(1)).findAll();
        verify(mapperDTO, times(2)).toDirectorDTOWithMoviesWithoutActors(any(Director.class));
    }

    @Test
    void testDelete() {
        Director director = new Director();
        Long id = 1L;

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        directorService.delete(id);

        verify(directorRepository, times(1)).findById(id);
        verify(directorRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteWithInvalidId() {
        when(directorRepository.findById(anyLong())).thenReturn(Optional.empty());

        Long invalidId = 2L;
        assertThrows(ResponseStatusException.class, () -> directorService.delete(invalidId));

        verify(directorRepository, times(1)).findById(invalidId);
        verify(directorRepository, never()).deleteById(invalidId);
        assertThrows(ResponseStatusException.class, () -> directorService.delete(null));
    }

    @Test
    void testCreateValidDirector() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setName("John Doe");
        directorDTO.setAge(30);

        Director director = new Director();

        when(mapperDTO.toDirector(directorDTO)).thenReturn(director);
        when(directorRepository.save(director)).thenReturn(director);

        directorService.create(directorDTO);

        verify(mapperDTO, times(1)).toDirector(directorDTO);
        verify(directorRepository, times(1)).save(director);
    }

    @Test
    void testCreateDirectorInvalid() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setAge(27);

        DirectorDTO directorDTO1 = null;

        DirectorDTO directorDTO2 = new DirectorDTO();
        directorDTO2.setName("John Doe");
        directorDTO2.setAge(120);

        DirectorDTO directorDTO3 = new DirectorDTO();
        directorDTO3.setId(1L);
        directorDTO3.setName("John Doe");
        directorDTO3.setAge(19);

        DirectorDTO directorDTO4 = new DirectorDTO();
        directorDTO4.setName("John Doe");

        DirectorDTO directorDTO5 = new DirectorDTO();
        directorDTO5.setName("John Doe");
        directorDTO5.setAge(12);

        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO2));
        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO));
        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO1));
        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO3));
        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO4));
        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO5));

        verify(mapperDTO, never()).toDirector(directorDTO);
        verify(directorRepository, never()).save(any(Director.class));
    }

    @Test
    void testCreateDirectorWithInvalidMovies() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setName("John Doe");
        directorDTO.setAge(30);

        MovieDTO movie1 = new MovieDTO();
        MovieDTO movie2 = new MovieDTO();
        List<MovieDTO> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        directorDTO.setMovieList(movies);

        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO));

        MovieDTO movie3 = new MovieDTO();
        MovieDTO movie4 = new MovieDTO();
        movie3.setDirectorId(6L);
        movie3.setYear(1901);
        movie3.setName("Max");

        movie4.setYear(1901);
        movie4.setName("Max");
        List<MovieDTO> movies1 = new ArrayList<>();
        movies1.add(movie3);
        movies1.add(movie4);
        directorDTO.setMovieList(movies1);

        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO));

        MovieDTO movie5 = new MovieDTO();
        MovieDTO movie6 = new MovieDTO();
        movie5.setId(6L);
        movie5.setYear(1901);
        movie5.setName("Max");

        movie6.setYear(1901);
        movie6.setName("Max");
        List<MovieDTO> movies2 = new ArrayList<>();
        movies2.add(movie5);
        movies2.add(movie6);
        directorDTO.setMovieList(movies2);

        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO));

        MovieDTO movie7 = new MovieDTO();
        movie7.setYear(1901);
        List<MovieDTO> movies3 = new ArrayList<>();
        movies3.add(movie7);
        directorDTO.setMovieList(movies3);

        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO));

        MovieDTO movie8 = new MovieDTO();
        movie8.setYear(19);
        movie8.setName("Max");
        List<MovieDTO> movies4 = new ArrayList<>();
        movies4.add(movie8);
        directorDTO.setMovieList(movies4);

        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO));

        MovieDTO movie9 = new MovieDTO();
        movie9.setYear(1902);
        movie9.setName("Max");
        ActorDTO actor = new ActorDTO();
        List<ActorDTO> actors = new ArrayList<>();
        actors.add(actor);
        movie9.setActorList(actors);
        List<MovieDTO> movies5 = new ArrayList<>();
        movies5.add(movie9);
        directorDTO.setMovieList(movies5);


        assertThrows(ResponseStatusException.class, () -> directorService.create(directorDTO));

        verify(mapperDTO, never()).toDirector(directorDTO);
        verify(directorRepository, never()).save(any(Director.class));
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testCreateDirectorWithValidMovie() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setName("John Doe");
        directorDTO.setAge(30);

        MovieDTO movie = new MovieDTO();
        movie.setName("Prost");
        movie.setYear(1999);
        List<MovieDTO> movieDTOList = new ArrayList<>(Collections.singletonList(movie));
        directorDTO.setMovieList(movieDTOList);

        Director director = new Director();
        director.setName("John Doe");
        director.setAge(30);

        Movie movie1 = new Movie();
        movie1.setName("Prost");
        movie1.setYearOfProduction(1999);
        List<Movie> movieList = new ArrayList<>(Collections.singletonList(movie1));
        director.setMovieList(movieList);

        Movie movie2 = new Movie();

        Director director1 = new Director();
        director1.setMovieList(new ArrayList<>());
        when(mapperDTO.toDirector(directorDTO)).thenReturn(director);
        when(directorRepository.save(director)).thenReturn(director1);
        when(movieRepository.save(movie1)).thenReturn(movie2);

        directorService.create(directorDTO);

        verify(mapperDTO, times(1)).toDirector(directorDTO);
        verify(directorRepository, times(1)).save(director);
        verify(movieRepository, times(1)).save(movie1);
    }

    @Test
    void testCUpdateValidDirector() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setId(1L);
        directorDTO.setName("John Doe");
        directorDTO.setAge(30);

        Director director = new Director();

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        directorService.update(directorDTO);

        verify(directorRepository, times(1)).findById(1L);

        when(directorRepository.findById(2L)).thenReturn(Optional.empty());
        directorDTO.setId(2L);
        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO));
    }

    @Test
    void testUpdateDirectorInvalid() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setAge(27);
        directorDTO.setId(1L);

        DirectorDTO directorDTO1 = null;

        DirectorDTO directorDTO2 = new DirectorDTO();
        directorDTO2.setName("John Doe");
        directorDTO2.setAge(120);
        directorDTO2.setId(1L);

        DirectorDTO directorDTO3 = new DirectorDTO();
        directorDTO3.setName("John Doe");
        directorDTO3.setAge(19);

        DirectorDTO directorDTO4 = new DirectorDTO();
        directorDTO4.setName("John Doe");
        directorDTO4.setId(1L);

        DirectorDTO directorDTO5 = new DirectorDTO();
        directorDTO5.setName("John Doe");
        directorDTO5.setAge(12);
        directorDTO5.setId(1L);

        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO2));
        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO));
        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO1));
        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO3));
        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO4));
        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO5));

        verify(mapperDTO, never()).toDirector(directorDTO);
        verify(directorRepository, never()).save(any(Director.class));
    }

    @Test
    void testUpdateDirectorWithInvalidMovies() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setName("John Doe");
        directorDTO.setAge(30);
        directorDTO.setId(1L);

        MovieDTO movie1 = new MovieDTO();
        MovieDTO movie2 = new MovieDTO();
        List<MovieDTO> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        directorDTO.setMovieList(movies);

        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO));

        MovieDTO movie3 = new MovieDTO();
        MovieDTO movie4 = new MovieDTO();
        movie3.setDirectorId(6L);
        movie3.setYear(1901);
        movie3.setName("Max");

        movie4.setYear(1901);
        movie4.setName("Max");
        List<MovieDTO> movies1 = new ArrayList<>();
        movies1.add(movie3);
        movies1.add(movie4);
        directorDTO.setMovieList(movies1);

        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO));

        MovieDTO movie5 = new MovieDTO();
        MovieDTO movie6 = new MovieDTO();
        movie5.setId(6L);
        movie5.setYear(1901);
        movie5.setName("Max");

        movie6.setYear(1901);
        movie6.setName("Max");
        List<MovieDTO> movies2 = new ArrayList<>();
        movies2.add(movie5);
        movies2.add(movie6);
        directorDTO.setMovieList(movies2);

        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO));

        MovieDTO movie7 = new MovieDTO();
        movie7.setYear(1901);
        List<MovieDTO> movies3 = new ArrayList<>();
        movies3.add(movie7);
        directorDTO.setMovieList(movies3);

        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO));

        MovieDTO movie8 = new MovieDTO();
        movie8.setYear(19);
        movie8.setName("Max");
        List<MovieDTO> movies4 = new ArrayList<>();
        movies4.add(movie8);
        directorDTO.setMovieList(movies4);

        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO));

        MovieDTO movie9 = new MovieDTO();
        movie9.setYear(1902);
        movie9.setName("Max");
        ActorDTO actor = new ActorDTO();
        List<ActorDTO> actors = new ArrayList<>();
        actors.add(actor);
        movie9.setActorList(actors);
        List<MovieDTO> movies5 = new ArrayList<>();
        movies5.add(movie9);
        directorDTO.setMovieList(movies5);


        assertThrows(ResponseStatusException.class, () -> directorService.update(directorDTO));

        verify(mapperDTO, never()).toDirector(directorDTO);
        verify(directorRepository, never()).save(any(Director.class));
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testUpdateDirectorWithValidMovie() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setName("John Doe");
        directorDTO.setAge(30);
        directorDTO.setId(1L);

        MovieDTO movie = new MovieDTO();
        movie.setName("Prost");
        movie.setYear(1999);
        List<MovieDTO> movieDTOList = new ArrayList<>(Collections.singletonList(movie));
        directorDTO.setMovieList(movieDTOList);

        Director director = new Director();
        director.setDirectorId(1L);


        Movie movie1 = new Movie();
        movie1.setName("Prost");
        movie1.setYearOfProduction(1999);
        List<Movie> movieList = new ArrayList<>(Collections.singletonList(movie1));
        director.setMovieList(movieList);

        Movie movie2 = new Movie();

        Director director1 = new Director();
        director1.setMovieList(new ArrayList<>(Collections.singletonList(movie1)));

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(mapperDTO.toDirector(directorDTO)).thenReturn(director1);
        when(movieRepository.save(movie1)).thenReturn(movie2);

        directorService.update(directorDTO);

        verify(mapperDTO, times(1)).toDirector(directorDTO);
        verify(directorRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).save(movie1);
    }

}






