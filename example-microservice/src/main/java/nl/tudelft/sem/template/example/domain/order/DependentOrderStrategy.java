package nl.tudelft.sem.template.example.domain.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.Order;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class DependentOrderStrategy implements NextOrderStrategy{
    private OrderRepository orderRepository;

    private static final int DELIVERY_SERVER_PORT = 8082;

    public DependentOrderStrategy(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * The strategy used for couriers working for a vendor (dependent couriers).
     *
     * @return a list containing a single order that was assigned by the vendor to that courier
     */
    @Override
    public List<Order> availableOrders(Optional<Long> vendorId) {
        if(vendorId.isEmpty()) {
            return null; // well something went wrong
        }

        return null;
    }


    private List<Order> getOrdersFromDeliveryMicroservice(Long vendorId) {
        RestTemplate restTemplate = new RestTemplate();
        String ordersServiceEndpoint = "http://localhost:" + DELIVERY_SERVER_PORT + "/ORDER/" + vendorId + "/type";
        try {
            ParameterizedTypeReference<List<Order>> responseType = new ParameterizedTypeReference<>() {};

//            List<Order> orders = restTemplate.getForObject(ordersServiceEndpoint);
//            return parseUserType(actualUserType);

            return restTemplate.exchange(ordersServiceEndpoint, HttpMethod.GET, null, responseType)
                .getBody();

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
