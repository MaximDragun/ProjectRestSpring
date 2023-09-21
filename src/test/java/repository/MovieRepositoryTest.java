package repository;

import org.example.models.Actor;
import org.example.models.Director;
import org.example.models.Movie;
import org.example.repositories.ActorRepository;
import org.example.repositories.DirectorRepository;
import org.example.repositories.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(JpaTestConfig.class)
@ExtendWith(PostgreSQLExtension.class)
@Transactional
class MovieRepositoryTest {

    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ActorRepository actorRepository;

    @Test
    void testSaveAndFindById() {
        Director director1 = new Director();
        director1.setName("Steven Spielberg");
        director1.setAge(75);
        director1.setMovieList(new ArrayList<>());
        directorRepository.save(director1);

        Movie movie = new Movie();
        movie.setName("KingKong");
        movie.setYearOfProduction(1998);
        movie.setDirector(director1);
        movieRepository.save(movie);
        Long movieId = movie.getMovieId();

        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        assertTrue(optionalMovie.isPresent());
        assertEquals("KingKong", optionalMovie.get().getName());
        assertEquals(1998, optionalMovie.get().getYearOfProduction());
    }

    @Test
    void testSaveAndFindAll() {
        Director director1 = new Director();
        director1.setName("Steven Spielberg");
        director1.setAge(75);
        director1.setMovieList(new ArrayList<>());
        directorRepository.save(director1);


        Movie movie1 = new Movie();
        movie1.setName("KingKong");
        movie1.setYearOfProduction(2010);
        movie1.setDirector(director1);
        movieRepository.save(movie1);
        Movie movie = new Movie();
        movie.setName("AllStars");
        movie.setYearOfProduction(1998);
        movie.setDirector(director1);
        movieRepository.save(movie);
        List<Movie> movieList = movieRepository.findAll();
        assertEquals(2, movieList.size());
    }

    @Test
    void testSaveAndDeleteForId() {
        Director director1 = new Director();
        director1.setName("Steven Spielberg");
        director1.setAge(75);
        director1.setMovieList(new ArrayList<>());
        directorRepository.save(director1);

        Movie movie = new Movie();
        movie.setName("KingKong");
        movie.setYearOfProduction(1998);
        movie.setDirector(director1);
        movieRepository.save(movie);
        Long movieId = movie.getMovieId();

        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        assertTrue(optionalMovie.isPresent());

        movieRepository.deleteById(movieId);
        Optional<Movie> deleteMovie = movieRepository.findById(movieId);
        assertTrue(deleteMovie.isEmpty());
    }

    @Test
    public void testDirectorMovieListAndActorList() {
        Director director1 = new Director();
        director1.setName("Steven Spielberg");
        director1.setAge(75);
        director1.setMovieList(new ArrayList<>());

        directorRepository.save(director1);
        assertEquals(0, director1.getMovieList().size());

        Movie movie1 = new Movie();
        movie1.setDirector(director1);
        movie1.setYearOfProduction(1901);
        movie1.setName("KingKong");
        movie1.setActorList(new ArrayList<>());
        movieRepository.save(movie1);

        director1.getMovieList().add(movie1);


        Actor actor1 = new Actor();
        actor1.setName("Джонни Депп");
        actor1.setAge(25);
        actor1.setMovieList(new ArrayList<>(Collections.singletonList(movie1)));

        Actor actor2 = new Actor();
        actor2.setName("Сидни Суинтон");
        actor2.setAge(28);
        actor2.setMovieList(new ArrayList<>(Collections.singletonList(movie1)));
        actorRepository.save(actor1);
        actorRepository.save(actor2);
        movie1.getActorList().add(actor1);
        movie1.getActorList().add(actor2);


        Optional<Director> optionalDirector = directorRepository.findById(director1.getDirectorId());
        Optional<Movie> optionalMovie = movieRepository.findById(movie1.getMovieId());
        Optional<Actor> optionalActor1 = actorRepository.findById(actor1.getActorId());
        Optional<Actor> optionalActor2 = actorRepository.findById(actor2.getActorId());

        assertTrue(optionalDirector.isPresent());
        assertTrue(optionalMovie.isPresent());
        assertTrue(optionalActor1.isPresent());
        assertTrue(optionalActor2.isPresent());

        Director saveDirector = optionalDirector.get();
        Movie saveMovie = optionalMovie.get();
        Actor saveActor1 = optionalActor1.get();
        Actor saveActor2 = optionalActor2.get();

        assertEquals(1, saveDirector.getMovieList().size());
        assertEquals(2, saveMovie.getActorList().size());
        assertEquals(1, saveActor1.getMovieList().size());
        assertEquals(1, saveActor2.getMovieList().size());

        assertTrue(saveMovie.getActorList().contains(saveActor1));
        assertTrue(saveMovie.getActorList().contains(saveActor2));
        assertTrue(saveActor1.getMovieList().contains(saveMovie));
        assertTrue(saveActor2.getMovieList().contains(saveMovie));
        assertEquals(saveDirector, saveMovie.getDirector());

    }
}
