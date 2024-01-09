package nl.tudelft.sem.template.example.domain.order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Vendor;

public class GeneralOrdersStrategy implements NextOrderStrategy {
    /**
     * This strategy is only applied to independent couriers that do not work for a specific vendor.
     * They get to choose from a list of possible available orders. These orders are from vendors that
     * do not have any couriers on their own and the orders are being prepared.
     */

    private OrderRepository orderRepository;
    private VendorRepository vendorRepository;

    public GeneralOrdersStrategy(OrderRepository orderRepository, VendorRepository vendorRepository) {
        this.orderRepository = orderRepository;
        this.vendorRepository = vendorRepository;
    }

    /**
     * Gets orders that are being prepared and that do not belong to an vendor that has couriers on their own.
     * @param vendorId the optional id of the vendor, in this strategy it has to be empty
     *                 as there is no specific vendor to get orders form
     * @return an optional list of available orders, empty list if there are currently none,
     *      empty optional if there is a vendor id passed
     */
    @Override
    public Optional<List<Order>> availableOrders(Optional<Long> vendorId) {
        if (vendorId.isPresent()) {
            return Optional.empty();
        }

        // get all orders that are being prepared
        List<Order> all = orderRepository.findByStatus(Order.StatusEnum.PREPARING);

        // now we have to get the ones whose vendors don't have couriers
        List<Order> orders = all.stream()
                .filter(order -> !vendorHasCouriers(order.getVendorId()))
                .collect(Collectors.toList());


        return Optional.of(orders);
    }

    /**
     * Returns if the given vendor exists and has its own couriers
     * @param vendorId the id of the vendor to check for
     * @return the boolean stating if the vendor has its oen couriers or not
     */
    public Boolean vendorHasCouriers(Long vendorId) {
        Optional<Vendor> vendor = vendorRepository.findById(vendorId);

        if (vendor.isEmpty()) {
            return false;
        }

        return vendor.get().getHasCouriers();
    }


}
