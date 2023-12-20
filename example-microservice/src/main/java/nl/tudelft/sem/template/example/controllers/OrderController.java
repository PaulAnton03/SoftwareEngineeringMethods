package nl.tudelft.sem.template.example.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.api.OrderApi;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/order")
public class OrderController implements OrderApi {

    public OrderService orderService;

    public AuthorizationService authorizationService;

    @Autowired
    public OrderController(OrderService orderService, AuthorizationService authorizationService) {
        this.orderService = orderService;
        this.authorizationService = authorizationService;
    }

    @Override
    public ResponseEntity<Order> getNextOrderForVendor(Long vendorId, Long authorization) {
        // todo properly implement with setter fro strategy and stuff
        return OrderApi.super.getNextOrderForVendor(vendorId, authorization);
    }

    /**
     * GET /order/{orderId}/final-destination : Get the final destination of a specific order
     * Retrieve the final destination of an order.
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param orderId       Id of the order to get its final destination (required)
     * @return Successful response, order found and final destination can be retrieved (status code 200)
     *         or Unsuccessful, entity does not have access rights to retrieve final destination (status code 403)
     *         or Unsuccessful, order not found by id (status code 404)
     */
    @Override
    public ResponseEntity getFinalDestination(Long authorization, Long orderId) {
        Optional<ResponseEntity> authorizationResponse =
            authorizationService.authorize(authorization, "getFinalDestination");
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }
        Optional<Location> location = orderService.getFinalDestinationOfOrder(orderId);

        if (location.isEmpty()) {
            // 404 not found if not found :(
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(location.get(), HttpStatus.OK);

    }

    /**
     * GET /order/{orderId}/pickup-destination : Retrieve vendor location of the order
     * Return the vendor location of the specified order.
     *
     * @param orderId       (required)
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @return Successful response, vendor location of the order received (status code 200)
     *         or Unsuccessful, vendor location of the order cannot be retrieved because of a bad request (status code 400)
     *         or Unsuccessful, entity does not have access rights to retrieve vendor location (status code 403)
     *         or Unsuccessful, vendor location for the order not found (status code 404)
     */
    @Override
    public ResponseEntity getPickupDestination(Long orderId, Long authorization) {
        Optional<ResponseEntity> authorizationResponse =
            authorizationService.authorize(authorization, "getPickupDestination");
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }
        Optional<Location> pickup = orderService.getPickupDestination(orderId);

        if (pickup.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(pickup.get(), HttpStatus.OK);
    }
}
