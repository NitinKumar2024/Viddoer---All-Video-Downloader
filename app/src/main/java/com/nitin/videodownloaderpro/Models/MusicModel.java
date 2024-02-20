package com.nitin.videodownloaderpro.Models;

public class MusicModel {

    String title;
    String music_url;

    public MusicModel(String title, String music_url) {
        this.title = title;
        this.music_url = music_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMusic_url() {
        return music_url;
    }

    public void setMusic_url(String music_url) {
        this.music_url = music_url;
    }
}
