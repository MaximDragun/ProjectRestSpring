package services;

import org.example.dto.ActorDTO;
import org.example.dto.MovieDTO;
import org.example.mapping.MapperDTO;
import org.example.models.Actor;
import org.example.models.Director;
import org.example.models.Movie;
import org.example.repositories.ActorRepository;
import org.example.repositories.DirectorRepository;
import org.example.repositories.MovieRepository;
import org.example.services.impl.ActorServiceImpl;
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
class ActorServiceTest {
    @InjectMocks
    private ActorServiceImpl actorService;
    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private MapperDTO mapperDTO;

    @Test
    void testFindById() {
        ActorDTO actorDTO = new ActorDTO();
        Actor actor = new Actor();

        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor));

        when(mapperDTO.toActorDTOWithoutMovies(actor)).thenReturn(actorDTO);

        ActorDTO result = actorService.findById(1L);

        assertEquals(actorDTO, result);

        verify(actorRepository, times(1)).findById(1L);
        verify(mapperDTO, times(1)).toActorDTOWithoutMovies(actor);

        when(actorRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> actorService.findById(2L));
        assertThrows(ResponseStatusException.class, () -> actorService.findById(null));
    }

    @Test
    void testFindAll() {
        ActorDTO actorDTO1 = new ActorDTO();
        ActorDTO actorDTO2 = new ActorDTO();
        Actor actor1 = new Actor();
        Actor actor2 = new Actor();

        List<Actor> actors = new ArrayList<>(List.of(actor1, actor2));

        when(actorRepository.findAll()).thenReturn(actors);

        when(mapperDTO.toActorDTOWithoutMovies(actors.get(0))).thenReturn(actorDTO1);
        when(mapperDTO.toActorDTOWithoutMovies(actors.get(1))).thenReturn(actorDTO2);

        List<ActorDTO> result = actorService.findAll();

        assertEquals(2, result.size());
        assertEquals(actorDTO1, result.get(0));
        assertEquals(actorDTO2, result.get(1));

        verify(actorRepository, times(1)).findAll();
        verify(mapperDTO, times(2)).toActorDTOWithoutMovies(any(Actor.class));
    }

    @Test
    void testDelete() {
        Actor actor = new Actor();
        Long id = 1L;

        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor));

        actorService.delete(id);

        verify(actorRepository, times(1)).findById(id);
        verify(actorRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteWithInvalidId() {
        when(actorRepository.findById(anyLong())).thenReturn(Optional.empty());

        Long invalidId = 2L;
        assertThrows(ResponseStatusException.class, () -> actorService.delete(invalidId));

        verify(actorRepository, times(1)).findById(invalidId);
        verify(actorRepository, never()).deleteById(invalidId);
        assertThrows(ResponseStatusException.class, () -> actorService.delete(null));
    }

    @Test
    void testCreateValidActor() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setName("John Doe");
        actorDTO.setAge(30);

        Actor actor = new Actor();

        when(mapperDTO.toActor(actorDTO)).thenReturn(actor);
        when(actorRepository.save(actor)).thenReturn(actor);

        actorService.create(actorDTO);

        verify(mapperDTO, times(1)).toActor(actorDTO);
        verify(actorRepository, times(1)).save(actor);
    }

    @Test
    void testCreateActorInvalid() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setAge(26);

        ActorDTO actorDTO1 = null;

        ActorDTO actorDTO2 = new ActorDTO();
        actorDTO2.setName("John Doe");
        actorDTO2.setAge(120);

        ActorDTO actorDTO3 = new ActorDTO();
        actorDTO3.setId(1L);
        actorDTO3.setName("John Doe");
        actorDTO3.setAge(19);

        ActorDTO actorDTO4 = new ActorDTO();
        actorDTO4.setName("John Doe");

        ActorDTO actorDTO5 = new ActorDTO();
        actorDTO5.setName("John Doe");
        actorDTO5.setAge(12);

        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO2));
        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO));
        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO1));
        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO3));
        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO4));
        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO5));

        verify(mapperDTO, never()).toActor(actorDTO);
        verify(actorRepository, never()).save(any(Actor.class));
    }

    @Test
    void testCreateActorWithInvalidMovies() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setName("John Doe");
        actorDTO.setAge(30);

        MovieDTO movie1 = new MovieDTO();
        MovieDTO movie2 = new MovieDTO();
        movie1.setDirectorId(6L);
        movie2.setDirectorId(6L);
        List<MovieDTO> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        actorDTO.setMovieList(movies);

        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO));

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
        actorDTO.setMovieList(movies1);

        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO));

        MovieDTO movie5 = new MovieDTO();
        MovieDTO movie6 = new MovieDTO();
        movie5.setDirectorId(6L);
        movie5.setId(6L);
        movie5.setYear(1901);
        movie5.setName("Max");

        movie6.setYear(1901);
        movie6.setName("Max");
        movie6.setDirectorId(6L);
        List<MovieDTO> movies2 = new ArrayList<>();
        movies2.add(movie5);
        movies2.add(movie6);
        actorDTO.setMovieList(movies2);

        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO));

        MovieDTO movie7 = new MovieDTO();
        movie7.setYear(1901);
        movie7.setDirectorId(6L);
        List<MovieDTO> movies3 = new ArrayList<>();
        movies3.add(movie7);
        actorDTO.setMovieList(movies3);

        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO));

        MovieDTO movie8 = new MovieDTO();
        movie8.setYear(19);
        movie8.setName("Max");
        movie8.setDirectorId(6L);
        List<MovieDTO> movies4 = new ArrayList<>();
        movies4.add(movie8);
        actorDTO.setMovieList(movies4);

        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO));

        MovieDTO movie9 = new MovieDTO();
        movie9.setYear(1902);
        movie9.setName("Max");
        movie9.setDirectorId(6L);
        ActorDTO actor = new ActorDTO();
        List<ActorDTO> actors = new ArrayList<>();
        actors.add(actor);
        movie9.setActorList(actors);
        List<MovieDTO> movies5 = new ArrayList<>();
        movies5.add(movie9);
        actorDTO.setMovieList(movies5);


        assertThrows(ResponseStatusException.class, () -> actorService.create(actorDTO));

        verify(mapperDTO, never()).toActor(actorDTO);
        verify(actorRepository, never()).save(any(Actor.class));
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testCreateActorWithValidMovie() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setName("John Doe");
        actorDTO.setAge(30);

        MovieDTO movie = new MovieDTO();
        movie.setName("Prost");
        movie.setYear(1999);
        movie.setDirectorId(1L);
        List<MovieDTO> movieDTOList = new ArrayList<>(Collections.singletonList(movie));
        actorDTO.setMovieList(movieDTOList);

        Actor actor = new Actor();
        actor.setName("John Doe");
        actor.setAge(30);

        Director director = new Director();
        director.setMovieList(new ArrayList<>());
        director.setDirectorId(1L);

        Movie movie1 = new Movie();
        movie1.setName("Prost");
        movie1.setYearOfProduction(1999);
        movie1.setDirector(director);
        List<Movie> movieList = new ArrayList<>(Collections.singletonList(movie1));
        actor.setMovieList(movieList);

        Movie movie2 = new Movie();

        Actor actor1 = new Actor();
        actor1.setMovieList(new ArrayList<>());

        when(mapperDTO.toActor(actorDTO)).thenReturn(actor);
        when(actorRepository.save(actor)).thenReturn(actor1);
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(movieRepository.save(movie1)).thenReturn(movie2);

        actorService.create(actorDTO);

        verify(mapperDTO, times(1)).toActor(actorDTO);
        verify(actorRepository, times(1)).save(actor);
        verify(directorRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).save(movie1);
    }

    @Test
    void testCUpdateValidActor() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setId(1L);
        actorDTO.setName("John Doe");
        actorDTO.setAge(30);

        Actor actor = new Actor();

        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor));

        actorService.update(actorDTO);

        verify(actorRepository, times(1)).findById(1L);

        when(actorRepository.findById(2L)).thenReturn(Optional.empty());
        actorDTO.setId(2L);
        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO));
    }

    @Test
    void testUpdateActorInvalid() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setAge(27);
        actorDTO.setId(1L);

        ActorDTO actorDTO1 = null;

        ActorDTO actorDTO2 = new ActorDTO();
        actorDTO2.setName("John Doe");
        actorDTO2.setAge(120);
        actorDTO2.setId(1L);

        ActorDTO actorDTO3 = new ActorDTO();
        actorDTO3.setName("John Doe");
        actorDTO3.setAge(19);

        ActorDTO actorDTO4 = new ActorDTO();
        actorDTO4.setName("John Doe");
        actorDTO4.setId(1L);

        ActorDTO actorDTO5 = new ActorDTO();
        actorDTO5.setName("John Doe");
        actorDTO5.setAge(12);
        actorDTO5.setId(1L);

        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO2));
        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO));
        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO1));
        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO3));
        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO4));
        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO5));

        verify(mapperDTO, never()).toActor(actorDTO);
        verify(actorRepository, never()).save(any(Actor.class));
    }

    @Test
    void testUpdateActorWithInvalidMovies() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setName("John Doe");
        actorDTO.setAge(30);
        actorDTO.setId(1L);

        MovieDTO movie1 = new MovieDTO();
        MovieDTO movie2 = new MovieDTO();
        movie1.setDirectorId(1L);
        movie2.setDirectorId(1L);
        List<MovieDTO> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        actorDTO.setMovieList(movies);

        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO));

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
        actorDTO.setMovieList(movies1);

        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO));

        MovieDTO movie5 = new MovieDTO();
        MovieDTO movie6 = new MovieDTO();
        movie5.setId(6L);
        movie5.setYear(1901);
        movie5.setName("Max");
        movie5.setDirectorId(1L);

        movie6.setYear(1901);
        movie6.setName("Max");
        movie6.setDirectorId(1L);
        List<MovieDTO> movies2 = new ArrayList<>();
        movies2.add(movie5);
        movies2.add(movie6);
        actorDTO.setMovieList(movies2);

        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO));

        MovieDTO movie7 = new MovieDTO();
        movie7.setYear(1901);
        movie7.setDirectorId(1L);
        List<MovieDTO> movies3 = new ArrayList<>();
        movies3.add(movie7);
        actorDTO.setMovieList(movies3);

        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO));

        MovieDTO movie8 = new MovieDTO();
        movie8.setYear(19);
        movie8.setName("Max");
        movie8.setDirectorId(1L);
        List<MovieDTO> movies4 = new ArrayList<>();
        movies4.add(movie8);
        actorDTO.setMovieList(movies4);

        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO));

        MovieDTO movie9 = new MovieDTO();
        movie9.setYear(1902);
        movie9.setName("Max");
        movie9.setDirectorId(1L);
        ActorDTO actor = new ActorDTO();
        List<ActorDTO> actors = new ArrayList<>();
        actors.add(actor);
        movie9.setActorList(actors);
        List<MovieDTO> movies5 = new ArrayList<>();
        movies5.add(movie9);
        actorDTO.setMovieList(movies5);


        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO));

        verify(mapperDTO, never()).toActor(actorDTO);
        verify(actorRepository, never()).save(any(Actor.class));
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testUpdateActorWithValidMovie() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setName("John Doe");
        actorDTO.setAge(30);
        actorDTO.setId(1L);

        MovieDTO movie = new MovieDTO();
        movie.setName("Prost");
        movie.setYear(1999);
        movie.setDirectorId(1L);
        List<MovieDTO> movieDTOList = new ArrayList<>(Collections.singletonList(movie));
        actorDTO.setMovieList(movieDTOList);

        Actor actor = new Actor();

        Director director = new Director();
        director.setMovieList(new ArrayList<>());
        director.setDirectorId(1L);

        Movie movie1 = new Movie();
        movie1.setName("Prost");
        movie1.setYearOfProduction(1999);
        movie1.setDirector(director);
        List<Movie> movieList = new ArrayList<>(Collections.singletonList(movie1));
        actor.setMovieList(movieList);

        Movie movie2 = new Movie();

        Actor actor1 = new Actor();
        actor1.setMovieList(new ArrayList<>());

        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor1));
        when(mapperDTO.toActor(actorDTO)).thenReturn(actor);
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(movieRepository.save(movie1)).thenReturn(movie2);

        actorService.update(actorDTO);

        verify(actorRepository, times(1)).findById(1L);
        verify(mapperDTO, times(1)).toActor(actorDTO);
        verify(directorRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).save(movie1);


        when(directorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> actorService.update(actorDTO));
    }
}
