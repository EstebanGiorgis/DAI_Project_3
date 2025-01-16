package ch.heigvd.poo;

import java.io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;

import java.util.concurrent.ConcurrentHashMap;

public class AuthController {
    private final ConcurrentHashMap<Integer, User> users;

    public AuthController(ConcurrentHashMap<Integer, User> users) {
        this.users = users;
    }

    // Méthode pour se connecter
    public void login(Context ctx) {
        User loginUser = ctx.bodyValidator(User.class)
                .check(obj -> obj.email != null, "Missing email")
                .check(obj -> obj.password != null, "Missing password")
                .get();

        for (User user : users.values()) {
            if (user.email.equalsIgnoreCase(loginUser.email) && user.password.equals(loginUser.password)) {
                // Ajouter un cookie pour identifier l'utilisateur
                ctx.cookie("user", String.valueOf(user.id));

                // Ajouter un cookie pour le rôle de l'utilisateur (ici LOGGED_IN par défaut)
                ctx.cookie("role", Role.LOGGED_IN.name());

                ctx.status(HttpStatus.NO_CONTENT);
                return;
            }
        }

        throw new UnauthorizedResponse();
    }

    // Méthode pour se déconnecter
    public void logout(Context ctx) {
        ctx.removeCookie("user");
        ctx.removeCookie("role"); // Supprimer également le rôle
        ctx.status(HttpStatus.NO_CONTENT);
    }

    // Méthode pour vérifier si l'utilisateur est connecté
    public boolean isLoggedIn(Context ctx) {
        String role = ctx.cookie("role");
        return role != null && role.equals(Role.LOGGED_IN.name());
    }

    // Méthode pour vérifier si l'utilisateur est admin
    public boolean isAdmin(Context ctx) {
        String role = ctx.cookie("role");
        return role != null && role.equals(Role.ADMIN.name());
    }
}
