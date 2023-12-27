package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.user.UserService;
import nl.tudelft.sem.template.model.Courier;
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


public class UserControllerTest {

    private UserService userService;
    private AuthorizationService authorizationService;
    private UserController controller;

    @BeforeEach
    void setUp() {
        this.userService = Mockito.mock(UserService.class);
        this.authorizationService = Mockito.mock(AuthorizationService.class);
        this.controller = new UserController(userService, authorizationService);
    }

    @Test
    void updateBossOfCourier200() {
        Mockito.when(authorizationService.authorize(1L, "updateBossOfCourier"))
                .thenReturn(Optional.empty());
        Mockito.when(userService.updateBossIdOfCourier(100L, 6L)).thenReturn(
                Optional.of(6L));

        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateBossOfCourier404() {
        Mockito.when(authorizationService.authorize(1L, "updateBossOfCourier"))
                .thenReturn(Optional.empty());
        Mockito.when(userService.updateBossIdOfCourier(100L, 6L)).thenReturn(
                Optional.empty());

        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
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

    @Test
    void makeVendorById200() {
        Vendor vendor = new Vendor().id(1L);
        Optional<Vendor> proper = Optional.of(vendor);

        Mockito.when(userService.makeVendorById(anyLong())).thenReturn(proper);
        Mockito.when(authorizationService.authorize(1L, "makeVendorById"))
                .thenReturn(Optional.empty());

        var res = controller.makeVendorById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void makeVendorById400() {
        Mockito.when(userService.existsVendor(anyLong())).thenReturn(true);
        Mockito.when(authorizationService.authorize(2L, "makeVendorById"))
                .thenReturn(Optional.empty());

        var res = controller.makeVendorById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void makeVendorById403() {
        Mockito.when(authorizationService.authorize(1L, "makeVendorById"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.makeVendorById(1L, 10L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void makeVendorById404() {
        Mockito.when(userService.makeVendorById(anyLong())).thenReturn(Optional.empty());
        Mockito.when(authorizationService.authorize(1L, "makeVendorById"))
                .thenReturn(Optional.empty());

        var res = controller.makeVendorById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void makeVendor200() {
        Vendor vendor = new Vendor().id(1L);
        Optional<Vendor> proper = Optional.of(vendor);

        Mockito.when(userService.makeVendor(any())).thenReturn(proper);
        Mockito.when(authorizationService.authorize(1L, "makeVendor"))
                .thenReturn(Optional.empty());
        var res = controller.makeVendor(2L, vendor);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void makeVendor400() {
        Mockito.when(userService.existsVendor(anyLong())).thenReturn(true);
        Mockito.when(authorizationService.authorize(2L, "makeVendor"))
                .thenReturn(Optional.empty());

        var res = controller.makeVendor(2L, new Vendor().id(3L));
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void makeVendor403() {
        Mockito.when(authorizationService.authorize(1L, "makeVendor"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.makeVendor(1L, new Vendor());
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void makeVendor404() {
        Mockito.when(userService.makeVendor(any())).thenReturn(Optional.empty());
        Mockito.when(authorizationService.authorize(1L, "makeVendor"))
                .thenReturn(Optional.empty());
        var res = controller.makeVendor(2L, new Vendor().id(1L));
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void makeCourierById200() {
        Courier courier = new Courier().id(1L);
        Optional<Courier> proper = Optional.of(courier);

        Mockito.when(userService.makeCourierById(anyLong())).thenReturn(proper);
        Mockito.when(authorizationService.authorize(1L, "makeCourierById"))
                .thenReturn(Optional.empty());

        var res = controller.makeCourierById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void makeCourierById400() {
        Mockito.when(userService.existsCourier(anyLong())).thenReturn(true);
        Mockito.when(authorizationService.authorize(2L, "makeCourierById"))
                .thenReturn(Optional.empty());

        var res = controller.makeCourierById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void makeCourierById403() {
        Mockito.when(authorizationService.authorize(1L, "makeCourierById"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.makeCourierById(1L, 10L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void makeCourierById404() {
        Mockito.when(userService.makeCourierById(anyLong())).thenReturn(Optional.empty());
        Mockito.when(authorizationService.authorize(1L, "makeCourierById"))
                .thenReturn(Optional.empty());

        var res = controller.makeCourierById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void makeCourier200() {
        Courier courier = new Courier().id(1L);
        Optional<Courier> proper = Optional.of(courier);

        Mockito.when(userService.makeCourier(any())).thenReturn(proper);
        Mockito.when(authorizationService.authorize(1L, "makeCourier"))
                .thenReturn(Optional.empty());
        var res = controller.makeCourier(2L, courier);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void makeCourier400() {
        Mockito.when(userService.existsCourier(anyLong())).thenReturn(true);
        Mockito.when(authorizationService.authorize(2L, "makeCourier"))
                .thenReturn(Optional.empty());

        var res = controller.makeCourier(2L, new Courier().id(3L));
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void makeCourier403() {
        Mockito.when(authorizationService.authorize(1L, "makeCourier"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.makeCourier(1L, new Courier());
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void makeCourier404() {
        Mockito.when(userService.makeCourier(any())).thenReturn(Optional.empty());
        Mockito.when(authorizationService.authorize(1L, "makeCourier"))
                .thenReturn(Optional.empty());
        var res = controller.makeCourier(2L, new Courier().id(1L));
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

}
