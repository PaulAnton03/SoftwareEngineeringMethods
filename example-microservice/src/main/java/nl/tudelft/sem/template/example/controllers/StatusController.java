package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.api.StatusApi;
import nl.tudelft.sem.template.example.domain.order.StatusService;
import nl.tudelft.sem.template.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/status")
public class StatusController implements StatusApi {

    public StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    /**
     * Handles put request for (/status/{orderId}/accepted).
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param orderId id of the order to update its status to accepted (required)
     * @return a response entity with nothing, 404 if not found  403 if not authorized, only for vendors
     */
    @Override
    public ResponseEntity<Void> updateToAccepted(Long orderId, Long authorization) {

        // TODO: authentication

        // TODO: once OrderService is merged add check if order status before change is equal to PENDING,
        //  if not throw 400 ?

        Optional<Order> order = statusService.updateStatusToAccepted(orderId);

        if(order.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Handles put request for (/status/{orderId}/intransit).
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param orderId id of the order to update its status to in_transit (required)
     * @return a response entity with nothing, 404 if not found  403 if not authorized, only for couriers
     */
    @Override
    public ResponseEntity<Void> updateToInTransit(Long orderId, Long authorization) {

        // TODO: authentication

        // TODO: once OrderService is merged add check if order status before change is equal to GIVEN_TO_COURIER,
        //  if not throw 400 ?

        Optional<Order> order = statusService.updateStatusToInTransit(orderId);

        if(order.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
