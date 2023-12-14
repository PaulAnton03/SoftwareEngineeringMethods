package nl.tudelft.sem.template.example.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.order.StatusService;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.UpdateToGivenToCourierRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class StatusServiceTest {

    public OrderRepository orderRepo;

    public Order order1;

    public Order order2;

    public Order order3;

    public StatusService ss;

    @BeforeEach
    void setUp() {
        this.orderRepo = mock(OrderRepository.class);
        this.order1 = new Order().id(1L).status(Order.StatusEnum.PENDING);
        this.order2 = new Order().id(1L).status(Order.StatusEnum.GIVEN_TO_COURIER);
        this.order3 = new Order().id(1L).status(Order.StatusEnum.PREPARING);
        this.ss = new StatusService(orderRepo);
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

}
