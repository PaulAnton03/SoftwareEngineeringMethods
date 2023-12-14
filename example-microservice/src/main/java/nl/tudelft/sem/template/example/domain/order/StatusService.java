package nl.tudelft.sem.template.example.domain.order;

import java.util.Optional;
import nl.tudelft.sem.template.model.Order;
import org.springframework.stereotype.Service;

@Service
public class StatusService {


    public OrderRepository orderRepo;


    public StatusService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    /**
     * Attempts to get the status of order.
     * Everyone can use this.
     *
     * @param orderId the id of the order
     * @return the optional of updated order object, empty if the order was not found
     */
    public Optional<Order.StatusEnum> getOrderStatus(Long orderId) {
        Optional<Order> o = orderRepo.findById(orderId);
        return o.map(Order::getStatus);
    }

    /**
     * Attempts to update the status of order to accepted.
     * Vendors use this.
     *
     * @param orderId the id of the order
     * @return the optional of updated order object, empty if the order was not found
     */
    public Optional<Order> updateStatusToAccepted(Long orderId) {
        Optional<Order> o = orderRepo.findById(orderId);

        if (o.isEmpty()) {
            return Optional.empty();
        }

        Order order = o.get();
        order.setStatus(Order.StatusEnum.ACCEPTED);
        return Optional.of(orderRepo.save(order));
    }


    /**
     * Attempts to update the status of order to in_transit.
     * Couriers use this.
     *
     * @param orderId the id of the order
     * @return the optional of updated order object, empty if the order was not found
     */
    public Optional<Order> updateStatusToInTransit(Long orderId) {
        Optional<Order> o = orderRepo.findById(orderId);

        if (o.isEmpty()) {
            return Optional.empty();
        }

        Order order = o.get();
        order.setStatus(Order.StatusEnum.IN_TRANSIT);
        return Optional.of(orderRepo.save(order));
    }
}