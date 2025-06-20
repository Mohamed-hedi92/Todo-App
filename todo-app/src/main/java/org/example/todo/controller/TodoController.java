package org.example.todo.controller;

import org.example.todo.model.Todo;
import org.example.todo.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Todo> getTodos() {
        return service.getAllTodos();
    }


    @PostMapping
    public ResponseEntity<Todo> addTodo(@RequestBody Todo todo) {
        Todo created = service.addTodo(todo);
        URI location = URI.create("/todos/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}/done")
    public Todo markDone(@PathVariable Long id) {
        return service.markDone(id).orElseThrow(() -> new RuntimeException("Todo not found"));
    }

    @PutMapping("/{id}/title")
    public Todo updateTitle(@PathVariable Long id, @RequestBody String newTitle) {

        newTitle = newTitle.replaceAll("^\"|\"$", "");
        return service.updateTitle(id, newTitle);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        service.deleteTodo(id);
    }
}
