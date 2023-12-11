package nl.tudelft.sem.template.example.domain.order;

import nl.tudelft.sem.template.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderService {

    public OrderRepository orderRepo;


    public OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    public Location getFinalDestinationOfOrder(BigDecimal orderId){
//        orderRepo.getOne(orderId);
        return null;
    }
}
