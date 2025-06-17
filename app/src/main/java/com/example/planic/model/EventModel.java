package com.example.planic.model;

public class EventModel {
    private String id;
    private String title;
    private String category;
    private String date;
    private String start;
    private String end;
    private String desc;
    private String imgUrl;

    public EventModel() {
        // Diperlukan oleh Firestore untuk deserialisasi otomatis
    }

    public EventModel(String id, String title, String category, String date, String start, String end, String desc, String imgUrl) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.date = date;
        this.start = start;
        this.end = end;
        this.desc = desc;
        this.imgUrl = imgUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }

    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
}
