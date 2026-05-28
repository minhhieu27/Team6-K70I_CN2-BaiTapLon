package shared.socket.dto;


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

    public Boolean getBoolean(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        return Boolean.parseBoolean(value.toString());
    }

    public void put(String key, Object value) {
        if (data == null) {
            data = new HashMap<>();
        }

        data.put(key, value);
    }

    public boolean containsKey(String key) {
        return data != null && data.containsKey(key);
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static Request fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        return gson.fromJson(json, Request.class);
    }

    public static Request of(MessageType type) {
        return new Request(type, new HashMap<>());
    }

    public static Request of(MessageType type, Map<String, Object> data) {
        return new Request(type, data);
    }

    public static Map<String, Object> mapOf(Object... keyValues) {
        Map<String, Object> map = new HashMap<>();

        if (keyValues == null) {
            return map;
        }

        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Key-value arguments must be even");
        }

        for (int i = 0; i < keyValues.length; i += 2) {
            String key = String.valueOf(keyValues[i]);
            Object value = keyValues[i + 1];

            map.put(key, value);
        }

        return map;
    }

    @Override
    public String toString() {
        return toJson();
    }
}