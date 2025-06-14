package com.example.planic.model;

public class TaskModel {
    public String id;
    public String title;
    public String description;
    public String deadline;
    public String imageUrl;
    public String userId;

    public TaskModel() {}

    public TaskModel(String id, String title, String description, String deadline, String imageUrl, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }
}
