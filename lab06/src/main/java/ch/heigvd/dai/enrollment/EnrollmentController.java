package ch.heigvd.dai.enrollment;

import ch.heigvd.dai.subjects.Subject;
import ch.heigvd.dai.users.User;
import io.javalin.http.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.aayushatharva.brotli4j.common.annotations.Local;

class OverviewSubmissionDTO {
  public Subject subject;

  public double avgBeforeExam;
  public Map<Double, Double> prevAvg;

  OverviewSubmissionDTO() {
  }
}

class GradeSubmissionDTO {

  public String gradeType;
  public Double grade;

  GradeSubmissionDTO() {
  }
}

public class EnrollmentController {

  private final ConcurrentHashMap<Integer, User> users;
  private final ConcurrentHashMap<Integer, Subject> subjects;

  private final CopyOnWriteArrayList<Enrollment> enrollments;

  private final CopyOnWriteArrayList<Double> labGrades;
  private final CopyOnWriteArrayList<Double> courseGrades;

  private final ConcurrentHashMap<Integer, LocalDateTime> cacheOverview; // Integer = userId

  public EnrollmentController(ConcurrentHashMap<Integer, User> users, ConcurrentHashMap<Integer, Subject> subjects) {
    this.users = users;
    this.subjects = subjects;
    this.enrollments = new CopyOnWriteArrayList<>();
    this.labGrades = new CopyOnWriteArrayList<>();
    this.courseGrades = new CopyOnWriteArrayList<>();
    this.cacheOverview = new ConcurrentHashMap<>();
  }

  public void create(Context ctx) {
    Integer userId = ctx.pathParamAsClass("userId", Integer.class).get();
    Integer subjectId = ctx.pathParamAsClass("subjectId", Integer.class).get();

    if (!users.containsKey(userId)) {
      throw new NotFoundResponse("User not found");
    }

    if (!subjects.containsKey(subjectId)) {
      throw new NotFoundResponse("Subject not found");
    }

    for (Enrollment e : enrollments) {
      if (e.userId.equals(userId) && e.subjectId.equals(subjectId)) {
        throw new ConflictResponse("Enrollment already exists");
      }
    }

    Enrollment e = new Enrollment(userId, subjectId);

    enrollments.add(e);

    LocalDateTime now = LocalDateTime.now();
    cacheOverview.put(userId, now);

    ctx.status(HttpStatus.CREATED);
    ctx.header("Last-Modified", String.valueOf(now));
    ctx.json(e);
  }

  public void delete(Context ctx) {
    Integer userId = ctx.pathParamAsClass("userId", Integer.class).get();
    Integer subjectId = ctx.pathParamAsClass("subjectId", Integer.class).get();

    if (!users.containsKey(userId)) {
      throw new NotFoundResponse("User not found");
    }

    if (!subjects.containsKey(subjectId)) {
      throw new NotFoundResponse("Subject not found");
    }

    LocalDateTime lastKnownModification = ctx.headerAsClass("If-Unmodified-Since", LocalDateTime.class)
        .getOrDefault(null);
    if (lastKnownModification != null) {
      LocalDateTime lastModified = cacheOverview.get(userId);

      if (lastModified != null && lastModified.isAfter(lastKnownModification)) {
        throw new PreconditionFailedResponse();
      }
    }

    int enrollmentIdx = -1;
    for (Enrollment e : enrollments) {
      if (e.userId.equals(userId) && e.subjectId.equals(subjectId)) {
        enrollmentIdx = enrollments.indexOf(e);
      }
    }

    if (enrollmentIdx != -1) {
      enrollments.remove(enrollmentIdx);

      LocalDateTime now = LocalDateTime.now();
      cacheOverview.put(userId, now);

      ctx.header("Last-Modified", String.valueOf(now));
      ctx.status(HttpStatus.NO_CONTENT);
    } else {
      throw new NotFoundResponse("Enrollment not found");
    }
  }

  // TODO: Will probably need caching verifications as it is used in overview
  public void addGradeToSubject(Context ctx) {
    Integer userId = ctx.pathParamAsClass("userId", Integer.class).get();
    Integer subjectId = ctx.pathParamAsClass("subjectId", Integer.class).get();
    GradeSubmissionDTO grade = ctx.bodyValidator(GradeSubmissionDTO.class)
        .check(obj -> obj.gradeType != null, "Missing grade type")
        .check(obj -> obj.grade != null, "Missing grade")
        .get();

    int enrollmentIdx = -1;
    for (Enrollment e : enrollments) {
      if (e.userId == userId && e.subjectId == subjectId) {
        enrollmentIdx = enrollments.indexOf(e);
      }
    }

    if (enrollmentIdx == -1) {
      throw new NotFoundResponse("No enrollment with these id exist");
    }

    Enrollment e = enrollments.get(enrollmentIdx);
    switch (grade.gradeType) {
      case "labGrade":
        e.labGrades.add(grade.grade);
        break;
      case "courseGrade":
        e.courseGrades.add(grade.grade);
        break;
      default:
        throw new BadRequestResponse("Grade type is wrong");
    }

    LocalDateTime now = LocalDateTime.now();

    cacheOverview.put(userId, now);

    ctx.header("Last-Modified", String.valueOf(now));
    ctx.status(HttpStatus.OK);
  }

  public void overview(Context ctx) {
    Integer userId = ctx.pathParamAsClass("userId", Integer.class).get();

    User usr = users.get(userId);

    if (usr == null) {
      throw new NotFoundResponse("Not found: No user with this id");
    }

    LocalDateTime lastKnownModification = ctx.headerAsClass("If-Unmodified-Since", LocalDateTime.class)
        .getOrDefault(null);

    if (lastKnownModification != null) {
      LocalDateTime lastModified = cacheOverview.get(userId);
      if (lastKnownModification != null && lastModified.isAfter(lastKnownModification)) {
        throw new PreconditionFailedResponse();
      }
    }

    List<OverviewSubmissionDTO> overviewBySubjects = new ArrayList<>();

    for (Enrollment e : enrollments) {
      if (e.userId == userId) {
        OverviewSubmissionDTO overview = new OverviewSubmissionDTO();

        overview.subject = subjects.get(e.subjectId);
        overview.avgBeforeExam = e.avgBeforeExam();
        overview.prevAvg = e.previsionnalAvg();

        overviewBySubjects.add(overview);
      }
    }

    if (overviewBySubjects.isEmpty()) {
      throw new NotFoundResponse("No subject associated with this user");
    }

    LocalDateTime lastModified = cacheOverview.getOrDefault(userId, LocalDateTime.now());

    ctx.status(HttpStatus.OK);
    ctx.header("Last-Modified", String.valueOf(lastModified));
    ctx.json(overviewBySubjects);
  }
}
