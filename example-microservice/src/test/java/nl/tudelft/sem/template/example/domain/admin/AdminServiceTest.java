package nl.tudelft.sem.template.example.domain.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.exception.DeliveryExceptionRepository;
import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.DeliveryException;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class AdminServiceTest {

    private Vendor vendor1;

    private AdminService adminService;

    private AuthorizationService authorizationService;

    private DeliveryException exception1;

    private DeliveryException exception2;
    private Order order1;

    private VendorRepository vendorRepo;

    private OrderRepository orderRepo;

    private DeliveryExceptionRepository exceptionRepo;


    @BeforeEach
    void setUp() {
        this.vendor1 = new Vendor().radius(1D).id(2L).location(new Location().latitude(22F).longitude(33F));
        this.order1 = new Order().status(Order.StatusEnum.DELIVERED).id(2L);
        this.exception1 = new DeliveryException().id(2L)
            .exceptionType(DeliveryException.ExceptionTypeEnum.LATEDELIVERY)
            .isResolved(false)
            .order(order1);
        this.exception2 = new DeliveryException().id(2L)
            .exceptionType(DeliveryException.ExceptionTypeEnum.LATEDELIVERY)
            .isResolved(false)
            .order(new Order().id(6L));
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
    void makeExceptionWorks() {
        when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        when(exceptionRepo.saveAndFlush(exception1)).thenReturn(exception1);
        when(exceptionRepo.existsById(anyLong())).thenReturn(false);
        var res = adminService.makeException(exception1, 2L);
        assertEquals(Optional.of(exception1), res);
    }

    @Test
    void makeExceptionButExceptionAlreadyExists() {
        when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        when(exceptionRepo.saveAndFlush(exception1)).thenReturn(exception1);
        when(exceptionRepo.existsByOrder(order1)).thenReturn(true);
        var res = adminService.makeException(exception1, 2L);
        assertEquals(Optional.empty(), res);
    }

    @Test
    void makeExceptionButNotLinkedToOrder() {
        when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        when(exceptionRepo.saveAndFlush(exception1)).thenReturn(exception1);
        when(exceptionRepo.existsByOrder(order1)).thenReturn(false);
        var res = adminService.makeException(new DeliveryException(), 2L);
        assertEquals(Optional.empty(), res);
    }

    @Test
    void makeExceptionButPathIdDoesNotMatch() {
        when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        when(exceptionRepo.saveAndFlush(exception1)).thenReturn(exception1);
        when(exceptionRepo.existsByOrder(order1)).thenReturn(false);
        var res = adminService.makeException(exception1, 9L);
        assertEquals(Optional.empty(), res);
    }

    @Test
    void makeExceptionButNullInput() {
        when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        when(exceptionRepo.saveAndFlush(exception1)).thenReturn(exception1);
        when(exceptionRepo.existsByOrder(order1)).thenReturn(false);
        var res = adminService.makeException(null, 3L);
        assertEquals(Optional.empty(), res);
    }

    @Test
    void updateExceptionWorks() {
        when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        when(exceptionRepo.saveAndFlush(exception1)).thenReturn(exception1);
        when(exceptionRepo.existsByOrder(order1)).thenReturn(false);
        var updated = exception1.exceptionType(DeliveryException.ExceptionTypeEnum.OTHER);
        var res = adminService.updateException(updated, updated.getOrder().getId());

        verify(exceptionRepo).saveAndFlush(exception1.exceptionType(DeliveryException.ExceptionTypeEnum.OTHER));
        assertEquals(Optional.of(updated), res);
    }

    @Test
    void updateExceptionNotValid() {
        when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        when(exceptionRepo.saveAndFlush(exception1)).thenReturn(exception1);
        when(exceptionRepo.existsByOrder(order1)).thenReturn(false);
        var updated = exception1.exceptionType(DeliveryException.ExceptionTypeEnum.OTHER);
        var res = adminService.updateException(null, updated.getOrder().getId());

        assertEquals(Optional.empty(), res);
    }

    @Test
    void getAllExceptionsWorks() {
        when(exceptionRepo.findAll()).thenReturn(List.of(exception1, exception2));
        var res = adminService.getAllExceptions();
        assertEquals(List.of(exception1, exception2), res);
    }

    @Test
    void doesExceptionExistWorks() {
        when(exceptionRepo.existsById(anyLong())).thenReturn(true);
        var res = adminService.doesExceptionExist(exception1);
        assertTrue(res);
    }

    @Test
    void doesExceptionExistDoesNotExist() {
        when(exceptionRepo.existsById(anyLong())).thenReturn(false);
        var res = adminService.doesExceptionExist(exception1);
        assertFalse(res);
    }

    @Test
    void doesExceptionExistNullInput() {
        when(exceptionRepo.existsByOrder(any())).thenReturn(true);
        var res = adminService.doesExceptionExist(null);
        assertFalse(res);
    }

    @Test
    void doesExceptionExistNullOrder() {
        when(exceptionRepo.existsByOrder(any())).thenReturn(true);
        var res = adminService.doesExceptionExist(new DeliveryException());
        assertFalse(res);
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
        List<Vendor> vendors2 = new ArrayList<>();
        Vendor vendor2 = new Vendor().radius(5D).id(2L).location(new Location().latitude(22F).longitude(33F));
        vendors2.add(vendor2);
        Mockito.when(vendorRepo.findVendorsByHasCouriers(false)).thenReturn(vendors);

        Optional<List<Vendor>> res = adminService.updateDefaultRadius(5D);
        assertTrue(res.isPresent());
        assertEquals(res.get(), vendors2);
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
