package nl.tudelft.sem.template.example.domain.user;

import nl.tudelft.sem.template.model.Courier;
import nl.tudelft.sem.template.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;

public class UserServiceTest {

    public CourierRepository courierRepo;
    public UserService us;

    Courier courier1;

    @BeforeEach
    void setUp() {
        this.courierRepo = mock(CourierRepository.class);
        this.us = new UserService(courierRepo);

        this.courier1 = new Courier().id(100L).bossId(5L).currentLocation(new Location().latitude(0F).longitude(0F));
    }

    @Test
    void updateBossIdOfCourier200() {
        Courier courier11 = new Courier().id(100L).bossId(6L)
                .currentLocation(new Location().latitude(0F).longitude(0F));

        Mockito.when(courierRepo.findById(anyLong())).thenReturn(Optional.of(courier1));
        Mockito.lenient().when(courierRepo.saveAndFlush(courier1)).thenReturn(courier11);

        assertEquals(5L, courier1.getBossId());

        us.updateBossIdOfCourier(100L, 6L);
        assertEquals(courier1.getBossId(), courier11.getBossId());
    }

    @Test
    void updateBossIdOfCourier404() {
        Mockito.when(courierRepo.getOne(anyLong())).thenThrow(new javax.persistence.EntityNotFoundException());

        Optional<Long> ret = us.updateBossIdOfCourier(courier1.getId(), 6L);
        assertTrue(ret.isEmpty());
    }


}
