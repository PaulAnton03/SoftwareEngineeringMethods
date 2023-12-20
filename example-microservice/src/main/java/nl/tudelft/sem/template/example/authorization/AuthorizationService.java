package nl.tudelft.sem.template.example.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.annotation.PostConstruct;
import nl.tudelft.sem.template.example.externalservices.UserExternalService;
import nl.tudelft.sem.template.example.utils.DbUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final UserExternalService userExternalService;
    private DbUtils dbUtils;
    private HashMap<String, List<Authorization.UserType>> permissions;

    private HashMap<String, BiFunction<Long, Long, Boolean>> validationMethods;

    /**
     * Constructor for the AuthorizationService.
     *
     * @param dbUtils             the dbUtils
     * @param userExternalService the userExternalService
     * @param permissions         the permissions
     * @param validationMethods   the validationMethods
     */
    public AuthorizationService(DbUtils dbUtils, UserExternalService userExternalService,
                                HashMap<String, List<Authorization.UserType>> permissions,
                                HashMap<String, BiFunction<Long, Long, Boolean>> validationMethods) {
        this.userExternalService = userExternalService;
        this.permissions = permissions;
        this.dbUtils = dbUtils;
        this.validationMethods = validationMethods;
    }


    /**
     * Checks if the user is authorized to call the method.
     *
     * @param userId     the id of the user
     * @param methodName the name of the method
     * @param other      id of order or vendor
     * @return an empty optional if the user is authorized, otherwise a response entity
     */
    public Optional<ResponseEntity> authorize(Long userId, String methodName, Long other) {
        Handler handler = Handler.link(new Authorization(userExternalService, permissions),
            new Validation(dbUtils, validationMethods));
        return handler.check(userId, methodName, other);
    }

    /**
     * Initializes the permissions map with default values if it is null.
     * You do not need to add permissions for admin only methods.
     * Initializes the validationMethods map with default values if it is null.
     */
    @PostConstruct
    private void init() throws NoSuchMethodException {
        if (permissions == null) {
            permissions = new HashMap<>(
                Map.of(//"Method name", List.of(UserType.ALLOWED_USER_TYPES) no need to add ADMIN
                ));

        }

        if (validationMethods == null) {
            validationMethods = new HashMap<>(
                Map.of(//"Method name", "dbUtils::userBelongsToOrder" or "dbUtils::courierBelongsToVendor")
                ));
        }

    }

}
