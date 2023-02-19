package com.northernneckgarbage.nngc.entity;

public class Service {
    private String name;
    private String description;
    private String price;
    private String id;

    public Service(String name, String description, String price, String id) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }
}
