package ch.heigvd.dai.enrollment;

import java.util.List;
import java.util.ArrayList;

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

}
