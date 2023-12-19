package nl.tudelft.sem.template.example.controllers;

import java.util.Optional;
import nl.tudelft.sem.template.api.OrderApi;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/order")
public class OrderController implements OrderApi {

    public OrderService orderService;

    public AuthorizationService authorizationService;

    public OrderController(OrderService orderService, AuthorizationService authorizationService) {
        this.orderService = orderService;
        this.authorizationService = authorizationService;
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
    @GetMapping("/{orderId}/final-destination")
    public ResponseEntity getFinalDestination(
        @RequestParam(name = "authorization") Long authorization,
        @PathVariable(name = "orderId") Long orderId
    ) {
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
     * GET /order/{orderId} : Retrieve order based on orderId.
     * Return the Order object.
     *
     * @param orderId       (required)
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @return Successful response, order object received (status code 200)
     *         or Unsuccessful, order cannot be retrieved because of bad request (status code 400)
     *         or Unsuccessful, entity does not have access rights to retrieve order (status code 403)
     *         or Unsuccessful, no order was found (status code 404)
     */
    @Override
    public ResponseEntity<Order> getOrder(Long orderId, Long authorization) {
        Optional<ResponseEntity> authorizationResponse =
                authorizationService.authorize(authorization, "getOrder");
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }

        Optional<Order> o = orderService.getOrderById(orderId);
        if (o.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(o.get(), HttpStatus.OK);

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
    @GetMapping("/{orderId}/pickup-destination")
    public ResponseEntity getPickupDestination(
        @RequestParam(name = "authorization") Long authorization,
        @PathVariable(name = "orderId") Long orderId) {
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

    /**
     * PUT /order/{orderId} : Retrieve order based on orderId.
     * Return the status of the update.
     *
     * @param orderId       (required)
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param order The updated object of the order
     * @return Successful response, order updated (status code 200)
     *         or Unsuccessful, order cannot be updated because of bad request (status code 400)
     *         or Unsuccessful, entity does not have access rights to update order (status code 403)
     *         or Unsuccessful, no order was found" (status code 404)
     */
    @Override
    public ResponseEntity<Void> updateOrder(Long orderId, Long authorization, Order order) {
        Optional<ResponseEntity> authorizationResponse =
                authorizationService.authorize(authorization, "updateOrder");
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }

        Optional<Order> response = orderService.updateOrderById(orderId, order);

        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
