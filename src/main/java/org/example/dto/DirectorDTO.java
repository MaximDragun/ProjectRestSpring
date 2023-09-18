package org.example.dto;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class DirectorDTO {
    private Long id;
    private String name;
    private Integer age;
    private List<MovieDTO> movieList;

    public DirectorDTO() {
    }

    public DirectorDTO(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public DirectorDTO(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public List<MovieDTO> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<MovieDTO> movieList) {
        this.movieList = movieList;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectorDTO that = (DirectorDTO) o;
        return Objects.equals(age, that.age) && Objects.equals(id, that.id) && Objects.equals(name, that.name);
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
