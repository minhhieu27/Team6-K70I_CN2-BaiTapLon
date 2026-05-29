package com.app.socket.dto;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private static final Gson gson = new Gson();

    private MessageType type;
    private Map<String, Object> data;

    public Request() {
        this.data = new HashMap<>();
    }

    public Request(MessageType type, Map<String, Object> data) {
        this.type = type;
        this.data = data != null ? data : new HashMap<>();
    }

    public static Request of(MessageType type) {
        return new Request(type, new HashMap<>());
    }

    public static Map<String, Object> mapOf(Object... values) {
        Map<String, Object> map = new HashMap<>();

        for (int i = 0; i < values.length - 1; i += 2) {
            map.put(String.valueOf(values[i]), values[i + 1]);
        }

        return map;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static Request fromJson(String json) {
        return gson.fromJson(json, Request.class);
    }

    public Object get(String key) {
        if (data == null) {
            return null;
        }

        return data.get(key);
    }

    public String getString(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }

    public Double getDouble(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Integer getInt(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data != null ? data : new HashMap<>();
    }
}