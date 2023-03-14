package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Priority;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmPriorityRepository implements PriorityRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<Priority> findById(int id) {
        return crudRepository.optional("from Priority where id = :fId", Priority.class,
                Map.of("fId", id));
    }

    @Override
    public List<Priority> findAll() {
        return crudRepository.query("from Priority ORDER BY id ASC", Priority.class);
    }
}
