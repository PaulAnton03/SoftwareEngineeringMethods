package nl.tudelft.sem.template.example.utils;

import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.user.CourierRepository;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbUtils {

    private final OrderRepository orderRepo;
    private final VendorRepository vendorRepo;
    private final CourierRepository courierRepo;

    /**
     * Constructor for DBUtils.
     *
     * @param orderRepo   The order repository.
     * @param vendorRepo  The vendor repository.
     * @param courierRepo The courier repository.
     */
    @Autowired
    public DbUtils(OrderRepository orderRepo, VendorRepository vendorRepo, CourierRepository courierRepo) {
        this.orderRepo = orderRepo;
        this.vendorRepo = vendorRepo;
        this.courierRepo = courierRepo;
    }

    public boolean userBelongsToOrder(Long userId, Long orderId) {
        return orderRepo.existsByIdAndCourierId(orderId, userId)
            || orderRepo.existsByIdAndVendorId(orderId, userId);
    }

    public boolean courierBelongsToVendor(Long courierId, Long vendorId) {
        return courierRepo.existsByIdAndBossId(courierId, vendorId);
    }

}
