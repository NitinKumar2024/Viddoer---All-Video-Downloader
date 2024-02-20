package com.nitin.videodownloaderpro.Models;

public class Contact {
    private String imageResource;
    private String name;
    private String time;

    public Contact(String imageResource, String name, String time) {
        this.imageResource = imageResource;
        this.name = name;
        this.time = time;
    }

    public String getImageResource() {
        return imageResource;
    }

    public String getName() {
        return name;
    }
    public String getTime(){

        return time;
    }
}
