package com.sourav.vegetables.Model;

public class Notification {
    private int id;
    private String title;
    private String body;
    private String send_time;
    private String is_read;

    public Notification() {
    }

    public Notification(int id, String title, String body, String send_time, String is_read) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.send_time = send_time;
        this.is_read = is_read;
    }

    public Notification(String title, String body, String send_time, String is_read) {
        this.title = title;
        this.body = body;
        this.send_time = send_time;
        this.is_read = is_read;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }
}
