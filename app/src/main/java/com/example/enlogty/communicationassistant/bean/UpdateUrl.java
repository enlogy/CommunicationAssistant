package com.example.enlogty.communicationassistant.bean;

public class UpdateUrl {
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private String body;

    @Override
    public String toString() {
        return "UpdateUrl{" +
                "body='" + body + '\'' +
                '}';
    }
}
