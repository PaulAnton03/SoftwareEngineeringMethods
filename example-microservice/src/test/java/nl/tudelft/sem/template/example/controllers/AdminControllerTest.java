package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.admin.AdminService;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

class AdminControllerTest {

    private AdminService adminService;
    private AdminController controller;

    private AuthorizationService authorizationService;


    @BeforeEach
    void setUp() {
        this.adminService = Mockito.mock(AdminService.class);
        this.authorizationService = Mockito.mock(AuthorizationService.class);
        Mockito.when(authorizationService.authorize(anyLong(), Mockito.anyString())).thenReturn(Optional.empty());
        this.controller = new AdminController(adminService, authorizationService);
    }

    @Test
    void updateDefaultRadius404(){

    }

    @Test
    void updateDefaultRadius200(){
        List<Vendor> listVendors = new ArrayList<>();
        Mockito.when(adminService.updateDefaultRadius(5D)).thenReturn(Optional.of(listVendors));
        var res = controller.updateDefaultRadius(1L, 5D);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

}
