package nl.tudelft.sem.template.example.domain;

import nl.tudelft.sem.template.model.DeliveryException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryExceptionRepository extends JpaRepository<DeliveryException, Long> {
}
