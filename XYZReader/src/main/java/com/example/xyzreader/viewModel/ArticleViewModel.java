package com.example.xyzreader.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.example.xyzreader.data.Article;
import com.example.xyzreader.data.ArticleDao;
import com.example.xyzreader.data.ArticleDatabase;
import com.example.xyzreader.data.ArticleDatabaseProvider;

import java.util.List;

public class ArticleViewModel extends AndroidViewModel {

    private final Context applicationContext;
    private ArticleDatabase mDb;

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
        ArticleDao articleDao = mDb.getArticleDao();
        return articleDao.getAllArticles();
    }
}
