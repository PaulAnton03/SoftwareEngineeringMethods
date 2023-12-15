package nl.tudelft.sem.template.example.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthorizationService {

    private static final int USER_SERVER_PORT = 5000;

    public enum UserType {
        VENDOR,
        COURIER,
        ADMIN,
        CUSTOMER,

        NAN
    }

    // Maps method names to the user types that are allowed to call them
    private HashMap<String, List<UserType>> permissions;

    // Default constructor. Use this one in test cases if you want to ignore authorization.
    public AuthorizationService(){

    }

    public AuthorizationService(HashMap<String, List<UserType>> permissions) {
        this.permissions = permissions;
    }

    /**
     * Authorizes a user based on the provided user ID and required user type.
     *
     * @param userId          The ID of the user to be authorized.
     * @param methodName      Name of the method that was called.
     * @return An optional containing a ResponseEntity with an error message if authorization fails, or empty if authorized.
     */
    public Optional<ResponseEntity> authorize(Long userId, String methodName) {
        UserType actualUserType = getUserTypeFromService(userId);
        if (actualUserType == UserType.NAN) {
            return Optional.of(ResponseEntity.status(500).body("Error while retrieving user type"));
        }
        if (actualUserType != UserType.ADMIN && !permissions.get(methodName).contains(actualUserType)) {
            return Optional.of(ResponseEntity.status(403).body("User with id " + userId + " does not have access rights"));
        }
        return Optional.empty();
    }

    /**
     * Retrieves the user type from the user microservice based on the provided user ID.
     *
     * @param userId The ID of the user.
     * @return The user type obtained from the user service, or UserType.NAN if an error occurs.
     */
    private UserType getUserTypeFromService(Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        String userTypeServiceEndpoint = "http://localhost:" + USER_SERVER_PORT + "/user/" + userId + "/type";
        try {
            String actualUserType = restTemplate.getForObject(userTypeServiceEndpoint, String.class);
            return parseUserType(actualUserType);
        } catch (Exception e) {
            return UserType.NAN;
        }
    }

    /**
     * Parses the string representation of a user type into the corresponding UserType enum value.
     *
     * @param userType The string representation of the user type.
     * @return The corresponding UserType enum value.
     * @throws IllegalArgumentException If the provided user type is invalid.
     */
    private UserType parseUserType(String userType) {
        return switch (userType) {
            case "vendor" -> UserType.VENDOR;
            case "courier" -> UserType.COURIER;
            case "admin" -> UserType.ADMIN;
            case "customer" -> UserType.CUSTOMER;
            default -> throw new IllegalArgumentException("Invalid user type: " + userType);
        };
    }

}
