package ch.heigvd.dai;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;

import java.util.Set;

public class AuthRoleController {
    public static void checkRole(Context requestContent, Set<Role> permittedRoles) {
        String giveRoleInCookie = requestContent.cookie("role");
        Role userRole = giveRoleInCookie == null ? Role.ANYONE: Role.valueOf(giveRoleInCookie);

        if (!permittedRoles.contains(userRole)) {
            throw new ForbiddenResponse("Accès non authorisé");
        }
    }
}




/*                               POUR CRÉER ENSUITE LES ROUTE IL FAUDRA APPELER LA CLASS AuthRoleController
import io.javalin.Javalin;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        // Route publique
        app.get("/public", ctx -> ctx.result("This is a public page!"));

        // Route pour utilisateurs connectés
        app.get("/user", ctx -> {
            RoleAuthorization.authorize(ctx, Set.of(Role.LOGGED_IN, Role.ADMIN));
            ctx.result("Welcome, User!");
        });

        // Route pour administrateurs
        app.get("/admin", ctx -> {
            RoleAuthorization.authorize(ctx, Set.of(Role.ADMIN));
            ctx.result("Welcome, Admin!");
        });
    }
}

 */
