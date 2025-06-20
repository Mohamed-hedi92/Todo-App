package com.integration;

import org.example.todo.TodoAppApplication; // importiere Hauptklasse

import org.example.todo.model.Todo;
import org.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TodoAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TodoRepository todoRepository;

    private String baseUrl;

    @BeforeEach
    void setup() {
        todoRepository.deleteAll();
        todoRepository.flush();
       // baseUrl = "http://localhost:8085/todos";
        baseUrl = "http://localhost:" + port + "/todos";
    }

    @Test
    void testAddTodo() {
        // Arrange

        Todo newTodo = new Todo("Neue Aufgabe");
        newTodo.setDone(true);

        ResponseEntity<Todo> response = restTemplate.postForEntity(baseUrl, newTodo, Todo.class);


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Neue Aufgabe", response.getBody().getTitle());

        System.out.println("Alle Todos: " + todoRepository.findAll());
        System.out.println("Antwort vom POST: " + response.getBody());
        System.out.println("Gefunden: " + todoRepository.findById(response.getBody().getId()));

        Optional<Todo> fromDb = todoRepository.findById(response.getBody().getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Neue Aufgabe", fromDb.get().getTitle());


    }

    @Test
    void testGetAllTodos() {

        todoRepository.save(new Todo("Task 1"));
        todoRepository.save(new Todo("Task 2"));


        ResponseEntity<Todo[]> response = restTemplate.getForEntity(baseUrl, Todo[].class);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }


}
