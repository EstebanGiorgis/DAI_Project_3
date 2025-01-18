package ch.heigvd.dai.data;

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
                .filePath("database.db")
                .openOrCreate();

        repository = db.getRepository(type);
    }

    public void save(T data) {
        repository.insert(data);
    }

    public void update(T data) {
        repository.update(data);
    }

    public void delete(T data) {
        repository.remove(data);
    }

    public List<T> findByEqualFields(String field1, String field2) {
        return repository.find(ObjectFilters.eq(field1, ObjectFilters.field(field2))).toList();
    }

    @Override
    public void close() {
        if (db != null && !db.isClosed()) {
            db.close();
        }
    }
}
