package nl.tudelft.sem.template.example.domain;

import nl.tudelft.sem.template.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}
