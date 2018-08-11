package com.example.xyzreader.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Article.class}, version=1)
public abstract class ArticleDatabase extends RoomDatabase {
    public abstract ArticleDao getArticleDao();

}


