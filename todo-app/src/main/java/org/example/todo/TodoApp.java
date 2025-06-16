package org.example.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TodoApp {
    private static List<TodoItem> todoList = new ArrayList<>();
    private static int nextId = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- ToDo Liste ---");
            for (TodoItem item : todoList) {
                System.out.println(item);
            }

            System.out.println("\n[1] Aufgabe hinzufügen");
            System.out.println("[2] Aufgabe als erledigt markieren");
            System.out.println("[0] Beenden");
            System.out.print("Auswahl: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Aufgabe: ");
                    String title = scanner.nextLine();
                    todoList.add(new TodoItem(nextId++, title));
                    break;
                case 2:
                    System.out.print("ID der Aufgabe: ");
                    int id = scanner.nextInt();
                    markAsDone(id);
                    break;
                case 0:
                    System.out.println("Programm beendet.");
                    return;
                default:
                    System.out.println("Ungültige Auswahl.");
            }
        }
    }

    private static void markAsDone(int id) {
        for (TodoItem item : todoList) {
            if (item.getId() == id) {
                item.markAsDone();
                return;
            }
        }
        System.out.println("Aufgabe mit ID " + id + " nicht gefunden.");
    }
}
