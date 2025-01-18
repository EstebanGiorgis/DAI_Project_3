package ch.heigvd.dai.users;

import io.javalin.http.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import java.time.LocalDateTime;

public class UsersController {

  private final Integer RESERVED_ID_TO_IDENTIFY_ALL_USERS = -1;
  private final ConcurrentHashMap<Integer, User> users;
  private final ConcurrentHashMap<Integer, LocalDateTime> usersCache;
  private final AtomicInteger userId = new AtomicInteger(1);

  public UsersController(ConcurrentHashMap<Integer, User> users, ConcurrentHashMap<Integer, LocalDateTime> usersCache) {
    this.users = users;
    this.usersCache = usersCache;
  }

  public void create(Context ctx) {
    User newUser = ctx.bodyValidator(User.class)
        .check(obj -> obj.firstName != null, "Missing first name")
        .check(obj -> obj.lastName != null, "Missing last name")
        .check(obj -> obj.username != null, "Missing username")
        .check(obj -> obj.password != null, "Missing password")
        .get();

    for (User u : users.values()) {
      if (u.username.equalsIgnoreCase(newUser.username)) {
        throw new ConflictResponse("User already exists with this username");
      }
    }

    // Not using newUser for security purpose (be sure of the datas in the object)
    User user = new User();

    user.id = userId.getAndIncrement();
    user.firstName = newUser.firstName;
    user.lastName = newUser.lastName;
    user.username = newUser.username;
    user.password = newUser.password;

    users.put(user.id, user);

    LocalDateTime now = LocalDateTime.now();
    usersCache.put(user.id, now);

    // Remove entry representing all users to invalidate cache for list of users
    usersCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);

    ctx.status(HttpStatus.CREATED);
    ctx.header("Last-Modified", String.valueOf(now)); // Setup cache in header
    ctx.json(user);
  }

  public void update(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    LocalDateTime lastKnownModification = ctx.headerAsClass("If-Unmodified-Since", LocalDateTime.class)
        .getOrDefault(null);

    if (lastKnownModification != null && !usersCache.get(id).equals(lastKnownModification)) {
      throw new PreconditionFailedResponse();
    }

    // TODO: Do not assert empty params, just update present ones
    User updateUser = ctx.bodyValidator(User.class)
        .check(obj -> obj.firstName != null, "Missing first name")
        .check(obj -> obj.lastName != null, "Missing last name")
        .get();

    User user = users.get(id);

    if (user == null) {
      throw new NotFoundResponse();
    }

    // TODO: Update should be done only on modified fields
    user.firstName = updateUser.firstName;
    user.lastName = updateUser.lastName;

    users.put(id, user);

    LocalDateTime now;
    if (usersCache.containsKey(user.id)) {
      now = usersCache.get(user.id);
    } else {
      now = LocalDateTime.now();
      usersCache.put(user.id, now);

      usersCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);
    }
    ctx.status(HttpStatus.CREATED);
    ctx.header("Last-Modified", String.valueOf(now));
    ctx.json(user);
  }

  public void delete(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    if (!users.containsKey(id)) {
      throw new NotFoundResponse("Not found: No user with this id exist");
    }

    users.remove(id);

    usersCache.remove(id);

    usersCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);
    ctx.status(HttpStatus.NO_CONTENT);
  }

  // INFO: Can be used if we implement a admin domain
  public void getOne(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    User user = users.get(id);

    if (user == null) {
      throw new NotFoundResponse();
    }

    ctx.json(user);
  }

  // INFO: Can be used if we implement a admin domain
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
}
