package nl.tudelft.sem.template.example.domain.order;

import java.util.List;
import nl.tudelft.sem.template.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByVendorIdAndStatus(Long vendorId, Order.StatusEnum status);

    List<Order> findByStatus(Order.StatusEnum status);
}
