package ch.heigvd.dai.users;


import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.IndexType;


@Index(value = "username", type = IndexType.Unique)
public class User {

  @Id
  public Integer id;
  public String firstName;
  public String lastName;
  public String username;
  public String password;

  public User() {
    // Empty constructor for serialisation/deserialization
  }
}
