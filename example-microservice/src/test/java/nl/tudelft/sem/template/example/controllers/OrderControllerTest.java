package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.example.domain.order.OrderStrategy.GeneralOrdersStrategy;
import nl.tudelft.sem.template.example.domain.order.OrderStrategy.NextOrderStrategy;
import nl.tudelft.sem.template.example.domain.order.OrderStrategy.OrderPerVendorStrategy;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


class OrderControllerTest {

    private OrderService orderService;

    private OrderRepository orderRepo;

    private VendorRepository vendorRepo;
    private OrderController controller;

    private Order order1;

    private NextOrderStrategy strategy;

    private OrderPerVendorStrategy perVendorStrategy;

    private GeneralOrdersStrategy generalStrategy;

    private AuthorizationService authorizationService;


    @BeforeEach
    void setUp() {
        this.orderService = Mockito.mock(OrderService.class);
        this.orderRepo = Mockito.mock(OrderRepository.class);
        this.strategy = Mockito.mock(NextOrderStrategy.class);
        this.authorizationService = Mockito.mock(AuthorizationService.class);
        this.vendorRepo = Mockito.mock(VendorRepository.class);
        this.generalStrategy = Mockito.mock(GeneralOrdersStrategy.class);
        this.perVendorStrategy = Mockito.mock(OrderPerVendorStrategy.class);
        this.order1 = new Order().id(2L).status(Order.StatusEnum.PREPARING).vendorId(44L);
        Mockito.when(authorizationService.authorize(anyLong(), anyString())).thenReturn(Optional.empty());
        this.controller = new OrderController(orderService, authorizationService, orderRepo, vendorRepo);
    }

    @Test
    void getIndependentOrdersWorks() {
        Mockito.when(orderRepo.findByStatus(any())).thenReturn(List.of(order1));
        Mockito.when(vendorRepo.findById(any())).thenReturn(Optional.ofNullable(new Vendor().id(33L).hasCouriers(false)));
        Mockito.when(generalStrategy.availableOrders(any())).thenReturn(Optional.of(List.of(order1)));

        var res = controller.getIndependentOrders(11L);
        assertEquals(new ResponseEntity<>(List.of(order1), HttpStatus.OK), res);
    }

    @Test
    void getIndependentOrdersForbidden() {
        Mockito.when(orderRepo.findByStatus(any())).thenReturn(List.of(order1));
        Mockito.when(vendorRepo.findById(any())).thenReturn(Optional.ofNullable(new Vendor().id(33L).hasCouriers(false)));
        Mockito.when(generalStrategy.availableOrders(any())).thenReturn(Optional.of(List.of(order1)));
        Mockito.when(authorizationService.authorize(any(), anyString()))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));

        var res = controller.getIndependentOrders(11L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void getIndependentOrders404() {
        Mockito.when(orderRepo.findByStatus(any())).thenReturn(List.of());
        Mockito.when(vendorRepo.findById(any())).thenReturn(Optional.ofNullable(new Vendor().id(33L).hasCouriers(false)));
        Mockito.when(generalStrategy.availableOrders(any())).thenReturn(Optional.of(List.of(order1)));

        var res = controller.getIndependentOrders(11L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void getNextOrderForVendorWorks() {
        Mockito.when(orderRepo.findByVendorIdAndStatus(anyLong(), any())).thenReturn(List.of(order1));
        Mockito.when(perVendorStrategy.availableOrders(any())).thenReturn(Optional.of(List.of(order1)));

        var res = controller.getNextOrderForVendor(11L, 1L);
        assertEquals(new ResponseEntity<>(order1, HttpStatus.OK), res);
    }

    @Test
    void getNextOrderForVendorForbidden() {
        Mockito.when(orderRepo.findByVendorIdAndStatus(anyLong(), any())).thenReturn(List.of(order1));
        Mockito.when(perVendorStrategy.availableOrders(any())).thenReturn(Optional.of(List.of(order1)));
        Mockito.when(authorizationService.authorize(any(), anyString()))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));

        var res = controller.getNextOrderForVendor(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void getNextOrderForVendorWorksMultipleOrders() {
        Mockito.when(orderRepo.findByVendorIdAndStatus(anyLong(), any())).thenReturn(List.of(order1, new Order()));
        Mockito.when(perVendorStrategy.availableOrders(any())).thenReturn(Optional.of(List.of(order1)));

        var res = controller.getNextOrderForVendor(11L, 1L);
        assertEquals(new ResponseEntity<>(order1, HttpStatus.OK), res);
    }

    @Test
    void getNextOrderForVendorNotFound() {
        Mockito.when(orderRepo.findByVendorIdAndStatus(anyLong(), any())).thenReturn(List.of());
        Mockito.when(perVendorStrategy.availableOrders(any())).thenReturn(Optional.of(List.of()));

        var res = controller.getNextOrderForVendor(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void getNextOrderForVendorBadRequest() {
        Mockito.when(orderRepo.findByVendorIdAndStatus(anyLong(), any())).thenReturn(null);
        Mockito.when(perVendorStrategy.availableOrders(any())).thenReturn(Optional.empty());

        var res = controller.getNextOrderForVendor(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
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