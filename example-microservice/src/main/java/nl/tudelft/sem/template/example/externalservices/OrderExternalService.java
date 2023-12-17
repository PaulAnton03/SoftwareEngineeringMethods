package nl.tudelft.sem.template.example.externalservices;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderExternalService {

    private final RestTemplate restTemplate;

    private final String orderServerBaseUrl = "http://localhost:8082";

    public OrderExternalService() {
        this.restTemplate = new RestTemplate();
    }

    //Add any methods you need to interact with the order microservice here
}
