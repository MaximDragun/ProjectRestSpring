package org.example.services.impl;

import org.example.dto.MovieDTO;
import org.example.mapping.MapperDTO;
import org.example.models.Actor;
import org.example.models.Director;
import org.example.models.Movie;
import org.example.repositories.ActorRepository;
import org.example.repositories.DirectorRepository;
import org.example.repositories.MovieRepository;
import org.example.services.interfaces.MovieService;
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
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final MapperDTO mapperDTO;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, ActorRepository actorRepository,
                            DirectorRepository directorRepository, MapperDTO mapperDTO) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.directorRepository = directorRepository;
        this.mapperDTO = mapperDTO;
    }

    public MovieDTO findById(Long id) {
        if (id == null)
            throw new ResponseStatusException(BAD_REQUEST, "Введите id фильма");
        return mapperDTO.toMovieDTO(checkOptionalMovie(id));
    }

    public List<MovieDTO> findAll() {
        return movieRepository.findAll().stream()
                .map(mapperDTO::toMovieDTO)
                .toList();
    }

    @Transactional
    public void create(MovieDTO movieDTO) {
        if (movieDTO == null || movieDTO.getId() != null || movieDTO.getYear() == null ||
                movieDTO.getDirectorId() == null || movieDTO.getName() == null || movieDTO.getYear() < 1901) {
            throw new ResponseStatusException(BAD_REQUEST, "У добавляемого фильма не может быть id, " +
                    "но обязаны быть name, year и directorId, при чем year больше 1900");
        }
        Long directorId = movieDTO.getDirectorId();
        Optional<Director> optionalDirector = directorRepository.findById(directorId);
        if (optionalDirector.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "У фильма directorId = " + directorId + ", режиссера с таким id нет в базе");
        }
        Director directorPersist = optionalDirector.get();
        if (movieDTO.getActorList() == null || movieDTO.getActorList().isEmpty()) {
            Movie mapperDTOMovie = mapperDTO.toMovie(movieDTO);
            mapperDTOMovie.setActorList(new ArrayList<>());
            mapperDTOMovie.setDirector(directorPersist);
            Movie movie = movieRepository.save(mapperDTOMovie);
            directorPersist.getMovieList().add(movie);
        } else {
            boolean checkActorsNull = movieDTO.getActorList().stream()
                    .allMatch(actor -> actor.getMovieList() == null && actor.getId() == null && actor.getName() != null
                            && actor.getAge() != null && actor.getAge() > 18 && actor.getAge() < 100);
            if (checkActorsNull) {
                Movie movie = mapperDTO.toMovie(movieDTO);
                List<Actor> actors = movie.getActorList();
                movie.setActorList(new ArrayList<>());
                movie.setDirector(directorPersist);
                Movie saveMovie = movieRepository.save(movie);
                directorPersist.getMovieList().add(movie);
                for (Actor actor : actors) {
                    actor.setMovieList(new ArrayList<>(List.of(saveMovie)));
                    Actor saveActor = actorRepository.save(actor);
                    saveMovie.getActorList().add(saveActor);
                }
            } else
                throw new ResponseStatusException(BAD_REQUEST, "У фильма должен быть или пустой список актеров " +
                        "или в его актерах должны быть пустые списки фильмов, так же у добавляемых актеров не " +
                        "может быть id, но должны быть age, name" +
                        ", age должен быть больше 18 и меньше 100, только таких берем!");
        }
    }

    @Transactional
    public void update(MovieDTO movieDTO) {
        if (movieDTO == null || movieDTO.getId() == null || movieDTO.getYear() == null ||
                movieDTO.getDirectorId() == null || movieDTO.getName() == null || movieDTO.getYear() < 1901) {
            throw new ResponseStatusException(BAD_REQUEST, "У добавляемого фильма " +
                    "обязаны быть id, name, year и directorId, при чем year больше 1900");
        }
        Long directorId = movieDTO.getDirectorId();
        Optional<Director> optionalDirector = directorRepository.findById(directorId);
        if (optionalDirector.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "У фильма directorId = " + directorId + ", режиссера с таким id нет в базе");
        }

        Director directorPersist = optionalDirector.get();
        Movie checkMovie = checkOptionalMovie(movieDTO.getId());
        Director director = checkMovie.getDirector();

        if (!director.getDirectorId().equals(directorPersist.getDirectorId())) {
            director.getMovieList().remove(checkMovie);
            checkMovie.setDirector(directorPersist);
            directorPersist.getMovieList().add(checkMovie);
        }
        checkMovie.setYearOfProduction(movieDTO.getYear());
        checkMovie.setName(movieDTO.getName());
        if (movieDTO.getActorList() != null && !movieDTO.getActorList().isEmpty()) {
            boolean checkActorsNull = movieDTO.getActorList().stream()
                    .allMatch(actor -> actor.getMovieList() == null && actor.getId() == null && actor.getName() != null
                            && actor.getAge() != null && actor.getAge() > 18 && actor.getAge() < 100);
            if (checkActorsNull) {
                Movie movie = mapperDTO.toMovie(movieDTO);
                List<Actor> actors = movie.getActorList();
                for (Actor actor : actors) {
                    actor.setMovieList(new ArrayList<>(List.of(checkMovie)));
                    Actor saveActor = actorRepository.save(actor);
                    checkMovie.getActorList().add(saveActor);
                }
            } else
                throw new ResponseStatusException(BAD_REQUEST, "У фильма должен быть или пустой список актеров " +
                        "или в его актерах должны быть пустые списки фильмов, так же у добавляемых актеров не " +
                        "может быть id, но должны быть age, name" +
                        ", age должен быть больше 18 и меньше 100, только таких берем!");
        }
    }

    @Transactional
    public void addLinkMovieActor(Long movieId, Long actorId) {
        if (movieId == null || actorId == null)
            throw new ResponseStatusException(BAD_REQUEST, "Введите movieId и actorId через параметры запроса");
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        Optional<Actor> optionalActor = actorRepository.findById(actorId);
        if (optionalMovie.isEmpty())
            throw new ResponseStatusException(BAD_REQUEST, "Фильма с таким id в базе нет");
        if (optionalActor.isEmpty())
            throw new ResponseStatusException(BAD_REQUEST, "Актера с таким id в базе нет");
        Movie movie = optionalMovie.get();
        Actor actor = optionalActor.get();
        if (movie.getActorList().contains(actor))
            throw new ResponseStatusException(BAD_REQUEST, "Связь между ними уже есть");
        movie.getActorList().add(actor);
        actor.getMovieList().add(movie);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null)
            throw new ResponseStatusException(BAD_REQUEST, "Введите id фильма");
        checkOptionalMovie(id);
        movieRepository.deleteById(id);
    }

    private Movie checkOptionalMovie(Long id) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent())
            return optionalMovie.get();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Фильма с таким id в базе нет");
    }
}
