package nl.tudelft.sem.template.example.domain.user;

import org.springframework.beans.factory.annotation.Autowired;

public class CourierService {

    private final CourierRepository courierRepo;

    @Autowired
    public CourierService(CourierRepository courierRepo) {
        this.courierRepo = courierRepo;
    }

}
