package nl.tudelft.sem.template.example.domain.admin;

import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class AdminServiceTest {
    private VendorRepository vendorRepo;
    private Vendor vendor1;
    private OrderRepository orderRepo;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        this.vendorRepo = mock(VendorRepository.class);
        this.orderRepo = mock(OrderRepository.class);
        this.vendor1 = new Vendor().radius(1D).id(2L).location(new Location().latitude(22F).longitude(33F));
        this.adminService = new AdminService(vendorRepo, orderRepo);
    }

    @Test
    void updateDefaultRadiusEmpty(){
        Mockito.when(vendorRepo.findVendorsByHasCouriers(false)).thenReturn(new ArrayList<>());

        Optional<List<Vendor>> res = adminService.updateDefaultRadius(5D);
        assertTrue(res.isEmpty());
    }

    @Test
    void updateDefaultRadiusNotEmpty(){
        List<Vendor> vendors = new ArrayList<>();
        vendors.add(vendor1);
        Mockito.when(vendorRepo.findVendorsByHasCouriers(false)).thenReturn(vendors);

        Optional<List<Vendor>> res = adminService.updateDefaultRadius(5D);
        assertEquals(res.get(), vendors);
    }

    @Test
    void getDefaultRadiusEmpty(){
        Mockito.when(vendorRepo.findVendorsByHasCouriers(false)).thenReturn(new ArrayList<>());

        Optional<Double> res = adminService.getDefaultRadius();
        assertTrue(res.isEmpty());
    }

    @Test
    void getDefaultRadiusNotEmpty(){
        List<Vendor> vendors = new ArrayList<>();
        vendors.add(vendor1);
        Mockito.when(vendorRepo.findVendorsByHasCouriers(false)).thenReturn(vendors);

        Optional<Double> res = adminService.getDefaultRadius();
        assertEquals(res.get(), 1D);
    }

    @Test
    void getDeliveredWorks() {
        List<Order> orders = List.of(new Order().id(1L).status(Order.StatusEnum.DELIVERED),
                new Order().id(2L).status(Order.StatusEnum.DELIVERED));
        Mockito.when(orderRepo.findByStatus(Order.StatusEnum.DELIVERED)).thenReturn(orders);

        Optional<List<Order>> res = adminService.getDelivered();
        assertEquals(orders, res.get());
    }

    @Test
    void getDeliveredDoesNotWork() {
        Mockito.when(orderRepo.findByStatus(Order.StatusEnum.DELIVERED))
                .thenReturn(new ArrayList<>());

        Optional<List<Order>> res = adminService.getDelivered();
        assertEquals(Optional.empty(), res);
    }
}
