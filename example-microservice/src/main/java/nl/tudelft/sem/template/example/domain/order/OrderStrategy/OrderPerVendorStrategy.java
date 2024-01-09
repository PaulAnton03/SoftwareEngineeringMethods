package nl.tudelft.sem.template.example.domain.order.OrderStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.model.Order;

public class OrderPerVendorStrategy implements NextOrderStrategy {
    /**
     * This strategy is used for dependent couriers, meaning couriers that work for a specific vendor.
     * They can only get - meaning "assigned" - one order that is available for that vendor.
     */

    private final OrderRepository orderRepository;

    public OrderPerVendorStrategy(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * The strategy used for couriers working for a vendor (dependent couriers).
     *
     * @return a list containing a single order that was assigned by the vendor to that courier
     */
    @Override
    public Optional<List<Order>> availableOrders(Optional<Long> vendorId) {
        if (vendorId.isEmpty()) {
            return Optional.empty(); // well something went wrong
        }

        List<Order> availableOrders = orderRepository.findByVendorIdAndStatus(vendorId.get(), Order.StatusEnum.PREPARING);

        if (availableOrders == null) {
            // something went wrong with communication
            return Optional.empty();
        }

        if (availableOrders.isEmpty()) {
            // if no available orders, return empty list
            return Optional.of(new ArrayList<>());
        }

        // return only one order as a courier is "assigned" an order, this behavior can be changed in the future
        // according to the needs of the vendor by for example first ordering and then getting the first one
        return Optional.of(List.of(availableOrders.get(0)));
    }


//    private List<Order> getOrdersFromDeliveryMicroservice(Long vendorId) {
//        RestTemplate restTemplate = new RestTemplate();
//        String ordersServiceEndpoint = "http://localhost:" + DELIVERY_SERVER_PORT + "/ORDER/" + vendorId + "/type";
//        try {
//            ParameterizedTypeReference<List<Order>> responseType = new ParameterizedTypeReference<>() {
//            };
//
////            List<Order> orders = restTemplate.getForObject(ordersServiceEndpoint);
////            return parseUserType(actualUserType);
//
//            return restTemplate.exchange(ordersServiceEndpoint, HttpMethod.GET, null, responseType)
//                .getBody();
//
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }
}
