package com.example.xyzreader.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ArticleDao {

    @Insert
    void insertAll(Article... articles);

    @Query("SELECT * FROM article ORDER BY publishedDate DESC")
    LiveData<List<Article>> getAllArticles();

    @Query("SELECT * FROM article where id = :articleId")
    LiveData<Article> getArticleByid(int articleId);

    @Query("SELECT id FROM article ORDER BY publishedDate DESC")
    LiveData<List<Integer>> articleIdsByDate();



}
