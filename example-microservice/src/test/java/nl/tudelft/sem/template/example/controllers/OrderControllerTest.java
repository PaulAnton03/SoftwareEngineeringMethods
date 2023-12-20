package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

import java.math.BigDecimal;
import java.util.Optional;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.example.wiremock.WireMockConfig;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.swing.text.html.Option;

class OrderControllerTest {

    private OrderService orderService;
    private OrderController controller;

    private AuthorizationService authorizationService;


    @BeforeEach
    void setUp() {
        this.orderService = Mockito.mock(OrderService.class);
        this.authorizationService = Mockito.mock(AuthorizationService.class);
        Mockito.when(authorizationService.authorize(anyLong(), Mockito.anyString())).thenReturn(Optional.empty());
        this.controller = new OrderController(orderService, authorizationService);
    }

    @Test
    void getFinalDestinationWorks() {
        Optional<Location> proper = Optional.of(new Location().latitude(1F).longitude(2F));
        Mockito.when(orderService.getFinalDestinationOfOrder(anyLong())).thenReturn(proper);

        var res = controller.getFinalDestination(11L, 1L);
        assertEquals(new ResponseEntity<>(proper.get(), HttpStatus.OK), res);
    }

    @Test
    void getFinalDestinationGives404() {
        Optional<Location> proper = Optional.empty();
        Mockito.when(orderService.getFinalDestinationOfOrder(anyLong())).thenReturn(proper);

        var res = controller.getFinalDestination(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void getPickUpDestinationWorks() {
        var proper = Optional.of(new Location().longitude(11f).latitude(22f));
        Mockito.when(orderService.getPickupDestination(anyLong())).thenReturn(proper);

        var res = controller.getPickupDestination(1L, 22L);
        assertEquals(new ResponseEntity<>(proper.get(), HttpStatus.OK), res);
    }

    @Test
    void getPickUpDestinationGives404() {
        Optional<Location> proper = Optional.empty();
        Mockito.when(orderService.getPickupDestination(anyLong())).thenReturn(proper);

        var res = controller.getPickupDestination(1L, 22L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void getOrderRating200() {
        Optional<BigDecimal> proper = Optional.of(new BigDecimal("5.0"));
        Mockito.when(orderService.getRating(1L)).thenReturn(proper);
        var res = controller.getOrderRating(1L, 1L);
        assertEquals(new ResponseEntity<>(proper.get(), HttpStatus.OK), res);
    }

    @Test
    void getOrderRating404() {
        Mockito.when(orderService.getRating(1L)).thenReturn(Optional.empty());
        var res = controller.getOrderRating(1L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateOrderRating200() {
        Optional<BigDecimal> rating1 = Optional.of(new BigDecimal("5.0"));
        Optional<BigDecimal> rating2 = Optional.of(new BigDecimal("2.0"));

        Mockito.when(orderService.getRating(1L)).thenReturn(rating1);
        Mockito.when(orderService.updateRating(1L, rating2.get())).thenReturn(rating2);

        var res = controller.putOrderRating(1L, 1L, rating2.get());

        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateOrderRating404() {
        Mockito.when(orderService.getRating(1L)).thenReturn(Optional.empty());
        var res = controller.putOrderRating(1L, 1L, new BigDecimal("1.0"));

        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

}