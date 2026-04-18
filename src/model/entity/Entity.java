package model.entity;

import java.util.UUID;

public abstract class Entity {
    protected final String id;

    public Entity() {
        this.id = generateId();
    }
    //tạo id ngẫu nhiên
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ID: " + id;
    }
}


