package nl.tudelft.sem.template.example.controllers;

import java.util.Optional;
import javax.validation.Valid;
import nl.tudelft.sem.template.api.StatusApi;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.example.domain.order.StatusService;
import nl.tudelft.sem.template.model.DeliveryException;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.UpdateToDeliveredRequest;
import nl.tudelft.sem.template.model.UpdateToGivenToCourierRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/status")
public class StatusController implements StatusApi {

    private final AuthorizationService authorizationService;
    public StatusService statusService;
    public OrderService orderService;

    public StatusController(StatusService statusService, OrderService orderService,
                            AuthorizationService authorizationService) {
        this.statusService = statusService;
        this.orderService = orderService;
        this.authorizationService = authorizationService;
    }

    /**
     * Handles put request for (/status/{orderId}/accepted).
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param orderId       id of the order to update its status to accepted (required)
     * @return a response entity with nothing,
     * 400 if previous status doesn't match method,
     * 404 if not found,
     * 403 if not authorized,
     * 500 if server error,
     * only for vendors
     */
    @Override
    public ResponseEntity<Void> updateToAccepted(Long orderId, Long authorization) {

        var auth = authorizationService.authorize(authorization, "updateToAccepted");
        if (auth.isPresent()) {
            return auth.get();
        }

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
     * Handles put request for (/status/{orderId}/rejected).
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param orderId       id of the order to update its status to accepted (required)
     * @return a response entity with nothing, 404 if not found  403 if not authorized, only for vendors
     */
    @Override
    @PutMapping("/{orderId}/rejected")
    public ResponseEntity<Void> updateToRejected(
        @RequestParam(name = "authorization") Long authorization,
        @PathVariable(name = "orderId") Long orderId
    ) {

        var auth = authorizationService.authorize(authorization, "updateToRejected");
        if (auth.isPresent()) {
            return auth.get();
        }

        Optional<Order.StatusEnum> currentStatus = statusService.getOrderStatus(orderId);

        if (currentStatus.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (currentStatus.get() != Order.StatusEnum.PENDING) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Order> order = statusService.updateStatusToRejected(orderId);
        if (order.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        DeliveryException e = new DeliveryException().isResolved(false)
            .exceptionType(DeliveryException.ExceptionTypeEnum.REJECTED)
            .orderId(orderId).message("Order was rejected by the vendor");

        statusService.addDeliveryException(e);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Handles put request for (/status/{orderId}/giventocourier).
     *
     * @param orderId       id of the order to update its status to given_to_courier (required)
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @return a response entity with nothing,
     * 400 if previous status doesn't match method,
     * 404 if not found,
     * 403 if not authorized,
     * 500 if server error,
     * only for vendors
     */
    @Override
    public ResponseEntity<Void> updateToGivenToCourier(Long orderId, Long authorization,
                                                       UpdateToGivenToCourierRequest updateToGivenToCourierRequest) {

        var auth = authorizationService.authorize(authorization, "updateToGivenToCourier");
        if (auth.isPresent()) {
            return auth.get();
        }

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
     * @param orderId       id of the order to update its status to in_transit (required)
     * @return a response entity with nothing,
     * 400 if previous status doesn't match method,
     * 404 if not found,
     * 403 if not authorized,
     * 500 if server error,
     * only for couriers
     */
    @Override
    public ResponseEntity<Void> updateToInTransit(Long orderId, Long authorization) {

        var auth = authorizationService.authorize(authorization, "updateToInTransit");
        if (auth.isPresent()) {
            return auth.get();
        }

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
     * PUT for /status/{orderId}/delivered.
     *
     * @param orderId                  Id of the order to change status (required)
     * @param authorization            the UserId to check if they have the rights to make this request (required)
     * @param updateToDeliveredRequest Request body for status change from in-transit to delivered (required)
     * @return a response entity with nothing,
     * 400 if previous status doesn't match method or the given variables are wrong,
     * 404 if not found,
     * 403 if not authorized,
     * 500 if server error,
     * only for couriers
     */
    @Override
    @PutMapping("/{orderId}/delivered")
    public ResponseEntity<Void> updateToDelivered(@PathVariable("orderId") Long orderId,
                                                  @RequestParam(value = "authorization", required = true) Long authorization,
                                                  @Valid @RequestBody UpdateToDeliveredRequest updateToDeliveredRequest) {
        var auth = authorizationService.authorize(authorization, "updateToDelivered");
        if (auth.isPresent()) {
            return auth.get();
        }

        if (!orderService.orderExists(orderId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Order> updatedTime = statusService.updateStatusToDelivered(orderId, updateToDeliveredRequest);

        if (updatedTime.isEmpty()) {
            // something went wrong with the specific fields and the logic inside the service method :(
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Handles get request for (/status/{orderId}).
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param orderId       id of the order to update its status to accepted (required)
     * @return a response entity with a status String,
     * 404 if not found,
     * 403 if not authorized,
     * 500 if server error,
     * for customers, vendors, couriers
     */

    @Override
    @GetMapping("/{orderId}")
    public ResponseEntity<String> getStatus(
        @RequestParam(name = "authorization") Long authorization,
        @PathVariable(name = "orderId") Long orderId
    ) {

        var auth = authorizationService.authorize(authorization, "getStatus");
        if (auth.isPresent()) {
            return auth.get();
        }

        Optional<Order.StatusEnum> currentStatus = statusService.getOrderStatus(orderId);

        return currentStatus.map(statusEnum -> new ResponseEntity<>(statusEnum.toString(), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

}
