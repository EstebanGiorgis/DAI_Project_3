package ch.heigvd.poo;

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
