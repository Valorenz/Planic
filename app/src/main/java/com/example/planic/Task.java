package com.example.planic;

public class Task {
    public String id;
    public String title;
    public String description;
    public String deadline;

    public Task() {
    }

    public Task(String id, String title, String description, String deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }
}
