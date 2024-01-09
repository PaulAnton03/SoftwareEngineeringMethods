package nl.tudelft.sem.template.example.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.api.OrderApi;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.order.GeneralOrdersStrategy;
import nl.tudelft.sem.template.example.domain.order.NextOrderStrategy;
import nl.tudelft.sem.template.example.domain.order.OrderPerVendorStrategy;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final OrderService orderService;

    private final AuthorizationService authorizationService;

    private NextOrderStrategy strategy;

    @Autowired
    public OrderController(OrderService orderService, AuthorizationService authorizationService) {
        this.orderService = orderService;
        this.authorizationService = authorizationService;
    }

    /**
     * Sets the strategy for getting available offers
     *
     * @param strategy the next order strategy, either for independent or dependent orders
     */
    public void setStrategy(NextOrderStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * GET /order/{vendorId} : Retrieve the next order that belongs to a given vendor.
     * return the next order object that is assigned to specific vendor.
     *
     * @param vendorId      id of the vendor to retrieve the order from (required)
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @return Successful response, order received (status code 200)
     * or Unsuccessful, order cannot be retrieved because of bad request (status code 400)
     * or Unsuccessful, entity does not have access rights to retrieve vendor order (status code 403)
     * or Unsuccessful, no order was found (status code 404)
     */
    @Override
    @GetMapping("/order/{vendorId}")
    public ResponseEntity<Order> getNextOrderForVendor(
        @PathVariable("vendorId") Long vendorId,
        @RequestParam(value = "authorization", required = true) Long authorization) {

        Optional<ResponseEntity> authorizationResponse =
            authorizationService.authorize(authorization, "getNextOrderForVendor");
        // if there is a response, then the authority is not sufficient
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }

        this.setStrategy(new OrderPerVendorStrategy());
        Optional<List<Order>> orders = strategy.availableOrders(Optional.of(vendorId));

        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (orders.get().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(orders.get().get(0), HttpStatus.OK);
    }

    /**
     * GET /order/unassigned : Retrieve all independent and unassigned orders.
     * Return a list of all independent and unassigned orders. Independent orders are orders that belong  to a vendor without own couriers.
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @return Successful response, independent and unassigned orders received (status code 200)
     * or Unsuccessful, independent and unassigned orders cannot be retrieved because of a bad request (status code 400)
     * or Unsuccessful, entity does not have access rights to retrieve independent and unassigned orders (status code 403)
     * or Unsuccessful, no independent and unassigned orders were found (status code 404)
     */
    @Override
    @GetMapping("/order/unassigned")
    public ResponseEntity<List<Order>> getIndependentOrders(
        @RequestParam(value = "authorization", required = true) Long authorization) {
        Optional<ResponseEntity> authorizationResponse =
            authorizationService.authorize(authorization, "getIndependentOrders");
        // if there is a response, then the authority is not sufficient
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }

        this.setStrategy(new GeneralOrdersStrategy());
        Optional<List<Order>> orders = strategy.availableOrders(Optional.empty());


        return OrderApi.super.getIndependentOrders(authorization);
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
     * or Unsuccessful, vendor location of the order cannot be retrieved because of a bad request (status code 400)
     * or Unsuccessful, entity does not have access rights to retrieve vendor location (status code 403)
     * or Unsuccessful, vendor location for the order not found (status code 404)
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
