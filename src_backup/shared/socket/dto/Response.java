package shared.socket.dto;

import com.google.gson.Gson;

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

    public String toJson() {
        return gson.toJson(this);
    }

    public static Response fromJson(String json) {
        return gson.fromJson(json, Response.class);
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
}