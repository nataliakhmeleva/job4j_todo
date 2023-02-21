package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmTaskRepository implements TaskRepository {
    private final CrudRepository crudRepository;

    @Override
    public Task save(Task task) {
        crudRepository.run(session -> session.persist(task));
        return task;
    }

    @Override
    public boolean deleteById(int id) {
        try {
            crudRepository.run("delete from Task where id = :fId",
                    Map.of("fId", id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean update(Task task) {
        try {
            crudRepository.run(session -> session.merge(task));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<Task> findById(int id) {
        return crudRepository.optional("from Task where id = :fId", Task.class,
                Map.of("fId", id));
    }

    @Override
    public Collection<Task> findByDone(boolean done) {
        return crudRepository.query("from Task as i where i.done = :fDone", Task.class,
                Map.of("fDone", done));
    }

    @Override
    public Collection<Task> findAll() {
        return crudRepository.query("from Task ORDER BY id ASC", Task.class);
    }
}
