package ch.heigvd.dai.subjects;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Subject {
  private static double COEF_LAB = 0.2;
  private static double COEF_COURSE = 0.3;

  private String name;
  private List<Double> labGrades;
  private List<Double> courseGrades;

  public Subject() {
  }

  public Subject(String name) {
    this.name = name;
    labGrades = new ArrayList<>();
    courseGrades = new ArrayList<>();
  }

  public double avgBeforeExam() {
    double sumLabGrades = sumGrades(labGrades);
    double sumCourseGrades = sumGrades(courseGrades);

    int nbGrades = labGrades.size() + courseGrades.size();

    // 2*COEF psk rapporté à 100% il faut doubler le coef
    return ((sumLabGrades * 2 * COEF_LAB + sumCourseGrades * 2 * COEF_COURSE) / nbGrades);

  }

  public Map<Double, Double> previsionnalAvg() {

    Map<Double, Double> previsions = new HashMap<>();

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

  public String name() {
    return name();
  }

}
