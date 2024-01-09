package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


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
    void getFinalDestinationGives403() {
        Mockito.when(authorizationService.authorize(1L, "getFinalDestination"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));

        var res = controller.getFinalDestination(1L, 11L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
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
    void getPickUpDestinationGives403() {
        Mockito.when(authorizationService.authorize(1L, "getPickupDestination"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));

        var res = controller.getPickupDestination(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void getOrder200() {
        Optional<Order> proper = Optional.of(new Order().id(11L).status(Order.StatusEnum.ACCEPTED));
        Mockito.when(orderService.getOrderById(11L)).thenReturn(proper);
        Mockito.when(authorizationService.authorize(1L, "getOrder"))
            .thenReturn(Optional.empty());

        var res = controller.getOrder(11L, 1L);
        assertEquals(new ResponseEntity<>(proper.get(), HttpStatus.OK), res);
    }

    @Test
    void getOrder404() {
        Optional<Order> proper = Optional.of(new Order().id(11L).status(Order.StatusEnum.ACCEPTED));
        Mockito.when(orderService.getOrderById(11L)).thenReturn(Optional.empty());
        Mockito.when(authorizationService.authorize(1L, "getOrder"))
            .thenReturn(Optional.empty());


        var res = controller.getOrder(1L, 11L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void getOrder403() {
        Optional<Order> proper = Optional.of(new Order().id(11L).status(Order.StatusEnum.ACCEPTED));
        Mockito.when(orderService.getOrderById(11L)).thenReturn(Optional.empty());
        Mockito.when(authorizationService.authorize(1L, "getOrder"))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));

        var res = controller.getOrder(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
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
    void getOrderRating403() {
        Mockito.when(authorizationService.authorize(1L, "getOrderRating"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.getOrderRating(1L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void updateOrder200() {
        Optional<Order> proper = Optional.of(new Order().id(11L).status(Order.StatusEnum.ACCEPTED));
        Order updated = new Order().id(11L).status(Order.StatusEnum.PENDING);
        Mockito.when(orderService.updateOrderById(11L, updated)).thenReturn(Optional.of(updated));
        Mockito.when(authorizationService.authorize(1L, "updateOrder"))
            .thenReturn(Optional.empty());


        var res = controller.updateOrder(11L, 1L, updated);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateOrder404() {
        Optional<Order> proper = Optional.of(new Order().id(11L).status(Order.StatusEnum.ACCEPTED));
        Order updated = new Order().id(11L).status(Order.StatusEnum.PENDING);
        Mockito.when(orderService.updateOrderById(11L, updated)).thenReturn(Optional.empty());
        Mockito.when(authorizationService.authorize(1L, "updateOrder"))
            .thenReturn(Optional.empty());


        var res = controller.updateOrder(11L, 1L, updated);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateOrder403() {
        Optional<Order> proper = Optional.of(new Order().id(11L).status(Order.StatusEnum.ACCEPTED));
        Order updated = new Order().id(11L).status(Order.StatusEnum.PENDING);
        Mockito.when(authorizationService.authorize(1L, "updateOrder"))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));

        var res = controller.updateOrder(11L, 1L, updated);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void getOrders200() {
        List<Order> proper = List.of(new Order().id(11L), new Order().id(22L), new Order().id(33L));
        Mockito.when(orderService.getOrders()).thenReturn(Optional.of(proper));
        Mockito.when(authorizationService.authorize(1L, "getOrders"))
            .thenReturn(Optional.empty());

        var res = controller.getOrders(1L);
        assertEquals(new ResponseEntity<>(proper, HttpStatus.OK), res);
    }

    @Test
    void getOrders403() {
        List<Order> proper = List.of(new Order().id(11L), new Order().id(22L), new Order().id(33L));
        Mockito.when(orderService.getOrders()).thenReturn(Optional.of(proper));
        Mockito.when(authorizationService.authorize(1L, "getOrders"))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));

        var res = controller.getOrders(1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void getOrders404() {
        Mockito.when(orderService.getOrders()).thenReturn(Optional.empty());
        Mockito.when(authorizationService.authorize(1L, "getOrders"))
            .thenReturn(Optional.empty());

        var res = controller.getOrders(1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void makeOrder200() {
        Order o = new Order().id(11L);
        Mockito.when(authorizationService.authorize(1L, "makeOrder"))
            .thenReturn(Optional.empty());
        Mockito.when(orderService.createOrder(o)).thenReturn(Optional.of(o));
        Mockito.when(orderService.getOrderById(11L)).thenReturn(Optional.empty());

        var res = controller.makeOrder(11L, 1L, o);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
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
    void updateOrderRating403() {
        Optional<BigDecimal> rating2 = Optional.of(new BigDecimal("2.0"));

        Mockito.when(authorizationService.authorize(1L, "putOrderRating"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));

        var res = controller.putOrderRating(1L, 1L, rating2.get());
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void makeOrder403() {
        Order o = new Order().id(11L);
        Mockito.when(authorizationService.authorize(1L, "makeOrder"))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        Mockito.when(orderService.createOrder(o)).thenReturn(Optional.of(o));
        Mockito.when(orderService.getOrderById(11L)).thenReturn(Optional.empty());

        var res = controller.makeOrder(11L, 1L, o);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void makeOrder400() {
        Order o = new Order().id(11L);
        Mockito.when(authorizationService.authorize(1L, "makeOrder"))
            .thenReturn(Optional.empty());
        Mockito.when(orderService.createOrder(o)).thenReturn(Optional.of(o));
        Mockito.when(orderService.getOrderById(11L)).thenReturn(Optional.of(o));

        var res = controller.makeOrder(11L, 1L, o);
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void updateOrderRating404() {
        Mockito.when(orderService.getRating(1L)).thenReturn(Optional.empty());
        var res = controller.putOrderRating(1L, 1L, new BigDecimal("1.0"));

        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

}