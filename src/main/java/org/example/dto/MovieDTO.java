package org.example.dto;


import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class MovieDTO {
    private Long id;
    private Long directorId;
    private String name;
    private Integer year;
    private List<ActorDTO> actorList;

    public MovieDTO() {
    }

    public MovieDTO(Long id, Long directorId, String name, Integer year) {
        this.id = id;
        this.directorId = directorId;
        this.name = name;
        this.year = year;
    }

    public MovieDTO(Long directorId, String name, Integer year) {
        this.directorId = directorId;
        this.name = name;
        this.year = year;
    }

    public List<ActorDTO> getActorList() {
        return actorList;
    }

    public void setActorList(List<ActorDTO> actorList) {
        this.actorList = actorList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieDTO movieDTO = (MovieDTO) o;
        return Objects.equals(id, movieDTO.id) && Objects.equals(directorId, movieDTO.directorId) && Objects.equals(name, movieDTO.name) && Objects.equals(year, movieDTO.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, directorId, name, year);
    }

    @Override
    public String toString() {
        return reflectionToString(this, JSON_STYLE);
    }
}
