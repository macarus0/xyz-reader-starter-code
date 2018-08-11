package com.example.xyzreader.data;

import android.arch.persistence.room.Room;
import android.content.Context;

public class ArticleDatabaseProvider {

    private static ArticleDatabase mDb;
    // Implement this bad boy

    public static ArticleDatabase getDatabase(Context context) {
        if (mDb == null) {
            mDb = createDatabase(context);
        }
        return mDb;
    }

    private static ArticleDatabase createDatabase(Context context) {
        mDb = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), ArticleDatabase.class).build();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Article[] articles = ArticleJson.fetchArticles();
                if(articles == null) {
                    mDb = null;
                    return;
                }
                mDb.getArticleDao().insertAll(articles);
            }
        });
        t.start();
        return mDb;
    }
}
