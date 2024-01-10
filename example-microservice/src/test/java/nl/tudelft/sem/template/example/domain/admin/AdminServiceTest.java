package nl.tudelft.sem.template.example.domain.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.exception.DeliveryExceptionRepository;
import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.DeliveryException;
import nl.tudelft.sem.template.model.Order;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AdminServiceTest {

    private AdminService adminService;

    private AuthorizationService authorizationService;

    private DeliveryException exception1;
    private Order order1;

    private Vendor vendor1;

    private VendorRepository vendorRepo;

    private OrderRepository orderRepo;

    private DeliveryExceptionRepository exceptionRepo;


    @BeforeEach
    void setUp() {
        this.order1 = new Order().status(Order.StatusEnum.DELIVERED).id(3L);
        this.exception1 = new DeliveryException().id(2L)
            .exceptionType(DeliveryException.ExceptionTypeEnum.LATEDELIVERY)
            .isResolved(false)
            .order(order1);
        this.vendor1 = new Vendor().radius(1D).id(2L).location(new Location().latitude(22F).longitude(33F));
        this.vendorRepo = Mockito.mock(VendorRepository.class);
        this.orderRepo = Mockito.mock(OrderRepository.class);
        this.exceptionRepo = Mockito.mock(DeliveryExceptionRepository.class);
        this.adminService = new AdminService(vendorRepo, orderRepo, exceptionRepo);
        this.authorizationService = Mockito.mock(AuthorizationService.class);
    }

    @Test
    void getExceptionByOrderNoOrder() {
        when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());
        var res = adminService.getExceptionByOrder(order1.getId());
        assertEquals(Optional.empty(), res);
    }

    @Test
    void getExceptionByOrderWorks() {
        when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        when(exceptionRepo.findByOrder(order1)).thenReturn(List.of(exception1));
        var res = adminService.getExceptionByOrder(order1.getId());
        assertEquals(Optional.of(exception1), res);
    }

    @Test
    void getExceptionByOrderNoException() {
        when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        when(exceptionRepo.findByOrder(order1)).thenReturn(List.of());
        var res = adminService.getExceptionByOrder(order1.getId());
        assertEquals(Optional.empty(), res);
    }

    @Test
    void updateDefaultRadiusEmpty() {
        Mockito.when(vendorRepo.findVendorsByHasCouriers(false)).thenReturn(new ArrayList<>());

        Optional<List<Vendor>> res = adminService.updateDefaultRadius(5D);
        assertTrue(res.isEmpty());
    }

    @Test
    void updateDefaultRadiusNotEmpty() {
        List<Vendor> vendors = new ArrayList<>();
        vendors.add(vendor1);
        Mockito.when(vendorRepo.findVendorsByHasCouriers(false)).thenReturn(vendors);

        Optional<List<Vendor>> res = adminService.updateDefaultRadius(5D);
        assertEquals(res.get(), vendors);
    }

    @Test
    void getDefaultRadiusEmpty() {
        Mockito.when(vendorRepo.findVendorsByHasCouriers(false)).thenReturn(new ArrayList<>());

        Optional<Double> res = adminService.getDefaultRadius();
        assertTrue(res.isEmpty());
    }

    @Test
    void getDefaultRadiusNotEmpty() {
        List<Vendor> vendors = new ArrayList<>();
        vendors.add(vendor1);
        Mockito.when(vendorRepo.findVendorsByHasCouriers(false)).thenReturn(vendors);

        Optional<Double> res = adminService.getDefaultRadius();
        assertEquals(res.get(), 1D);
    }
}