package mapper;

import org.example.dto.ActorDTO;
import org.example.dto.DirectorDTO;
import org.example.dto.MovieDTO;
import org.example.mapping.MapperDTO;
import org.example.models.Actor;
import org.example.models.Director;
import org.example.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapperDTOTest {
    private MapperDTO mapper;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(MapperDTO.class);
    }

    @Test
    void testToDirector() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setId(1L);

        Director director = mapper.toDirector(directorDTO);

        assertNotNull(director);
        assertEquals(directorDTO.getId(), director.getDirectorId());
    }

    @Test
    void testToMovie() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(1L);
        movieDTO.setYear(2022);

        Movie movie = mapper.toMovie(movieDTO);

        assertNotNull(movie);
        assertEquals(movieDTO.getId(), movie.getMovieId());
        assertEquals(movieDTO.getYear(), movie.getYearOfProduction());
    }

    @Test
    void testToActor() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setId(1L);

        Actor actor = mapper.toActor(actorDTO);

        assertNotNull(actor);
        assertEquals(actorDTO.getId(), actor.getActorId());
    }

    @Test
    void testToDirectorDTOWithMoviesWithoutActors() {
        Director director = new Director();
        director.setDirectorId(1L);

        DirectorDTO directorDTO = mapper.toDirectorDTOWithMoviesWithoutActors(director);

        assertNotNull(directorDTO);
        assertEquals(director.getDirectorId(), directorDTO.getId());
    }

    @Test
    void testToMovieDTOWithoutActors() {
        Director director = new Director();

        Movie movie = new Movie();
        movie.setMovieId(1L);
        movie.setYearOfProduction(2022);
        movie.setDirector(director);
        MovieDTO movieDTO = mapper.toMovieDTOWithoutActors(movie);

        assertNotNull(movieDTO);
        assertEquals(movie.getMovieId(), movieDTO.getId());
        assertEquals(movie.getYearOfProduction(), movieDTO.getYear());
    }

    @Test
    void testToActorDTOWithoutMovies() {
        Actor actor = new Actor();
        actor.setActorId(1L);

        ActorDTO actorDTO = mapper.toActorDTOWithoutMovies(actor);

        assertNotNull(actorDTO);
        assertEquals(actor.getActorId(), actorDTO.getId());
        assertNull(actorDTO.getMovieList());
    }

    @Test
    void testToMovieDTO() {
        Director director = new Director();
        Movie movie = new Movie();
        movie.setMovieId(1L);
        movie.setYearOfProduction(2022);
        movie.setDirector(director);
        MovieDTO movieDTO = mapper.toMovieDTO(movie);

        assertNotNull(movieDTO);
        assertEquals(movie.getMovieId(), movieDTO.getId());
        assertEquals(movie.getYearOfProduction(), movieDTO.getYear());
    }

    @Test
    void testToActorDTO() {
        Actor actor = new Actor();
        actor.setActorId(1L);

        ActorDTO actorDTO = mapper.toActorDTO(actor);

        assertNotNull(actorDTO);
        assertEquals(actor.getActorId(), actorDTO.getId());
        assertNull(actorDTO.getMovieList());
    }

    @Test
    void testToMovieDTOListWithoutActor() {
        Director director = new Director();
        List<Movie> movies = Arrays.asList(
                createMovie(1L, "Movie 1", 2022, director),
                createMovie(2L, "Movie 2", 2023, director)
        );

        List<MovieDTO> movieDTOList = mapper.toMovieDTOListWithoutActor(movies);

        assertNotNull(movieDTOList);
        assertEquals(movies.size(), movieDTOList.size());

        for (MovieDTO movieDTO : movieDTOList) {
            assertNull(movieDTO.getActorList());
        }
    }

    private Movie createMovie(Long id, String title, int year, Director director) {
        Movie movie = new Movie();
        movie.setMovieId(id);
        movie.setName(title);
        movie.setYearOfProduction(year);
        movie.setDirector(director);
        return movie;
    }

    @Test
    void testToActorDTOListWithoutMovie() {
        List<Actor> actors = Arrays.asList(
                createActor(1L, "Actor 1"),
                createActor(2L, "Actor 2")
        );

        List<ActorDTO> actorDTOList = mapper.toActorDTOListWithoutMovie(actors);

        assertNotNull(actorDTOList);
        assertEquals(actors.size(), actorDTOList.size());

        for (ActorDTO actorDTO : actorDTOList) {
            assertNull(actorDTO.getMovieList());
        }
    }

    private Actor createActor(Long id, String name) {
        Actor actor = new Actor();
        actor.setActorId(id);
        actor.setName(name);
        return actor;
    }
}
