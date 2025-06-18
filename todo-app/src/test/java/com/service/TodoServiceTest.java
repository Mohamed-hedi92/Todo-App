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
    public void testAddTodo() {
        Todo newTodo = new Todo("Neue Aufgabe");
        Todo savedTodo = new Todo(1L, "Neue Aufgabe", false);

        when(todoRepository.save(newTodo)).thenReturn(savedTodo);

        Todo result = todoService.addTodo(newTodo);
        assertNotNull(result);
        assertEquals("Neue Aufgabe", result.getTitle());
        verify(todoRepository).save(newTodo);
    }

    @Test
    public void testUpdateTodo() {
        Long id = 1L;
        Todo existing = new Todo(id, "Alt", false);
        Todo updated = new Todo(id, "Neu", true);

        when(todoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(todoRepository.save(any(Todo.class))).thenReturn(updated);

        Optional<Todo> result = todoService.updateTodo(id, updated);

        assertTrue(result.isPresent());
        assertEquals("Neu", result.get().getTitle());
        assertTrue(result.get().isDone());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    public void testDeleteTodo() {
        Long id = 1L;
        doNothing().when(todoRepository).deleteById(id);

        todoService.deleteTodo(id);

        verify(todoRepository).deleteById(id);
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

    @Test
    public void testUpdateTodo_NotFound() {
        Long id = 999L;
        Todo updated = new Todo(id,"Titel", true);

        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Todo> result = todoService.updateTodo(id, updated);

        assertTrue(result.isEmpty());
        verify(todoRepository, never()).save(any());
    }

    @Test
    public void testGetAllTodos_EmptyList() {
        when(todoRepository.findAll()).thenReturn(List.of());

        List<Todo> result = todoService.getAllTodos();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testMarkTodoDone_NotFound() {
        Long id = 9000L;

        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Todo> result = todoService.markDone(id);

        assertTrue(result.isEmpty());
        verify(todoRepository, never()).save(any());
    }

    @Test
    public void testAddByTitle() {
        String title = "Neue Aufgabe per Titel";
        Todo todoToSave = new Todo(title);
        Todo savedTodo = new Todo(1L, title, false);

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        Todo result = todoService.add(title);

        assertNotNull(result);
        assertEquals(title, result.getTitle());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    public void testDeleteTodo_NotExistingId() {
        Long id = 9000L;


        doNothing().when(todoRepository).deleteById(id);

        todoService.deleteTodo(id);

        verify(todoRepository).deleteById(id);
    }

    @Test
    public void testAddMultipleTodos() {
        Todo t1 = new Todo("eins");
        Todo t2 = new Todo("zwei");
        Todo saved1 = new Todo(1L, "eins", false);
        Todo saved2 = new Todo(2L, "zwei", false);

        when(todoRepository.save(t1)).thenReturn(saved1);
        when(todoRepository.save(t2)).thenReturn(saved2);

        Todo r1 = todoService.addTodo(t1);
        Todo r2 = todoService.addTodo(t2);

        assertEquals("eins", r1.getTitle());
        assertEquals("zwei", r2.getTitle());

        verify(todoRepository).save(t1);
        verify(todoRepository).save(t2);
    }

    @Test
    public void testToggleDone_TogglesFromFalseToTrueAndBack() {
        Long id = 1L;


        Todo todoFalse = new Todo(id, "Aufgabe", false);
        Todo todoTrue = new Todo(id, "Aufgabe", true);

        when(todoRepository.findById(id)).thenReturn(Optional.of(todoFalse));
        when(todoRepository.save(any())).thenReturn(todoTrue);

        Optional<Todo> result1 = todoService.toggleDone(id);

        assertTrue(result1.isPresent());
        assertTrue(result1.get().isDone());
        verify(todoRepository).save(any());


        Todo todoTrueNow = new Todo(id, "Aufgabe", true);
        Todo todoFalseAgain = new Todo(id, "Aufgabe", false);

        when(todoRepository.findById(id)).thenReturn(Optional.of(todoTrueNow));
        when(todoRepository.save(any())).thenReturn(todoFalseAgain);

        Optional<Todo> result2 = todoService.toggleDone(id);

        assertTrue(result2.isPresent());
        assertFalse(result2.get().isDone());
    }

    @Test
    public void testDeleteTodo_RepositoryThrowsException() {
        Long id = 42L;
        doThrow(new RuntimeException("DB Fehler")).when(todoRepository).deleteById(id);

        assertThrows(RuntimeException.class, () -> {
            todoService.deleteTodo(id);
        });

        verify(todoRepository).deleteById(id);
    }

    @Test
    public void testToggleDone_TodoNotFound() {
        Long id = 999L;

        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Todo> result = todoService.toggleDone(id);

        assertTrue(result.isEmpty());
        verify(todoRepository, never()).save(any());
    }

    @Test
    public void testUpdateTitle_Success() {
        Long id = 1L;
        String newTitle = "Updated Title";
        Todo existing = new Todo(id, "Old Title", false);
        Todo updated = new Todo(id, newTitle, false);

        when(todoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(todoRepository.save(any(Todo.class))).thenReturn(updated);

        Todo result = todoService.updateTitle(id, newTitle);

        assertNotNull(result);
        assertEquals(newTitle, result.getTitle());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    public void testAddTodo_WithEmptyTitle() {
        Todo emptyTitleTodo = new Todo("");

        when(todoRepository.save(emptyTitleTodo)).thenReturn(emptyTitleTodo);

        Todo result = todoService.addTodo(emptyTitleTodo);

        assertNotNull(result);
        assertEquals("", result.getTitle());
        verify(todoRepository).save(emptyTitleTodo);
    }
}

