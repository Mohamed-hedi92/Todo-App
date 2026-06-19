package com.integration;

import org.example.todo.TodoAppApplication;

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
import org.springframework.web.client.RestTemplate;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TodoAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
        //baseUrl = "http://localhost:8085/todos";
        baseUrl = "http://localhost:" + port + "/todos";
    }



    @Test
    void testAddTodo() {

        Todo newTodo = new Todo("Neue Aufgabe");
        newTodo.setDone(true);

        ResponseEntity<Todo> response = restTemplate.postForEntity(baseUrl, newTodo, Todo.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Neue Aufgabe", response.getBody().getTitle());


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

    @Test
    void testUpdateTodoTitle() {

        Todo saved = todoRepository.save(new Todo("Alter Titel"));
        String updateUrl = baseUrl + "/" + saved.getId() + "/title";
        String newTitle = "Neuer Titel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(newTitle, headers);


        ResponseEntity<Todo> response = restTemplate.exchange(updateUrl, HttpMethod.PUT, entity, Todo.class);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newTitle, response.getBody().getTitle());


        Optional<Todo> fromDb = todoRepository.findById(saved.getId());
        assertTrue(fromDb.isPresent());
        assertEquals(newTitle, fromDb.get().getTitle());
    }

    @Test
    void testDeleteTodo() {

        Todo saved = todoRepository.save(new Todo("Zu löschen"));

        restTemplate.delete(baseUrl + "/" + saved.getId());

        assertTrue(todoRepository.findById(saved.getId()).isEmpty());
    }
    @Test
    void testGetTodoById() {
        Todo saved = todoRepository.save(new Todo("Einzelne Aufgabe"));

        ResponseEntity<Todo> response = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), Todo.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(saved.getId(), response.getBody().getId());
        assertEquals("Einzelne Aufgabe", response.getBody().getTitle());
    }

    @Test
    void testGetTodoByInvalidId() {
        long invalidId = 9999L;

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/" + invalidId, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateTodoStatus() {
        Todo saved = todoRepository.save(new Todo("Status ändern"));
        String updateUrl = baseUrl + "/" + saved.getId() + "/done";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Boolean> entity = new HttpEntity<>(true, headers);

        ResponseEntity<Todo> response = restTemplate.exchange(updateUrl, HttpMethod.PUT, entity, Todo.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isDone());

        Optional<Todo> fromDb = todoRepository.findById(saved.getId());
        assertTrue(fromDb.isPresent());
        assertTrue(fromDb.get().isDone());
    }
}
