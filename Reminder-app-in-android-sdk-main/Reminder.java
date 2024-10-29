package com.example.reminderapp.models;

/**
 * Reminder model represents a reminder with title, description, date, time, and associated user.
 */
public class Reminder {
    private int id;
    private String title;
    private String description;
    private String date;
    private String time;
    private int userId;

    // Default constructor
    public Reminder() {
    }

    // Constructor for adding a new reminder (without ID)
    public Reminder(String title, String description, String date, String time, int userId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.userId = userId;
    }

    // Constructor for editing an existing reminder (with ID)
    public Reminder(int id, String title, String description, String date, String time, int userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.userId = userId;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    // No setter for ID as it is auto-incremented in the database

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
