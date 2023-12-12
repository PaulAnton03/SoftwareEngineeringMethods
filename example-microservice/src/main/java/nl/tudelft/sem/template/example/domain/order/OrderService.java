package nl.tudelft.sem.template.example.domain.order;

import java.util.Optional;
import nl.tudelft.sem.template.model.Location;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public OrderRepository orderRepo;


    public OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    /**
     * Attempts to return an optional of final delivery destination location of the given order with the order id.
     * Couriers use this
     *
     * @param orderId the id of the order
     * @return the optional of location object, empty if the order was not found
     */
    public Optional<Location> getFinalDestinationOfOrder(Long orderId) {
        try {
            return Optional.of(orderRepo.getOne(orderId).getDeliveryDestination());
        } catch (javax.persistence.EntityNotFoundException e) {
            return Optional.empty();
        }
    }
}
