package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Optional;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.user.UserService;
import nl.tudelft.sem.template.model.Courier;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Mockito.when(userService.updateBossIdOfCourier(100L, 6L)).thenReturn(
            Optional.of(6L));

        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateBossOfCourier404() {
        Mockito.when(userService.updateBossIdOfCourier(100L, 6L)).thenReturn(
            Optional.empty());

        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateBossOfCourier500() {
        Mockito.when(authorizationService.checkIfUserIsAuthorized(1L, "updateBossOfCourier", 6L))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), res);
    }

    @Test
    void updateBossOfCourier403() {
        Mockito.when(authorizationService.checkIfUserIsAuthorized(1L, "updateBossOfCourier", 6L))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
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
    void makeVendorById400() {
        Mockito.when(userService.existsVendor(anyLong())).thenReturn(true);

        var res = controller.makeVendorById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void makeVendorById403() {
        Mockito.when(authorizationService.authorizeAdminOnly(1L))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.makeVendorById(1L, 10L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void makeVendorById404() {
        Mockito.when(userService.makeVendorById(anyLong())).thenReturn(Optional.empty());

        var res = controller.makeVendorById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void makeVendor200() {
        Vendor vendor = new Vendor().id(1L);
        Optional<Vendor> proper = Optional.of(vendor);

        Mockito.when(userService.makeVendor(any())).thenReturn(proper);
        var res = controller.makeVendor(2L, vendor);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void makeVendor400() {
        Mockito.when(userService.existsVendor(anyLong())).thenReturn(true);

        var res = controller.makeVendor(2L, new Vendor().id(3L));
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void makeVendor403() {
        Mockito.when(authorizationService.authorizeAdminOnly(1L))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.makeVendor(1L, new Vendor());
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void makeVendor404() {
        Mockito.when(userService.makeVendor(any())).thenReturn(Optional.empty());
        var res = controller.makeVendor(2L, new Vendor().id(1L));
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void makeCourierById200() {
        Courier courier = new Courier().id(1L);
        Optional<Courier> proper = Optional.of(courier);

        Mockito.when(userService.makeCourierById(anyLong())).thenReturn(proper);

        var res = controller.makeCourierById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void makeCourierById400() {
        Mockito.when(userService.existsCourier(anyLong())).thenReturn(true);
        var res = controller.makeCourierById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void makeCourierById403() {
        Mockito.when(authorizationService.authorizeAdminOnly(1L))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.makeCourierById(1L, 10L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void makeCourierById404() {
        Mockito.when(userService.makeCourierById(anyLong())).thenReturn(Optional.empty());

        var res = controller.makeCourierById(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void makeCourier200() {
        Courier courier = new Courier().id(1L);
        Optional<Courier> proper = Optional.of(courier);

        Mockito.when(userService.makeCourier(any())).thenReturn(proper);
        var res = controller.makeCourier(2L, courier);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void makeCourier400() {
        Mockito.when(userService.existsCourier(anyLong())).thenReturn(true);

        var res = controller.makeCourier(2L, new Courier().id(3L));
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void makeCourier403() {
        Mockito.when(authorizationService.authorizeAdminOnly(1L))
            .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.makeCourier(1L, new Courier());
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void makeCourier404() {
        Mockito.when(userService.makeCourier(any())).thenReturn(Optional.empty());
        var res = controller.makeCourier(2L, new Courier().id(1L));
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void getSpecificRadiusWorks200() {
        Vendor vendor = new Vendor().id(1L).radius(3.0);
        Mockito.when(userService.getRadiusOfVendor(vendor.getId()))
                .thenReturn(Optional.of(vendor.getRadius()));

        var res = controller.getSpecificRadius(1L);
        assertEquals(new ResponseEntity<>(vendor.getRadius(), HttpStatus.OK), res);
    }

    @Test
    void getSpecificRadius403() {
        Mockito.when(authorizationService.authorizeAdminOnly(1L))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));

        var res = controller.getSpecificRadius(1L);
    }

    @Test
    void getCourier200() {
        Courier courier = new Courier().id(1L);
        Optional<Courier> proper = Optional.of(courier);

        Mockito.when(userService.getCourier(1L)).thenReturn(proper);
        var res = controller.getCourier(1L, 2L);
        assertEquals(new ResponseEntity<>(courier, HttpStatus.OK), res);
    }

    @Test
    void getCourier403() {
        Mockito.when(authorizationService.authorizeAdminOnly(1L))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.getCourier(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void getSpecificRadius404() {
        Mockito.when(authorizationService.authorizeAdminOnly(1L))
                .thenReturn(Optional.empty());
        Mockito.when(userService.getRadiusOfVendor(anyLong()))
                .thenReturn(Optional.empty());

        var res = controller.getSpecificRadius(1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateSpecificRadiusWorks200() {
        Vendor vendor = new Vendor().id(1L).radius(3.0);
        Mockito.when(authorizationService.authorizeAdminOnly(1L))
                .thenReturn(Optional.empty());
        Mockito.when(userService.getVendor(vendor.getId())).thenReturn(Optional.of(vendor));
        Mockito.when(userService.updateRadiusOfVendor(vendor.getId(), 5.0))
                .thenReturn(Optional.of(5.0));

        var res = controller.updateSpecificRadius(1L, 5.0);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateSpecificRadius404() {
        Vendor vendor = new Vendor().id(1L).radius(3.0);
        Mockito.when(authorizationService.authorizeAdminOnly(1L))
                .thenReturn(Optional.empty());
        Mockito.when(userService.getVendor(vendor.getId())).thenReturn(Optional.of(vendor));
        Mockito.when(userService.updateRadiusOfVendor(vendor.getId(), 5.0))
                .thenReturn(Optional.empty());

        var res = controller.updateSpecificRadius(1L, 5.0);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateSpecificRadius403() {
        Vendor vendor = new Vendor().id(1L).radius(3.0);
        Mockito.when(authorizationService.authorizeAdminOnly(1L))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));

        var res = controller.updateSpecificRadius(1L, 5.0);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void getCourier404() {
        Mockito.when(userService.getCourier(1L)).thenReturn(Optional.empty());
        var res = controller.getCourier(1L, 2L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

}
