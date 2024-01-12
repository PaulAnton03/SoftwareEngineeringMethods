package nl.tudelft.sem.template.example.authorization;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static nl.tudelft.sem.template.example.authorization.Authorization.UserType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;

import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import nl.tudelft.sem.template.example.controllers.OrderController;
import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.order.OrderService;
import nl.tudelft.sem.template.example.domain.user.CourierRepository;
import nl.tudelft.sem.template.example.domain.user.UserService;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.example.externalservices.OrderExternalService;
import nl.tudelft.sem.template.example.externalservices.UserExternalService;
import nl.tudelft.sem.template.example.utils.DbUtils;
import nl.tudelft.sem.template.example.wiremock.WireMockConfig;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AuthorizationServiceTest {

    private OrderRepository orderRepo;

    private VendorRepository vendorRepo;

    private CourierRepository courierRepo;

    private OrderService orderService;
    private UserService userService;
    private OrderController controller;

    private final UserExternalService userExternalService = new UserExternalService();

    private final OrderExternalService orderExternalService = new OrderExternalService();

    private AuthorizationService authorizationService;

    private Order order1;

    private Vendor vendor1;

    private DbUtils dbUtils;
    private HashMap<String, BiFunction<Long, Long, Boolean>> validationMethods;
    private HashMap<String, List<Authorization.UserType>> permissions = new HashMap<>(
        Map.of("getFinalDestination", List.of(Authorization.UserType.VENDOR),
            "getPickupDestination", List.of(Authorization.UserType.VENDOR))
    );

    @BeforeEach
    void setUp() {
        WireMockConfig.startUserServer();
        this.orderService = Mockito.mock(OrderService.class);
        this.userService = Mockito.mock(UserService.class);
        this.controller = new OrderController(orderService, userService, authorizationService);
        orderService = Mockito.mock(OrderService.class);
        orderRepo = mock(OrderRepository.class);
        vendorRepo = mock(VendorRepository.class);
        courierRepo = mock(CourierRepository.class);
        dbUtils = new DbUtils(orderRepo, vendorRepo, courierRepo, orderExternalService);
        validationMethods = new HashMap<>(
            Map.of(
                "getFinalDestination", dbUtils::userBelongsToOrder,
                "getPickupDestination", dbUtils::userBelongsToOrder
            )
        );
        order1 = new Order().id(1L).vendorId(2L).deliveryDestination(new Location().latitude(11F).longitude(22F));
        vendor1 = new Vendor().id(2L).location(new Location().latitude(22F).longitude(33F));
        authorizationService = new AuthorizationService(dbUtils, userExternalService, permissions, validationMethods);
        controller = new OrderController(orderService, userService, authorizationService);
    }

    @Test
    void authorizeAdminOnlyWorks(){
        WireMockConfig.userMicroservice.stubFor(WireMock.get(urlPathMatching(("/user/11/type")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("admin")));
        List<Order> proper = List.of(new Order().id(11L), new Order().id(22L), new Order().id(33L));
        Mockito.when(orderService.getOrders()).thenReturn(Optional.of(proper));
        var res = controller.getOrders(11L);
        assertEquals(new ResponseEntity<>(proper, HttpStatus.OK), res);

    }
    @Test
    void getFinalDestinationWorks() {
        WireMockConfig.userMicroservice.stubFor(WireMock.get(urlPathMatching(("/user/11/type")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("vendor")));
        Optional<Location> proper = Optional.of(new Location().latitude(1F).longitude(2F));
        Mockito.when(orderService.getFinalDestinationOfOrder(anyLong())).thenReturn(proper);
        Mockito.when(orderRepo.existsByIdAndVendorId(1L, 11L)).thenReturn(true);
        var res = controller.getFinalDestination(11L, 1L);
        assertEquals(new ResponseEntity<>(proper.get(), HttpStatus.OK), res);
    }

    @Test
    void getFinalDestinationNoAuthorization() {
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
    void getFinalDestinationNoValidation() {
        Mockito.when(orderRepo.existsByIdAndVendorId(1L, 11L)).thenReturn(false);
        Mockito.when(orderRepo.existsByIdAndCourierId(1L, 11L)).thenReturn(false);
        WireMockConfig.userMicroservice.stubFor(WireMock.get(urlPathMatching(("/user/11/type")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("customer")));
        Optional<Location> proper = Optional.of(new Location().latitude(1F).longitude(2F));
        Mockito.when(orderService.getFinalDestinationOfOrder(anyLong())).thenReturn(proper);

        var res = controller.getFinalDestination(11L, 1L);
        assertEquals(ResponseEntity.status(403).body("User with id " + 11 + " does not have access rights"), res);
    }

    @Test
    void adminOnlyMethodNoPermission() {
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
    void adminOnlyMethodPermission() {
        WireMockConfig.userMicroservice.stubFor(WireMock.get(urlPathMatching(("/user/11/type")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("admin")));
        Optional<Location> proper = Optional.of(new Location().latitude(1F).longitude(2F));
        Mockito.when(orderService.getFinalDestinationOfOrder(anyLong())).thenReturn(proper);
        var res = controller.getFinalDestination(11L, 1L);
        assertEquals(new ResponseEntity<>(proper.get(), HttpStatus.OK), res);
    }

    @Test
    void permissionsAndValidations() throws NoSuchMethodException {
        HashMap<String, List<Authorization.UserType>> permissionsExpected = new HashMap<>();
        HashMap<String, BiFunction<Long, Long, Boolean>> validationMethodsExpected = new HashMap<>();

        // OrderController
        permissionsExpected.put("getFinalDestination", List.of(CUSTOMER, VENDOR, COURIER));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("getOrder", List.of(VENDOR));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("getPickupDestination", List.of(COURIER));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("updateOrder", List.of(CUSTOMER, VENDOR, COURIER));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("getOrderRating", List.of(CUSTOMER, VENDOR));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("putOrderRating", List.of(CUSTOMER));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("setDeliverTime", List.of(CUSTOMER, VENDOR, COURIER));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        // StatusController
        permissionsExpected.put("updateToAccepted", List.of(VENDOR));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("updateToRejected", List.of(VENDOR));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("updateToGivenToCourier", List.of(VENDOR));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("updateToInTransit", List.of(COURIER));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("updateToPreparing", List.of(VENDOR));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("updateToDelivered", List.of(COURIER));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        permissionsExpected.put("getStatus", List.of(CUSTOMER, VENDOR, COURIER));
        validationMethodsExpected.put("getFinalDestination", dbUtils::userBelongsToOrder);

        // UserController
        permissionsExpected.put("updateBossOfCourier", List.of(VENDOR));
        validationMethodsExpected.put("getFinalDestination", dbUtils::courierBelongsToVendor);
        authorizationService.init();
        assertEquals(permissionsExpected, authorizationService.getPermissions());
        assertEquals(validationMethodsExpected.keySet(), authorizationService.getValidationMethods().keySet());

    }

    @AfterEach()
    void tearDown() {
        WireMockConfig.stopUserServer();
    }
}
