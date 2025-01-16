package ch.heigvd.dai.enrollment;

import ch.heigvd.dai.subjects.Subject;

import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.ConcurrentHashMap;

public class Enrollment {
  public Integer userId;
  public Integer subjectId;

  public List<Double> courseGrades;
  public List<Double> labGrades;

  public Enrollment() {

  }

  public Enrollment(Integer userId, Integer subjectId) {
    this.userId = userId;
    this.subjectId = subjectId;
    this.labGrades = new ArrayList<>();
    this.courseGrades = new ArrayList<>();
  }

  public double avgBeforeExam() {
    double sumLabGrades = sumGrades(labGrades);
    double sumCourseGrades = sumGrades(courseGrades);

    int nbGrades = labGrades.size() + courseGrades.size();

    // 2*COEF psk rapporté à 100% il faut doubler le coef
    return ((sumLabGrades * 2 * Subject.COEF_LAB + sumCourseGrades * 2 * Subject.COEF_COURSE) / nbGrades);

  }

  private double sumGrades(List<Double> grades) {
    double sum = 0.0;
    for (double grade : grades) {
      sum += grade;
    }

    return sum;
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
    return ((((sumLabGrades * Subject.COEF_LAB + sumCourseGrades * Subject.COEF_COURSE) / nbGrades) + avg) / 2);
  }
}
