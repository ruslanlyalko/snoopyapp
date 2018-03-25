package com.ruslanlyalko.snoopyapp.presentation.widget;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoPreviewActivity extends AppCompatActivity {

    @BindView(R.id.photo_view) PhotoView mPhotoView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    private String mUri = "";
    private String mUserName = "";
    private String mFolder = "";

    public static Intent getLaunchIntent(final AppCompatActivity launchActivity, String uri, String userName) {
        Intent intent = new Intent(launchActivity, PhotoPreviewActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_URI, uri);
        intent.putExtra(Keys.Extras.EXTRA_USER_NAME, userName);
        return intent;
    }

    public static Intent getLaunchIntent(final AppCompatActivity launchActivity, final String uri, final String userName, final String storage) {
        Intent intent = new Intent(launchActivity, PhotoPreviewActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_URI, uri);
        intent.putExtra(Keys.Extras.EXTRA_USER_NAME, userName);
        intent.putExtra(Keys.Extras.EXTRA_FOLDER, storage);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.nothing);
        setContentView(R.layout.activity_show_image);
        ButterKnife.bind(this);
        parseExtras();
        loadWithGlide(mUri);
    }

    private void parseExtras() {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUri = bundle.getString(Keys.Extras.EXTRA_URI);
            mUserName = bundle.getString(Keys.Extras.EXTRA_USER_NAME);
            mFolder = bundle.getString(Keys.Extras.EXTRA_FOLDER, DefaultConfigurations.STORAGE_EXPENSES);
        }
    }



    private void loadWithGlide(String uri) {
        if (uri == null || uri.isEmpty()) return;
        setTitle("Загрузка....");
        if (uri.contains("http")) {
            Glide.with(PhotoPreviewActivity.this).load(uri).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable final GlideException e, final Object model, final Target<Drawable> target, final boolean isFirstResource) {
                    setTitle("Помилка при загрузці");
                    mProgressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(final Drawable resource, final Object model, final Target<Drawable> target, final DataSource dataSource, final boolean isFirstResource) {
                    setTitle("Автор: " + mUserName);
                    mProgressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(mPhotoView);
        } else {
            StorageReference ref = FirebaseStorage.getInstance().getReference(mFolder).child(uri);
            //load mPhotoView using Glide
            ref.getDownloadUrl().addOnSuccessListener(uri1 -> Glide.with(
                    PhotoPreviewActivity.this).load(uri1).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable final GlideException e, final Object model, final Target<Drawable> target, final boolean isFirstResource) {
                    setTitle("Помилка при загрузці");
                    mProgressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(final Drawable resource, final Object model, final Target<Drawable> target, final DataSource dataSource, final boolean isFirstResource) {
                    setTitle("Автор: " + mUserName);
                    mProgressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(mPhotoView));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.fadeout);
    }
}
