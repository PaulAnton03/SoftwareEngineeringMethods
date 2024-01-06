package nl.tudelft.sem.template.example.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import nl.tudelft.sem.template.api.OrderApi;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * or Unsuccessful, entity does not have access rights to retrieve final destination (status code 403)
     * or Unsuccessful, order not found by id (status code 404)
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
     * or Unsuccessful, order cannot be retrieved because of bad request (status code 400)
     * or Unsuccessful, entity does not have access rights to retrieve order (status code 403)
     * or Unsuccessful, no order was found (status code 404)
     */
    @Override
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
        @RequestParam(name = "authorization") Long authorization,
        @PathVariable(name = "orderId") Long orderId
    ) {
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
     * GET /order : Retrieve all orders.
     * Return list of all Order objects.
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @return Successful response, order objects received (status code 200)
     * or Unsuccessful, orders cannot be retrieved because of bad request (status code 400)
     * or Unsuccessful, entity does not have access rights to retrieve all orders (status code 403)
     * or Unsuccessful, no orders were found (status code 404)
     */
    @Override
    @GetMapping("")
    public ResponseEntity<List<Order>> getOrders(@RequestParam(name = "authorization") Long authorization) {
        Optional<ResponseEntity> authorizationResponse =
            authorizationService.authorize(authorization, "getOrders");
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }

        Optional<List<Order>> o = orderService.getOrders();
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
     * or Unsuccessful, vendor location of the order cannot be retrieved because of a bad request (status code 400)
     * or Unsuccessful, entity does not have access rights to retrieve vendor location (status code 403)
     * or Unsuccessful, vendor location for the order not found (status code 404)
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
     * POST /order/{orderId} : Create new order with specified id.
     * Return the response of POST operation.
     *
     * @param orderId       (required)
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param order         Order object to create
     * @return Successful response, order created (status code 200)
     * or Unsuccessful, order cannot be added because of bad request (status code 400)
     * - order with this id already exists
     * or Unsuccessful, entity does not have access rights to add order (status code 403)
     * or Unsuccessful, no order was found (status code 404)
     */
    @Override
    @PostMapping("/{orderId}")
    public ResponseEntity<Void> makeOrder(
        @PathVariable(name = "orderId") Long orderId,
        @RequestParam(name = "authorization") Long authorization,
        @Parameter(name = "Order") @RequestBody @Valid Order order
    ) {
        Optional<ResponseEntity> authorizationResponse =
            authorizationService.authorize(authorization, "makeOrder");
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }

        Optional<Order> o = orderService.getOrderById(orderId);
        if (o.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Order> created = orderService.createOrder(order);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * PUT /order/{orderId} : Retrieve order based on orderId.
     * Return the status of the update.
     *
     * @param orderId       (required)
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param order         The updated object of the order
     * @return Successful response, order updated (status code 200)
     * or Unsuccessful, order cannot be updated because of bad request (status code 400)
     * or Unsuccessful, entity does not have access rights to update order (status code 403)
     * or Unsuccessful, no order was found" (status code 404)
     */
    @Override
    @PutMapping("/{orderId}")
    public ResponseEntity<Void> updateOrder(
        @PathVariable(name = "orderId") Long orderId,
        @RequestParam(name = "authorization") Long authorization,
        @Parameter(name = "Order") @RequestBody @Valid Order order) {
        var authorizationResponse =
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


    /**
     * GET /order/{orderId}/rating : Retrieve rating of the order
     * Return the rating of the specified order.
     *
     * @param authorization Id of the order to update rating (required)
     * @param orderId       The userId to check if they have the rights to make this request (required)
     * @return Successful response, rating of the order received (status code 200)
     * or Unsuccessful, rating of the order cannot be retrieved because of a bad request (status code 400)
     * or Unsuccessful, entity does not have access rights to retrieve rating (status code 403)
     * or Unsuccessful, rating for the order not found (status code 404)
     */
    @Override
    @GetMapping("/{orderId}/rating")
    public ResponseEntity getOrderRating(
        @RequestParam(name = "authorization") Long authorization,
        @PathVariable(name = "orderId") Long orderId
    ) {
        Optional<ResponseEntity> authorizationResponse =
            authorizationService.authorize(authorization, "getOrderRating");
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }

        Optional<BigDecimal> currentRating = orderService.getRating(orderId);

        if (currentRating.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(currentRating.get(), HttpStatus.OK);
    }

    /**
     * PUT /order/{orderId}/rating : Update rating of the order
     * Update the rating of the specified order and return response.
     *
     * @param authorization Id of the order to update rating (required)
     * @param orderId       The userId to check if they have the rights to make this request (required)
     * @param body          Order object where ratingNumber is updated (required)
     * @return Successful response, rating of the order received (status code 200)
     * or Unsuccessful, rating of the order cannot be retrieved because of a bad request (status code 400)
     * or Unsuccessful, entity does not have access rights to retrieve rating (status code 403)
     * or Unsuccessful, rating for the order not found (status code 404)
     * only for customers
     */
    @Override
    @PutMapping("/{orderId}/rating")
    public ResponseEntity putOrderRating(
        @RequestParam(name = "authorization") Long authorization,
        @PathVariable(name = "orderId") Long orderId,
        @RequestBody @Valid BigDecimal body
    ) {
        Optional<ResponseEntity> authorizationResponse =
            authorizationService.authorize(authorization, "putOrderRating");
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }

        Optional<BigDecimal> currentRating = orderService.getRating(orderId);

        if (currentRating.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<BigDecimal> newRating = orderService.updateRating(orderId, body);

        if (newRating.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
