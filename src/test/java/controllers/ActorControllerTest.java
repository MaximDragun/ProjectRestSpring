package controllers;

import org.example.controllers.ActorController;
import org.example.dto.ActorDTO;
import org.example.services.interfaces.ActorService;
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
class ActorControllerTest {
    @InjectMocks
    private ActorController actorController;
    @Mock
    private ActorService actorService;

    @Test
    void testGetActors() {
        ActorDTO actorDTO1 = new ActorDTO();
        ActorDTO actorDTO2 = new ActorDTO();

        List<ActorDTO> actors = new ArrayList<>(List.of(actorDTO1, actorDTO2));
        when(actorService.findAll()).thenReturn(actors);

        ResponseEntity<List<ActorDTO>> response = actorController.getActors();

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(actors, response.getBody());

        verify(actorService, times(1)).findAll();
    }

    @Test
    void testGetActor() {
        ActorDTO actor = new ActorDTO();
        Long actorId = 1L;
        when(actorService.findById(actorId)).thenReturn(actor);

        ResponseEntity<ActorDTO> response = actorController.getActor(actorId);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(actor, response.getBody());

        verify(actorService, times(1)).findById(actorId);
    }

    @Test
    void testCreate() {
        ActorDTO actorDTO = new ActorDTO();

        ResponseEntity<HttpStatus> response = actorController.create(actorDTO);

        assertSame(HttpStatus.OK, response.getStatusCode());

        verify(actorService, times(1)).create(actorDTO);
    }

    @Test
    void testUpdate() {
        ActorDTO actorDTO = new ActorDTO();

        ResponseEntity<HttpStatus> response = actorController.update(actorDTO);

        assertSame(HttpStatus.OK, response.getStatusCode());

        verify(actorService, times(1)).update(actorDTO);
    }

    @Test
    void testDelete() {
        Long actorId = 1L;

        ResponseEntity<HttpStatus> response = actorController.delete(actorId);

        assertSame(HttpStatus.OK, response.getStatusCode());

        verify(actorService, times(1)).delete(actorId);
    }
}
