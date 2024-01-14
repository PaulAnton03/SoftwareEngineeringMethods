package nl.tudelft.sem.template.example.domain.order;

import nl.tudelft.sem.template.example.domain.user.CourierRepository;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.example.externalservices.NavigationMock;
import nl.tudelft.sem.template.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;

class OrderServiceTest {
    private OrderRepository orderRepo;
    private VendorRepository vendorRepo;
    private CourierRepository courierRepo;
    private Order order1;
    private Vendor vendor1;
    private OffsetDateTime eta;
    private OrderService os;

    @BeforeEach
    void setUp() {
        this.orderRepo = mock(OrderRepository.class);
        this.vendorRepo = mock(VendorRepository.class);
        this.courierRepo = mock(CourierRepository.class);

        this.order1 = new Order().id(1L).vendorId(2L).deliveryDestination(new Location().latitude(11F).longitude(22F))
                .ratingNumber(BigDecimal.valueOf(5L)).courierId(2L);
        this.vendor1 = new Vendor().id(2L).location(new Location().latitude(22F).longitude(33F));
        this.eta = OffsetDateTime.of(2000, 1, 1,
                1, 30, 0, 0, ZoneOffset.ofTotalSeconds(0));
        this.os = new OrderService(orderRepo, vendorRepo, courierRepo);
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
    void getRatingWorks() {
        order1.ratingNumber(new BigDecimal(5));
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));

