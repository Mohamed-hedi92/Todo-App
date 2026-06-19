package org.example.todo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel darf nicht leer sein")
    @Size(max = 200, message = "Titel darf maximal 200 Zeichen lang sein")
    private String title;

    private boolean done;

    // Convenience-Konstruktor: nur Titel, done=false, id=null (für neue Todos)
    public Todo(String title) {
        this.title = title;
        this.done = false;
    }
}