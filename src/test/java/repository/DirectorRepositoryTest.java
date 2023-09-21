package repository;

import org.example.models.Director;
import org.example.models.Movie;
import org.example.repositories.DirectorRepository;
import org.example.repositories.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(JpaTestConfig.class)
@ExtendWith(PostgreSQLExtension.class)
@Transactional
class DirectorRepositoryTest {

    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private MovieRepository movieRepository;

    @Test
    void testSaveAndFindById() {
        Director director = new Director();
        director.setName("Steven Spielberg");
        director.setAge(75);
        directorRepository.save(director);
        Long directorId = director.getDirectorId();

        Optional<Director> retrievedDirector = directorRepository.findById(directorId);
        assertTrue(retrievedDirector.isPresent());
        assertEquals("Steven Spielberg", retrievedDirector.get().getName());
    }

    @Test
    void testSaveAndFindAll() {
        Director director1 = new Director();
        director1.setName("Steven Spielberg");
        director1.setAge(75);
        Director director2 = new Director();
        director2.setName("Maximus");
        director2.setAge(71);
        directorRepository.save(director1);
        directorRepository.save(director2);
        List<Director> allDirectors = directorRepository.findAll();
        assertEquals(2, allDirectors.size());
    }

    @Test
    void testSaveAndDeleteForId() {
        Director director1 = new Director();
        director1.setName("Steven Spielberg");
        director1.setAge(75);
        directorRepository.save(director1);
        Long directorId = director1.getDirectorId();

        Optional<Director> retrievedDirector = directorRepository.findById(directorId);
        assertTrue(retrievedDirector.isPresent());

        directorRepository.deleteById(directorId);
        Optional<Director> deletedDirector = directorRepository.findById(directorId);
        assertTrue(deletedDirector.isEmpty());
    }

    @Test
    void testDirectorMovieList() {
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
        movieRepository.save(movie1);

        Movie movie2 = new Movie();
        movie2.setDirector(director1);
        movie2.setYearOfProduction(1999);
        movie2.setName("AllStars");
        movieRepository.save(movie2);

        director1.getMovieList().add(movie1);
        director1.getMovieList().add(movie2);

        Optional<Director> retrievedDirector = directorRepository.findById(director1.getDirectorId());
        assertTrue(retrievedDirector.isPresent());
        List<Movie> movieList = retrievedDirector.get().getMovieList();
        assertTrue(movieList.contains(movie1));
        assertTrue(movieList.contains(movie2));
        assertEquals(2, director1.getMovieList().size());

    }
}
