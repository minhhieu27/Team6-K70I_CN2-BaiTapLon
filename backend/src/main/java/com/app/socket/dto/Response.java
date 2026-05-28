package com.app.socket.dto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private static final Gson gson = new Gson();

    private boolean success;
    private MessageType type;
    private String message;
    private Object data;

    public Response() {
    }

    public Response(boolean success, MessageType type, String message, Object data) {
        this.success = success;
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public static Response success(MessageType type, String message, Object data) {
        return new Response(true, type, message, data);
    }

    public static Response error(String message) {
        return new Response(false, MessageType.ERROR, message, null);
    }

    public static Response error(String message, Object data) {
        return new Response(false, MessageType.ERROR, message, data);
    }

    public static Response connectionError(String message) {
        return new Response(false, MessageType.CONNECTION_ERROR, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public MessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<String, Object> getDataAsMap() {
        if (data == null) {
            return new HashMap<>();
        }

        String json = gson.toJson(data);
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

        Map<String, Object> map = gson.fromJson(json, mapType);

        return map != null ? map : new HashMap<>();
    }

    public Object get(String key) {
        Map<String, Object> map = getDataAsMap();
        return map.get(key);
    }

    public String getString(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public Double getDouble(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Integer getInt(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static Response fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        return gson.fromJson(json, Response.class);
    }

    @Override
    public String toString() {
        return toJson();
    }
}