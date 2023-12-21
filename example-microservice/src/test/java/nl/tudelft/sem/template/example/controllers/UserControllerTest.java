package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.user.UserService;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserControllerTest {

    private UserService userService;

    private AuthorizationService authorizationService;

    private UserController controller;

    @BeforeEach
    void setUp() {
        this.userService = Mockito.mock(UserService.class);
        this.authorizationService = Mockito.mock(AuthorizationService.class);
        Mockito.when(authorizationService.authorize(anyLong(), Mockito.anyString())).thenReturn(Optional.empty());
        this.controller = new UserController(userService, authorizationService);
    }

    @Test
    void makeVendor200() {
        Vendor vendor = new Vendor().id(1L);
        Optional<Vendor> proper = Optional.of(vendor);

        Mockito.when(userService.makeVendor(any())).thenReturn(proper);

        var res = controller.makeVendor(2L, vendor);
    void updateBossOfCourier200() {
        Mockito.when(authorizationService.authorize(1L, "updateBossOfCourier"))
                .thenReturn(Optional.empty());
        Mockito.when(userService.updateBossIdOfCourier(100L, 6L)).thenReturn(
                Optional.of(6L));

        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void makeVendor404() {
        Mockito.when(userService.makeVendor(any())).thenReturn(Optional.empty());

        var res = controller.makeVendor(2L, new Vendor().id(1L));
    void updateBossOfCourier404() {
        Mockito.when(authorizationService.authorize(1L, "updateBossOfCourier"))
                .thenReturn(Optional.empty());
        Mockito.when(userService.updateBossIdOfCourier(100L, 6L)).thenReturn(
                Optional.empty());

        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void makeVendorById200() {
        Vendor vendor = new Vendor().id(1L);
        Optional<Vendor> proper = Optional.of(vendor);

        Mockito.when(userService.makeVendorById(anyLong())).thenReturn(proper);

        var res = controller.makeVendorById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void makeVendorById404() {
        Mockito.when(userService.makeVendorById(anyLong())).thenReturn(Optional.empty());

        var res = controller.makeVendorById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }
    void updateBossOfCourier500() {
        Mockito.when(authorizationService.authorize(1L, "updateBossOfCourier"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), res);
    }

    @Test
    void updateBossOfCourier403() {
        Mockito.when(authorizationService.authorize(1L, "updateBossOfCourier"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }


}
