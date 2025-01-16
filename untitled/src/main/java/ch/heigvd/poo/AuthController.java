package ch.heigvd.poo;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;

import java.util.concurrent.ConcurrentHashMap;


public class AuthController {
    private final ConcurrentHashMap<Integer, User> users;

    public AuthController(ConcurrentHashMap<Integer, User> users) {
        this.users = users;
    }

    public void login(Context requestContent) {
        User loginUser = requestContent.bodyValidator(User.class)
                .check(obj -> obj.email != null, "Missing email")
                .check(obj -> obj.password != null, "Missing password")
                .get();

        for (User user : users.values()) {

            //dans le cas ou l'utilisateur existe
            if (user.email.equalsIgnoreCase(loginUser.email) && user.password.equals(loginUser.password)) {
                //création d'un cookie avec l'id du user
                requestContent.cookie("user", String.valueOf(user.id));

                //création d'un cookie avec son role
                requestContent.cookie("role", Role.LOGGED_IN.name());

                requestContent.status(HttpStatus.NO_CONTENT);
                return;
            }
        }

        throw new UnauthorizedResponse();
    }

    // Méthode pour se déconnecter
    public void logout(Context requestContent) {
        requestContent.removeCookie("user");
        requestContent.removeCookie("role");
        requestContent.status(HttpStatus.NO_CONTENT);
    }

}
