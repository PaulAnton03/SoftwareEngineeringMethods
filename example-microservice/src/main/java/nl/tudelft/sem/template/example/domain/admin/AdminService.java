package nl.tudelft.sem.template.example.domain.admin;

import java.util.List;
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
    public Optional<DeliveryException> makeException(DeliveryException deliveryException) {

        if (deliveryException == null) {
            return Optional.empty();
        }

        // can not make an exception that is not linked to an order
        if (deliveryException.getOrder() == null) {
            return Optional.empty();
        }

        // can not add an exception if that order already has an exception
        if (exceptionRepo.existsByOrder(deliveryException.getOrder())) {
            return Optional.empty();
        }

        return Optional.of(exceptionRepo.saveAndFlush(deliveryException));
    }


}
