package nl.tudelft.sem.template.example.domain.order;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.model.Order;

public class GeneralOrdersStrategy implements NextOrderStrategy {

    @Override
    public Optional<List<Order>> availableOrders(Optional<Long> vendorId) {
        if (vendorId.isPresent()) {
            return Optional.empty();
        }


        return Optional.empty();
    }
}
