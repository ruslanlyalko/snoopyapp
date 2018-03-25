package com.ruslanlyalko.snoopyapp.presentation.ui.main.mk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Mk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pub.devrel.easypermissions.EasyPermissions;

public class MkEditActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_IMAGE_PERMISSION = 1;

    @BindView(R.id.edit_title1) EditText textTitle1;
    @BindView(R.id.text_title2) TextView textTitle2;
    @BindView(R.id.edit_description) EditText textDescription;
    @BindView(R.id.edit_link) EditText textLink;
    @BindView(R.id.image_view) PhotoView mImageView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.button_upload) Button mButtonUpload;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    boolean mIsNew = false;
    boolean mNeedToSave = false;
    private Mk mMk = new Mk();
    private String mMkKey;
    private String mMkTitle2;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public static Intent getLaunchIntent(final Activity launchIntent, final String mkKey) {
        Intent intent = new Intent(launchIntent, MkEditActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_ITEM_ID, mkKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.nothing);
        setContentView(R.layout.activity_mk_edit);
        ButterKnife.bind(this);
        parseExtras();
        mIsNew = mMkKey == null;
        initRef();
        loadMkItem();
        updateUI();
    }

    private void parseExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mMkKey = bundle.getString(Keys.Extras.EXTRA_ITEM_ID);
            mMkTitle2 = bundle.getString(Keys.Extras.EXTRA_TITLE2);
        }
    }

    private void initRef() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mNeedToSave = true;
            }
        };
        textTitle1.addTextChangedListener(watcher);
        textTitle2.addTextChangedListener(watcher);
        textLink.addTextChangedListener(watcher);
        textDescription.addTextChangedListener(watcher);
    }

    private void loadMkItem() {
        if (mIsNew) return;
        DatabaseReference ref = database.getReference(DefaultConfigurations.DB_MK).child(mMkKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMk = dataSnapshot.getValue(Mk.class);
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateUI() {
        if (mIsNew) {
            setTitle(R.string.title_activity_add);
            textTitle2.setText(mMkTitle2);
        } else {
            setTitle(R.string.title_activity_edit);
            if (mMk != null) {
                textTitle1.setText(mMk.getTitle1());
                textTitle2.setText(mMk.getTitle2());
                textLink.setText(mMk.getLink());
                textDescription.setText(mMk.getDescription());
                if (mMk.getImageUri() != null && !mMk.getImageUri().isEmpty()) {
                    Glide.with(MkEditActivity.this).load(mMk.getImageUri()).into(mImageView);
                }
            }
        }
        mNeedToSave = false;
    }

    @OnClick(R.id.button_upload)
    void onUploadCLicked() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            EasyImage.openChooserWithGallery(this, getString(R.string.choose_images), 0);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.image_permissions), REQUEST_IMAGE_PERMISSION, perms);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(final File imageFile, final EasyImage.ImageSource source, final int type) {
                onPhotosReturned(imageFile);
            }
        });
    }

    private void onPhotosReturned(final File imageFile) {
        mProgressBar.setVisibility(View.VISIBLE);
        mButtonUpload.setVisibility(View.GONE);
        mNeedToSave = true;
        Glide.with(this).load(imageFile).into(mImageView);
        String imageFileName = DateUtils.getCurrentTimeStamp() + "_original" + ".jpg";
        uploadFile(imageFile, imageFileName, 95).addOnSuccessListener(taskSnapshot -> {
            if (taskSnapshot.getDownloadUrl() != null)
                mMk.setImageUri(taskSnapshot.getDownloadUrl().toString());
            mProgressBar.setVisibility(View.GONE);
            mButtonUpload.setVisibility(View.VISIBLE);
        }).addOnFailureListener(exception -> {
            mProgressBar.setVisibility(View.GONE);
            mButtonUpload.setVisibility(View.VISIBLE);
        });
    }

    private UploadTask uploadFile(File file, String fileName, int quality) {
        Bitmap bitmapOriginal = BitmapFactory.decodeFile(file.toString());//= imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        // Meta data for imageView
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("Title1", mMk.getTitle1())
                .setCustomMetadata("UserName", mMk.getUserName())
                .build();
        // name of file in Storage
        return FirebaseStorage.getInstance()
                .getReference(DefaultConfigurations.STORAGE_MK)
                .child(fileName)
                .putBytes(bytes, metadata);
    }

    @Override
    public void onBackPressed() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            Toast.makeText(this, R.string.photo_uploading, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mNeedToSave) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MkEditActivity.this);
            builder.setTitle(R.string.dialog_discart_changes)
                    .setPositiveButton(R.string.action_discard, (dialog, which) -> {
                        mNeedToSave = false;
                        onBackPressed();
                    })
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.nothing, R.anim.fadeout);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        switch (requestCode) {
            case REQUEST_IMAGE_PERMISSION:
                EasyImage.openChooserWithGallery(this, getString(R.string.choose_images), 0);
                break;
        }
    }

    @Override
    public void onPermissionsDenied(final int requestCode, final List<String> perms) {
    }

    private void updateMkModel() {
        mMk.setTitle1(textTitle1.getText().toString());
        mMk.setTitle2(textTitle2.getText().toString());
        mMk.setLink(textLink.getText().toString());
        mMk.setDescription(textDescription.getText().toString());
    }

    private void addMk() {
        updateMkModel();
        mIsNew = false;
        mMkKey = database.getReference(DefaultConfigurations.DB_MK).push().getKey();
        mMk.setKey(mMkKey);
        mMk.setUserId(mUser.getUid());
        mMk.setUserName(mUser.getDisplayName());
        database.getReference(DefaultConfigurations.DB_MK)
                .child(mMkKey).setValue(mMk).addOnCompleteListener(task ->
                Snackbar.make(mImageView, getString(R.string.mk_added), Snackbar.LENGTH_SHORT).show());
        mNeedToSave = false;
    }

    private void updateMk() {
        updateMkModel();
        database.getReference(DefaultConfigurations.DB_MK)
                .child(mMk.getKey()).setValue(mMk).addOnCompleteListener(task -> {
            Toast.makeText(MkEditActivity.this, getString(R.string.mk_updated), Toast.LENGTH_SHORT).show();
            onBackPressed();
        });
        mNeedToSave = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_save) {
            if (mIsNew)
                addMk();
            else
                updateMk();
        }
        return super.onOptionsItemSelected(item);
    }
}
