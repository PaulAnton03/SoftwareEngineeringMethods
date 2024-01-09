package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.order.StatusService;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Time;
import nl.tudelft.sem.template.model.UpdateToGivenToCourierRequest;
import nl.tudelft.sem.template.model.UpdateToPreparingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import wiremock.org.apache.hc.core5.util.TimeValue;


public class StatusControllerTest {

    private StatusService statusService;

    private AuthorizationService authorizationService;

    private StatusController controller;


    @BeforeEach
    void setUp() {
        this.statusService = Mockito.mock(StatusService.class);
        this.authorizationService = Mockito.mock(AuthorizationService.class);
        this.controller = new StatusController(statusService, authorizationService);
    }

    @Test
    void updateStatusToAccepted200() {
        Mockito.when(authorizationService.authorize(1L, "updateToAccepted"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.getOrderStatus(11L)).thenReturn(
                Optional.of(Order.StatusEnum.PENDING));
        Mockito.when(statusService.updateStatusToAccepted(11L)).thenReturn(
                Optional.of(new Order().id(11L).status(Order.StatusEnum.ACCEPTED)));

        var res = controller.updateToAccepted(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateStatusToAccepted404() {
        Mockito.when(authorizationService.authorize(1L, "updateToAccepted"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.updateStatusToAccepted(anyLong())).thenReturn(Optional.empty());

        var res = controller.updateToAccepted(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateStatusToAccepted403() {
        Mockito.when(authorizationService.authorize(1L, "updateToAccepted"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        var res = controller.updateToAccepted(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void updateStatusToAccepted400() {
        Mockito.when(authorizationService.authorize(1L, "updateToAccepted"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.getOrderStatus(11L))
                .thenReturn(Optional.of(Order.StatusEnum.DELIVERED));
        var res = controller.updateToAccepted(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void getStatus404() {
        Mockito.when(authorizationService.authorize(1L, "getStatus"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.getOrderStatus(anyLong())).thenReturn(Optional.empty());
        var res = controller.getStatus(1L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void getStatus200() {
        Mockito.when(authorizationService.authorize(1L, "getStatus"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.getOrderStatus(1L)).thenReturn(Optional.of(Order.StatusEnum.ACCEPTED));
        var res = controller.getStatus(1L, 1L);
        assertEquals(new ResponseEntity<>("accepted", HttpStatus.OK), res);
    }

    @Test
    void getStatus403() {
        Mockito.when(authorizationService.authorize(1L, "getStatus"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        Mockito.when(statusService.getOrderStatus(11L)).thenReturn(Optional.of(Order.StatusEnum.ACCEPTED));
        var res = controller.getStatus(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void updateStatusToGivenToCourier200() {
        UpdateToGivenToCourierRequest req = new UpdateToGivenToCourierRequest();
        req.courierId(3L);

        Mockito.when(authorizationService.authorize(1L, "updateToGivenToCourier"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.getOrderStatus(2L)).thenReturn(
                Optional.of(Order.StatusEnum.PREPARING));
        Mockito.when(statusService.updateStatusToGivenToCourier(2L, req)).thenReturn(
                Optional.of(new Order().id(2L).status(Order.StatusEnum.GIVEN_TO_COURIER)));

        var res = controller.updateToGivenToCourier(2L, 1L, req);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateStatusToGivenToCourier404() {
        Mockito.when(authorizationService.authorize(1L, "updateToGivenToCourier"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.updateStatusToGivenToCourier(anyLong(), any())).thenReturn(Optional.empty());

        UpdateToGivenToCourierRequest req = new UpdateToGivenToCourierRequest();
        req.courierId(3L);
        var res = controller.updateToGivenToCourier(2L, 1L, req);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateStatusToGivenToCourier403() {
        Mockito.when(authorizationService.authorize(1L, "updateToGivenToCourier"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        Mockito.when(statusService.updateStatusToGivenToCourier(anyLong(), any())).thenReturn(Optional.empty());

        UpdateToGivenToCourierRequest req = new UpdateToGivenToCourierRequest();
        req.courierId(3L);
        var res = controller.updateToGivenToCourier(2L, 1L, req);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void updateStatusToGivenToCourier400() {
        UpdateToGivenToCourierRequest req = new UpdateToGivenToCourierRequest();
        req.courierId(3L);

        Mockito.when(authorizationService.authorize(1L, "updateToGivenToCourier"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.getOrderStatus(11L))
                .thenReturn(Optional.of(Order.StatusEnum.DELIVERED));

        var res = controller.updateToGivenToCourier(11L, 1L, req);
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }


    @Test
    void updateStatusToInTransit200() {
        Mockito.when(authorizationService.authorize(1L, "updateToInTransit"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.getOrderStatus(11L)).thenReturn(
                Optional.of(Order.StatusEnum.GIVEN_TO_COURIER));
        Mockito.when(statusService.updateStatusToInTransit(11L)).thenReturn(
                Optional.of(new Order().id(11L).status(Order.StatusEnum.IN_TRANSIT)));

        var res = controller.updateToInTransit(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateStatusToInTransit404() {
        Mockito.when(authorizationService.authorize(1L, "updateToInTransit"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.updateStatusToInTransit(anyLong())).thenReturn(Optional.empty());

        var res = controller.updateToInTransit(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateStatusToInTransit403() {
        Mockito.when(authorizationService.authorize(1L, "updateToInTransit"))
                .thenReturn(Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN)));
        Mockito.when(statusService.updateStatusToInTransit(anyLong())).thenReturn(Optional.empty());

        var res = controller.updateToInTransit(2L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), res);
    }

    @Test
    void updateStatusToInTransit400() {
        Mockito.when(authorizationService.authorize(1L, "updateToInTransit"))
                .thenReturn(Optional.empty());
        Mockito.when(statusService.getOrderStatus(11L)).thenReturn(
                Optional.of(Order.StatusEnum.DELIVERED));

        var res = controller.updateToInTransit(11L, 1L);
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), res);
    }

    @Test
    void updateToPreparingWorks200() {
        OffsetDateTime time1 = OffsetDateTime.of(2023, 01, 01,
                12, 30, 00, 0, ZoneOffset.ofHours(2));
        OffsetDateTime time2 = OffsetDateTime.of(2024, 01, 01,
                14, 30, 00, 0, ZoneOffset.ofHours(2));
        UpdateToPreparingRequest req = new UpdateToPreparingRequest().expectedDeliveryTime(time2).prepTime("02:30:00");
        Time tv = new Time().prepTime("01:30:00").expectedDeliveryTime(time1);
        Time tv2 = new Time().prepTime("02:30:00").expectedDeliveryTime(time2);
        Order order1 = new Order().id(11L).status(Order.StatusEnum.PREPARING).timeValues(tv);
        Order order2 = new Order().id(11L).status(Order.StatusEnum.ACCEPTED).timeValues(tv2);

        Mockito.when(statusService.getOrderStatus(11L)).thenReturn(
                Optional.of(Order.StatusEnum.ACCEPTED));
        Mockito.when(statusService.updateStatusToPreparing(11L, req)).thenReturn(
                Optional.of(order2));

        var res = controller.updateToPreparing(11L, 1L, req);
        assertEquals(new ResponseEntity<>(HttpStatus.OK), res);
    }

    @Test
    void updateToPreparingGives404() {
        OffsetDateTime time2 = OffsetDateTime.of(2024, 01, 01,
                14, 30, 00, 0, ZoneOffset.ofHours(2));
        UpdateToPreparingRequest req = new UpdateToPreparingRequest().expectedDeliveryTime(time2).prepTime("02:30:00");

        Mockito.when(statusService.getOrderStatus(anyLong())).thenReturn(
                Optional.empty());

        var res = controller.updateToPreparing(11L, 1L, req);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

    @Test
    void updateToPreparingGives404Variant() {
        OffsetDateTime time2 = OffsetDateTime.of(2024, 01, 01,
                14, 30, 00, 0, ZoneOffset.ofHours(2));
        UpdateToPreparingRequest req = new UpdateToPreparingRequest().expectedDeliveryTime(time2).prepTime("02:30:00");

        Mockito.when(statusService.getOrderStatus(anyLong())).thenReturn(
                Optional.of(Order.StatusEnum.ACCEPTED));
        Mockito.when(statusService.updateStatusToPreparing(11L, req)).thenReturn(
                Optional.empty());

        var res = controller.updateToPreparing(11L, 1L, req);
        assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND), res);
    }

}
