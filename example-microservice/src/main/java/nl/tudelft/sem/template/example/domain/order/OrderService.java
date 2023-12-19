package nl.tudelft.sem.template.example.domain.order;

import java.util.Optional;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final VendorRepository vendorRepo;


    @Autowired
    public OrderService(OrderRepository orderRepo, VendorRepository vendorRepo) {
        this.vendorRepo = vendorRepo;
        this.orderRepo = orderRepo;
    }

    /**
     * Attempts to return an optional of final delivery destination location of the given order with the order id.
     * Couriers use this.
     *
     * @param orderId the id of the order
     * @return the optional of location object, empty if the order was not found
     */
    public Optional<Location> getFinalDestinationOfOrder(Long orderId) {
        Optional<Order> order = orderRepo.findById(orderId);

        if (order.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(order.get().getDeliveryDestination());
    }

    /**
     * Gets the pickup destination per order, uses the vendor id to get the location of the vendor,
     * which is the pickup destination.
     *
     * @param orderId the id of the order
     * @return empty optional if either order, vendor, or location DNE, optional of location otherwise
     */
    public Optional<Location> getPickupDestination(Long orderId) {
        Optional<Order> order = orderRepo.findById(orderId);

        // if there is no found order
        if (order.isEmpty()) {
            return Optional.empty();
        }

        Optional<Vendor> vendor = vendorRepo.findById(order.get().getVendorId());
        // if the vendor does not exist
        if (vendor.isEmpty()) {
            return Optional.empty();
        }

        Location vendorLocation = vendor.get().getLocation();
        // if there is no location for whatever reason
        if (vendorLocation == null) {
            return Optional.empty();
        }

        return Optional.of(vendorLocation);
    }
}
