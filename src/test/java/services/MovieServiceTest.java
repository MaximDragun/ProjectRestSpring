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
import org.example.services.impl.MovieServiceImpl;
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
class MovieServiceTest {
    @InjectMocks
    private MovieServiceImpl movieService;
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
        MovieDTO movieDTO = new MovieDTO();
        Movie movie = new Movie();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        when(mapperDTO.toMovieDTO(movie)).thenReturn(movieDTO);

        MovieDTO result = movieService.findById(1L);

        assertEquals(movieDTO, result);

        verify(movieRepository, times(1)).findById(1L);
        verify(mapperDTO, times(1)).toMovieDTO(movie);

        when(movieRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> movieService.findById(2L));
        assertThrows(ResponseStatusException.class, () -> movieService.findById(null));
    }

    @Test
    void testFindAll() {
        MovieDTO movieDTO = new MovieDTO();
        MovieDTO movieDTO1 = new MovieDTO();
        Movie movie = new Movie();
        Movie movie1 = new Movie();

        List<Movie> movies = new ArrayList<>(List.of(movie, movie1));

        when(movieRepository.findAll()).thenReturn(movies);

        when(mapperDTO.toMovieDTO(movies.get(0))).thenReturn(movieDTO);
        when(mapperDTO.toMovieDTO(movies.get(1))).thenReturn(movieDTO1);

        List<MovieDTO> result = movieService.findAll();

        assertEquals(2, result.size());
        assertEquals(movieDTO, result.get(0));
        assertEquals(movieDTO1, result.get(1));

        verify(movieRepository, times(1)).findAll();
        verify(mapperDTO, times(2)).toMovieDTO(any(Movie.class));
    }

    @Test
    void testDelete() {
        Movie movie = new Movie();
        Long id = 1L;

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        movieService.delete(id);

        verify(movieRepository, times(1)).findById(id);
        verify(movieRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteWithInvalidId() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

        Long invalidId = 2L;
        assertThrows(ResponseStatusException.class, () -> movieService.delete(invalidId));

        verify(movieRepository, times(1)).findById(invalidId);
        verify(movieRepository, never()).deleteById(invalidId);
        assertThrows(ResponseStatusException.class, () -> movieService.delete(null));
    }

    @Test
    void testAddLinkMovieActorValid() {
        Long movieId = 1L;
        Long actorId = 1L;
        Movie movie = new Movie();
        Actor actor = new Actor();
        movie.setActorList(new ArrayList<>());
        actor.setMovieList(new ArrayList<>());
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(actorRepository.findById(actorId)).thenReturn(Optional.of(actor));

        movieService.addLinkMovieActor(movieId, actorId);

        assertEquals(movie, actor.getMovieList().get(0));
        assertEquals(actor, movie.getActorList().get(0));

        verify(movieRepository, times(1)).findById(movieId);
        verify(actorRepository, times(1)).findById(actorId);
    }

    @Test
    void testAddLinkMovieActorInvalid() {
        Long firstMovieId = null;
        Long movieId = 1L;
        Long actorId = 1L;
        assertThrows(ResponseStatusException.class, () -> movieService.addLinkMovieActor(firstMovieId, actorId));
        Movie movie = new Movie();
        Actor actor = new Actor();
        movie.setActorList(new ArrayList<>(Collections.singletonList(actor)));

        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> movieService.addLinkMovieActor(movieId, actorId));

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(actorRepository.findById(actorId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> movieService.addLinkMovieActor(movieId, actorId));

        when(actorRepository.findById(actorId)).thenReturn(Optional.of(actor));

        assertThrows(ResponseStatusException.class, () -> movieService.addLinkMovieActor(movieId, actorId));
    }

    @Test
    void testCreateValidMovie() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setName("John Doe");
        movieDTO.setYear(1998);
        movieDTO.setDirectorId(1L);

        Director director = new Director();
        director.setMovieList(new ArrayList<>());
        Movie movie = new Movie();
        Movie movie1 = new Movie();

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(mapperDTO.toMovie(movieDTO)).thenReturn(movie);
        when(movieRepository.save(movie)).thenReturn(movie1);

        movieService.create(movieDTO);

        verify(directorRepository, times(1)).findById(anyLong());
        verify(mapperDTO, times(1)).toMovie(movieDTO);
        verify(movieRepository, times(1)).save(movie);

        when(directorRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO));
    }

    @Test
    void testCreateMovieInvalid() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setYear(1999);
        movieDTO.setDirectorId(1L);

        MovieDTO movieDTO1 = null;

        MovieDTO movieDTO2 = new MovieDTO();
        movieDTO2.setName("John Doe");
        movieDTO2.setYear(120);
        movieDTO2.setDirectorId(1L);

        MovieDTO movieDTO3 = new MovieDTO();
        movieDTO3.setId(1L);
        movieDTO3.setName("John Doe");
        movieDTO3.setYear(1999);
        movieDTO3.setDirectorId(1L);

        MovieDTO movieDTO4 = new MovieDTO();
        movieDTO4.setName("John Doe");
        movieDTO4.setDirectorId(1L);

        MovieDTO movieDTO5 = new MovieDTO();
        movieDTO5.setName("John Doe");
        movieDTO5.setYear(1999);

        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO2));
        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO));
        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO1));
        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO3));
        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO4));
        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO5));

        verify(mapperDTO, never()).toMovie(movieDTO);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testCreateMovieWithInvalidActors() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setName("John Doe");
        movieDTO.setYear(1999);
        movieDTO.setDirectorId(1L);

        Director director = new Director();
        director.setDirectorId(1L);
        director.setMovieList(new ArrayList<>());

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        ActorDTO actorDTO = new ActorDTO();
        List<ActorDTO> movies = new ArrayList<>();
        movies.add(actorDTO);
        movieDTO.setActorList(movies);

        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO));

        ActorDTO actorDTO1 = new ActorDTO();
        actorDTO1.setName("Max");
        List<ActorDTO> movies1 = new ArrayList<>();
        movies1.add(actorDTO1);
        movieDTO.setActorList(movies1);

        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO));

        ActorDTO actorDTO2 = new ActorDTO();
        actorDTO2.setId(6L);
        actorDTO2.setAge(55);
        actorDTO2.setName("Max");
        List<ActorDTO> movies2 = new ArrayList<>();
        movies2.add(actorDTO2);
        movieDTO.setActorList(movies2);

        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO));

        ActorDTO actorDTO3 = new ActorDTO();
        actorDTO3.setName("Max");
        actorDTO3.setAge(1901);
        List<ActorDTO> movies3 = new ArrayList<>();
        movies3.add(actorDTO3);
        movieDTO.setActorList(movies3);

        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO));

        ActorDTO actorDTO4 = new ActorDTO();
        actorDTO4.setAge(5);
        actorDTO4.setName("Max");
        List<ActorDTO> movies4 = new ArrayList<>();
        movies4.add(actorDTO4);
        movieDTO.setActorList(movies4);

        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO));

        ActorDTO movie9 = new ActorDTO();
        movie9.setAge(55);
        movie9.setName("Max");

        MovieDTO movieDTO1 = new MovieDTO();
        List<MovieDTO> actors = new ArrayList<>();
        actors.add(movieDTO1);
        movie9.setMovieList(actors);

        List<ActorDTO> movies5 = new ArrayList<>();
        movies5.add(movie9);
        movieDTO.setActorList(movies5);


        assertThrows(ResponseStatusException.class, () -> movieService.create(movieDTO));

        verify(mapperDTO, never()).toMovie(movieDTO);
        verify(actorRepository, never()).save(any(Actor.class));
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testCreateMovieWithValidActors() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setName("John Doe");
        movieDTO.setYear(1999);
        movieDTO.setDirectorId(1L);

        Director director = new Director();
        director.setDirectorId(1L);
        director.setMovieList(new ArrayList<>());

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setAge(25);
        actorDTO.setName("Max");
        List<ActorDTO> actorDTOList = new ArrayList<>();
        actorDTOList.add(actorDTO);
        movieDTO.setActorList(actorDTOList);

        Actor actor = new Actor();
        Actor actor1 = new Actor();

        Movie movie = new Movie();
        movie.setActorList(new ArrayList<>(Collections.singletonList(actor)));

        Movie movie1 = new Movie();
        movie1.setActorList(new ArrayList<>());

        when(mapperDTO.toMovie(movieDTO)).thenReturn(movie);
        when(movieRepository.save(movie)).thenReturn(movie1);
        when(actorRepository.save(actor)).thenReturn(actor1);

        movieService.create(movieDTO);

        verify(directorRepository, times(1)).findById(anyLong());
        verify(mapperDTO, times(1)).toMovie(movieDTO);
        verify(movieRepository, times(1)).save(movie);
        verify(actorRepository, times(1)).save(actor);
    }

    @Test
    void testUpdateValidDirector() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(1L);
        movieDTO.setName("John Doe");
        movieDTO.setYear(1998);
        movieDTO.setDirectorId(1L);

        Director director = new Director();
        director.setDirectorId(1L);
        director.setMovieList(new ArrayList<>());

        Director directorBase = new Director();
        Movie movie = new Movie();
        movie.setDirector(directorBase);
        directorBase.setDirectorId(2L);
        directorBase.setMovieList(new ArrayList<>(Collections.singletonList(movie)));

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        movieService.update(movieDTO);

        assertEquals(movie.getName(),movieDTO.getName());
        assertEquals(movie.getYearOfProduction(),movieDTO.getYear());

        verify(directorRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findById(1L);


        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));

        when(directorRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));
    }

    @Test
    void testCUpdateValidDirectorWithEqualsDirector() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(1L);
        movieDTO.setName("John Doe");
        movieDTO.setYear(1998);
        movieDTO.setDirectorId(1L);

        Director director = new Director();
        director.setDirectorId(1L);
        director.setMovieList(new ArrayList<>());

        Director directorBase = new Director();
        Movie movie = new Movie();
        movie.setDirector(directorBase);
        directorBase.setDirectorId(1L);
        directorBase.setMovieList(new ArrayList<>(Collections.singletonList(movie)));

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        movieService.update(movieDTO);

        assertEquals(movie.getName(),movieDTO.getName());
        assertEquals(movie.getYearOfProduction(),movieDTO.getYear());

        verify(directorRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findById(1L);


        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));

        when(directorRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));
    }

    @Test
    void testUpdateMovieInvalid() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setYear(1999);
        movieDTO.setDirectorId(1L);
        movieDTO.setId(1L);

        MovieDTO movieDTO1 = null;

        MovieDTO movieDTO2 = new MovieDTO();
        movieDTO2.setName("John Doe");
        movieDTO2.setYear(120);
        movieDTO2.setDirectorId(1L);
        movieDTO2.setId(1L);

        MovieDTO movieDTO3 = new MovieDTO();
        movieDTO3.setName("John Doe");
        movieDTO3.setYear(1999);
        movieDTO3.setDirectorId(1L);

        MovieDTO movieDTO4 = new MovieDTO();
        movieDTO4.setName("John Doe");
        movieDTO4.setDirectorId(1L);
        movieDTO4.setId(1L);

        MovieDTO movieDTO5 = new MovieDTO();
        movieDTO5.setName("John Doe");
        movieDTO5.setYear(1999);
        movieDTO5.setId(1L);

        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO2));
        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));
        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO1));
        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO3));
        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO4));
        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO5));

        verify(mapperDTO, never()).toMovie(movieDTO);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testUpdateMovieWithInvalidActors() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(1L);
        movieDTO.setName("John Doe");
        movieDTO.setYear(1998);
        movieDTO.setDirectorId(1L);

        Director director = new Director();
        director.setDirectorId(1L);
        director.setMovieList(new ArrayList<>());

        Director directorBase = new Director();
        Movie movie = new Movie();
        movie.setDirector(directorBase);
        directorBase.setDirectorId(1L);
        directorBase.setMovieList(new ArrayList<>(Collections.singletonList(movie)));

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        ActorDTO actorDTO = new ActorDTO();
        List<ActorDTO> movies = new ArrayList<>();
        movies.add(actorDTO);
        movieDTO.setActorList(movies);

        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));

        ActorDTO actorDTO1 = new ActorDTO();
        actorDTO1.setName("Max");
        List<ActorDTO> movies1 = new ArrayList<>();
        movies1.add(actorDTO1);
        movieDTO.setActorList(movies1);

        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));

        ActorDTO actorDTO2 = new ActorDTO();
        actorDTO2.setId(6L);
        actorDTO2.setAge(55);
        actorDTO2.setName("Max");
        List<ActorDTO> movies2 = new ArrayList<>();
        movies2.add(actorDTO2);
        movieDTO.setActorList(movies2);

        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));

        ActorDTO actorDTO3 = new ActorDTO();
        actorDTO3.setName("Max");
        actorDTO3.setAge(1901);
        List<ActorDTO> movies3 = new ArrayList<>();
        movies3.add(actorDTO3);
        movieDTO.setActorList(movies3);

        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));

        ActorDTO actorDTO4 = new ActorDTO();
        actorDTO4.setAge(5);
        actorDTO4.setName("Max");
        List<ActorDTO> movies4 = new ArrayList<>();
        movies4.add(actorDTO4);
        movieDTO.setActorList(movies4);

        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));

        ActorDTO movie9 = new ActorDTO();
        movie9.setAge(55);
        movie9.setName("Max");

        MovieDTO movieDTO1 = new MovieDTO();
        List<MovieDTO> actors = new ArrayList<>();
        actors.add(movieDTO1);
        movie9.setMovieList(actors);

        List<ActorDTO> movies5 = new ArrayList<>();
        movies5.add(movie9);
        movieDTO.setActorList(movies5);


        assertThrows(ResponseStatusException.class, () -> movieService.update(movieDTO));

        verify(mapperDTO, never()).toMovie(movieDTO);
        verify(directorRepository, never()).save(any(Director.class));
        verify(actorRepository, never()).save(any(Actor.class));
    }

    @Test
    void testUpdateMovieWithValidActors() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(1L);
        movieDTO.setName("John Doe");
        movieDTO.setYear(1998);
        movieDTO.setDirectorId(1L);

        Director director = new Director();
        director.setDirectorId(1L);
        director.setMovieList(new ArrayList<>());

        Director directorBase = new Director();
        Movie movie = new Movie();
        movie.setDirector(directorBase);
        movie.setActorList(new ArrayList<>());
        directorBase.setDirectorId(1L);
        directorBase.setMovieList(new ArrayList<>(Collections.singletonList(movie)));

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setAge(25);
        actorDTO.setName("Max");
        List<ActorDTO> actorDTOList = new ArrayList<>();
        actorDTOList.add(actorDTO);
        movieDTO.setActorList(actorDTOList);

        Actor actor = new Actor();
        Actor actor1 = new Actor();

        Movie movie4 = new Movie();
        movie4.setActorList(new ArrayList<>(Collections.singletonList(actor)));

        when(mapperDTO.toMovie(movieDTO)).thenReturn(movie4);
        when(actorRepository.save(actor)).thenReturn(actor1);

        movieService.update(movieDTO);

        verify(directorRepository, times(1)).findById(anyLong());
        verify(mapperDTO, times(1)).toMovie(movieDTO);
        verify(movieRepository, times(1)).findById(anyLong());
        verify(actorRepository, times(1)).save(actor);
    }
}
