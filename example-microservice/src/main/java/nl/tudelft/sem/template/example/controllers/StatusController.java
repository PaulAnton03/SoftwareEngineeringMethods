package nl.tudelft.sem.template.example.controllers;

import java.util.Optional;
import nl.tudelft.sem.template.api.StatusApi;
import nl.tudelft.sem.template.example.domain.order.StatusService;
import nl.tudelft.sem.template.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
