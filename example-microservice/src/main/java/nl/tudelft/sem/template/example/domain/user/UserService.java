package nl.tudelft.sem.template.example.domain.user;

import nl.tudelft.sem.template.model.Courier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private CourierRepository courierRepo;

    public UserService(CourierRepository courierRepo) {
        this.courierRepo = courierRepo;
    }


    /**
     * Attempts to update the bossId of the courier.
     * Vendors use this.
     *
     * @param courierId the id of the courier
     * @param bossId    the new bossId of the courier
     * @return the optional of updated courier object, empty if the courier was not found
     */
    public Optional<Long> updateBossIdOfCourier(Long courierId, Long bossId) {
        Optional<Courier> courier = courierRepo.findById(courierId);

        if(courier.isEmpty()){
            return Optional.empty();
        }

        Courier c = courier.get();
        c.setBossId(bossId);

        return Optional.of(courierRepo.saveAndFlush(c).getBossId());
    }


    /**
     * Gets the courier based on id.
     *
     * @param courierId the id of the courier
     * @return empty optional if courier DNE, optional of Courier otherwise
     */
    public Optional<Courier> getCourierById(Long courierId) {
        return courierRepo.findById(courierId);
    }
}
