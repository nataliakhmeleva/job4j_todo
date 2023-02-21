package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.TaskService;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Task task, HttpSession httpSession) {
        var user = (User) httpSession.getAttribute("user");
        task.setUser(user);
        taskService.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}")
    public String getInformPage(Model model, @PathVariable int id) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задание не найдено");
            return "errors/404";
        }
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
        return "tasks/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Task task, Model model, HttpSession httpSession) {
        var user = (User) httpSession.getAttribute("user");
        task.setUser(user);
        var isUpdated = taskService.update(task);
        if (!isUpdated) {
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
