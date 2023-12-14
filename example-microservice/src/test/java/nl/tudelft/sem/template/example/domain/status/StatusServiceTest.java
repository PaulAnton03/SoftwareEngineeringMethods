package nl.tudelft.sem.template.example.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.order.StatusService;
import nl.tudelft.sem.template.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class StatusServiceTest {

    public OrderRepository orderRepo;

    public Order order1;

    public StatusService ss;

    @BeforeEach
    void setUp() {
        this.orderRepo = mock(OrderRepository.class);
        this.order1 = new Order().id(1L).status(Order.StatusEnum.PENDING);
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


}
