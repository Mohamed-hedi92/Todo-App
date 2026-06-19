package org.example.todo.controller;

import org.example.todo.mapper.TodoMapper;
import org.example.todo.model.Todo;
import org.example.todo.service.TodoService;

import org.openapitools.api.TodosApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

@RestController
public class TodoController implements TodosApi {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @Override
    public ResponseEntity<List<org.openapitools.model.Todo>> todosGet() {
        List<Todo> entities = todoService.getAllTodos();
        List<org.openapitools.model.Todo> apiTodos = entities.stream()
                .map(TodoMapper::toApiTodo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(apiTodos);
    }

    @Override
    public ResponseEntity<org.openapitools.model.Todo> todosPost(@Valid org.openapitools.model.Todo apiTodo) {
        Todo entity = TodoMapper.toEntityTodo(apiTodo);
        Todo saved = todoService.addTodo(entity);
        org.openapitools.model.Todo response = TodoMapper.toApiTodo(saved);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Void> todosIdDelete(Integer id) {
        todoService.deleteTodo(id.longValue());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<org.openapitools.model.Todo> todosIdGet(Integer id) {
        return todoService.getTodoById(id.longValue())
                .map(TodoMapper::toApiTodo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<org.openapitools.model.Todo> todosIdDonePut(Integer id) {
        return todoService.toggleDone(id.longValue())
                .map(TodoMapper::toApiTodo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<org.openapitools.model.Todo> todosIdTitlePut(Integer id, String body) {
        Todo updated = todoService.updateTitle(id.longValue(), body);
        org.openapitools.model.Todo response = TodoMapper.toApiTodo(updated);
        return ResponseEntity.ok(response);
    }
}
