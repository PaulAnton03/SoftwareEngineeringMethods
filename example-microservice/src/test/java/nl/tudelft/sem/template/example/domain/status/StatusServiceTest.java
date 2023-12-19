package nl.tudelft.sem.template.example.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.exception.DeliveryExceptionRepository;
import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.order.StatusService;
import nl.tudelft.sem.template.model.DeliveryException;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Time;
import nl.tudelft.sem.template.model.UpdateToDeliveredRequest;
import nl.tudelft.sem.template.model.UpdateToGivenToCourierRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class StatusServiceTest {

    public OrderRepository orderRepo;
    private DeliveryExceptionRepository exceptionRepo;

    public Order order1;

    public Order order2;

    public Order order3;
    public DeliveryException delException1;
    public Order order4;

    public StatusService ss;

    @BeforeEach
    void setUp() {
        this.orderRepo = mock(OrderRepository.class);
        this.exceptionRepo = mock(DeliveryExceptionRepository.class);
        this.order1 = new Order().id(1L).status(Order.StatusEnum.PENDING);
        this.order2 = new Order().id(1L).status(Order.StatusEnum.GIVEN_TO_COURIER);
        this.order3 = new Order().id(1L).status(Order.StatusEnum.PREPARING);
        this.order4 = new Order().id(1L).status(Order.StatusEnum.IN_TRANSIT).timeValues(new Time().prepTime("00:22::00"));
        this.delException1 = new DeliveryException().exceptionType(DeliveryException.ExceptionTypeEnum.OTHER)
                .message("Test exception").isResolved(false).orderId(1L);
        this.ss = new StatusService(orderRepo, exceptionRepo);
    }

    @Test
    void updateStatusToAccepted200() {
        Order order11 = new Order().id(1L).status(Order.StatusEnum.ACCEPTED);

        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Mockito.lenient().when(orderRepo.save(order1)).thenReturn(order11);

        assertEquals(order1.getStatus(), Order.StatusEnum.PENDING);

        ss.updateStatusToAccepted(order1.getId());
        assertEquals(order1.getStatus(), Order.StatusEnum.ACCEPTED);
    }

    @Test
    void updateStatusToAccepted404() {
        Mockito.when(orderRepo.getOne(anyLong())).thenThrow(new javax.persistence.EntityNotFoundException());

        Optional<Order> ret = ss.updateStatusToAccepted(order1.getId());
        assertTrue(ret.isEmpty());
    }

    @Test
    void updateStatusToRejected200() {
        Order order11 = new Order().id(1L).status(Order.StatusEnum.REJECTED);

        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));
        Mockito.lenient().when(orderRepo.save(order1)).thenReturn(order11);

        assertEquals(order1.getStatus(), Order.StatusEnum.PENDING);

        ss.updateStatusToRejected(order1.getId());
        assertEquals(order1.getStatus(), Order.StatusEnum.REJECTED);
    }

    @Test
    void updateStatusToRejected404() {
        Mockito.when(orderRepo.getOne(anyLong())).thenThrow(new javax.persistence.EntityNotFoundException());

        Optional<Order> ret = ss.updateStatusToRejected(order1.getId());
        assertTrue(ret.isEmpty());
    }

    @Test
    void updateStatusToGivenToCourier200() {
        Order order33 = new Order().id(1L).status(Order.StatusEnum.GIVEN_TO_COURIER);
        UpdateToGivenToCourierRequest req = new UpdateToGivenToCourierRequest();
        req.courierId(3L);

        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.ofNullable(order3));
        Mockito.lenient().when(orderRepo.save(order3)).thenReturn(order33);

        assertEquals(order3.getStatus(), Order.StatusEnum.PREPARING);

        ss.updateStatusToGivenToCourier(order3.getId(), req);
        assertEquals(order3.getStatus(), Order.StatusEnum.GIVEN_TO_COURIER);
        assertEquals(order3.getCourierId(), 3L);
    }

    @Test
    void updateStatusToGivenToCourier404() {
        UpdateToGivenToCourierRequest req = new UpdateToGivenToCourierRequest();
        req.courierId(3L);

        Mockito.when(orderRepo.getOne(anyLong())).thenThrow(new javax.persistence.EntityNotFoundException());

        Optional<Order> ret = ss.updateStatusToGivenToCourier(order3.getId(), req);
        assertTrue(ret.isEmpty());
    }

    @Test
    void updateStatusToInTransit200() {
        Order order22 = new Order().id(1L).status(Order.StatusEnum.IN_TRANSIT);

        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.ofNullable(order2));
        Mockito.lenient().when(orderRepo.save(order2)).thenReturn(order22);

        assertEquals(order2.getStatus(), Order.StatusEnum.GIVEN_TO_COURIER);

        ss.updateStatusToInTransit(order2.getId());
        assertEquals(order2.getStatus(), Order.StatusEnum.IN_TRANSIT);
    }

    @Test
    void updateStatusToInTransit404() {
        Mockito.when(orderRepo.getOne(anyLong())).thenThrow(new javax.persistence.EntityNotFoundException());

        Optional<Order> ret = ss.updateStatusToInTransit(order2.getId());
        assertTrue(ret.isEmpty());
    }

    @Test
    void getOrderStatus200() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order1));

        Optional<Order.StatusEnum> status = ss.getOrderStatus(1L);

        assertTrue(status.isPresent());
        assertEquals(status.get(), Order.StatusEnum.PENDING);
    }

    @Test
    void getOrderStatus404() {
        Mockito.when(orderRepo.getOne(anyLong())).thenThrow(new javax.persistence.EntityNotFoundException());

        Optional<Order.StatusEnum> status = ss.getOrderStatus(1L);

        assertTrue(status.isEmpty());
    }

    @Test
    void addDeliveryExceptionEmpty() {
        Optional<DeliveryException> res = Optional.empty();
        assertEquals(res, ss.addDeliveryException(null));
    }

    @Test
    void addDeliveryExceptionSuccess() {
        Mockito.when(exceptionRepo.saveAndFlush(delException1)).thenReturn(delException1);

        Optional<DeliveryException> res = Optional.of(delException1);
        assertEquals(res, ss.addDeliveryException(delException1));
    }

    @Test
    void updateStatusToDelivered200() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order4));
        Mockito.when(orderRepo.save(order4)).thenReturn(order4);

        OffsetDateTime deliveryTime = OffsetDateTime.of(2023, 12, 17, 12, 30, 0, 0, ZoneOffset.UTC);
        UpdateToDeliveredRequest req = new UpdateToDeliveredRequest().actualDeliveryTime(deliveryTime);
        Optional<Order> ret = ss.updateStatusToDelivered(order4.getId(), req);

        ArgumentCaptor<Order> argumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepo).save(argumentCaptor.capture());
        Order res = argumentCaptor.getValue();

        assertEquals(res.getStatus(), Order.StatusEnum.DELIVERED);
        assertEquals(res.getTimeValues().getActualDeliveryTime(), deliveryTime);
    }

    @Test
    void updateStatusToDeliveredPrevStatusDoesntMatch() {
        Order o4 = order4.status(Order.StatusEnum.PREPARING);
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(o4));
        Mockito.when(orderRepo.save(order4)).thenReturn(order4);

        OffsetDateTime deliveryTime = OffsetDateTime.of(2023, 12, 17, 12, 30, 0, 0, ZoneOffset.UTC);
        UpdateToDeliveredRequest req = new UpdateToDeliveredRequest().actualDeliveryTime(deliveryTime);
        Optional<Order> ret = ss.updateStatusToDelivered(order4.getId(), req);

        assertTrue(ret.isEmpty());
    }

    @Test
    void updateStatusToDeliveredNullTimeValues() {
        Order o4 = new Order().id(22L);
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(o4));
        Mockito.when(orderRepo.save(order4)).thenReturn(order4);

        OffsetDateTime deliveryTime = OffsetDateTime.of(2023, 12, 17, 12, 30, 0, 0, ZoneOffset.UTC);
        UpdateToDeliveredRequest req = new UpdateToDeliveredRequest().actualDeliveryTime(deliveryTime);
        Optional<Order> ret = ss.updateStatusToDelivered(order4.getId(), req);

        assertTrue(ret.isEmpty());
    }

    @Test
    void updateStatusToDeliveredNullDeliveryTime() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order4));
        Mockito.when(orderRepo.save(order4)).thenReturn(order4);

        UpdateToDeliveredRequest req = new UpdateToDeliveredRequest();
        Optional<Order> ret = ss.updateStatusToDelivered(order4.getId(), req);

        assertTrue(ret.isEmpty());
    }

    @Test
    void updateStatusToDeliveredDeliveryTimeAlreadySet() {
        OffsetDateTime deliveryTime = OffsetDateTime.of(2023, 12, 17, 12, 30, 0, 0, ZoneOffset.UTC);

        Order o4 = order4.timeValues(new Time().actualDeliveryTime(deliveryTime));
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.of(o4));
        Mockito.when(orderRepo.save(order4)).thenReturn(order4);

        UpdateToDeliveredRequest req = new UpdateToDeliveredRequest();
        Optional<Order> ret = ss.updateStatusToDelivered(order4.getId(), req);

        assertTrue(ret.isEmpty());
    }


    @Test
    void updateStatusToDeliveredOrderDoesntExist() {
        Mockito.when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());
        Mockito.when(orderRepo.save(order4)).thenReturn(order4);

        UpdateToDeliveredRequest req = new UpdateToDeliveredRequest();
        Optional<Order> ret = ss.updateStatusToDelivered(order4.getId(), req);

        assertTrue(ret.isEmpty());
    }


}
