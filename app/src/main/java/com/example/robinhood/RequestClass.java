package com.example.robinhood;


public class RequestClass {
    String title, description, name, phone, address;
    int category;
    boolean active;

    public RequestClass(String title, String description, String name, String phone, String address, int category, boolean active) {
        this.title = title;
        this.description = description;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.category = category;
        this.active = active;
    }
}