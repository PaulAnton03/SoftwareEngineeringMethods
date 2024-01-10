package nl.tudelft.sem.template.example.authorization;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import nl.tudelft.sem.template.example.controllers.OrderController;
import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.example.domain.user.UserService;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.example.externalservices.UserExternalService;
import nl.tudelft.sem.template.example.utils.DbUtils;
import nl.tudelft.sem.template.example.wiremock.WireMockConfig;
import nl.tudelft.sem.template.model.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AuthorizationTest {

    private OrderService orderService;
    private OrderController controller;

    private OrderRepository orderRepository;

    private VendorRepository vendorRepository;

    private UserExternalService userExternalService = new UserExternalService();

    private AuthorizationService authorizationService;

    private UserService userService;


    private HashMap<String, List<Authorization.UserType>> permissions = new HashMap<>(
        Map.of("getFinalDestination", List.of(Authorization.UserType.CUSTOMER, Authorization.UserType.COURIER))
    );

    private HashMap<String, BiFunction<Long, Long, Boolean>> validationMethods;

    private DbUtils dbUtils;

    @BeforeEach
    void setUp() {
        WireMockConfig.startUserServer();
        dbUtils = Mockito.mock(DbUtils.class);
        orderService = Mockito.mock(OrderService.class);
        orderRepository = Mockito.mock(OrderRepository.class);
        vendorRepository = Mockito.mock(VendorRepository.class);
        validationMethods = Mockito.mock(HashMap.class);
        Mockito.when(validationMethods.get(anyString())).thenReturn((a, b) -> true);
        authorizationService = new AuthorizationService(dbUtils, userExternalService, permissions, validationMethods);
        userService = Mockito.mock(UserService.class);
        controller = new OrderController(orderService, userService, authorizationService, orderRepository, vendorRepository);
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

    @Test
    void getFinalDestinationWorks2() {
        WireMockConfig.userMicroservice.stubFor(WireMock.get(urlPathMatching(("/user/11/type")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("courier")));
        Optional<Location> proper = Optional.of(new Location().latitude(1F).longitude(2F));
        Mockito.when(orderService.getFinalDestinationOfOrder(anyLong())).thenReturn(proper);
        var res = controller.getFinalDestination(11L, 1L);
        assertEquals(new ResponseEntity<>(proper.get(), HttpStatus.OK), res);
    }


    @Test
    void adminOnlyMethodPermission() {
        WireMockConfig.userMicroservice.stubFor(WireMock.get(urlPathMatching(("/user/1/type")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("admin")));
        Optional<Location> proper = Optional.of(new Location().latitude(1F).longitude(2F));
        Mockito.when(orderService.getPickupDestination(anyLong())).thenReturn(proper);
        var res = controller.getPickupDestination(11L, 1L);
        assertEquals(new ResponseEntity<>(proper.get(), HttpStatus.OK), res);
    }


    @Test
    void userMicroServiceIsDown() {
        WireMockConfig.stopUserServer();
        var res = controller.getFinalDestination(11L, 1L);
        assertEquals(ResponseEntity.status(500).body("Error while retrieving user type"), res);
    }

    @AfterEach()
    void tearDown() {
        WireMockConfig.stopUserServer();
    }
}

