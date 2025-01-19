package ch.heigvd.dai.subjects;

import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.IndexType;

@Index(value = "shortName", type = IndexType.Unique)
public class Subject {
  public static double COEF_LAB = 0.2;
  public static double COEF_COURSE = 0.3;
  @Id
  public Integer id;
  public String shortName;
  public String fullName;

  public Subject() {
  }
}
