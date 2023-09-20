package repository;

import org.example.models.Actor;
import org.example.models.Director;
import org.example.models.Movie;
import org.example.repositories.ActorRepository;
import org.example.repositories.DirectorRepository;
import org.example.repositories.MovieRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringJUnitConfig(classes = {ActorRepositoryTest.Config.class})
@Transactional
public class ActorRepositoryTest {

    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DirectorRepository directorRepository;

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
        ActorRepositoryTest.Config.stop();
    }

    @Test
    public void testSaveAndFindById() {
        Actor actor = new Actor();
        actor.setName("Steven Spielberg");
        actor.setAge(75);
        actorRepository.save(actor);
        Long actorId = actor.getActorId();

        Optional<Actor> optionalActor = actorRepository.findById(actorId);
        assertTrue(optionalActor.isPresent());
        assertEquals("Steven Spielberg", optionalActor.get().getName());
    }

    @Test
    public void testSaveAndFindAll() {
        Actor actor1 = new Actor();
        actor1.setName("Steven Spielberg");
        actor1.setAge(75);
        Actor actor2 = new Actor();
        actor2.setName("Джонни Депп");
        actor2.setAge(67);
        actorRepository.save(actor1);
        actorRepository.save(actor2);
        List<Actor> actors = actorRepository.findAll();
        assertEquals(2, actors.size());
    }

    @Test
    public void testSaveAndDeleteForId() {
        Actor actor1 = new Actor();
        actor1.setName("Steven Spielberg");
        actor1.setAge(75);
        actorRepository.save(actor1);
        Long actorId = actor1.getActorId();

        Optional<Actor> optionalActor = actorRepository.findById(actorId);
        assertTrue(optionalActor.isPresent());

        actorRepository.deleteById(actorId);
        Optional<Actor> optionalActor1 = actorRepository.findById(actorId);
        assertTrue(optionalActor1.isEmpty());
    }

    @Test
    public void testActorMovieList() {
        Director director1 = new Director();
        director1.setName("Steven Spielberg");
        director1.setAge(75);
        director1.setMovieList(new ArrayList<>());
        directorRepository.save(director1);

        Actor actor = new Actor();
        actor.setName("Джонни Депп");
        actor.setAge(67);
        actor.setMovieList(new ArrayList<>());
        actorRepository.save(actor);
        assertEquals(0, actor.getMovieList().size());

        Movie movie1 = new Movie();
        movie1.setName("KingKong");
        movie1.setYearOfProduction(2010);
        movie1.setDirector(director1);
        movie1.setActorList(new ArrayList<>(Collections.singletonList(actor)));
        movieRepository.save(movie1);
        Movie movie = new Movie();
        movie.setName("AllStars");
        movie.setYearOfProduction(1998);
        movie.setDirector(director1);
        movie.setActorList(new ArrayList<>(Collections.singletonList(actor)));
        movieRepository.save(movie);

        actor.getMovieList().add(movie1);
        actor.getMovieList().add(movie);
        assertEquals(2, actor.getMovieList().size());
        movieRepository.deleteById(movie1.getMovieId());
        movieRepository.deleteById(movie.getMovieId());

        Optional<Actor> optionalActor = actorRepository.findById(actor.getActorId());
        assertTrue(optionalActor.isPresent());
        optionalActor.get().getMovieList().remove(movie1);
        optionalActor.get().getMovieList().remove(movie);

        assertFalse(optionalActor.get().getMovieList().contains(movie));
        assertFalse(optionalActor.get().getMovieList().contains(movie1));

    }
}
