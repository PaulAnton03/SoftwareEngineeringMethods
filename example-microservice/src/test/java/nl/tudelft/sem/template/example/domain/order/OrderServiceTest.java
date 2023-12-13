package nl.tudelft.sem.template.example.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import nl.tudelft.sem.template.model.Location;
import nl.tudelft.sem.template.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OrderServiceTest {
    public OrderRepository orderRepo;
    public Order order1;

    public OrderService os;

    @BeforeEach
    void setUp() {
        this.orderRepo = mock(OrderRepository.class);
        this.order1 = new Order().id(1L).deliveryDestination(new Location().latitude(11F).longitude(22F));
        this.os = new OrderService(orderRepo);
    }

    @Test
    void getFinalDestinationOfOrderWorks() {
        Mockito.when(orderRepo.getOne(anyLong())).thenReturn(order1);

        Location exp = new Location().latitude(11F).longitude(22F);
        Location res = os.getFinalDestinationOfOrder(order1.getId()).get();
        assertEquals(res, exp);
    }

    @Test
    void getFinalDestinationOfOrderThrows404() {
        Mockito.when(orderRepo.getOne(anyLong())).thenThrow(new javax.persistence.EntityNotFoundException());

        Optional<Location> res = os.getFinalDestinationOfOrder(order1.getId());
        assertTrue(res.isEmpty());
    }
}