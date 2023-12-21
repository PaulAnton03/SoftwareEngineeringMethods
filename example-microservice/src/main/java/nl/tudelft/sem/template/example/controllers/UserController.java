package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.api.UserApi;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.user.UserService;
import nl.tudelft.sem.template.model.Vendor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController implements UserApi {

    public UserService userService;

    public AuthorizationService authorizationService;

    public UserController(UserService userService, AuthorizationService authorizationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
    }

    /**
     * Adds the given user to the database
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param vendor  (optional)
     * @return the saved user
     */
    @Override
    @PostMapping("/vendor/add-whole")
    public ResponseEntity<Void> makeVendor(
            @RequestParam(name = "authorization") Long authorization,
            @RequestBody Vendor vendor) {

        Optional<ResponseEntity> authorizationResponse =
                authorizationService.authorize(authorization, "makeVendor");
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }

        Optional<Vendor> saved = userService.makeVendor(vendor);

        if (saved.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Creates a new user with given id and adds it to database
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param vendorId Id of the vendor to create (required)
     * @return the saved user
     */
    @Override
    @PostMapping("/vendor/{vendorId}")
    public ResponseEntity<Void> makeVendorById(
                        @RequestParam(name = "authorization") Long authorization,
                        @PathVariable(name = "vendorId") Long vendorId) {

        Optional<ResponseEntity> authorizationResponse =
                authorizationService.authorize(authorization, "makeVendorById");
        if (authorizationResponse.isPresent()) {
            return authorizationResponse.get();
        }

        Optional<Vendor> saved = userService.makeVendorById(vendorId);

        if (saved.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
