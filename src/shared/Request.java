package shared;

import shared.MessageType;

import java.io.Serializable;
import java.util.Map;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private Map<String, Object> data;

    public Request(MessageType type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
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
        this.data = data;
    }
}