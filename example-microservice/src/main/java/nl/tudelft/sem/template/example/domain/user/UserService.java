package nl.tudelft.sem.template.example.domain.user;

import nl.tudelft.sem.template.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
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
