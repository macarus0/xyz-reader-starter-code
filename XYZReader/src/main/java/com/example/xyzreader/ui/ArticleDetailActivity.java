package com.example.xyzreader.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.example.xyzreader.R;
import com.example.xyzreader.viewModel.ArticleViewModel;

import java.util.List;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    public static final String ARTICLE_ID = "article_id";

    private static final String TAG = "ArticleDetailActivity";

    private long mStartId;

    private long mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    private int mTopInset;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private View mUpButtonContainer;
    private View mUpButton;

    private ArticleViewModel mArticleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);

        mArticleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mUpButton.animate()
                        .alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
                        .setDuration(300);
            }

            @Override
            public void onPageSelected(int position) {
                mSelectedItemId = mArticleViewModel.mArticleIds.getValue().get(position);
            }
        });

        mUpButtonContainer = findViewById(R.id.up_container);

        mUpButton = findViewById(R.id.action_up);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });

        if (savedInstanceState == null) {
            if (getIntent() != null) {
                mStartId = getIntent().getIntExtra(ARTICLE_ID, 0);
                mSelectedItemId = mStartId;
            }
        } else {
            mStartId = savedInstanceState.getInt(ARTICLE_ID);
            mSelectedItemId = mStartId;
        }
        Log.i(TAG, "onCreate: " + mStartId);
        mArticleViewModel.getArticleIds();
        mArticleViewModel.mArticleIds.observe(this, new Observer<List<Integer>>() {
            @Override
            public void onChanged(@Nullable List<Integer> articleIds) {
                onLoadFinished(articleIds);
            }
        });
    }


    public void onLoadFinished(List<Integer> articleIds) {
        mPager.setAdapter(mPagerAdapter);
        mPagerAdapter.notifyDataSetChanged();
        // Select the start ID
        if (mStartId > 0) {
            for(int i=0; i < articleIds.size(); i++) {
                Integer id = articleIds.get(i);
                if (id == mStartId) {
                    final int position = i;
                    mPager.setCurrentItem(i, false);
                    Log.i(TAG, "onLoadFinished: " + i);
                    break;
                }
            }
            mStartId = 0;
        }

    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Integer id = mArticleViewModel.mArticleIds.getValue().get(position);
            return ArticleDetailFragment.newInstance(id);
        }

        @Override
        public int getCount() {
            return (mArticleViewModel.mArticleIds != null) ? mArticleViewModel.mArticleIds.getValue().size() : 0;
        }
    }
}
