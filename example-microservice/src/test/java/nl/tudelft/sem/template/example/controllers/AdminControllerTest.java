package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.admin.AdminService;
import nl.tudelft.sem.template.model.DeliveryException;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AdminControllerTest {

    private AdminService adminService;
    private AdminController controller;
    private AuthorizationService authorizationService;

    private DeliveryException exception1;

    private Order order1;


    @BeforeEach
    void setUp() {
        this.order1 = new Order().status(Order.StatusEnum.DELIVERED).id(3L);
        this.exception1 = new DeliveryException().id(2L)
            .exceptionType(DeliveryException.ExceptionTypeEnum.LATEDELIVERY)
            .isResolved(false)
            .order(order1);
        this.adminService = Mockito.mock(AdminService.class);
        this.authorizationService = Mockito.mock(AuthorizationService.class);
        this.controller = new AdminController(adminService, authorizationService);
    }

    @Test
    void updateDefaultRadius404() {
        Mockito.when(adminService.updateDefaultRadius(5D)).thenReturn(Optional.empty());
        var res = controller.updateDefaultRadius(1L, 5D);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateDefaultRadius200() {
        List<Vendor> listVendors = new ArrayList<>();
        listVendors.add(new Vendor());
        Mockito.when(adminService.updateDefaultRadius(5D)).thenReturn(Optional.of(listVendors));
        var res = controller.updateDefaultRadius(1L, 5D);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void getDefaultRadius200() {
        Mockito.when(adminService.getDefaultRadius()).thenReturn(Optional.of(5D));
        var res = controller.getDefaultRadius(1L);
        assertEquals(new ResponseEntity<>(5D, HttpStatus.OK), res);
    }

    @Test
    void getDefaultRadius404() {
        Mockito.when(adminService.getDefaultRadius()).thenReturn(Optional.empty());
        var res = controller.getDefaultRadius(1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void getException200() {
        Mockito.when(adminService.getExceptionByOrder(anyLong())).thenReturn(Optional.of(exception1));
        var res = controller.getExceptionForOrder(1L, 5L);
        assertEquals(new ResponseEntity<>(exception1, HttpStatus.OK), res);
    }

    @Test
    void getException404() {
        Mockito.when(adminService.getExceptionByOrder(anyLong())).thenReturn(Optional.empty());
        var res = controller.getExceptionForOrder(1L, 5L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

}
