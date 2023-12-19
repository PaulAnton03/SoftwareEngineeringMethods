package nl.tudelft.sem.template.example.domain.order;

import java.util.Optional;
import nl.tudelft.sem.template.example.domain.exception.DeliveryExceptionRepository;
import nl.tudelft.sem.template.model.DeliveryException;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.UpdateToGivenToCourierRequest;
import org.springframework.stereotype.Service;

@Service
public class StatusService {

    private DeliveryExceptionRepository exceptionRepo;
    private OrderRepository orderRepo;


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
     * Attempts to update the status of order to "accepted".
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
     * Attempts to update the status of order to "rejected".
     * Vendors use this.
     *
     * @param orderId the id of the order
     * @return the optional of updated order object, empty if the order was not found
     */
    public Optional<Order> updateStatusToRejected(Long orderId) {
        Optional<Order> o = orderRepo.findById(orderId);

        if (o.isEmpty()) {
            return Optional.empty();
        }

        Order order = o.get();
        order.setStatus(Order.StatusEnum.REJECTED);
        return Optional.of(orderRepo.save(order));
    }


    /**
     * Attempts to update the status of order to "given_to_courier".
     * Vendors use this.
     *
     * @param orderId the id of the order
     * @return the optional of updated order object, empty if the order was not found
     */
    public Optional<Order> updateStatusToGivenToCourier(Long orderId, UpdateToGivenToCourierRequest req) {
        Optional<Order> o = orderRepo.findById(orderId);

        if (o.isEmpty()) {
            return Optional.empty();
        }

        Order order = o.get();
        order.setStatus(Order.StatusEnum.GIVEN_TO_COURIER);
        order.courierId(req.getCourierId());
        return Optional.of(orderRepo.save(order));
    }


    /**
     * Attempts to update the status of order to "in_transit".
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

    /**
     * Adds an exception to the DeliveryExceptionRepo.
     *
     * @return the HttpStatus of the request
     */
    public Optional<DeliveryException> addDeliveryException(DeliveryException e) {

        if (e == null) {
            return Optional.empty();
        }

        return Optional.of(exceptionRepo.saveAndFlush(e));
    }
}
