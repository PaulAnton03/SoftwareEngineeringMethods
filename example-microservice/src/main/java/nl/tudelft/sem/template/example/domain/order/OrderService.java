package nl.tudelft.sem.template.example.domain.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import nl.tudelft.sem.template.example.domain.user.CourierRepository;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final VendorRepository vendorRepo;
    private final CourierRepository courierRepo;


    @Autowired
    public OrderService(OrderRepository orderRepo, VendorRepository vendorRepo, CourierRepository courierRepo) {
        this.vendorRepo = vendorRepo;
        this.orderRepo = orderRepo;
        this.courierRepo = courierRepo;
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

    /**
     * Returns the time value object of an order.
     *
     * @param orderId the id of the order to get the values from
     * @return optional of time object, empty if the order does not exist or values not found
     */
    public Optional<Time> getTimeValuesForOrder(Long orderId) {
        Optional<Order> order = orderRepo.findById(orderId);

        // oh no order is not found
        if (order.isEmpty()) {
            return Optional.empty();
        }

        Time timeValues = order.get().getTimeValues();

        // oh no something went wrong and there is no object
        if (timeValues == null) {
            return Optional.empty();
        }

        return Optional.of(order.get().getTimeValues());
    }


    /**
     * Returns true if an order if it exists.
     *
     * @param orderId the id of the order to retrieve
     * @return boolean
     */
    public Boolean orderExists(Long orderId) {
        return orderRepo.existsById(orderId);
    }

    /**
     * Gets the order based on id.
     *
     * @param orderId the id of the order
     * @return empty optional if order  DNE, optional of order otherwise
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepo.findById(orderId);
    }

    /**
     * Updated the order based on id and updated object.
     *
     * @param orderId the id of the order
     * @param order   the updated order object
     * @return empty optional if order  DNE, optional of order otherwise
     */
    public Optional<Order> updateOrderById(Long orderId, Order order) {
        Optional<Order> o = orderRepo.findById(orderId);
        if (o.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(orderRepo.saveAndFlush(order));
    }

    /**
     * Gets all orders.
     *
     * @return empty optional if no order exists, optional of list of order otherwise
     */
    public Optional<List<Order>> getOrders() {
        List<Order> o = orderRepo.findAll();
        if (o.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(o);
    }

    /**
     * Creates new order.
     *
     * @return optional of order
     */
    public Optional<Order> createOrder(Order order) {
        return Optional.of(orderRepo.saveAndFlush(order));
    }

    /**
     * Gets the rating per order, uses the order id to get the rating of the order.
     *
     * @param orderId the id of the order
     * @return empty optional if either order DNE, optional of rating otherwise
     */
    public Optional<BigDecimal> getRating(Long orderId) {
        Optional<Order> order = orderRepo.findById(orderId);

        if (order.isEmpty()) {
            return Optional.empty();
        }

        if (order.get().getRatingNumber() == null) {
            return Optional.empty();
        }

        return Optional.of(order.get().getRatingNumber());
    }

    /**
     * Update the rating per order, uses the order id to get the rating of the order and
     * updates it using the body parameter provided in the signature.
     *
     * @param orderId the id of the order
     * @param body the new rating that the order will have
     * @return empty optional if order DNE, optional of rating otherwise
     */
    public Optional<BigDecimal> updateRating(Long orderId, BigDecimal body) {
        Optional<Order> order = orderRepo.findById(orderId);

        if (order.isEmpty()) {
            return Optional.empty();
        }

        Order newOrder = order.get();
        newOrder.setRatingNumber(body);
        orderRepo.saveAndFlush(newOrder);

        return Optional.of(body);
    }

    /**
     * Update the rating per order, uses the order id to get the rating of the order and
     * updates it using the body parameter provided in the signature.
     *
     * @param orderId the id of the order
     * @param body the new preparation time that the order will have
     * @return empty optional if the order DNE, optional of prepTime otherwise
     */
    public Optional<String> updatePrepTime(Long orderId, String body) {
        Optional<Order> order = orderRepo.findById(orderId);

        if(order.isEmpty()) {
            return Optional.empty();
        }

        Order o = order.get();
        Time timeOfOrder = o.getTimeValues();
        timeOfOrder.setPrepTime(body);

        Order newOrder = orderRepo.saveAndFlush(o);

        return Optional.of(body);
    }

    /**
     * Update the courier of the order.
     *
     * @param orderId the id of the order
     * @param courierId the new courier of the order
     * @return empty optional if either order DNE, optional of updated order otherwise
     */
    public Optional<Order> updateCourier(Long orderId, Long courierId) {
        Optional<Order> order = orderRepo.findById(orderId);

        if(order.isEmpty()) {
            return Optional.empty();
        }

        Order o = order.get();
        o.setCourierId(courierId);

        return Optional.of(orderRepo.saveAndFlush(o));
    }

    public Optional<Location> getOrderLocation(Order o){
        Order.StatusEnum status = o.getStatus();

        Optional<Vendor> v = vendorRepo.findById(o.getVendorId());
        if(v.isEmpty()) {
            return Optional.empty();
        }
        Location vLocation = v.get().getLocation();

        Optional<Courier> c = courierRepo.findById(o.getCourierId());
        Location cLocation = null;
        if(c.isPresent()){
            cLocation = c.get().getCurrentLocation();
        }
        switch(status){
            case ACCEPTED, PREPARING -> {
                return Optional.of(vLocation);
            }
            case GIVEN_TO_COURIER, IN_TRANSIT -> {
                if(cLocation == null){
                    return Optional.empty();
                }
                return Optional.of(cLocation);
            }
            case DELIVERED -> {
                return Optional.of(o.getDeliveryDestination());
            }
            default -> {
                return Optional.empty();
            }
        }
    }

    public Optional<Location> updateLocation(Order order, Location location){
        Order.StatusEnum status = order.getStatus();

        Optional<Courier> c = courierRepo.findById(order.getCourierId());
        if(c.isEmpty()){
            return Optional.empty();
        }
        Courier courier = c.get();
        switch(status){
            case GIVEN_TO_COURIER, IN_TRANSIT -> {
                courier.setCurrentLocation(location);
                Courier res = courierRepo.saveAndFlush(courier);
                return Optional.of(res.getCurrentLocation());
            }
            default -> {
                return Optional.empty();
            }
        }
    }
}
