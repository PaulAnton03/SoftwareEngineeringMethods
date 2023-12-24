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
import nl.tudelft.sem.template.model.Vendor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class UserServiceTest {

    public CourierRepository courierRepo;
    private VendorRepository vendorRepo;
    Courier courier1;
    private Vendor vendor1;
    private UserService userService;

    @BeforeEach
    void setUp() {
        this.courierRepo = mock(CourierRepository.class);
        this.vendorRepo = mock(VendorRepository.class);
        this.userService = new UserService(courierRepo, vendorRepo);
        this.courier1 = new Courier().id(100L).bossId(5L).currentLocation(new Location().latitude(0F).longitude(0F));
        this.vendor1 = new Vendor().id(2L);
    }

    @Test
    void updateBossIdOfCourier200() {
        Courier courier11 = new Courier().id(100L).bossId(6L)
                .currentLocation(new Location().latitude(0F).longitude(0F));

        Mockito.when(courierRepo.findById(anyLong())).thenReturn(Optional.of(courier1));
        Mockito.lenient().when(courierRepo.saveAndFlush(courier1)).thenReturn(courier11);

        assertEquals(5L, courier1.getBossId());

        userService.updateBossIdOfCourier(100L, 6L);
        assertEquals(courier1.getBossId(), courier11.getBossId());
    }

    @Test
    void updateBossIdOfCourier404() {
        Mockito.when(courierRepo.getOne(anyLong())).thenThrow(new javax.persistence.EntityNotFoundException());

        Optional<Long> ret = userService.updateBossIdOfCourier(courier1.getId(), 6L);
        assertTrue(ret.isEmpty());
    }

    @Test
    void makeVendorWorks() {
        Mockito.when(vendorRepo.saveAndFlush(any())).thenReturn(vendor1);

        Optional<Vendor> res = userService.makeVendor(vendor1);
        assert(res.isPresent());
        assertEquals(res.get().getId(), vendor1.getId());
    }

    @Test
    void makeVendorDoesNotWork() {
        Mockito.when(vendorRepo.saveAndFlush(any())).thenThrow(new IllegalArgumentException());

        Optional<Vendor> res = userService.makeVendor(null);
        assert(res.isEmpty());
    }

    @Test
    void makeVendorByIdWorks() {
        Mockito.when(vendorRepo.saveAndFlush(any())).thenReturn(vendor1);

        Optional<Vendor> res = userService.makeVendorById(vendor1.getId());
        assert(res.isPresent());
        assertEquals(res.get().getId(), vendor1.getId());
    }

    @Test
    void makeVendorByIdDoesNotWork() {
        Mockito.when(vendorRepo.saveAndFlush(any())).thenThrow(new IllegalArgumentException());

        Optional<Vendor> res = userService.makeVendorById(null);
        assert(res.isEmpty());
    }
}
