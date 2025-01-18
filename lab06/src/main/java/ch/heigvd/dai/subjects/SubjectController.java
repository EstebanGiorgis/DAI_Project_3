package ch.heigvd.dai.subjects;

import ch.heigvd.dai.data.Data;
import ch.heigvd.dai.users.User;
import io.javalin.http.*;
import org.dizitart.no2.NitriteId;

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

    try (Data<Subject> data = new Data<>(Subject.class)) {
      data.save(subject);
      LocalDateTime now = LocalDateTime.now();
      subjectsCache.put(subject.id, now);

      subjectsCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);

      ctx.status(HttpStatus.CREATED);
      ctx.header("Last-Modified", String.valueOf(now));
      ctx.json(subject);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void update(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    LocalDateTime lastKnownModification = ctx.headerAsClass("If-Unmodified-Since", LocalDateTime.class)
        .getOrDefault(null);

    if (lastKnownModification != null && !subjectsCache.get(id).equals(lastKnownModification)) {
      throw new PreconditionFailedResponse();
    }

    Subject updateSub = ctx.bodyValidator(Subject.class)
            .check(obj -> obj.shortName != null, "Missing shortName")
            .check(obj -> obj.fullName != null, "Missing fullName").get();


    try (Data<Subject> data = new Data<>(Subject.class)) {
      NitriteId nitriteId = NitriteId.createId(Long.valueOf(id));
      Subject sub = data.repository.getById(nitriteId);
      sub.shortName = updateSub.shortName;
      sub.fullName = updateSub.fullName;
      data.update(sub);
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

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void delete(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();

    try (Data<Subject> data = new Data<>(Subject.class)) {
      NitriteId nitriteId = NitriteId.createId(Long.valueOf(id));
      Subject sub = data.repository.getById(nitriteId);
      data.delete(sub);
      subjectsCache.remove(id);
      subjectsCache.remove(RESERVED_ID_TO_IDENTIFY_ALL_USERS);
      ctx.status(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
