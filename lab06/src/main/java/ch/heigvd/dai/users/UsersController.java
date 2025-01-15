package ch.heigvd.dai.users;

import ch.heigvd.dai.subjects.Subject;

import io.javalin.http.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UsersController {
  private final ConcurrentHashMap<Integer, User> users;
  private final AtomicInteger userId = new AtomicInteger(1);

  private List<Subject> subjects;

  public UsersController(ConcurrentHashMap<Integer, User> users) {
    subjects = new ArrayList<>();
    this.users = users;
  }

  public void addSubject(Context ctx) {
    Subject newSubject = ctx.bodyValidator(Subject.class)
        .check(obj -> obj.name() != null, "Missing subject name").get();

    Subject subject = new Subject(newSubject.name());

    subjects.add(subject);

    ctx.status(HttpStatus.CREATED);
    ctx.json(subject);
  }

  public void create(Context ctx) {
    User newUser = ctx.bodyValidator(User.class)
        .check(obj -> obj.firstName != null, "Missing first name")
        .check(obj -> obj.lastName != null, "Missing last name")
        .get();

    User user = new User();

    user.id = userId.getAndIncrement();
    user.firstName = newUser.firstName;
    user.lastName = newUser.lastName;

    users.put(user.id, user);

    ctx.status(HttpStatus.CREATED);
    ctx.json(user);
  }

  public void update(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    User updateUser = ctx.bodyValidator(User.class)
        .check(obj -> obj.firstName != null, "Missing first name")
        .check(obj -> obj.lastName != null, "Missing last name")
        .get();

    User user = users.get(id);

    if (user == null) {
      throw new NotFoundResponse();
    }

    user.firstName = updateUser.firstName;
    user.lastName = updateUser.lastName;

    users.put(id, user);

    ctx.json(user);
  }

  public void getOne(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    User user = users.get(id);

    if (user == null) {
      throw new NotFoundResponse();
    }

    ctx.json(user);
  }

  public void getMany(Context ctx) {
    String firstName = ctx.queryParam("firstName");
    String lastName = ctx.queryParam("lastName");

    List<User> users = new ArrayList<>();

    for (User user : this.users.values()) {
      if (firstName != null && !user.firstName.equalsIgnoreCase(firstName)) {
        continue;
      }

      if (lastName != null && !user.lastName.equalsIgnoreCase(lastName)) {
        continue;
      }

      users.add(user);
    }

    ctx.json(users);
  }

  class OverviewSubmissionDTO {
    protected Subject subject;

    protected double avgBeforeExam;
    protected Map<Double, Double> prevAvg;

    OverviewSubmissionDTO() {
    }
  }

  public void overview(Context ctx) {
    Integer userId = ctx.pathParamAsClass("id", Integer.class).get();

    User usr = users.get(userId);

    if (usr == null) {
      throw new NotFoundResponse("Not found: No user with this id exist");
    }

    List<OverviewSubmissionDTO> overviewBySubjects = new ArrayList<>();

    // User found - Set the response body with the overview
    for (Subject sub : subjects) {
      OverviewSubmissionDTO overview = new OverviewSubmissionDTO();
      overview.subject = sub;
      overview.avgBeforeExam = overview.subject.avgBeforeExam();
      overview.prevAvg = overview.subject.previsionnalAvg();

      overviewBySubjects.add(overview);
    }

    ctx.status(HttpStatus.OK);
    ctx.json(overviewBySubjects);
  }

  public void delete(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    if (!users.containsKey(id)) {
      throw new NotFoundResponse("Not found: No user with this id exist");
    }

    users.remove(id);

    ctx.status(HttpStatus.NO_CONTENT);
  }
}
