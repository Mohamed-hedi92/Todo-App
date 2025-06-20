package com.integration;

import org.example.todo.TodoAppApplication;
import org.example.todo.model.Todo;
import org.example.todo.repository.TodoRepository;
import org.example.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TodoAppApplication.class)
@ActiveProfiles("test")
public class TodoServiceIntegrationTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoService todoService;

    @BeforeEach
    public void setup() {
        todoService.deleteAllTodos();
    }

    @Test
    public void testAddAndGetAllTodos() {
        todoRepository.save(new Todo("Integration Test 1"));
        todoRepository.save(new Todo("Integration Test 2"));

        List<Todo> todos = todoService.getAllTodos();

        assertEquals(2, todos.size());
    }

    @Test
    public void testUpdateTodoTitle() {
        Todo original = todoRepository.save(new Todo("Original Title"));

        Todo updated = todoService.updateTitle(original.getId(), "New Title");

        assertEquals("New Title", updated.getTitle());

        Optional<Todo> fromDb = todoRepository.findById(original.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("New Title", fromDb.get().getTitle());
    }

    @Test
    public void testDeleteTodo() {
        Todo todo = todoRepository.save(new Todo("To be deleted"));

        todoService.deleteTodo(todo.getId());

        Optional<Todo> deleted = todoRepository.findById(todo.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    public void testToggleDone() {
        Todo todo = todoRepository.save(new Todo("Toggle Test"));
        assertFalse(todo.isDone());

        Optional<Todo> toggled = todoService.toggleDone(todo.getId());

        assertTrue(toggled.isPresent());
        assertTrue(toggled.get().isDone());

        Optional<Todo> toggledAgain = todoService.toggleDone(todo.getId());
        assertTrue(toggledAgain.isPresent());
        assertFalse(toggledAgain.get().isDone());
    }

    @Test
    public void testMarkDone() {
        Todo todo = todoRepository.save(new Todo("Mark Test"));
        assertFalse(todo.isDone());

        Optional<Todo> marked = todoService.markDone(todo.getId());

        assertTrue(marked.isPresent());
        assertTrue(marked.get().isDone());

        // Wenn markDone bei einem bereits erledigten Todo aufgerufen wird, wird es wieder "nicht erledigt"
        Optional<Todo> markedAgain = todoService.markDone(todo.getId());
        assertTrue(markedAgain.isPresent());
        assertFalse(markedAgain.get().isDone());
    }

    @Test
    public void testUpdateTodo() {
        Todo original = todoRepository.save(new Todo("Original"));
        Todo updatedTodo = new Todo("Updated");
        updatedTodo.setDone(true);

        Optional<Todo> result = todoService.updateTodo(original.getId(), updatedTodo);

        assertTrue(result.isPresent());
        assertEquals("Updated", result.get().getTitle());
        assertTrue(result.get().isDone());

        Optional<Todo> fromDb = todoRepository.findById(original.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Updated", fromDb.get().getTitle());
        assertTrue(fromDb.get().isDone());
    }

    @Test
    public void testUpdateTitle_NotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            todoService.updateTitle(900L, "Will fail");
        });

        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    public void testAddWithTitleOnly() {
        Todo todo = todoService.add("Title Only");

        assertNotNull(todo.getId());
        assertEquals("Title Only", todo.getTitle());
        assertFalse(todo.isDone());

        Optional<Todo> fromDb = todoRepository.findById(todo.getId());
        assertTrue(fromDb.isPresent());
    }
}
