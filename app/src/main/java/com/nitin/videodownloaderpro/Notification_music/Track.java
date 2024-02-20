package com.nitin.videodownloaderpro.Notification_music;

public class Track {

    private String title;
    private String artist;
    private int image;
    private String musicUri;

    public Track(String title, String artist, int image) {
        this.title = title;
        this.artist = artist;
        this.image = image;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    // Add getter method for the music URI

}
