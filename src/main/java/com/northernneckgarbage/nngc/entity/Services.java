package com.northernneckgarbage.nngc.entity;

public class Services {
    private final String name;
    private final String description;
    private final String price;
    private final String id;

    public Services(String name, String description, String price, String id) {
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
