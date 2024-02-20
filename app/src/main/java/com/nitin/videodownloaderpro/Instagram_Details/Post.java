package com.nitin.videodownloaderpro.Instagram_Details;

public class Post {
    private String type;
    private String caption;
    private String imageUrl;
    private String videoUrl;
    private String postUrl;

    public Post(String type, String caption, String imageUrl, String postUrl) {
        this.type = type;
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.postUrl = postUrl;
    }

    public String getType() {
        return type;
    }

    public String getCaption() {
        return caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getPostUrl() {
        return postUrl;
    }
}

