package ch.heigvd.dai;

import io.javalin.Javalin;
import java.util.concurrent.ConcurrentHashMap;

import ch.heigvd.dai.users.*;

public class Main {
  public static final int PORT = 8080;

  public static void main(String[] args) {

    ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    UsersController usersController = new UsersController(users);

    Javalin app = Javalin.create();

    // CRUD users

    // TODO: Add parameters
    app.post("/users/", usersController::create);
    app.get("/users/{id}", usersController::overview);

    // TODO: Add parameters
    app.put("/users/{id}", usersController::update);
    app.delete("/users/{id}", usersController::delete);

    app.start(PORT);

  }

}
