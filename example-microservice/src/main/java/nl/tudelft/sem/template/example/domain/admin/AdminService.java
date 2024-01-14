package nl.tudelft.sem.template.example.domain.admin;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.exception.DeliveryExceptionRepository;
import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.DeliveryException;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    OrderRepository orderRepo;

    DeliveryExceptionRepository exceptionRepo;

    VendorRepository vendorRepo;

    @Autowired
    public AdminService(VendorRepository vendorRepo, OrderRepository orderRepo, DeliveryExceptionRepository exceptionRepo) {
        this.exceptionRepo = exceptionRepo;
        this.orderRepo = orderRepo;
        this.vendorRepo = vendorRepo;
    }


    public Optional<List<Vendor>> updateDefaultRadius(Double body) {

        List<Vendor> vendors = vendorRepo.findVendorsByHasCouriers(false);

        if (vendors.isEmpty()) {
            return Optional.empty();
        }
        for (Vendor v : vendors) {
            v.setRadius(body);
            vendorRepo.saveAndFlush(v);
        }

        return Optional.of(vendors);
    }

    public Optional<Double> getDefaultRadius(){
        List<Vendor> vendors = vendorRepo.findVendorsByHasCouriers(false);

        if(vendors.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(vendors.get(0).getRadius());
    }

    /**
     * Get all delivered orders 
     * 
     * @return an optional list of all delivered orders
     */
    public Optional<List<Order>> getDelivered() {
        List<Order> orders = orderRepo.findByStatus(Order.StatusEnum.DELIVERED);

        if(orders.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(orders);
    }

    /**
     * Get the exception of an order
     *
     * @param orderId id of the order
     * @return the optional of th exception, empty if order or exception not found
     */
    public Optional<DeliveryException> getExceptionByOrder(Long orderId) {
        Optional<Order> order = orderRepo.findById(orderId);

        if (order.isEmpty()) {
            return Optional.empty();
        }

        List<DeliveryException> exceptionList = exceptionRepo.findByOrder(order.get());

        if (exceptionList.isEmpty()) {
            return Optional.empty();
        }
        // only get the first exception as there only should be one
        return Optional.of(exceptionList.get(0));
    }

    /**
     * Saves the exception in the database
     *
     * @param deliveryException the exception to save
     * @return the optional object saved, empty if there is an exception for that order
     */
    public Optional<DeliveryException> makeException(DeliveryException deliveryException, Long orderId) {

        Optional<DeliveryException> empty = validateException(deliveryException, orderId);
        // the order is not valid
        if (empty.isEmpty()) {
            return empty;
        }

        // can not add an exception if that order already has an exception
        if (exceptionRepo.existsByOrder(deliveryException.getOrder())) {
            return Optional.empty();
        }

        return Optional.of(exceptionRepo.saveAndFlush(deliveryException));
    }

    /**
     * Checks if the exception has valid fields to continue performing operations
     *
     * @param deliveryException the exception to be checked
     * @param orderId           the id of the related order gathered form the path of the request
     * @return an empty optional if it is not valid
     */
    private Optional<DeliveryException> validateException(DeliveryException deliveryException, Long orderId) {
        if (deliveryException == null) {
            return Optional.empty();
        }

        // can not make an exception that is not linked to an order
        if (deliveryException.getOrder() == null) {
            return Optional.empty();
        }

        // order id on the path does not match order id in the object
        if (!Objects.equals(orderId, deliveryException.getOrder().getId())) {
            return Optional.empty();
        }
        return Optional.of(deliveryException);
    }

    /**
     * Updates an exception
     *
     * @param deliveryException the exception to be checked
     * @param orderId           the id of the related order gathered form the path of the request
     */
    public Optional<DeliveryException> updateException(DeliveryException deliveryException, Long orderId) {

        Optional<DeliveryException> empty = validateException(deliveryException, orderId);
        // the order is not valid
        if (empty.isEmpty()) {
            return empty;
        }
        return Optional.of(exceptionRepo.saveAndFlush(deliveryException));
    }


    /**
     * Returns all the exceptions stored in the database
     *
     * @return the list of exceptions, empty if there are none
     */
    public List<DeliveryException> getAllExceptions() {
        return exceptionRepo.findAll();
    }


    /**
     * Checks if the current exception exists by id
     *
     * @param exception the exception to check
     * @return the boolean value
     */
    public Boolean doesExceptionExist(DeliveryException exception) {
        if (exception == null) {
            return false;
        }

        if (exception.getOrder() == null) {
            return false;
        }
        return exceptionRepo.existsById(exception.getId());
    }
}
