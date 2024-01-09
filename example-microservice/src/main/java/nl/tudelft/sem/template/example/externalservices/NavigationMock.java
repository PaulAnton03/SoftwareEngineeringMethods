package nl.tudelft.sem.template.example.externalservices;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class NavigationMock {

    public NavigationMock() {
    }

    public OffsetDateTime getETA(Long orderId) {
        return OffsetDateTime.of(2024, 1, 9,
                11, 45, 0, 0, ZoneOffset.ofTotalSeconds(0));
    }
}
