package nl.tudelft.sem.template.example.domain.user;

import nl.tudelft.sem.template.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    List<Vendor> findVendorsByHasCouriers(boolean bool);
}
