package nl.tudelft.sem.template.example.domain;

import nl.tudelft.sem.template.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRepository extends JpaRepository<Courier, Long> {
}
