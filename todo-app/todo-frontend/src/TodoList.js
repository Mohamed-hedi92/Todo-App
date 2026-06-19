import React, { useState, useEffect } from "react";
import { todoApi } from "./api";
import "./App.css";

function TodoList() {
  const [todos, setTodos] = useState([]);
  const [newTitle, setNewTitle] = useState("");
  const [editId, setEditId] = useState(null);
  const [editTitle, setEditTitle] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchTodos();
  }, []);

  const fetchTodos = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await todoApi.getAll();
      setTodos(res.data);
    } catch (err) {
      console.error("Fehler beim Laden:", err);
      setError("Todos konnten nicht geladen werden. Bitte später erneut versuchen.");
    } finally {
      setLoading(false);
    }
  };

  const addTodo = async () => {
    if (!newTitle.trim()) return;
    try {
      setError(null);
      await todoApi.create(newTitle);
      setNewTitle("");
      await fetchTodos();
    } catch (err) {
      console.error("Fehler beim Hinzufügen:", err);
      setError("Todo konnte nicht hinzugefügt werden.");
    }
  };

  const deleteTodo = async (id) => {
    try {
      setError(null);
      await todoApi.delete(id);
      await fetchTodos();
    } catch (err) {
      console.error("Fehler beim Löschen:", err);
      setError("Todo konnte nicht gelöscht werden.");
    }
  };

  const markDone = async (id) => {
    try {
      setError(null);
      await todoApi.toggleDone(id);
      await fetchTodos();
    } catch (err) {
      console.error("Fehler beim Markieren:", err);
      setError("Status konnte nicht geändert werden.");
    }
  };

  const updateTitle = async (id) => {
    try {
      setError(null);
      await todoApi.updateTitle(id, editTitle);
      setEditId(null);
      setEditTitle("");
      await fetchTodos();
    } catch (err) {
      console.error("Fehler beim Aktualisieren:", err);
      setError("Titel konnte nicht aktualisiert werden.");
    }
  };

  return (
    <div className="todo-container">
      <h2>Meine Todos</h2>

      {error && (
        <div className="error-banner" role="alert">
          {error}
          <button className="error-close" onClick={() => setError(null)}>×</button>
        </div>
      )}

      <div className="todo-input">
        <input
          type="text"
          name="new-todo"
          placeholder="Neue Aufgabe"
          value={newTitle}
          onChange={(e) => setNewTitle(e.target.value)}
        />
        <button id="add-todo" onClick={addTodo}>Hinzufügen</button>
      </div>

      {loading ? (
        <p className="loading">Lade Todos...</p>
      ) : todos.length === 0 ? (
        <p className="empty-state">Keine Todos vorhanden. Leg eine neue Aufgabe an!</p>
      ) : (
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
      )}
    </div>
  );
}

export default TodoList;