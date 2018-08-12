package com.example.xyzreader.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.xyzreader.data.Article;
import com.example.xyzreader.data.ArticleDao;
import com.example.xyzreader.data.ArticleDatabase;
import com.example.xyzreader.data.ArticleDatabaseProvider;

import java.util.List;

public class ArticleViewModel extends AndroidViewModel {

    private final Context applicationContext;
    private ArticleDatabase mDb;
    public LiveData<List<Integer>> mArticleIds;

    public ArticleViewModel(Application application) {
        super(application);
        applicationContext = application.getApplicationContext();
        getmDb();
    }

    private ArticleDatabase getmDb() {
        if(mDb == null){
            mDb = ArticleDatabaseProvider.getDatabase(applicationContext);
        }
        return mDb;
    }

    public LiveData<List<Article>> getAllArticles() {
        return getmDb().getArticleDao().getAllArticles();
    }

    public LiveData<Article> getArticleById(Integer articleId) {
        return getmDb().getArticleDao().getArticleByid(articleId);
    }

    public void getArticleIds() {
        this.mArticleIds = getmDb().getArticleDao().articleIdsByDate();
    }
}
