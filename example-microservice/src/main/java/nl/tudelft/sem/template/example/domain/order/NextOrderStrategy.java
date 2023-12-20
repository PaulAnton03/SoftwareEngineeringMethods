package nl.tudelft.sem.template.example.domain.order;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.model.Order;

public interface NextOrderStrategy {
    public List<Order> availableOrders(Optional<Long> vendorId);
}
