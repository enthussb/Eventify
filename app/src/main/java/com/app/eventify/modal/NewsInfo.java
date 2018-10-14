package com.app.eventify.modal;

public class NewsInfo
{
    private String description, image_url, title, thumbnail_url;
    private long timestamp;

    public  NewsInfo()
    {

    }

    public NewsInfo(String description, String image_url, String title, String thumbnail_url, long timestamp)
    {
        this.description = description;
        this.image_url = image_url;
        this.title = title;
        this.thumbnail_url = thumbnail_url;
        this.timestamp = timestamp;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
