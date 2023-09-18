package org.example.dto;


import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class ActorDTO {
    private Long id;
    private String name;
    private Integer age;
    private List<MovieDTO> movieList;

    public ActorDTO() {
    }

    public ActorDTO(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public ActorDTO(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<MovieDTO> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<MovieDTO> movieList) {
        this.movieList = movieList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActorDTO actorDTO = (ActorDTO) o;
        return Objects.equals(age, actorDTO.age) && Objects.equals(id, actorDTO.id) && Objects.equals(name, actorDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }

    @Override
    public String toString() {
        return reflectionToString(this, JSON_STYLE);
    }
}
