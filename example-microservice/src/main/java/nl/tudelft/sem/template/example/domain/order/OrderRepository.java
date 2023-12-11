package nl.tudelft.sem.template.example.domain.order;

import nl.tudelft.sem.template.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
