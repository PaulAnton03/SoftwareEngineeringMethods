package nl.tudelft.sem.template.example.domain.admin;

import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import nl.tudelft.sem.template.example.domain.user.VendorRepository;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    VendorRepository vendorRepo;
    OrderRepository orderRepository;
    @Autowired
    public AdminService(VendorRepository vendorRepo, OrderRepository orderRepo) {
        this.vendorRepo = vendorRepo;
        this.orderRepository = orderRepo;
    }

    public Optional<List<Vendor>> updateDefaultRadius(Double body) {

        List<Vendor> vendors = vendorRepo.findVendorsByHasCouriers(false);

        if(vendors.isEmpty()){
            return Optional.empty();
        }
        for(Vendor v : vendors){
            v.setRadius(body);
            vendorRepo.saveAndFlush(v);
        }

        return Optional.of(vendors);
    }

    public Optional<Double> getDefaultRadius(){
        List<Vendor> vendors = vendorRepo.findVendorsByHasCouriers(false);

        if(vendors.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(vendors.get(0).getRadius());
    }

    public Optional<List<Order>> getDelivered() {
        List<Order> orders = orderRepository.findByStatus(Order.StatusEnum.DELIVERED);

        if(orders.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(orders);
    }
}
