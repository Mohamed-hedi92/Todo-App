import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import TodoList from "./TodoList";
import axios from "axios";

jest.mock("axios");

describe("TodoList", () => {
  beforeEach(() => {
    axios.get.mockResolvedValue({ data: [] });
  });

  describe("Hinzufügen von Todos", () => {
    // Testet das Hinzufügen eines neuen Todos
    it("fügt ein neues Todo hinzu", async () => {
      const testTitle = "Buch lesen";
      axios.post.mockResolvedValue({ status: 201 });

      render(<TodoList />);

      const input = screen.getByPlaceholderText(/Neue Aufgabe/i);
      const button = screen.getByText(/Hinzufügen/i);

      await userEvent.type(input, testTitle);
      expect(input).toHaveValue(testTitle);

      await userEvent.click(button);

      await waitFor(() => {
        expect(axios.post).toHaveBeenCalledWith(
          "http://localhost:8085/todos",
          { title: testTitle, done: false }
        );
      });

      await waitFor(() => {
        expect(input).toHaveValue("");
      });
    });

    // Testet das Hinzufügen mehrerer Todos hintereinander
    it("fügt mehrere Todos nacheinander hinzu", async () => {
      const todos = [
        [],
        [{ id: 1, title: "Erstes Todo", done: false }],
        [
          { id: 1, title: "Erstes Todo", done: false },
          { id: 2, title: "Zweites Todo", done: false },
        ],
      ];

      axios.get
        .mockResolvedValueOnce({ data: todos[0] })
        .mockResolvedValueOnce({ data: todos[1] })
        .mockResolvedValueOnce({ data: todos[2] });

      axios.post.mockResolvedValue({ status: 201 });

      render(<TodoList />);
      const input = screen.getByPlaceholderText(/Neue Aufgabe/i);
      const button = screen.getByText(/Hinzufügen/i);

      await userEvent.type(input, "Erstes Todo");
      await userEvent.click(button);

      await waitFor(() => {
        expect(screen.getByText(/Erstes Todo/i)).toBeInTheDocument();
      });

      await userEvent.clear(input);
      await userEvent.type(input, "Zweites Todo");
      await userEvent.click(button);

      await waitFor(() => {
        expect(screen.getByText(/Zweites Todo/i)).toBeInTheDocument();
      });
    });

    // Testet, dass kein Todo hinzugefügt wird, wenn Eingabefeld leer ist
    it("fügt kein Todo hinzu, wenn das Eingabefeld leer ist", async () => {
      render(<TodoList />);
      const button = screen.getByText(/Hinzufügen/i);

      await userEvent.click(button);

      await waitFor(() => {
        expect(axios.post).not.toHaveBeenCalled();
      });

      expect(screen.queryByText(/❌|✅/)).not.toBeInTheDocument();
    });
  });

  describe("Todo bearbeiten", () => {
    // Testet das Ändern des Titels eines Todos
    it("ändert den Titel eines Todos", async () => {
      const testTodo = { id: 3, title: "Alter Titel", done: false };
      axios.get.mockResolvedValue({ data: [testTodo] });
      axios.put.mockResolvedValue({ data: { success: true } });

      render(<TodoList />);

      const alterTitel = await screen.findByText(/Alter Titel/i);
      expect(alterTitel).toBeInTheDocument();

      const bearbeitenButton = screen.getByText("Bearbeiten");
      await userEvent.click(bearbeitenButton);

      const input = screen.getByDisplayValue("Alter Titel");
      await userEvent.clear(input);
      await userEvent.type(input, "Neuer Titel");

      const speichernButton = screen.getByText("Speichern");
      await userEvent.click(speichernButton);

      await waitFor(() => {
        expect(axios.put).toHaveBeenCalledWith(
          `http://localhost:8085/todos/3/title`,
          JSON.stringify("Neuer Titel"),
          { headers: { "Content-Type": "application/json" } }
        );
      });
    });
  });

  describe("Todo erledigen und zurücksetzen", () => {
    const testTodo = { id: 1, title: "Test Todo", done: false };

    beforeEach(() => {
      axios.get.mockResolvedValue({ data: [testTodo] });
    });

    // Testet das Markieren eines Todos als erledigt
    it("markiert ein Todo als erledigt", async () => {
      axios.put.mockResolvedValue({ data: { success: true } });

      render(<TodoList />);

      const todoText = await screen.findByText(/Test Todo/i);
      expect(todoText).toBeInTheDocument();

      const erledigenButton = screen.getByText("✓ Erledigen");
      await userEvent.click(erledigenButton);

      await waitFor(() => {
        expect(axios.put).toHaveBeenCalledWith(
          `http://localhost:8085/todos/${testTodo.id}/done`
        );
      });
    });

    // Testet das Zurücksetzen eines erledigten Todos
    it("setzt ein erledigtes Todo zurück", async () => {
      const doneTodo = { id: 2, title: "Erledigtes Todo", done: true };
      axios.get.mockResolvedValue({ data: [doneTodo] });
      axios.put.mockResolvedValue({ data: { success: true } });

      render(<TodoList />);

      const todoText = await screen.findByText(/Erledigtes Todo/i);
      expect(todoText).toBeInTheDocument();

      const zuruecksetzenButton = screen.getByText("↩️ Zurücksetzen");
      await userEvent.click(zuruecksetzenButton);

      await waitFor(() => {
        expect(axios.put).toHaveBeenCalledWith(
          `http://localhost:8085/todos/${doneTodo.id}/done`
        );
      });
    });
  });

  describe("Todo löschen", () => {
    // Testet das Löschen eines Todos
    it("löscht ein Todo beim Klick auf Löschen", async () => {
      const todoToDelete = { id: 3, title: "Todo zum Löschen", done: false };
      axios.get.mockResolvedValue({ data: [todoToDelete] });
      axios.delete.mockResolvedValue({ status: 200 });

      render(<TodoList />);

      const todoElement = await screen.findByText(/Todo zum Löschen/i);
      expect(todoElement).toBeInTheDocument();

      const deleteButton = screen.getByRole("button", { name: /Löschen/i });
      await userEvent.click(deleteButton);

      await waitFor(() => {
        expect(axios.delete).toHaveBeenCalledWith(
          `http://localhost:8085/todos/${todoToDelete.id}`
        );
      });
    });
  });

  describe("Anzeige der Todo-Liste", () => {
    // Testet das Laden und Anzeigen von Todos beim Start
    it("lädt und zeigt Todos beim Start an", async () => {
      const todos = [
        { id: 1, title: "Test Todo 1", done: false },
        { id: 2, title: "Test Todo 2", done: true },
      ];

      axios.get.mockResolvedValue({ data: todos });

      render(<TodoList />);

      for (const todo of todos) {
        await waitFor(() => {
          expect(screen.getByText(todo.title, { exact: false })).toBeInTheDocument();
        });
      }

      expect(screen.getByText("Test Todo 1 ❌")).toBeInTheDocument();
      expect(screen.getByText("Test Todo 2 ✅")).toBeInTheDocument();
    });

    // Testet, dass keine Todos angezeigt werden, wenn die Liste leer ist
    it("zeigt keine Todos, wenn die Liste leer ist", async () => {
      axios.get.mockResolvedValue({ data: [] });

      render(<TodoList />);

      await waitFor(() => {
        expect(screen.queryByText(/❌|✅/)).not.toBeInTheDocument();
      });
    });
  });
});
