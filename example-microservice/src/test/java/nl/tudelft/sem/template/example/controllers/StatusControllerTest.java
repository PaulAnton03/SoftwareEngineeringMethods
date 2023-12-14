package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Optional;
import nl.tudelft.sem.template.example.domain.order.StatusService;
import nl.tudelft.sem.template.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class StatusControllerTest {

    private StatusService statusService;
    private StatusController controller;


    @BeforeEach
    void setUp() {
        this.statusService = Mockito.mock(StatusService.class);
        this.controller = new StatusController(statusService);
    }

    @Test
    void updateStatusToAccepted200() {
        Mockito.when(statusService.getOrderStatus(11L)).thenReturn(
                Optional.of(Order.StatusEnum.PENDING));
        Mockito.when(statusService.updateStatusToAccepted(11L)).thenReturn(
                Optional.of(new Order().id(11L).status(Order.StatusEnum.ACCEPTED)));

        var res = controller.updateToAccepted(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateStatusToAccepted404() {
        Mockito.when(statusService.updateStatusToAccepted(anyLong())).thenReturn(Optional.empty());

        var res = controller.updateToAccepted(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void getStatus404() {
        Mockito.when(statusService.getOrderStatus(anyLong())).thenReturn(Optional.empty());
        var res = controller.getStatus(1L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void getStatus200() {
        Mockito.when(statusService.getOrderStatus(1L)).thenReturn(Optional.of(Order.StatusEnum.ACCEPTED));
        var res = controller.getStatus(1L, 1L);
        assertEquals(new ResponseEntity<>("accepted", HttpStatus.OK), res);

    }


    @Test
    void updateStatusToInTransit200() {
        Mockito.when(statusService.getOrderStatus(11L)).thenReturn(
                Optional.of(Order.StatusEnum.GIVEN_TO_COURIER));
        Mockito.when(statusService.updateStatusToInTransit(11L)).thenReturn(
                Optional.of(new Order().id(11L).status(Order.StatusEnum.IN_TRANSIT)));

        var res = controller.updateToInTransit(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateStatusToInTransit404() {
        Mockito.when(statusService.updateStatusToInTransit(anyLong())).thenReturn(Optional.empty());

        var res = controller.updateToInTransit(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }


}
