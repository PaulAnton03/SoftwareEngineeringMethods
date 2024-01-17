package nl.tudelft.sem.template.example.externalservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import nl.tudelft.sem.template.model.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderExternalService {

    private final RestTemplate restTemplate;

    private final String orderServerBaseUrl = "http://localhost:8082";

    public OrderExternalService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Retrieves the order from the order microservice based on the provided order ID.
     *
     * @param userId  The ID of the user.
     * @param orderId The ID of the order.
     * @return The response entity obtained from the order service as a String.
     */
    public ResponseEntity<String> getOrder(Long userId, Long orderId) {
        String getOrderServiceEndpoint = orderServerBaseUrl + "/order/" + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("userId", String.valueOf(userId));

        return restTemplate.exchange(
            getOrderServiceEndpoint,
            org.springframework.http.HttpMethod.GET,
            new org.springframework.http.HttpEntity<>(headers),
            String.class
        );
    }


    /**
     * Propagates the status change of an order in the orders microservice
     *
     * @param userId  The ID of the user that made the status change.
     * @param orderId The ID of the order.
     */
    public void updateOrderStatus(Long orderId, Long userId, Order.StatusEnum newStatus) {
        String getOrderServiceEndpoint = orderServerBaseUrl + "/order/" + orderId + "/status";

        HttpHeaders headers = new HttpHeaders();
        headers.set("userId", String.valueOf(userId));
        headers.set("Content-Type", "application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String requestJson = objectMapper.writeValueAsString(Map.of("status", newStatus.getValue()));
            HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
            restTemplate
                .exchange(getOrderServiceEndpoint, HttpMethod.PUT, requestEntity, String.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
