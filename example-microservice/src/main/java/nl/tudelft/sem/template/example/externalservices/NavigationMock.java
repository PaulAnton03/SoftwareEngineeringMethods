package nl.tudelft.sem.template.example.externalservices;

import nl.tudelft.sem.template.model.Time;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class NavigationMock {

    public NavigationMock() {
    }

    public OffsetDateTime getETA(Long orderId, Time timeValues) {
        // this is a mock, so it does not actually do anything with the time values or the id

        return OffsetDateTime.of(2024, 1, 9,
                11, 45, 0, 0, ZoneOffset.ofTotalSeconds(0));
    }
}
