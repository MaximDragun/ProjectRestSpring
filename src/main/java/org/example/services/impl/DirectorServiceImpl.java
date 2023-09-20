package org.example.services.impl;

import org.example.dto.DirectorDTO;
import org.example.mapping.MapperDTO;
import org.example.models.Director;
import org.example.models.Movie;
import org.example.repositories.DirectorRepository;
import org.example.repositories.MovieRepository;
import org.example.services.interfaces.DirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Transactional(readOnly = true)
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;
    private final MovieRepository movieRepository;
    private final MapperDTO mapperDTO;

    @Autowired
    public DirectorServiceImpl(DirectorRepository directorRepository, MovieRepository movieRepository, MapperDTO mapperDTO) {
        this.directorRepository = directorRepository;
        this.movieRepository = movieRepository;
        this.mapperDTO = mapperDTO;
    }

    public DirectorDTO findById(Long id) {
        if (id == null)
            throw new ResponseStatusException(BAD_REQUEST, "Введите id режиссера");
        return mapperDTO.toDirectorDTOWithMoviesWithoutActors(checkOptionalDirector(id));
    }

    public List<DirectorDTO> findAll() {
        return directorRepository.findAll().stream()
                .map(mapperDTO::toDirectorDTOWithMoviesWithoutActors)
                .toList();
    }

    @Transactional
    public void create(DirectorDTO directorDTO) {
        if (directorDTO == null || directorDTO.getId() != null || directorDTO.getAge() == null ||
                directorDTO.getName() == null || directorDTO.getAge() > 99 || directorDTO.getAge() < 19) {
            throw new ResponseStatusException(BAD_REQUEST, "У добавляемого режиссера не может быть id, " +
                    "но обязаны быть name и age, при чем age больше 18 и age меньше 100");
        }
        if (directorDTO.getMovieList() == null || directorDTO.getMovieList().isEmpty()) {
            Director director = directorRepository.save(mapperDTO.toDirector(directorDTO));
            director.setMovieList(new ArrayList<>());
        } else {
            boolean checkActorsNull = directorDTO.getMovieList().stream()
                    .allMatch(movie -> movie.getActorList() == null && movie.getId() == null && movie.getDirectorId() == null
                            && movie.getYear() != null && movie.getYear() > 1900 && movie.getName() != null);
            if (checkActorsNull) {
                Director director = mapperDTO.toDirector(directorDTO);
                List<Movie> movies = director.getMovieList();
                director.setMovieList(new ArrayList<>());
                Director saveDirector = directorRepository.save(director);

                for (Movie movie : movies) {
                    movie.setDirector(saveDirector);
                    movie.setActorList(new ArrayList<>());
                    Movie saveMovie = movieRepository.save(movie);
                    saveDirector.getMovieList().add(saveMovie);
                }
            } else
                throw new ResponseStatusException(BAD_REQUEST, "У режиссера должен быть или пустой список фильмов " +
                        "или в его фильмах должны быть пустые списки актеров, так же у добавляемых фильмов не " +
                        "может быть id и directorId, но должны быть year, name" +
                        ", год фильма должен быть больше 1900, такие вот правила");
        }
    }

    @Transactional
    public void update(DirectorDTO directorDTO) {
        if (directorDTO == null || directorDTO.getId() == null || directorDTO.getAge() == null ||
                directorDTO.getName() == null || directorDTO.getAge() > 99 || directorDTO.getAge() < 19) {
            throw new ResponseStatusException(BAD_REQUEST, "У изменяемого режиссера должны быть такие поля как id, " +
                    "name и age, при чем age больше 18 и age меньше 100");
        }
        if (directorDTO.getMovieList() == null || directorDTO.getMovieList().isEmpty()) {
            Director checkDirector = checkOptionalDirector(directorDTO.getId());
            checkDirector.setAge(directorDTO.getAge());
            checkDirector.setName(directorDTO.getName());
        } else {
            boolean checkActorsNull = directorDTO.getMovieList().stream()
                    .allMatch(movie -> movie.getActorList() == null && movie.getId() == null && movie.getDirectorId() == null
                            && movie.getYear() != null && movie.getYear() > 1900 && movie.getName() != null);

            if (checkActorsNull) {
                Director checkDirector = checkOptionalDirector(directorDTO.getId());// из базы
                checkDirector.setName(directorDTO.getName());
                checkDirector.setAge(directorDTO.getAge());
                Director director = mapperDTO.toDirector(directorDTO);// мой из dto
                List<Movie> movies = director.getMovieList();

                for (Movie movie : movies) {
                    movie.setDirector(checkDirector);
                    movie.setActorList(new ArrayList<>());
                    Movie saveMovie = movieRepository.save(movie);
                    checkDirector.getMovieList().add(saveMovie);
                }
            } else
                throw new ResponseStatusException(BAD_REQUEST, "У режиссера должен быть или пустой список фильмов " +
                        "или в его фильмах должны быть пустые списки актеров, так же у добавляемых фильмов не " +
                        "может быть id и directorId, но должны быть year, name" +
                        " и год фильма должен быть больше 1900, такие вот правила");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (id == null)
            throw new ResponseStatusException(BAD_REQUEST, "Введите id режиссера");
        checkOptionalDirector(id);
        directorRepository.deleteById(id);
    }

    private Director checkOptionalDirector(Long id) {
        Optional<Director> optionalDirector = directorRepository.findById(id);
        if (optionalDirector.isPresent())
            return optionalDirector.get();
        throw new ResponseStatusException(BAD_REQUEST, "Режиссера с таким id в базе нет");
    }

}
