package ru.job4j.todo.service.util;

import lombok.NoArgsConstructor;
import ru.job4j.todo.model.User;

import javax.servlet.http.HttpSession;

@NoArgsConstructor
public class UserHttpSession {
    public static User addSession(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        return user;
    }
}
