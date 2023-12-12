package nl.tudelft.sem.template.example.domain.order;

import java.util.Optional;
import nl.tudelft.sem.template.api.OrderApi;
import nl.tudelft.sem.template.model.Location;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/order")
public class OrderController implements OrderApi {

    public OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Handles get request for (/order/{orderId}/final-destination).
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param orderId id of the order to get its final destination (required)
     * @return a response entity with location, 404 if not found  403 if not authorized, only for couriers and admin
     */
    @Override
    public ResponseEntity<Location> getFinalDestination(Long authorization, Long orderId) {
        // todo add verification to check if the authorization belongs to a courier once the auth service is implemented
        //  return 403 if not authorized
        Optional<Location> location = orderService.getFinalDestinationOfOrder(orderId);

        if (location.isEmpty()) {
            // 404 not found if not found :(
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(location.get(), HttpStatus.OK);

    }

}
