package ch.heigvd.dai.users;


import ch.heigvd.dai.data.Data;
import ch.heigvd.dai.subjects.Subject;

import io.javalin.http.*;
import org.dizitart.no2.exceptions.NitriteException;
import org.dizitart.no2.exceptions.UniqueConstraintException;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import java.time.LocalDateTime;

public class UsersController {

  private final Integer RESERVED_ID_TO_IDENTIFY_ALL_USERS = -1;
  private final ConcurrentHashMap<Integer, LocalDateTime> usersCache;
  private final AtomicInteger userId = new AtomicInteger(1);

  public UsersController(ConcurrentHashMap<Integer, LocalDateTime> usersCache) {
    this.usersCache = usersCache;
  }

  public void create(Context ctx) {
    System.out.println(ctx.body());
    User newUser = ctx.bodyValidator(User.class)
        .check(obj -> obj.firstName != null, "Missing first name")
        .check(obj -> obj.lastName != null, "Missing last name")
        .check(obj -> obj.username != null, "Missing username")
        .check(obj -> obj.password != null, "Missing password")
        .get();

    // Not using newUser for security purpose (be sure of the datas in the object)
    User user = new User();

    user.id = userId.getAndIncrement();
    user.firstName = newUser.firstName;
    user.lastName = newUser.lastName;
    user.username = newUser.username;
    user.password = newUser.password;

    Data.create(user, User.class);

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

    User updateUser = ctx.bodyAsClass(User.class);

    User usr = Data.get(id.toString(), User.class, true);

    if(usr == null) {
      throw new NotFoundResponse();
    }

    if (updateUser.firstName != null) {
      usr.firstName = updateUser.firstName;
    }
    if (updateUser.lastName != null) {
      usr.lastName = updateUser.lastName;
    }
    if(updateUser.password != null) {
      usr.password = updateUser.password;
    }
    if(updateUser.username != null) {
      usr.username = updateUser.username;
    }

    Data.update(usr, User.class);
    LocalDateTime now;
    if (usersCache.containsKey(usr.id)) {
      now = usersCache.get(usr.id);
    } else {
      now = LocalDateTime.now();
      usersCache.put(usr.id, now);
      usersCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);
    }

    ctx.status(HttpStatus.CREATED);
    ctx.header("Last-Modified", String.valueOf(now));
    ctx.json(usr);
  }

  public void delete(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    Data.delete(id.toString(), User.class, true);

    usersCache.remove(id);
    usersCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);
    ctx.status(HttpStatus.NO_CONTENT);
  }
}
