package nl.tudelft.sem.template.example.authorization;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nl.tudelft.sem.template.example.controllers.OrderController;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.example.wiremock.WireMockConfig;
import nl.tudelft.sem.template.model.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AuthorizationServiceTest {

    private OrderService orderService;
    private OrderController controller;

    private HashMap<String, List<AuthorizationService.UserType>> permissions = new HashMap<>(
        Map.of("getFinalDestination", List.of(AuthorizationService.UserType.CUSTOMER),
            "getPickupDestination", List.of(AuthorizationService.UserType.VENDOR))
    );

    private AuthorizationService authorizationService = new AuthorizationService(permissions);


    @BeforeEach
    void setUp() {
        WireMockConfig.startUserServer();
        this.orderService = Mockito.mock(OrderService.class);
        this.controller = new OrderController(orderService, authorizationService);
    }

    @Test
    void getFinalDestinationWorks() {
        WireMockConfig.userMicroservice.stubFor(WireMock.get(urlPathMatching(("/user/11/type")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("customer")));
        Optional<Location> proper = Optional.of(new Location().latitude(1F).longitude(2F));
        Mockito.when(orderService.getFinalDestinationOfOrder(anyLong())).thenReturn(proper);

        var res = controller.getFinalDestination(11L, 1L);
        assertEquals(new ResponseEntity<>(proper.get(), HttpStatus.OK), res);
    }

//    @Test
//    void getFinalDestinationWorksA() {
//        WireMockConfig.orderMicroservice.stubFor(WireMock.get(urlPathMatching(("/order/11/pending")))
//            .willReturn(aResponse()
//                .withStatus(200)
//                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                .withBody("[\n" +
//                    "  {\n" +
//                    "    \"id\": 1,\n" +
//                    "    \"customerId\": 1,\n" +
//                    "    \"vendorId\": 1,\n" +
//                    "    \"ratingNumber\": 1,\n" +
//                    "    \"orderTime\": \"1985-04-12T23:20:50.520Z\",\n" +
//                    "    \"dishes\": [\n" +
//                    "      1\n" +
//                    "    ],\n" +
//                    "    \"dishRequirements\": [\n" +
//                    "      \"no nuts\"\n" +
//                    "    ],\n" +
//                    "    \"status\": {\n" +
//                    "      \"status\": \"pending\"\n" +
//                    "    },\n" +
//                    "    \"deliveryDestination\": {\n" +
//                    "      \"latitude\": 51.925298,\n" +
//                    "      \"longitude\": 4.754099\n" +
//                    "    },\n" +
//                    "    \"comment\": \"deliver on the door\",\n" +
//                    "    \"price\": 14.5,\n" +
//                    "    \"isPayed\": true\n" +
//                    "  }\n" +
//                    "]")));
//        Optional<Location> proper = Optional.of(new Location().latitude(1F).longitude(2F));
//        Mockito.when(orderService.getPendingOrdersForVendor(anyLong())).thenReturn(proper);
//
//        var res = controller.getFinalDestination(11L, 1L);
//        assertEquals(new ResponseEntity<>(proper.get(), HttpStatus.OK), res);
//    }

    @Test
    void getFinalDestinationNoPermission() {
        WireMockConfig.userMicroservice.stubFor(WireMock.get(urlPathMatching(("/user/11/type")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("vendor")));
        Optional<Location> proper = Optional.of(new Location().latitude(1F).longitude(2F));
        Mockito.when(orderService.getFinalDestinationOfOrder(anyLong())).thenReturn(proper);

        var res = controller.getFinalDestination(11L, 1L);
        assertEquals(ResponseEntity.status(403).body("User with id " + 11 + " does not have access rights"), res);
    }

    @Test
    void userMicroServiceIsDown() {
        WireMockConfig.stopUserServer();
        Optional<Location> proper = Optional.of(new Location().latitude(1F).longitude(2F));
        Mockito.when(orderService.getFinalDestinationOfOrder(anyLong())).thenReturn(proper);

        var res = controller.getFinalDestination(11L, 1L);
        assertEquals(ResponseEntity.status(500).body("Error while retrieving user type"), res);
    }

    @AfterEach()
    void tearDown() {
        WireMockConfig.stopUserServer();
    }
}
