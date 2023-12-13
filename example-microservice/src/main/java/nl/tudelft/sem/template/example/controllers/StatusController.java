package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.api.StatusApi;
import org.springframework.http.ResponseEntity;

public class StatusController implements StatusApi {
    @Override
    public ResponseEntity<Void> updateToAccepted(Long orderId, Long authorization) {
        return StatusApi.super.updateToAccepted(orderId, authorization);
    }
}
