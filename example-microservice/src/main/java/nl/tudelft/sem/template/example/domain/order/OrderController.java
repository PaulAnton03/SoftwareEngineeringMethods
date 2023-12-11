package nl.tudelft.sem.template.example.domain.order;

import nl.tudelft.sem.template.api.OrderApi;
import nl.tudelft.sem.template.model.Location;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order")
public class OrderController implements OrderApi {

    public OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<Location> getFinalDestination(Long authorization, Long orderId) {
        // todo add verification to check if the authorization belongs to a courier once the auth service is implemented
        // todo actually implement this
        return OrderApi.super.getFinalDestination(authorization, orderId);
    }




}
