package org.example.todo;

    public class TodoItem {
        private int id;
        private String title;
        private boolean done;

        public TodoItem(int id, String title) {
            this.id = id;
            this.title = title;
            this.done = false;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public boolean isDone() {
            return done;
        }

        public void markAsDone() {
            this.done = true;
        }

        @Override
        public String toString() {
            return (done ? "[x] " : "[ ] ") + id + ": " + title;
        }
    }


