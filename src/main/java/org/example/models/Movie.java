package org.example.models;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

@Entity
public class Movie {
    @Id
    @Column(name = "movie_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "director_id", referencedColumnName = "director_id")
    private Director directorId;
    private String name;
    @Column(name = "year_of_production")
    private Integer yearOfProduction;
    @ManyToMany(mappedBy = "movieList", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Actor> actorList;

    public Movie() {
    }

    public Movie(Director directorId, String name, int yearOfProduction) {
        this.directorId = directorId;
        this.name = name;
        this.yearOfProduction = yearOfProduction;
    }

    public Movie(Long movieId, Director directorId, String name, int yearOfProduction) {
        this.movieId = movieId;
        this.directorId = directorId;
        this.name = name;
        this.yearOfProduction = yearOfProduction;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Director getDirector() {
        return directorId;
    }

    public void setDirector(Director aDirector) {
        this.directorId = aDirector;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYearOfProduction() {
        return yearOfProduction;
    }

    public void setYearOfProduction(Integer yearOfProduction) {
        this.yearOfProduction = yearOfProduction;
    }

    public List<Actor> getActorList() {
        return actorList;
    }

    public void setActorList(List<Actor> actorList) {
        this.actorList = actorList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return yearOfProduction == movie.yearOfProduction && Objects.equals(movieId, movie.movieId) && Objects.equals(directorId, movie.directorId) && Objects.equals(name, movie.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, directorId, name, yearOfProduction);
    }

    @Override
    public String toString() {
        return reflectionToString(this, JSON_STYLE);
    }
}
