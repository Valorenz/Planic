package com.example.planic;

public class Task {
    public String id;
    public String title;
    public String description;
    public String deadline;
    public String imageUrl;

    public Task() {}

    public Task(String id, String title, String description, String deadline, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.imageUrl = imageUrl;
    }

}
