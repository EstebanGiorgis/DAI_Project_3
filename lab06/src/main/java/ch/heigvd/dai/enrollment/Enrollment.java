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

  private double calculateAverageIfPresent(List<Double> grades) {
    if (grades == null || grades.isEmpty()) {
      return 0.0;
    }
    return grades.stream().mapToDouble(Double::doubleValue).sum() / grades.size();
  }

  public double avgBeforeExam() {
    double labAvg = calculateAverageIfPresent(labGrades);
    double courseAvg = calculateAverageIfPresent(courseGrades);

    double totalWeight = 0.0;
    double weightedSum = 0.0;

    // Add lab component if there are lab grades
    if (!labGrades.isEmpty()) {
      weightedSum += labAvg * Subject.COEF_LAB;
      totalWeight += Subject.COEF_LAB;
    }

    // Add course component if there are course grades
    if (!courseGrades.isEmpty()) {
      weightedSum += courseAvg * Subject.COEF_COURSE;
      totalWeight += Subject.COEF_COURSE;
    }

    // If no grades at all, return 0
    if (totalWeight == 0.0) {
      return 0.0;
    }

    // Return weighted average on the 1-6 scale
    return weightedSum / totalWeight;

  }

  public ConcurrentHashMap<Double, Double> previsionnalAvg() {

    ConcurrentHashMap<Double, Double> previsions = new ConcurrentHashMap<>();
    double currentAvg = avgBeforeExam();

    // If we have no grades yet, the final grade will just be half of the exam grade
    if (currentAvg == 0.0) {
      for (double examGrade = 1.0; examGrade <= 6.0; examGrade += 0.5) {
        previsions.put(examGrade, examGrade);
      }
      return previsions;
    }

    // Calculate previsional averages for different exam grades (50% current
    // average, 50% exam)
    for (double examGrade = 1.0; examGrade <= 6.0; examGrade += 0.5) {
      double finalAvg = (currentAvg + examGrade) / 2.0;
      previsions.put(examGrade, finalAvg);
    }

    return previsions;
  }
}
