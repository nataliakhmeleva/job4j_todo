package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;

import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class HbmCategoryRepository implements CategoryRepository {

    private final CrudRepository crudRepository;

    @Override
    public List<Category> findByIdList(List<Integer> list) {
        return crudRepository.query("from Category where id in :fId", Category.class,
                Map.of("fId", list));
    }

    @Override
    public List<Category> findAll() {
        return crudRepository.query("from Category ORDER BY id", Category.class);
    }
}
