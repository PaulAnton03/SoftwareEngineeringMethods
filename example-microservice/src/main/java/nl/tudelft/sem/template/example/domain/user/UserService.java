package nl.tudelft.sem.template.example.domain.user;

import java.util.Optional;
import nl.tudelft.sem.template.model.Courier;
import nl.tudelft.sem.template.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final CourierRepository courierRepo;
    private final VendorRepository vendorRepo;

    @Autowired
    public UserService(CourierRepository courierRepo, VendorRepository vendorRepo) {
        this.courierRepo = courierRepo;
        this.vendorRepo = vendorRepo;
    }

    /**
     * Attempts to update the bossId of the courier.
     * Vendors use this.
     * @param courierId the id of the courier
     * @param bossId    the new bossId of the courier
     * @return the optional of updated courier object, empty if the courier was not found
     */
    public Optional<Long> updateBossIdOfCourier(Long courierId, Long bossId) {
        Optional<Courier> courier = courierRepo.findById(courierId);

        if (courier.isEmpty()) {
            return Optional.empty();
        }

        Courier c = courier.get();
        c.setBossId(bossId);

        return Optional.of(courierRepo.saveAndFlush(c).getBossId());
    }

    public Optional<Courier> getCourier(Long courierId) {
        return courierRepo.findById(courierId);
    }

    /**
     * Tries to add the given vendor to the database
     * @param vendor the vendor to add
     * @return an optional of the added vendor (or empty optional)
     */
    public Optional<Vendor> makeVendor(Vendor vendor) {
        return Optional.of(vendorRepo.saveAndFlush(vendor));
    }

    /**
     * Gets the courier based on id.
     * @param courierId the id of the courier
     * @return empty optional if courier DNE, optional of Courier otherwise
     */
    public Optional<Courier> getCourierById(Long courierId) {
        return courierRepo.findById(courierId);
    }


    /**
     * Tries to add a new vendor with given id to the database
     * @param vendorId the id to create a vendor with
     * @return an optional of the added vendor (or empty optional)
     */
    public Optional<Vendor> makeVendorById(Long vendorId) {
        Vendor vendor = new Vendor().id(vendorId);
        return Optional.of(vendorRepo.saveAndFlush(vendor));
    }

    /**
     * Tries to add the given courier to the database
     * @param courier the courier to add
     * @return an optional of the added courier (or empty optional)
     */
    public Optional<Courier> makeCourier(Courier courier) {
        return Optional.of(courierRepo.saveAndFlush(courier));
    }

    /**
     * Tries to add a new courier with given id to the database
     * @param courierId the id to create a courier with
     * @return an optional of the added courier (or empty optional)
     */
    public Optional<Courier> makeCourierById(Long courierId) {
        Courier courier = new Courier().id(courierId);
        return Optional.of(courierRepo.saveAndFlush(courier));
    }

    public boolean existsCourier(Long id) throws IllegalArgumentException {
        return courierRepo.existsById(id);
    }

    public boolean existsVendor(Long id) {
        return vendorRepo.existsById(id);
    }
}
