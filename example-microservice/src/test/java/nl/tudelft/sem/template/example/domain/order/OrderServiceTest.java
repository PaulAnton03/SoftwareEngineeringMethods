package nl.tudelft.sem.template.example.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
}