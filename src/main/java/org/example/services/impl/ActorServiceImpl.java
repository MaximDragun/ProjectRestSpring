package org.example.services.impl;

import org.example.dto.ActorDTO;
import org.example.mapping.MapperDTO;
import org.example.models.Actor;
import org.example.models.Director;
import org.example.models.Movie;
import org.example.repositories.ActorRepository;
import org.example.repositories.DirectorRepository;
import org.example.repositories.MovieRepository;
import org.example.services.interfaces.ActorService;
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
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final MovieRepository movieRepository;
    private final MapperDTO mapperDTO;

    @Autowired
    public ActorServiceImpl(ActorRepository actorRepository, DirectorRepository directorRepository, MovieRepository movieRepository, MapperDTO mapperDTO) {
        this.actorRepository = actorRepository;
        this.directorRepository = directorRepository;
        this.movieRepository = movieRepository;
        this.mapperDTO = mapperDTO;
    }

    public ActorDTO findById(Long id) {
        if (id == null)
            throw new ResponseStatusException(BAD_REQUEST, "Введите id актера");
        return mapperDTO.toActorDTOWithoutMovies(checkOptionalActor(id));
    }

    public List<ActorDTO> findAll() {
        return actorRepository.findAll().stream()
                .map(mapperDTO::toActorDTOWithoutMovies)
                .toList();
    }

    @Transactional
    public void create(ActorDTO actorDTO) {
        if (actorDTO == null || actorDTO.getId() != null || actorDTO.getAge() == null ||
                actorDTO.getName() == null || actorDTO.getAge() > 99 || actorDTO.getAge() < 19) {
            throw new ResponseStatusException(BAD_REQUEST, "У добавляемого актера не может быть id, " +
                    "но обязаны быть name и age, при чем age больше 18 и age меньше 100");
        }
        if (actorDTO.getMovieList() == null || actorDTO.getMovieList().isEmpty()) {
            Actor actor = actorRepository.save(mapperDTO.toActor(actorDTO));
            actor.setMovieList(new ArrayList<>());
        } else {
            boolean checkActorsNull = actorDTO.getMovieList().stream()
                    .allMatch(movie -> movie.getActorList() == null && movie.getId() == null && movie.getDirectorId() != null
                            && movie.getYear() != null && movie.getYear() > 1900 && movie.getName() != null);
            if (checkActorsNull) {
                Actor actor = mapperDTO.toActor(actorDTO);
                List<Movie> movies = actor.getMovieList();
                actor.setMovieList(new ArrayList<>());
                Actor saveActor = actorRepository.save(actor);
                forMoviesAddMovieRepository(saveActor, movies);
            } else
                throw new ResponseStatusException(BAD_REQUEST, "У актера должен быть или пустой список фильмов " +
                        "или в его фильмах должны быть пустые списки актеров, так же у добавляемых фильмов не " +
                        "может быть id и должен быть directorId, year, name" +
                        " ,год фильма должен быть больше 1900, такие вот правила");
        }
    }

    @Transactional
    public void update(ActorDTO actorDTO) {
        if (actorDTO == null || actorDTO.getId() == null || actorDTO.getAge() == null ||
                actorDTO.getName() == null || actorDTO.getAge() > 99 || actorDTO.getAge() < 19) {
            throw new ResponseStatusException(BAD_REQUEST, "У изменяемого актера должны быть такие поля как id, " +
                    "name и age, при чем age больше 18 и age меньше 100");
        }
        if (actorDTO.getMovieList() == null || actorDTO.getMovieList().isEmpty()) {//Не меняем список фильмов
            Actor checkActor = checkOptionalActor(actorDTO.getId());
            checkActor.setAge(actorDTO.getAge());
            checkActor.setName(actorDTO.getName());
        } else {
            boolean checkActorsNull = actorDTO.getMovieList().stream()
                    .allMatch(movie -> movie.getActorList() == null && movie.getId() == null && movie.getDirectorId() != null
                            && movie.getYear() != null && movie.getYear() > 1900 && movie.getName() != null);

            if (checkActorsNull) {
                Actor checkActor = checkOptionalActor(actorDTO.getId());// из базы
                checkActor.setName(actorDTO.getName());
                checkActor.setAge(actorDTO.getAge());

                Actor actor = mapperDTO.toActor(actorDTO);// мой из dto
                List<Movie> movies = actor.getMovieList();
                forMoviesAddMovieRepository(checkActor, movies);
            } else
                throw new ResponseStatusException(BAD_REQUEST, "У актера должен быть или пустой список фильмов " +
                        "или в его фильмах должны быть пустые списки актеров, так же у добавляемых фильмов не " +
                        "может быть id, но должен быть directorId, year, name" +
                        " и год фильма должен быть больше 1900, такие вот правила");
        }
    }

    private void forMoviesAddMovieRepository(Actor checkActor, List<Movie> movies) {
        for (Movie movie : movies) {
            Long directorId = movie.getDirector().getDirectorId();
            Optional<Director> director = directorRepository.findById(directorId);
            if (director.isPresent()) {
                Director directorPersistent = director.get();
                movie.setDirector(directorPersistent);
                movie.setActorList(new ArrayList<>(List.of(checkActor)));
                Movie saveMovie = movieRepository.save(movie);
                directorPersistent.getMovieList().add(saveMovie);
                checkActor.getMovieList().add(saveMovie);
            } else {
                throw new ResponseStatusException(BAD_REQUEST, "У фильма directorId = " + directorId + ", режиссера с таким id нет в базе");
            }
        }
    }

    @Transactional
    public void delete(Long id) {
        if (id == null)
            throw new ResponseStatusException(BAD_REQUEST, "Введите id актера");
        checkOptionalActor(id);
        actorRepository.deleteById(id);
    }

    private Actor checkOptionalActor(Long id) {
        Optional<Actor> optionalActor = actorRepository.findById(id);
        if (optionalActor.isPresent())
            return optionalActor.get();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Актера с таким id в базе нет");
    }
}
