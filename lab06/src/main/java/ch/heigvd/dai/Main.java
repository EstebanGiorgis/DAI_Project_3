package ch.heigvd.dai;

import io.javalin.Javalin;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import ch.heigvd.dai.users.*;

public class Main {
  public static final int PORT = 8080;

  public static void main(String[] args) {

    ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    ConcurrentHashMap<Integer, LocalDateTime> usersCache = new ConcurrentHashMap<>();
    UsersController usersController = new UsersController(users, usersCache);

    Javalin app = Javalin.create();

    // CRUD users

    app.post("/users/", usersController::create);
    app.put("/users/{id}", usersController::update);
    app.delete("/users/{id}", usersController::delete);

    app.start(PORT);

  }

}
