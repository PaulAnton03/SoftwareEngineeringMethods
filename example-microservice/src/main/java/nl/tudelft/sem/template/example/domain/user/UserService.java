package nl.tudelft.sem.template.example.domain.user;

import nl.tudelft.sem.template.model.Courier;
import nl.tudelft.sem.template.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.Optional;

@Service
public class UserService {

    private CourierRepository courierRepo;

    public UserService(CourierRepository courierRepo) {
        this.courierRepo = courierRepo;
    }


    /**
     * Attempts to update the bossId of the courier.
     * Vendors use this.
     *
     * @param courierId the id of the courier
     * @param bossId    the new bossId of the courier
     * @return the optional of updated courier object, empty if the courier was not found
     */
    public Optional<Long> updateBossIdOfCourier(Long courierId, Long bossId) {
        Optional<Courier> courier = courierRepo.findById(courierId);

        if(courier.isEmpty()){
            return Optional.empty();
        }

        Courier c = courier.get();
        c.setBossId(bossId);

        return Optional.of(courierRepo.saveAndFlush(c).getBossId());
    }


    private final VendorRepository vendorRepo;

    @Autowired
    public UserService(VendorRepository vendorRepo) {
        this.vendorRepo = vendorRepo;
    }

    /**
     * Tries to add the given vendor to the database
     * @param vendor the vendor to add
     * @return an optional of the added vendor (or empty optional)
     */
    public Optional<Vendor> makeVendor(Vendor vendor) {
        try{
            Vendor saved = vendorRepo.save(vendor);
            return Optional.of(saved);
        }
        catch (IllegalArgumentException e){
            return Optional.empty();
        }
    }

    /**
     * Tries to add a new vendor with given id to the database
     * @param vendorId the id to create a vendor with
     * @return an optional of the added vendor (or empty optional)
     */
    public Optional<Vendor> makeVendorById(Long vendorId) {
        try{
            Vendor vendor = new Vendor().id(vendorId);
            Vendor saved = vendorRepo.save(vendor);
            return Optional.of(saved);
        }
        catch (IllegalArgumentException e){
            return Optional.empty();
        }
    }
}
