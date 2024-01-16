package nl.tudelft.sem.template.example.domain.user;

import org.springframework.beans.factory.annotation.Autowired;

public class VendorService {

    private final VendorRepository vendorRepo;

    @Autowired
    public VendorService(VendorRepository vendorRepo) {
        this.vendorRepo = vendorRepo;
    }
}
