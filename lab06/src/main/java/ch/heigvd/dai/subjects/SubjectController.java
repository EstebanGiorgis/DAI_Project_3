package ch.heigvd.dai.subjects;

import ch.heigvd.dai.data.Data;
import ch.heigvd.dai.users.User;
import io.javalin.http.*;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SubjectController {

  private final Integer RESERVED_ID_TO_IDENTIFY_ALL_USERS = -1;
  private final ConcurrentHashMap<Integer, LocalDateTime> subjectsCache;
  private final AtomicInteger subjectId = new AtomicInteger(1);

  public SubjectController(ConcurrentHashMap<Integer, LocalDateTime> subjectsCache) {
    this.subjectsCache = subjectsCache;
  }

  public void create(Context ctx) {
    Subject newSubject = ctx.bodyValidator(Subject.class)
        .check(obj -> obj.shortName != null, "Missing short name")
        .check(obj -> obj.fullName != null, "Missing full name")
        .get();

    Subject subject = new Subject();

    subject.id = subjectId.getAndIncrement();
    subject.shortName = newSubject.shortName;
    subject.fullName = newSubject.fullName;

    Data.create(subject, Subject.class);

    LocalDateTime now = LocalDateTime.now();
    subjectsCache.put(subject.id, now);

    subjectsCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);

    ctx.status(HttpStatus.CREATED);
    ctx.header("Last-Modified", String.valueOf(now));
    ctx.json(subject);
  }

  public void update(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    LocalDateTime lastKnownModification = ctx.headerAsClass("If-Unmodified-Since", LocalDateTime.class)
        .getOrDefault(null);

    if (lastKnownModification != null && !subjectsCache.get(id).equals(lastKnownModification)) {
      throw new PreconditionFailedResponse();
    }

    Subject updateSub = ctx.bodyAsClass(Subject.class);

    Subject sub = Data.get(id.toString(), Subject.class, true);

    if(sub == null) {
      throw new NotFoundResponse();
    }

    if(updateSub.shortName != null) {
      sub.shortName = updateSub.shortName;
    }
    if(updateSub.fullName != null) {
      sub.fullName = updateSub.fullName;
    }

    Data.update(sub, Subject.class);

    LocalDateTime now;
    if (subjectsCache.containsKey(sub.id)) {
      now = subjectsCache.get(sub.id);
    } else {
      now = LocalDateTime.now();
      subjectsCache.put(sub.id, now);
      subjectsCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);
    }

    ctx.status(HttpStatus.CREATED);
    ctx.header("Last-Modified", String.valueOf(now));
    ctx.json(sub);
  }

  public void delete(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();
    Data.delete(id.toString(), Subject.class, true);
    subjectsCache.remove(id);
    subjectsCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);
    ctx.status(HttpStatus.NO_CONTENT);
  }
}
