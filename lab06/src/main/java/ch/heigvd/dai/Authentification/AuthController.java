package ch.heigvd.dai.Authentification;

import ch.heigvd.dai.users.User;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;


import java.util.concurrent.ConcurrentHashMap;


public class AuthController {
    private final ConcurrentHashMap<Integer, User> users;

    public AuthController(ConcurrentHashMap<Integer, User> users){
        this.users = users;

    }

    public void login(Context requestContent) {
        User loginUser = requestContent.bodyValidator(User.class)
                .check(obj -> obj.username != null, "Missing username")
                .check(obj -> obj.password != null, "Missing password")
                .get();

        for (User user : users.values()) {

            if (user.username.equalsIgnoreCase(loginUser.username) && user.password.equals(loginUser.password)) {

                requestContent.cookie("user", String.valueOf(user.id));

                requestContent.status(HttpStatus.NO_CONTENT);
                return;
            }
        }

        throw new UnauthorizedResponse();
    }

    public void logout(Context requestContent) {
        requestContent.removeCookie("user");
        requestContent.status(HttpStatus.NO_CONTENT);
    }

}
