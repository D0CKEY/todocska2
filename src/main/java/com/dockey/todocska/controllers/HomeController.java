package com.dockey.todocska.controllers;

import com.dockey.todocska.entities.Role;
import com.dockey.todocska.entities.Todo;
import com.dockey.todocska.entities.TodoRepository;
import com.dockey.todocska.entities.User;
import com.dockey.todocska.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j

public class HomeController {

    private final String UPLOAD_DIR = "C:\\uploads";

    @Autowired
    private UserService service;

    @Autowired
    private final TodoRepository todoRepository;

    @Autowired
    public HomeController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping("/")  // INDEX.HTML
    public String index(Model model) {
        log.info("Load index.html");
        return "index";
    }

    @GetMapping({"/login"})
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/registration")  // USER REGISTRATION
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        log.info("Load user registration page");
        return "registration";
    }

    @PostMapping("/registration_process")  // SAVE USER REGISTRATION
    public String processRegister(User user) throws IOException {
        service.registerDefaultUser(user);
        log.info("User registration / " + user);
        return "register_success";
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/users")  // LIST USERS
    public String listUsers(Model model, Authentication authentication) {
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("admin", authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        log.info("Logged user: " + authentication.getName() + " / List users");
        return "users";
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @authenticatedUserService.hasId(#userId)")
    @GetMapping("/users/edit/{userId}")  // EDIT USER
    public String editUser(@PathVariable("userId") Long userId, Model model, Authentication authentication) {
        User user = service.get(userId);
        List<Role> listRoles = service.listRoles();
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("admin", authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        log.info("Logged user: " + authentication.getName() + " User: " + user.getUsername() + " / Edit user");
        return "user_form";
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @authenticatedUserService.hasId(#userId)")
    @DeleteMapping("/users/{userId}")  // DELETE USER
    public void deleteUser(@PathVariable("userId") Long userId, Authentication authentication) {
        User user = service.get(userId);
        log.info("Logged user: " + authentication.getName() + " User: " + user.getUsername() + " / Delete user");
        service.removeUser(userId);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @authenticatedUserService.hasId(#userId)")
    @PostMapping("/users/{userId}/save")  // SAVE USER
    public RedirectView saveUser(@PathVariable("userId") Long userId, User user, Authentication authentication) throws IOException {
        User savedUser = service.save(user);
        log.info("Logged user: " + authentication.getName() + " User: " + savedUser.getUsername() + " / Save user / " + savedUser);
        return new RedirectView("/users", true);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @authenticatedUserService.hasId(#userId)")
    @GetMapping("/users/{userId}/todos")  // LIST TODOS
    public String listTodo(@PathVariable("userId") Long userId, Model model, Authentication authentication) {
        List<Todo> listTodos = service.listTodos(userId);
        model.addAttribute("listTodos", listTodos);
        model.addAttribute("todoGid", userId);
        User user = service.get(userId);
        log.info("Logged user: " + authentication.getName() + " User: " + user.getUsername() + " / List todos");
        return "todos";
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @authenticatedUserService.hasId(#userId)")
    @GetMapping("/users/{userId}/todo/{todoId}")  // EDIT TODO
    public String editTodo(@PathVariable("userId") Long userId, @PathVariable("todoId") Long todoId, Model model, Authentication authentication) {
        Todo todo = todoRepository.findById(todoId).get();
        model.addAttribute("todo", todo);
        User user = service.get(userId);
        log.info("Logged user: " + authentication.getName() + " User: " + user.getUsername() + " / Edit todo");
        return "todo_form";
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @authenticatedUserService.hasId(#userId)")
    @PostMapping("/users/{userId}/todo/save")  // SAVE TODO
    public String saveUser(@PathVariable("userId") Long userId, Todo todo, Authentication authentication) {
        todoRepository.save(todo);
        User user = service.get(userId);
        log.info("Logged user: " + authentication.getName() + " User: " + user.getUsername() + " / Save todo / " + todo);
        return "redirect:/users/" + todo.getGid() + "/todos";
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @authenticatedUserService.hasId(#userId)")
    @GetMapping("/users/{userId}/newtodo")  // NEW TODO
    public String newTodo(@PathVariable("userId") Long userId, Model model, Authentication authentication) {
        Todo todo = new Todo();
        todo.setGid(userId);
        model.addAttribute("todo", todo);
        User user = service.get(userId);
        log.info("Logged user: " + authentication.getName() + " User: " + user.getUsername() + " / New todo");
        return "todo_form";
    }

    //create = POST /users
    //update = Put /users/id
    //delete = delete /users/id
    //get 1  = get /users/id
    //get all  = get /users
}
