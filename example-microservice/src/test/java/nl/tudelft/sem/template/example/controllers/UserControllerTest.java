package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        this.controller = new UserController(userService, authorizationService);
    }

    @Test
    void updateBossOfCourier200() {
        Mockito.when(authorizationService.authorize(1L, "updateBossOfCourier", 6L))
                .thenReturn(Optional.empty());
        Mockito.when(userService.updateBossIdOfCourier(100L, 6L)).thenReturn(
                Optional.of(6L));

        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateBossOfCourier404() {
        Mockito.when(authorizationService.authorize(1L, "updateBossOfCourier", 6L))
                .thenReturn(Optional.empty());
        Mockito.when(userService.updateBossIdOfCourier(100L, 6L)).thenReturn(
                Optional.empty());

        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateBossOfCourier500() {
        Mockito.when(authorizationService.authorize(1L, "updateBossOfCourier", 6L))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), res);
    }

    @Test
    void updateBossOfCourier403() {
        Mockito.when(authorizationService.authorize(1L, "updateBossOfCourier", 6L))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.updateBossOfCourier(100L, 6L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }


}
