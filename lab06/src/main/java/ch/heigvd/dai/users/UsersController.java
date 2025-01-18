package ch.heigvd.dai.users;

import ch.heigvd.dai.data.Data;
import ch.heigvd.dai.subjects.Subject;

import io.javalin.http.*;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.filters.Filters;

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

    try (Data<User> data = new Data<>(User.class)) {
      data.save(user);
    } catch (Exception e) {
      e.printStackTrace();
    }

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

    try (Data<User> data = new Data<>(User.class)) {
      NitriteId nitriteId = NitriteId.createId(Long.valueOf(id));
      User usr = data.repository.getById(nitriteId);
      usr.firstName = updateUser.firstName;
      usr.lastName = updateUser.lastName;
      data.update(usr);
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

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void delete(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    try (Data<User> data = new Data<>(User.class)) {
      NitriteId nitriteId = NitriteId.createId(Long.valueOf(id));
      User usr = data.repository.getById(nitriteId);
      data.delete(usr);
      usersCache.remove(id);
      usersCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);
      ctx.status(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // INFO: Can be used if we implement a admin domain
  public void getOne(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    try (Data<User> data = new Data<>(User.class)) {
      NitriteId nitriteId = NitriteId.createId(Long.valueOf(id));
      User usr = data.repository.getById(nitriteId);
      if (usr == null) {
        throw new NotFoundResponse();
      }
      ctx.json(usr);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // INFO: Can be used if we implement a admin domain
  public void getMany(Context ctx) {
    String firstName = ctx.queryParam("firstName");
    String lastName = ctx.queryParam("lastName");

    try (Data<User> data = new Data<>(User.class)) {
      List<User> usrs = data.repository.find().toList();
      Iterator<User> iterator = usrs.iterator();
      while (iterator.hasNext()) {
        User usr = iterator.next();
        if (firstName != null && !usr.firstName.equalsIgnoreCase(firstName) ||
                lastName != null && !usr.lastName.equalsIgnoreCase(lastName)) {
          iterator.remove();
        }
      }
      ctx.json(usrs);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
