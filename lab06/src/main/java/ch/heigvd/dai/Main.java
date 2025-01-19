package ch.heigvd.dai;

import io.javalin.Javalin;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import ch.heigvd.dai.users.*;
import ch.heigvd.dai.subjects.*;
import ch.heigvd.dai.enrollment.*;
import ch.heigvd.dai.Authentification.*;

public class Main {
  public static final int PORT = 8080;

  public static void main(String[] args) {

    ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    ConcurrentHashMap<Integer, LocalDateTime> usersCache = new ConcurrentHashMap<>();

    ConcurrentHashMap<Integer, Subject> subjects = new ConcurrentHashMap<>();
    ConcurrentHashMap<Integer, LocalDateTime> subjectsCache = new ConcurrentHashMap<>();


    UsersController usersController = new UsersController(usersCache);
    SubjectController subjectsController = new SubjectController(subjectsCache);
    EnrollmentController enrollmentController = new EnrollmentController();

    Javalin app = Javalin.create(config -> {
      config.validation.register(LocalDateTime.class, LocalDateTime::parse);
    });

    // Login et Logout
    app.post("/login", authController::login);
    app.post("/logout", authController::logout);

    // CRUD users
    app.post("/users/", usersController::create);
    app.put("/users/{id}", usersController::update);
    app.delete("/users/{id}", usersController::delete);

    // CRUD Subject
    app.post("/subjects/", subjectsController::create);
    app.put("/subjects/{id}", subjectsController::update);
    app.delete("/subjects/{id}", subjectsController::delete);

    // Enrollment (Links user and subjects)
    app.post("/enrollment/users/{userId}/subjects/{subjectId}", enrollmentController::create);
    app.post("/enrollment/users/{userId}/subjects/{subjectId}/addGrade", enrollmentController::addGradeToSubject);
    app.delete("/enrollment/users/{userId}/subjects/{subjectId}", enrollmentController::delete);
    app.get("/enrollment/users/{userId}/overview", enrollmentController::overview);
    app.start(PORT);
  }
}