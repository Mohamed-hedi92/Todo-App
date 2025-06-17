package org.example.todo.service;

import org.example.todo.model.Todo;
import org.example.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public List<Todo> getAllTodos() {
        return repository.findAll();
    }

    public Todo add(String title) {
        return repository.save(new Todo(title));
    }

    public Optional<Todo> markDone(Long id) {
        Optional<Todo> todo = repository.findById(id);
        todo.ifPresent(t -> {
            t.setDone(true);
            repository.save(t);
        });
        return todo;
    }
}
