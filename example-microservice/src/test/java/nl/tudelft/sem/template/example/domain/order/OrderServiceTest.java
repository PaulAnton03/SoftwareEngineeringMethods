package nl.tudelft.sem.template.example.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Time;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class OrderServiceTest {
    private OrderRepository orderRepo;
    private VendorRepository vendorRepo;
    private Order order1;

    private Vendor vendor1;

    private OrderService os;

    @BeforeEach
    void setUp() {
        this.orderRepo = mock(OrderRepository.class);
        this.vendorRepo = mock(VendorRepository.class);
        this.order1 = new Order().id(1L).vendorId(2L).deliveryDestination(new Location().latitude(11F).longitude(22F));
        this.vendor1 = new Vendor().id(2L).location(new Location().latitude(22F).longitude(33F));
        this.os = new OrderService(orderRepo, vendorRepo);
    }

    @Test
    void getFinalDestinationOfOrderWorks() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));

        Location exp = new Location().latitude(11F).longitude(22F);
        Location res = os.getFinalDestinationOfOrder(order1.getId()).get();
        assertEquals(res, exp);
    }

    @Test
    void getFinalDestinationOfOrderThrows404() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Location> res = os.getFinalDestinationOfOrder(order1.getId());
        assertTrue(res.isEmpty());
    }

    @Test
    void getPickupDestinationOfOrderWorks() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Mockito.when(vendorRepo.findById(anyLong())).thenReturn(Optional.of(vendor1));

        Location exp = vendor1.getLocation();
        Location res = os.getPickupDestination(order1.getId()).get();
        assertEquals(res, exp);
    }

    @Test
    void getPickupDestinationOfOrderDives404NoVendor() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Mockito.when(vendorRepo.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Location> res = os.getPickupDestination(order1.getId());
        assertEquals(res, Optional.empty());
    }

    @Test
    void getPickupDestinationOfOrderDives404NoOrder() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Location> res = os.getPickupDestination(order1.getId());
        assertEquals(res, Optional.empty());
    }

    @Test
    void getPickupDestinationOfOrderDives404NoLocation() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Mockito.when(vendorRepo.findById(anyLong())).thenReturn(Optional.of(new Vendor().id(2L))); // no location for vendor

        Optional<Location> res = os.getPickupDestination(order1.getId());
        assertEquals(res, Optional.empty());
    }

    @Test
    void getOrder200() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Optional<Order> res = os.getOrderById(order1.getId());
        assertEquals(res, Optional.of(order1));
    }

    @Test
    void getOrder400() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());
        Optional<Order> res = os.getOrderById(order1.getId());
        assertEquals(res, Optional.empty());
    }

    @Test
    void updateOrder200() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Mockito.when(orderRepo.saveAndFlush(order1)).thenReturn(order1);
        Optional<Order> res = os.updateOrderById(order1.getId(), order1);
        assertEquals(res, Optional.of(order1));
    }

    @Test
    void updateOrder400() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());
        Optional<Order> res = os.updateOrderById(order1.getId(), order1);
        assertEquals(res, Optional.empty());
    }

    @Test
    void getOrders200() {
        List<Order> o = List.of(order1, new Order().id(22L));
        Mockito.when(orderRepo.findAll()).thenReturn(o);
        Optional<List<Order>> res = os.getOrders();
        assertEquals(res, Optional.of(o));
    }

    @Test
    void getOrders404() {
        List<Order> o = List.of();
        Mockito.when(orderRepo.findAll()).thenReturn(o);
        Optional<List<Order>> res = os.getOrders();
        assertEquals(res, Optional.empty());
    }

    @Test
    void makeOrder200() {
        Mockito.when(orderRepo.saveAndFlush(order1)).thenReturn(order1);
        Optional<Order> o = os.createOrder(order1);
        assertEquals(o, Optional.of(order1));
    }

    @Test
    void orderExistsTrue() {
        Mockito.when(orderRepo.existsById(anyLong())).thenReturn(true);

        Boolean res = os.orderExists(order1.getId());
        assertTrue(res);
    }

    @Test
    void orderExistsFalse() {
        Mockito.when(orderRepo.existsById(anyLong())).thenReturn(false);

        Boolean res = os.orderExists(order1.getId());
        assertFalse(res);
    }

    @Test
    void getTimeValuesWorks() {
        OffsetDateTime delivTime = OffsetDateTime.of(2023, 12, 17, 12, 30, 0, 0, ZoneOffset.UTC);
        Time timeVals = new Time().prepTime("00::33::00").actualDeliveryTime(delivTime);
        Order o = order1.timeValues(timeVals);
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(o));

        Optional<Time> res = os.getTimeValuesForOrder(order1.getId());
        assertEquals(res.get(), timeVals);
    }

    @Test
    void getTimeValuesNoOrder() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Time> res = os.getTimeValuesForOrder(order1.getId());
        assertTrue(res.isEmpty());
    }

    @Test
    void getTimeValuesNoTimeVals() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));

        Optional<Time> res = os.getTimeValuesForOrder(order1.getId());
        assertTrue(res.isEmpty());
    }

    @Test
    void getRating200() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Optional<BigDecimal> res = os.getRating(order1.getId());
        assertTrue(res.isPresent());
        assertEquals(res.get(),order1.getRatingNumber());
    }

    @Test
    void getRating404() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());
        Optional<BigDecimal> res = os.getRating(order1.getId());
        assertTrue(res.isEmpty());
    }

    @Test
    void updateRating200() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Order order3 = order1;
        order3.setRatingNumber(BigDecimal.valueOf(10));
        Mockito.when(orderRepo.saveAndFlush(order3)).thenReturn(order3);
        Optional<BigDecimal> res = os.updateRating(order1.getId(), BigDecimal.valueOf(10));
        assertTrue(res.isPresent());
        assertEquals(res.get(), order3.getRatingNumber());
    }

    @Test
    void updateRating404() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());
        Optional<BigDecimal> res = os.updateRating(order1.getId(), BigDecimal.valueOf(10));
        assertTrue(res.isEmpty());
    }
}