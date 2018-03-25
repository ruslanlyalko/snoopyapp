package com.ruslanlyalko.snoopyapp.presentation.ui.main.expenses.edit;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Expense;
import com.ruslanlyalko.snoopyapp.presentation.base.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pub.devrel.easypermissions.EasyPermissions;

public class ExpenseEditActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_IMAGE_PERMISSION = 1;
    @BindView(R.id.image_expense) PhotoView mImageExpense;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.button_upload) Button mButtonUpload;
    @BindView(R.id.edit_title1) EditText mEditTitle1;
    @BindView(R.id.edit_price) EditText mEditPrice;
    @BindView(R.id.text_date) TextView mTextDate;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private Expense mExpense = new Expense();
    private String mExpenseType;
    private boolean mNeedToSave = false;
    private boolean mIsNew = false;

    public static Intent getLaunchIntent(final Activity launchIntent, final Expense expense) {
        Intent intent = new Intent(launchIntent, ExpenseEditActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_ITEM_ID, expense);
        return intent;
    }

    public static Intent getLaunchIntent(final Activity launchIntent, String expenseType) {
        Intent intent = new Intent(launchIntent, ExpenseEditActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_EXPENSE_TYPE, expenseType);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_expense_edit;
    }

    @Override
    protected void parseExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mExpense = (Expense) bundle.getSerializable(Keys.Extras.EXTRA_ITEM_ID);
            mExpenseType = bundle.getString(Keys.Extras.EXTRA_EXPENSE_TYPE);
        }
        mIsNew = mExpense == null;
        if (mIsNew) {
            mTextDate.setText(DateUtils.toString(new Date(), "d-M-yyyy"));
            mExpense = new Expense(mExpenseType,
                    new Date(),
                    mUser.getUid(),
                    mUser.getDisplayName());
        }
    }

    @Override
    public void setupView() {
        setupChangeWatcher();
        if (mIsNew) {
            setTitle(R.string.title_activity_add);
        } else {
            setTitle(R.string.title_activity_edit);
            mTextDate.setText(DateUtils.toString(mExpense.getExpenseDate()));
            mEditTitle1.setText(mExpense.getTitle1());
            mEditPrice.setText(String.valueOf(mExpense.getPrice()));
            if (mExpense.getImage() != null && !mExpense.getImage().isEmpty() && mExpense.getImage().startsWith("http")) {
                try {
                    mImageExpense.setVisibility(View.VISIBLE);
                    Glide.with(ExpenseEditActivity.this).load(mExpense.getImage()).into(mImageExpense);
                } catch (Exception e) {
                    //
                }
            }
        }
        mNeedToSave = false;
    }

    private void setupChangeWatcher() {
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
        mEditTitle1.addTextChangedListener(watcher);
        mEditPrice.addTextChangedListener(watcher);
    }

    @Override
    protected boolean isModalView() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (mIsNew)
                addExpense();
            else
                updateExpense();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            Toast.makeText(this, R.string.photo_uploading, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mNeedToSave) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseEditActivity.this);
            builder.setTitle(R.string.dialog_discart_changes)
                    .setPositiveButton(R.string.action_discard, (dialog, which) -> {
                        mNeedToSave = false;
                        onBackPressed();
                    })
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();
        } else {
            super.onBackPressed();
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
        Glide.with(this).load(imageFile).into(mImageExpense);
        mImageExpense.setVisibility(View.VISIBLE);
        String imageFileName = DateUtils.getCurrentTimeStamp() + "_original" + ".jpg";
        uploadFile(imageFile, imageFileName, 85).addOnSuccessListener(taskSnapshot -> {
            if (taskSnapshot.getDownloadUrl() != null)
                mExpense.setImage(taskSnapshot.getDownloadUrl().toString());
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
                .setCustomMetadata("Title1", mExpense.getTitle1())
                .setCustomMetadata("UserName", mExpense.getUserName())
                .build();
        // name of file in Storage
        return FirebaseStorage.getInstance()
                .getReference(DefaultConfigurations.STORAGE_EXPENSES)
                .child(fileName)
                .putBytes(bytes, metadata);
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

    private void updateModel() {
        if (mEditPrice.getText().toString().isEmpty())
            mEditPrice.setText("0");
        mExpense.setTitle1(mEditTitle1.getText().toString());
        mExpense.setPrice(Integer.parseInt(mEditPrice.getText().toString()));
    }

    private void addExpense() {
        updateModel();
        mIsNew = false;
        DatabaseReference ref = database.getReference(DefaultConfigurations.DB_EXPENSES)
                .child(DateUtils.getCurrentYear())
                .child(DateUtils.getCurrentMonth())
                .push();
        mExpense.setKey(ref.getKey());
        ref.setValue(mExpense).addOnCompleteListener(task -> {
            Snackbar.make(mImageExpense, getString(R.string.expense_added), Snackbar.LENGTH_SHORT).show();
            mNeedToSave = false;
            onBackPressed();
        });
    }

    private void updateExpense() {
        updateModel();
        database.getReference(DefaultConfigurations.DB_EXPENSES)
                .child(DateUtils.toString(mExpense.getExpenseDate(), "yyyy"))
                .child(DateUtils.toString(mExpense.getExpenseDate(), "M"))
                .child(mExpense.getKey())
                .setValue(mExpense)
                .addOnCompleteListener(task -> {
                    Toast.makeText(ExpenseEditActivity.this, getString(R.string.mk_updated), Toast.LENGTH_SHORT).show();
                    mNeedToSave = false;
                    onBackPressed();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @OnClick(R.id.button_upload)
    public void onUploadClicked() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            EasyImage.openChooserWithGallery(this, getString(R.string.choose_images), 0);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.image_permissions), REQUEST_IMAGE_PERMISSION, perms);
        }
    }

    @OnClick(R.id.text_date)
    public void onDateClicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mExpense.getExpenseDate());
        new DatePickerDialog(ExpenseEditActivity.this, (datePicker, year, month, day)
                -> {
            mExpense.setExpenseDate(DateUtils.getDate(year, month, day));
            mTextDate.setText(DateUtils.toString(mExpense.getExpenseDate()));
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
