package com.app.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Request {

    private String type;

    private Map<String, Object> data;

    public Request(){}

    public Request(String type, Map<String, Object> data){
        this.type = type;
        this.data = data;
    }
}
