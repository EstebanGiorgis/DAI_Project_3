package ch.heigvd.dai.data;

import ch.heigvd.dai.users.User;
import io.javalin.http.ConflictResponse;
import io.javalin.http.HttpStatus;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.dizitart.no2.exceptions.UniqueConstraintException;
import java.util.List;

public class Data<T> implements AutoCloseable {
  final Nitrite db;
  public final ObjectRepository<T> repository;

  public Data(Class<T> type) {
    db = Nitrite.builder()
        .filePath("/app/database/database.db")
        .openOrCreate();

    repository = db.getRepository(type);
  }

  public static <T> void create(T data, Class<T> type) {
    try (Data<T> d = new Data<>(type)) {
      d.repository.insert(data);
    } catch (UniqueConstraintException e) {
      throw new ConflictResponse(type.getSimpleName() + " already exists");
    } catch (Exception e) {
      throw new InternalServerErrorResponse("Error creating " + type.getSimpleName() + e.getMessage());
    }
  }

  public static <T> void delete(String id, Class<T> type, boolean integerId) {
    try (Data<T> d = new Data<>(type)) {

      //Retrieves the data in question
      T data = d.repository.find(ObjectFilters.eq("id", integerId ? Integer.parseInt(id) : id))
          .firstOrDefault();
      if (data == null) {
        throw new NotFoundResponse();
      }
      d.repository.remove(data);
    } catch (Exception e) {
      throw new InternalServerErrorResponse("Error deleting " + type.getSimpleName());
    }
  }

  public static <T> T get(String id, Class<T> type, boolean integerId) {
    try (Data<T> d = new Data<>(type)) {
      //Retrieves the data in question
      return d.repository.find(ObjectFilters.eq("id", integerId ? Integer.parseInt(id) : id))
          .firstOrDefault();
    } catch (Exception e) {
      throw new InternalServerErrorResponse("Error deleting " + type.getSimpleName());
    }
  }

  public static <T> List<T> getAll(Class<T> type) {
    try (Data<T> d = new Data<>(type)) {
      //Retrieves all datas
      return d.repository.find()
          .toList();
    } catch (Exception e) {
      throw new InternalServerErrorResponse("Error fetching all " + type.getSimpleName());
    }
  }

  public static <T> void update(T data, Class<T> type) {
    try (Data<T> d = new Data<>(type)) {
      //Update the data in question
      d.repository.update(data);
    } catch (UniqueConstraintException e) {
      throw new ConflictResponse(type.getSimpleName() + " already exists");
    } catch (Exception e) {
      throw new InternalServerErrorResponse("Error creating " + type.getSimpleName());
    }
  }

  @Override
  public void close() {
    if (db != null && !db.isClosed()) {
      db.close();
    }
  }
}
