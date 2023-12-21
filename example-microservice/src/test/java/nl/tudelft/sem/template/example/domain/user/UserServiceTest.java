package nl.tudelft.sem.template.example.domain.user;

import nl.tudelft.sem.template.model.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class UserServiceTest {

    private VendorRepository vendorRepo;
    private Vendor vendor1;

    private UserService userService;

    @BeforeEach
    void setUp() {
        this.vendorRepo = mock(VendorRepository.class);
        this.vendor1 = new Vendor().id(2L);
        this.userService = new UserService(vendorRepo);
    }

    @Test
    void makeVendorWorks() {
        Mockito.when(vendorRepo.save(any())).thenReturn(vendor1);

        Optional<Vendor> res = userService.makeVendor(vendor1);
        assert(res.isPresent());
        assertEquals(res.get().getId(), vendor1.getId());
    }

    @Test
    void makeVendorDoesNotWork() {
        Mockito.when(vendorRepo.save(any())).thenThrow(new IllegalArgumentException());

        Optional<Vendor> res = userService.makeVendor(null);
        assert(res.isEmpty());
    }

    @Test
    void makeVendorByIdWorks() {
        Mockito.when(vendorRepo.save(any())).thenReturn(vendor1);

        Optional<Vendor> res = userService.makeVendorById(vendor1.getId());
        assert(res.isPresent());
        assertEquals(res.get().getId(), vendor1.getId());
    }

    @Test
    void makeVendorByIdDoesNotWork() {
        Mockito.when(vendorRepo.save(any())).thenThrow(new IllegalArgumentException());

        Optional<Vendor> res = userService.makeVendorById(null);
        assert(res.isEmpty());
    }
}
