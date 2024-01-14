package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.admin.AdminService;
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


    @BeforeEach
    void setUp() {
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

}