        BigDecimal res = os.getRating(order1.getId()).get();
        assertEquals(order1.getRatingNumber(), res);
    }

    @Test
    void getRatingGives404NoOrder() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());

        Optional<BigDecimal> res = os.getRating(order1.getId());
        assertEquals(Optional.empty(), res);
    }

    @Test
    void updateRatingWorks() {
        order1.ratingNumber(new BigDecimal(5));
        Order order11 = new Order().id(1L).ratingNumber(new BigDecimal(3));

        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Mockito.lenient().when(orderRepo.save(order1)).thenReturn(order11);

        BigDecimal res = os.updateRating(order1.getId(), new BigDecimal(3)).get();
        assertEquals(order11.getRatingNumber(), res);
    }

    @Test
    void updateRatingGives404NoOrder() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());

        Optional<BigDecimal> res = os.getRating(order1.getId());
        assertEquals(Optional.empty(), res);
    }

    @Test
    void updatePrepTimeWorks() {
        Time time1 = new Time().prepTime("01:30:00");
        Time time2 = new Time().prepTime("03:30:00");
        order1.setTimeValues(time1);
        Order order11 = new Order().id(1L).timeValues(time2);

        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Mockito.lenient().when(orderRepo.saveAndFlush(order1)).thenReturn(order11);

        Optional<String> res = os.updatePrepTime(order1.getId(), order11.getTimeValues().getPrepTime());
        assertTrue(res.isPresent());
        assertEquals(res.get(), time2.getPrepTime());
        assertEquals(order1.getTimeValues().getPrepTime(), time2.getPrepTime());
    }

    @Test
    void updatePrepTimeGives404NoOrder() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());

        Optional<String> res = os.updatePrepTime(order1.getId(), "03:30:00");
        assertEquals(Optional.empty(), res);
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
    void updateCourier200() {
        Mockito.when(orderRepo.findById(order1.getId())).thenReturn(Optional.of(order1));
        Order order2 = new Order().id(order1.getId()).courierId(2L);
        Mockito.when(orderRepo.saveAndFlush(order1)).thenReturn(order2);

        Optional<Order> o = os.updateCourier(order1.getId(), 2L);
        assertTrue(o.isPresent());
        assertEquals(o.get().getCourierId(), 2L);
        assertEquals(order1.getCourierId(), order2.getCourierId());
    }

    @Test
    void updateCourier404() {
        Mockito.when(orderRepo.findById(order1.getId())).thenReturn(Optional.empty());

        Optional<Order> o = os.updateCourier(order1.getId(), 2L);
        assertTrue(o.isEmpty());
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
        Order o = order1;
        o.setId(10L);
        o.setRatingNumber(BigDecimal.valueOf(5));
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(o));
        Optional<BigDecimal> res = os.getRating(o.getId());
        assertTrue(res.isPresent());
        assertEquals(res.get(), o.getRatingNumber());
    }

    @Test
    void getRating404() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());
        Optional<BigDecimal> res = os.getRating(order1.getId());
        assertTrue(res.isEmpty());
    }

    @Test
    void getRatingNoRatingNumber() {
        order1.setRatingNumber(null);
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Optional<BigDecimal> res = os.getRating(order1.getId());
        assertTrue(res.isEmpty());
    }

    @Test
    void updateRating200() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Order order3 = new Order().id(order1.getId()).ratingNumber(BigDecimal.valueOf(10));
        Mockito.when(orderRepo.saveAndFlush(order1)).thenReturn(order3);
        Optional<BigDecimal> res = os.updateRating(order1.getId(), BigDecimal.valueOf(10));
        assertTrue(res.isPresent());
        assertEquals(res.get(), order3.getRatingNumber());
        assertEquals(order1.getRatingNumber(), order3.getRatingNumber());
    }

    @Test
    void updateRating404() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());
        Optional<BigDecimal> res = os.updateRating(order1.getId(), BigDecimal.valueOf(10));
        assertTrue(res.isEmpty());
    }

    @Test
    void getETAValid() {
        Order order2 = new Order().timeValues(new Time().expectedDeliveryTime(eta).prepTime("03:30:00"));
        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.of(order2));
        Optional<OffsetDateTime> res = os.getETA(1L);
        assertFalse(res.isEmpty());
        assertEquals(res.get(), eta);
    }

    @Test
    void getETANoTime() {
        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.of(order1));
        Optional<OffsetDateTime> res = os.getETA(1L);
        assertTrue(res.isEmpty());
    }

    @Test
    void getETANoPrep() {
        Order order2 = new Order().timeValues(new Time());
        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.of(order2));
        Optional<OffsetDateTime> res = os.getETA(1L);
        assertTrue(res.isEmpty());
    }

    @Test
    void getETANoETA() {
        Order order2 = new Order().timeValues(new Time().prepTime("03:30:00"));
        Mockito.when(orderRepo.saveAndFlush(any())).thenReturn(order2);
        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.of(order2));

        Optional<OffsetDateTime> res = os.getETA(1L);
        assertFalse(res.isEmpty());
        assertEquals(res.get(), new NavigationMock().getETA(1L, new Time()));
    }

    @Test
    void getETAEmpty() {
        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.empty());

        Optional<OffsetDateTime> res = os.getETA(1L);
        assertTrue(res.isEmpty());
    }

    @Test
    void getDistanceValid() {
        Courier courier1 = new Courier().currentLocation(new Location());

        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.of(order1));
        Mockito.when(courierRepo.findById(2L)).thenReturn(Optional.of(courier1));

        Optional<Float> res = os.getDistance(1L);
        Float expected = new NavigationMock().getDistance(new Location(), new Location());
        assertFalse(res.isEmpty());
        assertEquals(res, Optional.of(expected));
    }

    @Test
    void getDistanceEmptyOrder() {
        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.empty());
        Optional<Float> res = os.getDistance(1L);
        assertTrue(res.isEmpty());
    }

    @Test
    void getDistanceNoDeliveryDestination() {
        Order order2 = new Order();
        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.of(order2));
        Optional<Float> res = os.getDistance(1L);
        assertTrue(res.isEmpty());
    }

    @Test
    void getDistanceNoCourierId() {
        Order order2 = new Order().deliveryDestination(new Location());
        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.of(order2));
        Optional<Float> res = os.getDistance(1L);
        assertTrue(res.isEmpty());
    }

    @Test
    void getDistanceEmptyCourier() {
        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.of(order1));
        Mockito.when(courierRepo.findById(2L)).thenReturn(Optional.empty());
        Optional<Float> res = os.getDistance(1L);
        assertTrue(res.isEmpty());
    }

    @Test
    void getDistanceNoCourierLocation() {
        Courier courier1 = new Courier();
        Mockito.when(orderRepo.findById(1L)).thenReturn(Optional.of(order1));
        Mockito.when(courierRepo.findById(2L)).thenReturn(Optional.of(courier1));
        Optional<Float> res = os.getDistance(1L);
        assertTrue(res.isEmpty());
    }


}