package repository;

import org.example.models.Director;
import org.example.models.Movie;
import org.example.repositories.DirectorRepository;
import org.example.repositories.MovieRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringJUnitConfig(classes = {DirectorRepositoryTest.Config.class})
@Transactional
public class DirectorRepositoryTest {

    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Configuration
    @ComponentScan("org.example.repositories")
    @EnableTransactionManagement
    @EnableJpaRepositories("org.example.repositories")
    static public class Config {
        private static PostgreSQLContainer<?> container;

        static {
            container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                    .withDatabaseName("project2")
                    .withUsername("postgres")
                    .withPassword("maxim")
                    .withInitScript("db/NewTables.sql");
            container.start();
        }

        static void stop() {
            container.stop();
            container.close();
        }

        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();

            dataSource.setDriverClassName(container.getDriverClassName());
            dataSource.setUrl(container.getJdbcUrl());
            dataSource.setUsername(container.getUsername());
            dataSource.setPassword(container.getPassword());

            return dataSource;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource());
            em.setPackagesToScan("org.example.models");

            final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            em.setJpaVendorAdapter(vendorAdapter);

            return em;
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

            return transactionManager;
        }
    }

    @AfterAll
    public static void testAfter() {
        DirectorRepositoryTest.Config.stop();
    }
    @Test
    public void testSaveAndFindById() {
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
    public void testSaveAndFindAll() {
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
    public void testSaveAndDeleteForId() {
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
    public void testDirectorMovieList() {
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
