package ch.heigvd.dai.enrollment;

import ch.heigvd.dai.subjects.Subject;
import ch.heigvd.dai.users.User;
import io.javalin.http.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.aayushatharva.brotli4j.common.annotations.Local;

import ch.heigvd.dai.data.Data;
import org.dizitart.no2.objects.filters.ObjectFilters;

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

  private final ConcurrentHashMap<Integer, LocalDateTime> cacheOverview; // Integer = userId

  public EnrollmentController() {
    this.cacheOverview = new ConcurrentHashMap<>();
  }

  public void create(Context ctx) {
    Integer userId = ctx.pathParamAsClass("userId", Integer.class).get();
    Integer subjectId = ctx.pathParamAsClass("subjectId", Integer.class).get();

    Enrollment en = new Enrollment(userId, subjectId);

    Data.create(en, Enrollment.class);

    LocalDateTime now = LocalDateTime.now();
    cacheOverview.put(userId, now);

    ctx.status(HttpStatus.CREATED);
    ctx.header("Last-Modified", String.valueOf(now));
    ctx.json(en);
  }

  public void delete(Context ctx) {
    Integer userId = ctx.pathParamAsClass("userId", Integer.class).get();
    Integer subjectId = ctx.pathParamAsClass("subjectId", Integer.class).get();

    LocalDateTime lastKnownModification = ctx.headerAsClass("If-Unmodified-Since", LocalDateTime.class)
        .getOrDefault(null);
    if (lastKnownModification != null) {
      LocalDateTime lastModified = cacheOverview.get(userId);

      if (lastModified != null && lastModified.isAfter(lastKnownModification)) {
        throw new PreconditionFailedResponse();
      }
    }
    Data.delete(userId + "_" + subjectId, Enrollment.class, false);
    ctx.status(HttpStatus.NO_CONTENT);
  }

  // TODO: Will probably need caching verifications as it is used in overview
  public void addGradeToSubject(Context ctx) {
    Integer userId = ctx.pathParamAsClass("userId", Integer.class).get();
    Integer subjectId = ctx.pathParamAsClass("subjectId", Integer.class).get();
    GradeSubmissionDTO grade = ctx.bodyValidator(GradeSubmissionDTO.class)
        .check(obj -> obj.gradeType != null, "Missing grade type")
        .check(obj -> obj.grade != null, "Missing grade")
        .get();

    Enrollment enr = Data.get(userId + "_" + subjectId, Enrollment.class, false);

    switch (grade.gradeType) {
      case "labGrade":
        enr.labGrades.add(grade.grade);
        break;
      case "courseGrade":
        enr.courseGrades.add(grade.grade);
        break;
      default:
        throw new BadRequestResponse("Grade type is wrong");
    }

    Data.update(enr, Enrollment.class);

    LocalDateTime now = LocalDateTime.now();

    cacheOverview.put(userId, now);

    ctx.header("Last-Modified", String.valueOf(now));
    ctx.status(HttpStatus.OK);
  }

  public void overview(Context ctx) {
    Integer userId = ctx.pathParamAsClass("userId", Integer.class).get();

    User usr = Data.get(userId.toString(), User.class, true);

    LocalDateTime lastKnownModification = ctx.headerAsClass("If-Unmodified-Since", LocalDateTime.class)
        .getOrDefault(null);

    if (lastKnownModification != null) {
      LocalDateTime lastModified = cacheOverview.get(userId);
      if (lastKnownModification != null && lastModified.isAfter(lastKnownModification)) {
        throw new PreconditionFailedResponse();
      }
    }

    List<OverviewSubmissionDTO> overviewBySubjects = new ArrayList<>();
    List<Enrollment> enrollments = Data.getAll(Enrollment.class);
    if (enrollments == null) {
      throw new NotFoundResponse("No enrollments found");
    }

    for (Enrollment enr : enrollments) {
      if (Objects.equals(enr.userId, userId)) {
        OverviewSubmissionDTO overview = new OverviewSubmissionDTO();
        overview.subject = Data.get(enr.subjectId.toString(), Subject.class, true);
        overview.avgBeforeExam = enr.avgBeforeExam();
        overview.prevAvg = enr.previsionnalAvg();
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
