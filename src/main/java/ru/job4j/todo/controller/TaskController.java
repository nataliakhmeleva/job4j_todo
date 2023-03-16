package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.PriorityService;
import ru.job4j.todo.service.TaskService;

import javax.servlet.http.HttpSession;
import java.util.List;

import static ru.job4j.todo.util.UserTimeZone.addUserTimeZone;

@Controller
@AllArgsConstructor
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final PriorityService priorityService;
    private final CategoryService categoryService;

    @GetMapping
    public String getAll(Model model, HttpSession httpSession) {
        var user = (User) httpSession.getAttribute("user");
        var tasks = taskService.findAll();
        tasks.forEach(t -> addUserTimeZone(user, t));
        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", categoryService.findAll());
        return "tasks/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("priorities", priorityService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Task task, @RequestParam("category.id") List<Integer> list, Model model, HttpSession httpSession) {
        var user = (User) httpSession.getAttribute("user");
        var priorityOptional = priorityService.findById(task.getPriority().getId());
        var categoriesList = categoryService.findByIdList(list);
        task.setUser(user);
        if (priorityOptional.isEmpty() || categoriesList.isEmpty()) {
            model.addAttribute("message", "Задание не создано");
            return "errors/404";
        }
        task.setCategories(categoriesList);
        taskService.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}")
    public String getInformPage(Model model, @PathVariable int id, HttpSession httpSession) {
        var user = (User) httpSession.getAttribute("user");
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задание не найдено");
            return "errors/404";
        }
        addUserTimeZone(user, taskOptional.get());
        model.addAttribute("priorities", priorityService.findAll());
        model.addAttribute("task", taskOptional.get());
        return "tasks/info";
    }

    @GetMapping("/update/{id}")
    public String getEditPage(Model model, @PathVariable int id) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задание не найдено");
            return "errors/404";
        }
        model.addAttribute("task", taskOptional.get());
        model.addAttribute("priorities", priorityService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "tasks/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Task task, @RequestParam("category.id") List<Integer> list, Model model, HttpSession httpSession) {
        var user = (User) httpSession.getAttribute("user");
        var priorityOptional = priorityService.findById(task.getPriority().getId());
        var categoriesList = categoryService.findByIdList(list);
        task.setUser(user);
        task.setCategories(categoriesList);
        var isUpdated = taskService.update(task);
        if (!isUpdated || priorityOptional.isEmpty() || categoriesList.isEmpty()) {
            model.addAttribute("message", "Задание с указанным идентификатором не найдено");
            return "errors/404";
        }
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        var isDeleted = taskService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Задание с указанным идентификатором не найдено");
            return "errors/404";
        }
        return "redirect:/tasks";
    }

    @GetMapping("/execute/{id}")
    public String execute(Model model, @PathVariable int id) {
        var taskOptional = taskService.findById(id);
        taskOptional.get().setDone(true);
        var isUpdated = taskService.update(taskOptional.get());
        if (!isUpdated) {
            model.addAttribute("message", "Задание с указанным идентификатором не найдено");
            return "errors/404";
        }
        return "redirect:/tasks";
    }

    @GetMapping("/new")
    public String getNewTasks(Model model) {
        model.addAttribute("tasks", taskService.findByDone(false));
        return "tasks/list";
    }

    @GetMapping("/done")
    public String getDoneTasks(Model model) {
        model.addAttribute("tasks", taskService.findByDone(true));
        return "tasks/list";
    }
}
