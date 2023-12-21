package nl.tudelft.sem.template.example.domain.user;

import nl.tudelft.sem.template.example.domain.order.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private CourierRepository courierRepo;

    public UserService(CourierRepository courierRepo) {
        this.courierRepo = courierRepo;
    }


}
