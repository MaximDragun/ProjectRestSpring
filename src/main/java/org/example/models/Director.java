package org.example.models;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static javax.persistence.GenerationType.*;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

@Entity
public class Director {
    @Id
    @Column(name = "director_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long directorId;
    private String name;
    private Integer age;
    @OneToMany(mappedBy = "directorId")
    private List<Movie> movieList;

    public Director() {
    }

    public Director(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public Director(Long id, String name, Integer age) {
        this.directorId = id;
        this.name = name;
        this.age = age;
    }

    public Long getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Long id) {
        this.directorId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Director aDirector = (Director) o;
        return Objects.equals(age, aDirector.age) && Objects.equals(directorId, aDirector.directorId) && Objects.equals(name, aDirector.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directorId, name, age);
    }

    @Override
    public String toString() {
        return reflectionToString(this, JSON_STYLE);
    }
}
