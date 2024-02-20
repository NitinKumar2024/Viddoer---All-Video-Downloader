package com.nitin.videodownloaderpro;

public class MediaItem  {
    public enum MediaType {
        VIDEO,
        AUDIO
    }

    private String fileName;
    private String filePath;
    private MediaType mediaType;
    private int position; // Position of the file

    public MediaItem(String fileName, String filePath, MediaType mediaType, int position) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.mediaType = mediaType;
        this.position = position;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
