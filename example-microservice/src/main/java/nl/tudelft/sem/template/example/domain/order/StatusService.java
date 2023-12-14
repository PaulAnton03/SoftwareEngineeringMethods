package nl.tudelft.sem.template.example.domain.order;

import nl.tudelft.sem.template.model.Order;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatusService {


    public OrderRepository orderRepo;


    public StatusService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    /**
     * Attempts to update the status of order to accepted
     * Vendors use this
     *
     * @param orderId the id of the order
     * @return the optional of updated order object, empty if the order was not found
     */
    public Optional<Order> updateStatusToAccepted(Long orderId) {
        try {
            Order o = orderRepo.getOne(orderId);
            o.setStatus(Order.StatusEnum.ACCEPTED);
            return Optional.of(orderRepo.save(o));
        } catch (javax.persistence.EntityNotFoundException e) {
            return Optional.empty();
        }
    }


    /**
     * Attempts to update the status of order to in_transit
     * Couriers use this
     *
     * @param orderId the id of the order
     * @return the optional of updated order object, empty if the order was not found
     */
    public Optional<Order> updateStatusToInTransit(Long orderId) {
        try {
            Order o = orderRepo.getOne(orderId);
            o.setStatus(Order.StatusEnum.IN_TRANSIT);
            return Optional.of(orderRepo.save(o));
        } catch (javax.persistence.EntityNotFoundException e) {
            return Optional.empty();
        }
    }
}
