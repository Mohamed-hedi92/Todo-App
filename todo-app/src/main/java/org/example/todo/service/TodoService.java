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

    public Todo addTodo(Todo todo) {
        return repository.save(todo);
    }

    public Optional<Todo> markDone(Long id) {
        Optional<Todo> todo = repository.findById(id);
        todo.ifPresent(t -> {
            t.setDone(!t.isDone()); // Toggle: true → false oder false → true
            repository.save(t);
        });
        return todo;
    }
    public Optional<Todo> updateTodo(Long id, Todo updatedTodo) {
        return repository.findById(id).map(existingTodo -> {
            existingTodo.setTitle(updatedTodo.getTitle());
            existingTodo.setDone(updatedTodo.isDone());
            return repository.save(existingTodo);
        });
    }

    public Todo updateTitle(Long id, String newTitle) {
        Optional<Todo> optionalTodo = repository.findById(id);
        if (optionalTodo.isEmpty()) {
            throw new RuntimeException("Todo not found");
        }
        Todo todo = optionalTodo.get();
        todo.setTitle(newTitle);
        return repository.save(todo);
    }

    public void deleteTodo(Long id) {
        repository.deleteById(id);
    }

    public Optional<Todo> toggleDone(Long id) {
        return repository.findById(id).map(todo -> {
            todo.setDone(!todo.isDone());
            return repository.save(todo);
        });
    }
}
