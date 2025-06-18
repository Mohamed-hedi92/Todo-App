import React, { useState, useEffect } from "react";
import axios from "axios";
import './App.css';

function TodoList() {
  const [todos, setTodos] = useState([]);
  const [newTitle, setNewTitle] = useState("");
  const [editId, setEditId] = useState(null);
  const [editTitle, setEditTitle] = useState("");

  useEffect(() => {
    fetchTodos();
  }, []);

  const fetchTodos = async () => {
    try {
      const res = await axios.get("http://localhost:8085/todos");
      setTodos(res.data);
    } catch (error) {
      console.error("Fehler beim Laden:", error);
    }
  };

  const addTodo = async () => {
    if (!newTitle.trim()) return;
    try {
      await axios.post("http://localhost:8085/todos", {
        title: newTitle,
        done: false,
      });
      setNewTitle("");
      fetchTodos();
    } catch (error) {
      console.error("Fehler beim Hinzufügen:", error);
    }
  };

  const deleteTodo = async (id) => {
    try {
      await axios.delete(`http://localhost:8085/todos/${id}`);
      fetchTodos();
    } catch (error) {
      console.error("Fehler beim Löschen:", error);
    }
  };

  const markDone = async (id) => {
    try {
      await axios.put(`http://localhost:8085/todos/${id}/done`);
      fetchTodos();
    } catch (error) {
      console.error("Fehler beim Markieren als erledigt:", error);
    }
  };

  const updateTitle = async (id) => {
    try {
         await axios.put(
           `http://localhost:8085/todos/${id}/title`,
           JSON.stringify(editTitle),
           { headers: { "Content-Type": "application/json" } }
         );
      setEditId(null);
      setEditTitle("");
      fetchTodos();
    } catch (error) {
      console.error("Fehler beim Aktualisieren des Titels:", error);
    }
  };

  return (
    <div className="todo-container">
      <h2>Meine Todos</h2>
      <div className="todo-input">
        <input
          type="text"
          placeholder="Neue Aufgabe"
          value={newTitle}
          onChange={(e) => setNewTitle(e.target.value)}
        />
        <button onClick={addTodo}>Hinzufügen</button>
      </div>
      <ul>
        {todos.map((todo) => (
          <li key={todo.id} className={todo.done ? "done" : ""}>
            {editId === todo.id ? (
              <>
                <input
                  value={editTitle}
                  onChange={(e) => setEditTitle(e.target.value)}
                />
                <button className="save" onClick={() => updateTitle(todo.id)}>Speichern</button>
                <button className="cancel" onClick={() => setEditId(null)}>Abbrechen</button>
              </>
            ) : (
              <>
                <span>{todo.title} {todo.done ? "✅" : "❌"}</span>
                <button
                  className="mark"
                  onClick={() => markDone(todo.id)}
                >
                  {todo.done ? "↩️ Zurücksetzen" : "✓ Erledigen"}
                </button>
                <button
                  className="delete"
                  onClick={() => deleteTodo(todo.id)}
                >
                  Löschen
                </button>
                <button
                  className="edit"
                  onClick={() => {
                    setEditId(todo.id);
                    setEditTitle(todo.title);
                  }}
                >
                  Bearbeiten
                </button>
              </>
            )}
          </li>
        ))}
      </ul>
    </div>
  );

}

export default TodoList;
