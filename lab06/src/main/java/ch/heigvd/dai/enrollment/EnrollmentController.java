package ch.heigvd.dai.enrollment;

import ch.heigvd.dai.subjects.Subject;
import ch.heigvd.dai.users.User;
import io.javalin.http.*;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnrollmentController {

  private final ConcurrentHashMap<Integer, User> users;
  private final ConcurrentHashMap<Integer, Subject> subjects;

  private final CopyOnWriteArrayList<Enrollment> enrollments;

  private final CopyOnWriteArrayList<Double> labGrades;
  private final CopyOnWriteArrayList<Double> courseGrades;

  public EnrollmentController(ConcurrentHashMap<Integer, User> users, ConcurrentHashMap<Integer, Subject> subjects) {
    this.users = users;
    this.subjects = subjects;
    this.enrollments = new CopyOnWriteArrayList<>();
    this.labGrades = new CopyOnWriteArrayList<>();
    this.courseGrades = new CopyOnWriteArrayList<>();
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

    ctx.status(HttpStatus.CREATED);
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

    int enrollmentIdx = -1;
    for (Enrollment e : enrollments) {
      if (e.userId == userId && e.subjectId == subjectId) {
        enrollmentIdx = enrollments.indexOf(e);
      }
    }

    if (enrollmentIdx != -1) {
      enrollments.remove(enrollmentIdx);
    } else {
      throw new NotFoundResponse("Enrollment not found");
    }

    ctx.status(HttpStatus.NO_CONTENT);
  }

  class GradeSubmissionDTO {
    String gradeType;
    Double grade;

    GradeSubmissionDTO() {
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
      case "lab":
        e.labGrades.add(grade.grade);
        break;
      case "course":
        e.courseGrades.add(grade.grade);
        break;
      default:
        throw new BadRequestResponse("Grade type is wrong");
    }

    ctx.status(HttpStatus.OK);
  }

  public ConcurrentHashMap<Double, Double> previsionnalAvg() {

    ConcurrentHashMap<Double, Double> previsions = new ConcurrentHashMap<>();

    for (double i = 1.0; i <= 6.0; i += 0.5) {
      double avg = processAvgWithExam(i);
      previsions.put(i, avg);
    }

    return previsions;
  }

  private double processAvgWithExam(double avg) {
    double sumLabGrades = sumGrades(labGrades);
    double sumCourseGrades = sumGrades(courseGrades);

    int nbGrades = labGrades.size() + courseGrades.size();
    return ((((sumLabGrades * COEF_LAB + sumCourseGrades * COEF_COURSE) / nbGrades) + avg) / 2);
  }

  private double sumGrades(List<Double> grades) {
    double sum = 0.0;
    for (double grade : grades) {
      sum += grade;
    }

    return sum;
  }

  public double avgBeforeExam() {
    double sumLabGrades = sumGrades(labGrades);
    double sumCourseGrades = sumGrades(courseGrades);

    int nbGrades = labGrades.size() + courseGrades.size();

    // 2*COEF psk rapporté à 100% il faut doubler le coef
    return ((sumLabGrades * 2 * COEF_LAB + sumCourseGrades * 2 * COEF_COURSE) / nbGrades);

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
}
