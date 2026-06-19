import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import TodoList from "./TodoList";
import axios from "axios";
import '@testing-library/jest-dom';

// Mock für axios — WICHTIG: die Mock-Instanz wird INNERHALB der Factory definiert,
// damit jeder Aufruf von axios.create() dieselbe Instanz zurückgibt.
// So teilen sich Test und Komponente denselben Mock.
jest.mock("axios", () => {
  const mockApi = {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  };
  return {
    create: jest.fn(() => mockApi),
    defaults: { headers: { common: {} } },
  };
});

// Greift auf die gemeinsame Mock-Instanz zu
const api = axios.create();

describe("TodoList", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    api.get.mockResolvedValue({ data: [] });
  });

  describe("Hinzufügen von Todos", () => {
    it("fügt ein neues Todo hinzu", async () => {
      const testTitle = "Buch lesen";
      api.post.mockResolvedValue({ status: 201 });

      render(<TodoList />);

      const input = screen.getByPlaceholderText(/Neue Aufgabe/i);
      const button = screen.getByText(/Hinzufügen/i);

      await userEvent.type(input, testTitle);
      expect(input).toHaveValue(testTitle);

      await userEvent.click(button);

      await waitFor(() => {
        expect(api.post).toHaveBeenCalledWith(
          "/todos",
          { title: testTitle, done: false }
        );
      });

      await waitFor(() => {
        expect(input).toHaveValue("");
      });
    });

    it("fügt mehrere Todos nacheinander hinzu", async () => {
      const todos = [
        [],
        [{ id: 1, title: "Erstes Todo", done: false }],
        [
          { id: 1, title: "Erstes Todo", done: false },
          { id: 2, title: "Zweites Todo", done: false },
        ],
      ];

      api.get
        .mockResolvedValueOnce({ data: todos[0] })
        .mockResolvedValueOnce({ data: todos[1] })
        .mockResolvedValueOnce({ data: todos[2] });

      api.post.mockResolvedValue({ status: 201 });

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

    it("fügt kein Todo hinzu, wenn das Eingabefeld leer ist", async () => {
      api.post.mockClear();

      render(<TodoList />);
      const button = screen.getByText(/Hinzufügen/i);

      await userEvent.click(button);

      await waitFor(() => {
        expect(api.post).not.toHaveBeenCalled();
      });

      expect(screen.queryByText(/❌|✅/)).not.toBeInTheDocument();
    });
  });

  describe("Todo bearbeiten", () => {
    it("ändert den Titel eines Todos", async () => {
      const testTodo = { id: 3, title: "Alter Titel", done: false };
      api.get.mockResolvedValue({ data: [testTodo] });
      api.put.mockResolvedValue({ data: { success: true } });

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
        expect(api.put).toHaveBeenCalledWith(
          `/todos/3/title`,
          "Neuer Titel",
          { headers: { "Content-Type": "text/plain" } }
        );
      });
    });
  });

  describe("Todo erledigen und zurücksetzen", () => {
    const testTodo = { id: 1, title: "Test Todo", done: false };

    beforeEach(() => {
      api.get.mockResolvedValue({ data: [testTodo] });
    });

    it("markiert ein Todo als erledigt", async () => {
      api.put.mockResolvedValue({ data: { success: true } });

      render(<TodoList />);

      const todoText = await screen.findByText(/Test Todo/i);
      expect(todoText).toBeInTheDocument();

      const erledigenButton = screen.getByText("✓ Erledigen");
      await userEvent.click(erledigenButton);

      await waitFor(() => {
        expect(api.put).toHaveBeenCalledWith(
          `/todos/${testTodo.id}/done`
        );
      });
    });

    it("setzt ein erledigtes Todo zurück", async () => {
      const doneTodo = { id: 2, title: "Erledigtes Todo", done: true };
      api.get.mockResolvedValue({ data: [doneTodo] });
      api.put.mockResolvedValue({ data: { success: true } });

      render(<TodoList />);

      const todoText = await screen.findByText(/Erledigtes Todo/i);
      expect(todoText).toBeInTheDocument();

      const zuruecksetzenButton = screen.getByText("↩️ Zurücksetzen");
      await userEvent.click(zuruecksetzenButton);

      await waitFor(() => {
        expect(api.put).toHaveBeenCalledWith(
          `/todos/${doneTodo.id}/done`
        );
      });
    });
  });

  describe("Todo löschen", () => {
    it("löscht ein Todo beim Klick auf Löschen", async () => {
      const todoToDelete = { id: 3, title: "Todo zum Löschen", done: false };
      api.get.mockResolvedValue({ data: [todoToDelete] });
      api.delete.mockResolvedValue({ status: 200 });

      render(<TodoList />);

      const todoElement = await screen.findByText(/Todo zum Löschen/i);
      expect(todoElement).toBeInTheDocument();

      const deleteButton = screen.getByRole("button", { name: /Löschen/i });
      await userEvent.click(deleteButton);

      await waitFor(() => {
        expect(api.delete).toHaveBeenCalledWith(
          `/todos/${todoToDelete.id}`
        );
      });
    });
  });

  describe("Anzeige der Todo-Liste", () => {
    it("lädt und zeigt Todos beim Start an", async () => {
      const todos = [
        { id: 1, title: "Test Todo 1", done: false },
        { id: 2, title: "Test Todo 2", done: true },
      ];

      api.get.mockResolvedValue({ data: todos });

      render(<TodoList />);

      for (const todo of todos) {
        await waitFor(() => {
          expect(screen.getByText(todo.title, { exact: false })).toBeInTheDocument();
        });
      }

      expect(screen.getByText("Test Todo 1 ❌")).toBeInTheDocument();
      expect(screen.getByText("Test Todo 2 ✅")).toBeInTheDocument();
    });

    it("zeigt keine Todos, wenn die Liste leer ist", async () => {
      api.get.mockResolvedValue({ data: [] });

      render(<TodoList />);

      await waitFor(() => {
        expect(screen.queryByText(/❌|✅/)).not.toBeInTheDocument();
      });
    });
  });
});