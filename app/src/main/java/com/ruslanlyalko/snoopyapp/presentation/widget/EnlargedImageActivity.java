package com.ruslanlyalko.snoopyapp.presentation.widget;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.transition.TransitionInflater;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.presentation.base.BaseActivity;
import com.ruslanlyalko.snoopyapp.presentation.widget.elasticdrag.ElasticDragDismissFrameLayout;

import butterknife.BindView;

public class EnlargedImageActivity extends BaseActivity {

    private static final String ENLARGED_IMAGE_EXT = "q";
    @BindView(R.id.enlarged_image_view) PhotoView mImageView;
    @BindView(R.id.draggable_frame) ElasticDragDismissFrameLayout mDraggableFrame;

    private String mPath;
    private ElasticDragDismissFrameLayout.SystemChromeFader mListener;

    public static Intent getLaunchIntent(BaseActivity baseActivity, String imageUrl) {
        Intent intent = new Intent(baseActivity, EnlargedImageActivity.class);
        intent.putExtra(ENLARGED_IMAGE_EXT, imageUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAttributes();
        if (savedInstanceState != null) {
            mPath = savedInstanceState.getString(ENLARGED_IMAGE_EXT);
        }
        Glide.with(this)
                .load(mPath)
                .into(mImageView);
        supportPostponeEnterTransition();
        mImageView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                }
        );
        mListener = new ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            @Override
            public void onDragDismissed() {
                if (mDraggableFrame.getTranslationY() > 0) {
                    getWindow().setReturnTransition(
                            TransitionInflater.from(EnlargedImageActivity.this)
                                    .inflateTransition(R.transition.enlarged_return_downward));
                }
                finishAfterTransition();
            }
        };
        mDraggableFrame.addListener(mListener);
    }
    
    @Override
    public int getLayoutResource() {
        return R.layout.activity_enlarged_image;
    }

    @Override
    protected void setupView() {
    }

    @Override
    public void onDestroy() {
        mDraggableFrame.removeListener(mListener);
        super.onDestroy();
    }

    private void initAttributes() {
        mPath = getIntent().getStringExtra(ENLARGED_IMAGE_EXT);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(ENLARGED_IMAGE_EXT, mPath);
    }
}
