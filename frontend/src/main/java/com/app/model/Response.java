package com.app.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    
    private String type;

    private String message;

    private Map<String, Object> data;

    public Response(){}
}
