package org.example.mapping;

import org.example.dto.ActorDTO;
import org.example.dto.DirectorDTO;
import org.example.dto.MovieDTO;
import org.example.models.Actor;
import org.example.models.Director;
import org.example.models.Movie;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface MapperDTO {

    //toEntity
    @Mapping(target = "directorId", source = "id")
    Director toDirector(DirectorDTO directorDTO);

    @Mapping(source = "id", target = "movieId")
    @Mapping(source = "year", target = "yearOfProduction")
    Movie toMovie(MovieDTO movieDTO);

    @Mapping(target = "actorId", source = "id")
    Actor toActor(ActorDTO actorDTO);

    @AfterMapping
    default void mapDirectorForMovie(MovieDTO movieDTO, @MappingTarget Movie movie) {
        Director aDirector = new Director();
        aDirector.setDirectorId(movieDTO.getDirectorId());
        movie.setDirector(aDirector);
    }


    //toDTO
    @Mapping(target = "movieList", source = "movieList", qualifiedByName = "A")
    @Mapping(target = "id", source = "directorId")
    DirectorDTO toDirectorDTOWithMoviesWithoutActors(Director aDirector);

    @Mapping(target = "actorList", ignore = true)
    @Mapping(source = "movieId", target = "id")
    @Mapping(source = "yearOfProduction", target = "year")
    @Named("toMovieDTOWithoutActors")
    MovieDTO toMovieDTOWithoutActors(Movie movie);

    @Mapping(target = "movieList", source = "movieList", qualifiedByName = "A")
    @Mapping(target = "id", source = "actorId")
    ActorDTO toActorDTOWithoutMovies(Actor actor);


    @Mapping(target = "actorList", source = "actorList", qualifiedByName = "B")
    @Mapping(source = "movieId", target = "id")
    @Mapping(source = "yearOfProduction", target = "year")
    MovieDTO toMovieDTO(Movie movie);

    @Named("toActorDTOWithoutMovies")
    @Mapping(target = "id", source = "actorId")
    @Mapping(target = "movieList", ignore = true)
    ActorDTO toActorDTO(Actor actor);

    @IterableMapping(qualifiedByName = "toMovieDTOWithoutActors")
    @Named("A")
    List<MovieDTO> toMovieDTOListWithoutActor(List<Movie> movies);

    @IterableMapping(qualifiedByName = "toActorDTOWithoutMovies")
    @Named("B")
    List<ActorDTO> toActorDTOListWithoutMovie(List<Actor> actors);

    @AfterMapping
    default void mapDirectorIdForMovieDTO(Movie movie, @MappingTarget MovieDTO movieDTO) {
        movieDTO.setDirectorId(movie.getDirector().getDirectorId());
    }
}
