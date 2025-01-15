package ch.heigvd.dai.users;

import io.javalin.http.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UsersController {
  private final ConcurrentHashMap<Integer, User> users;
  private final AtomicInteger userId = new AtomicInteger(1);

  public UsersController(ConcurrentHashMap<Integer, User> users) {
    this.users = users;
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

  public void delete(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    if (!users.containsKey(id)) {
      throw new NotFoundResponse();
    }

    users.remove(id);

    ctx.status(HttpStatus.NO_CONTENT);
  }
}
