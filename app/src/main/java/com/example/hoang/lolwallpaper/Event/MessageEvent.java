package com.example.hoang.lolwallpaper.Event;

/**
 * Created by HOANG on 3/5/2018.
 */

public class MessageEvent {
    String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
