package ch.heigvd.dai.data;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import java.util.List;

public class Data<T> implements AutoCloseable {
    private final Nitrite db;
    private final ObjectRepository<T> repository;

    public Data(Class<T> type) {
        db = Nitrite.builder()
                .filePath("database.db")
                .openOrCreate();

        repository = db.getRepository(type);
    }

    public void save(T data) {
        repository.insert(data);
    }

    public List<T> findAll() {
        return repository.find().toList();
    }

    public void update(T data) {
        repository.update(data);
    }

    public void delete(T data) {
        repository.remove(data);
    }

    @Override
    public void close() {
        if (db != null && !db.isClosed()) {
            db.close();
        }
    }
}
