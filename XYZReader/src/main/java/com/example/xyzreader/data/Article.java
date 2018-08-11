package com.example.xyzreader.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Article {
    @PrimaryKey
    int id;
    String serverId;
    @Ignore
    public int getId() {
        return id;
    }
    @Ignore
    public String getServerId() {
        return serverId;
    }
    @Ignore
    public String getTitle() {
        return title;
    }
    @Ignore
    public String getAuthor() {
        return author;
    }
    @Ignore
    public String getBody() {
        return body;
    }
    @Ignore
    public String getThumbUrl() {
        return thumb;
    }
    @Ignore
    public String getPhotoUrl() {
        return photo;
    }
    @Ignore
    public float getAspectRatio() {
        return aspectRatio;
    }
    @Ignore
    public String getPublishedDate() {
        return publishedDate;
    }

    String title;
    String author;
    String body;
    String thumb;
    String photo;
    float aspectRatio;
    String publishedDate;

}
