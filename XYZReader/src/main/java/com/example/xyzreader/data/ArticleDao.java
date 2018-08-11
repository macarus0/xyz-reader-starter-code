package com.example.xyzreader.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ArticleDao {

    @Insert
    void insertAll(Article... articles);

    @Query("SELECT * FROM article")
    LiveData<List<Article>> getAllArticles();

}
