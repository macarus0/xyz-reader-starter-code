package com.example.xyzreader.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.Article;
import com.example.xyzreader.viewModel.ArticleViewModel;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";

    private Integer mItemId;
    private View mRootView;
    private int mMutedColor;
    private NestedScrollView mScrollView;
    private CoordinatorLayout mCoordinatorLayout;
    private View mPhotoContainerView;
    private ImageView mPhotoView;
    private int mScrollY;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;
    private ArticleViewModel mArticleViewModel;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);
    private TextView mTitleView;
    private TextView mBylineDateTextView;
    private TextView mBylineAuthorTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(Integer itemId) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getInt(ARG_ITEM_ID);
        }
        mArticleViewModel = ViewModelProviders.of(getActivity()).get(ArticleViewModel.class);

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
        mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(
                R.dimen.detail_card_top_margin);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mCoordinatorLayout =
                mRootView.findViewById(R.id.detail_coordinator_layout);

        mScrollView = mRootView.findViewById(R.id.scrollview);


        mTitleView = mRootView.findViewById(R.id.article_title);
        mBylineDateTextView =  mRootView.findViewById(R.id.article_byline_date);
        mBylineAuthorTextView = mRootView.findViewById(R.id.article_byline_author);

        mPhotoView = (ImageView) mRootView.findViewById(R.id.photo);
        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        mArticleViewModel.getArticleById(mItemId).observe(this, new Observer<Article>() {
            @Override
            public void onChanged(@Nullable Article article) {
                onLoadFinished(article);
            }
        });
        return mRootView;
    }

    private void updateStatusBar(int sourceColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = Color.argb(255,
                    Color.red(sourceColor),
                    Color.green(sourceColor),
                    Color.blue(sourceColor));
            getActivity().getWindow().setStatusBarColor(color);
        }
    }

    static float progress(float v, float min, float max) {
        return constrain((v - min) / (max - min), 0, 1);
    }

    static float constrain(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    private Date parsePublishedDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    private void setTitleTextColors(Palette.Swatch swatch) {
        mBylineAuthorTextView.setTextColor(swatch.getBodyTextColor());
        mBylineDateTextView.setTextColor(swatch.getTitleTextColor());
        mTitleView.setTextColor(swatch.getTitleTextColor());
    }

    private void bindViews(Article article) {
        if (mRootView == null) {
            return;
        }

        TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);

        if (article != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            loadPhoto(article.getPhotoUrl());
            mTitleView.setText(article.getTitle());
            Date publishedDate = parsePublishedDate(article.getPublishedDate());
            String dateString;
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                dateString = DateUtils.getRelativeTimeSpanString(
                        publishedDate.getTime(),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL).toString();

            } else {
                // If date is before 1902, just show the string
                dateString = outputFormat.format(publishedDate);

            }
            mBylineDateTextView.setText(String.format(getString(R.string.byline_date), dateString));
            mBylineAuthorTextView.setText(article.getAuthor());
            bodyView.setText(Html.fromHtml(article.getBody().replaceAll("(\r\n\r\n|\n\n)", "<br />")));

        } else {
            Log.e(TAG, "bindViews: Loading with no content");
            mRootView.setVisibility(View.GONE);
            mTitleView.setText("N/A");
            bodyView.setText("N/A");
        }
    }

    public void onLoadFinished(Article article) {
        if (!isAdded()) {
            return;
        }
        bindViews(article);
    }

    private void loadPhoto(String photoUrl) {
        ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
                .get(photoUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap bitmap = imageContainer.getBitmap();
                        if (bitmap != null) {
                            mPhotoView.setImageBitmap(imageContainer.getBitmap());
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@NonNull Palette palette) {
                                    mMutedColor = palette.getDarkMutedColor(mMutedColor);
                                    mRootView.findViewById(R.id.meta_bar)
                                            .setBackgroundColor(mMutedColor);
                                    setTitleTextColors(palette.getDarkMutedSwatch());
                                    updateStatusBar(mMutedColor);
                                }
                            });

                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
    }

}
