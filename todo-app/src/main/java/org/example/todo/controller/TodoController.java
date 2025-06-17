package org.example.todo.controller;

import org.example.todo.model.Todo;
import org.example.todo.service.TodoService;
import org.springframework.web.bind.annotation.*;

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
    public Todo addTodo(@RequestBody Todo todo) {
        return service.addTodo(todo);
    }
    @PutMapping("/{id}/done")
    public Todo markDone(@PathVariable Long id) {
        return service.markDone(id).orElseThrow(() -> new RuntimeException("Todo not found"));
    }
}
