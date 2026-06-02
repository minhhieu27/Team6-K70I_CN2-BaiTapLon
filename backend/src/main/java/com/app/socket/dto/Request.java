package com.app.socket.dto;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


//đóng gói dữ liệu mà client và server gửi cho nhau qua socket
public class Request {

    private static final Gson gson = new Gson();

    private MessageType type;
    private Map<String, Object> data;

    //Tạo một Request rỗng, đồng thời khởi tạo data, tránh lỗi NullPointerException
    public Request() {
        this.data = new HashMap<>();
    }

    public Request(MessageType type, Map<String, Object> data) {
        this.type = type;
        this.data = data != null ? data : new HashMap<>();
    }

    //Tạo nhanh một request chỉ có type, không có dữ liệu kèm theo
    public static Request of(MessageType type) {
        return new Request(type, new HashMap<>());
    }

    //Tạo nhanh một Map<String, Object> để đưa vào data
    public static Map<String, Object> mapOf(Object... values) {
        Map<String, Object> map = new HashMap<>();

        for (int i = 0; i < values.length - 1; i += 2) {
            map.put(String.valueOf(values[i]), values[i + 1]);
        }

        return map;
    }

    //Chuyển object Request thành chuỗi JSON để gửi qua socket
    public String toJson() {
        return gson.toJson(this);
    }

    //Chuyển chuỗi JSON nhận được từ socket thành object Request
    public static Request fromJson(String json) {
        return gson.fromJson(json, Request.class);
    }

    //Lấy giá trị trong data theo key
    public Object get(String key) {
        if (data == null) {
            return null;
        }

        return data.get(key);
    }

    //Lấy dữ liệu trong data và chuyển về kiểu String
    public String getString(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }

    //Lấy dữ liệu trong data và chuyển về kiểu Double
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

    //Lấy dữ liệu trong data và chuyển về kiểu Integer
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
