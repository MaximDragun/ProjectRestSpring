package controllers;

import org.example.controllers.DirectorController;
import org.example.dto.DirectorDTO;
import org.example.services.interfaces.DirectorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class DirectorControllerTest {
    @InjectMocks
    private DirectorController directorController;
    @Mock
    private DirectorService directorService;

    @Test
    void testGetDirectors() {
        DirectorDTO directorDTO1 = new DirectorDTO();
        DirectorDTO directorDTO2 = new DirectorDTO();

        List<DirectorDTO> directors = new ArrayList<>(List.of(directorDTO1, directorDTO2));
        when(directorService.findAll()).thenReturn(directors);

        ResponseEntity<List<DirectorDTO>> response = directorController.getDirectors();

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(directors, response.getBody());

        verify(directorService, times(1)).findAll();
    }

    @Test
    void testGetDirector() {
        DirectorDTO director = new DirectorDTO();
        Long directorId = 1L;
        when(directorService.findById(directorId)).thenReturn(director);

        ResponseEntity<DirectorDTO> response = directorController.getDirector(directorId);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(director, response.getBody());

        verify(directorService, times(1)).findById(directorId);
    }

    @Test
    void testCreate() {
        DirectorDTO director = new DirectorDTO();

        ResponseEntity<HttpStatus> response = directorController.create(director);

        assertSame(HttpStatus.OK, response.getStatusCode());

        verify(directorService, times(1)).create(director);
    }

    @Test
    void testUpdate() {
        DirectorDTO director = new DirectorDTO();

        ResponseEntity<HttpStatus> response = directorController.update(director);

        assertSame(HttpStatus.OK, response.getStatusCode());

        verify(directorService, times(1)).update(director);
    }

    @Test
    void testDelete() {
        Long directorId = 1L;

        ResponseEntity<HttpStatus> response = directorController.delete(directorId);

        assertSame(HttpStatus.OK, response.getStatusCode());

        verify(directorService, times(1)).delete(directorId);
    }
}
