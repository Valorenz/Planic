package com.example.planic.model;

public class NoteModel {
    public String id;
    public String content;
    public String imageUrl;
    public String userId;

    public NoteModel() {
    }

    public NoteModel(String id, String content, String imageUrl, String userId) {
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }
}