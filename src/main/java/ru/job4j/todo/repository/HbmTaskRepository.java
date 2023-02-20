package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmTaskRepository implements TaskRepository {
    private final SessionFactory sf;

    @Override
    public Task save(Task task) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.save(task);
        session.getTransaction().commit();
        session.close();
        return task;
    }

    @Override
    public boolean deleteById(int id) {
        boolean result = false;
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("DELETE Task WHERE id = :fId")
                    .setParameter("fId", id)
                    .executeUpdate() > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }

    @Override
    public boolean update(Task task) {
        boolean result = false;
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery(
                            "UPDATE Task SET description = :fDescription, created = :fCreated, done =: fDone WHERE id = :fId")
                    .setParameter("fDescription", task.getDescription())
                    .setParameter("fCreated", task.getCreated())
                    .setParameter("fDone", task.isDone())
                    .setParameter("fId", task.getId())
                    .executeUpdate() > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }

    @Override
    public Optional<Task> findById(int id) {
        Optional<Task> tasks = Optional.empty();
        Session session = sf.openSession();
                try {
            session.beginTransaction();
            tasks = session.createQuery("from Task as i where i.id = :fId", Task.class)
                    .setParameter("fId", id)
                    .uniqueResultOptional();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return tasks;
    }

    @Override
    public Collection<Task> findByDone(boolean done) {
        List<Task> tasks = new ArrayList<>();
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            tasks = session.createQuery("from Task as i where i.done = :fDone", Task.class)
                    .setParameter("fDone", done)
                    .list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return tasks;
    }

    @Override
    public Collection<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            tasks = session.createQuery("from Task ORDER BY id ASC", Task.class)
                    .list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return tasks;
    }
}
