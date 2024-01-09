package nl.tudelft.sem.template.example.controllers;


import io.swagger.v3.oas.annotations.Parameter;
import nl.tudelft.sem.template.api.AdminApi;
import nl.tudelft.sem.template.example.authorization.AuthorizationService;
import nl.tudelft.sem.template.example.domain.admin.AdminService;
import nl.tudelft.sem.template.model.Vendor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController implements AdminApi {
    public AdminService adminService;
    public AuthorizationService authorizationService;
    public AdminController(AdminService adminService, AuthorizationService authorizationService) {
        this.adminService = adminService;
        this.authorizationService = authorizationService;
    }

    /**
     * PUT /admin/vendor/radius : Update the default radius for vendors that don&#39;t have their own couriers (independent vendors)
     * Update the default radius of all independent vendors. To be used by admin.
     *
     * @param authorization The userId to check if they have the rights to make this request (required)
     * @param body          Radius in meters to be set as a new value (required)
     * @return Successful response, default radius of vendors with independent couriers updated (status code 200)
     * or Unsuccessful, default radius cannot be updated because of a bad request (status code 400)
     * or Unsuccessful, entity does not have access rights to update radius. Only admins have this right. (status code 403)
     * or Unsuccessful, no default radius was found (status code 404)
     */
    @Override
    @PutMapping("/vendor/radius")
    public ResponseEntity<Void> updateDefaultRadius(
            @RequestParam(name = "authorization") Long authorization,
            @RequestBody Double body) {

        var auth = authorizationService.authorize(authorization, "updateDefaultRadius");
        if (auth.isPresent()) {
            return auth.get();
        }
        Optional<List<Vendor>> res = adminService.updateDefaultRadius(body);

        if (res.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
