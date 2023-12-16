package nl.tudelft.sem.template.example.controllers;

import java.util.Optional;
import nl.tudelft.sem.template.api.StatusApi;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.order.StatusService;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.UpdateToGivenToCourierRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/status")
public class StatusController implements StatusApi {

    public StatusService statusService;

    public AuthorizationService authorizationService;

    public StatusController(StatusService statusService, AuthorizationService authorizationService) {
        this.statusService = statusService;
        this.authorizationService = authorizationService;
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

        Optional<Order.StatusEnum> currentStatus = statusService.getOrderStatus(orderId);

        if (currentStatus.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (currentStatus.get() != Order.StatusEnum.PENDING) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Order> order = statusService.updateStatusToAccepted(orderId);

        if (order.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Handles put request for (/status/{orderId}/giventocourier).
     *
     * @param orderId id of the order to update its status to given_to_courier (required)
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @return a response entity with nothing, 404 if not found  403 if not authorized, only for vendors
     */
    @Override
    public ResponseEntity<Void> updateToGivenToCourier(Long orderId, Long authorization,
                                                       UpdateToGivenToCourierRequest updateToGivenToCourierRequest) {

        // TODO: authentication

        Optional<Order.StatusEnum> currentStatus = statusService.getOrderStatus(orderId);

        if (currentStatus.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (currentStatus.get() != Order.StatusEnum.PREPARING) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Order> order = statusService.updateStatusToGivenToCourier(orderId, updateToGivenToCourierRequest);

        if (order.isEmpty()) {
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

        Optional<Order.StatusEnum> currentStatus = statusService.getOrderStatus(orderId);

        if (currentStatus.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (currentStatus.get() != Order.StatusEnum.GIVEN_TO_COURIER) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Order> order = statusService.updateStatusToInTransit(orderId);

        if (order.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Handles get request for (/status/{orderId}).
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param orderId       id of the order to update its status to accepted (required)
     * @return a response entity with a String, 200 if found, 404 if not found,  403 if not authorized
     */

    @Override
    public ResponseEntity<String> getStatus(Long orderId, Long authorization) {

        // TODO: authentication

        Optional<Order.StatusEnum> currentStatus = statusService.getOrderStatus(orderId);

        return currentStatus.map(statusEnum -> new ResponseEntity<>(statusEnum.toString(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

}
