package com.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.example.todo.model.Todo;
import org.example.todo.repository.TodoRepository;
import org.example.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    public TodoServiceTest() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetAllTodos() {
        List<Todo> todos = List.of(
                new Todo(1L, "Erste Todo", false),
                new Todo(2L, "Zweite Todo", true)
        );

        when(todoRepository.findAll()).thenReturn(todos);

        List<Todo> result = todoService.getAllTodos();
        assertEquals(2, result.size());
        verify(todoRepository).findAll();
    }

    @Test
    public void testMarkTodoDone() {
        Long id = 1L;
        Todo todo = new Todo("Test Done");
        todo.setId(id);
        todo.setDone(false);

        when(todoRepository.findById(id)).thenReturn(Optional.of(todo));
        when(todoRepository.save(todo)).thenReturn(todo);

        Optional<Todo> result = todoService.markDone(id);

        assertTrue(result.isPresent());
        assertTrue(result.get().isDone());
        verify(todoRepository).save(todo);
    }
}

