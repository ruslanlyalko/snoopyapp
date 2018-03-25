package com.ruslanlyalko.snoopyapp.presentation.ui.main.report;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.common.LocationHandler;
import com.ruslanlyalko.snoopyapp.common.ViewUtils;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Mk;
import com.ruslanlyalko.snoopyapp.data.models.Report;
import com.ruslanlyalko.snoopyapp.presentation.base.BaseActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.alarm.AlarmReceiverActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.calendar.CalendarActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.maps.MapsActivity;
import com.ruslanlyalko.snoopyapp.presentation.widget.PhotoPreviewActivity;
import com.ruslanlyalko.snoopyapp.presentation.widget.SwipeLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pub.devrel.easypermissions.EasyPermissions;

public class ReportActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_IMAGE_PERMISSION = 1;
    @BindView(R.id.text_date) TextView textDate;
    @BindView(R.id.panel_date) LinearLayout panelDate;
    @BindView(R.id.progress_bar_upload) ProgressBar progressBarUpload;
    @BindView(R.id.edit_photo) TextView textPhoto;
    @BindView(R.id.panel_photo) LinearLayout panelPhoto;
    @BindView(R.id.text_room_total) TextView textRoomTotal;
    @BindView(R.id.panel_room_expand) LinearLayout panelRoomExpand;
    @BindView(R.id.input_room_60) EditText inputRoom60;
    @BindView(R.id.input_room_30) EditText inputRoom30;
    @BindView(R.id.input_room_20) EditText inputRoom20;
    @BindView(R.id.input_room_10) EditText inputRoom10;
    @BindView(R.id.swipe_menu) LinearLayout swipeMenu;
    @BindView(R.id.text_room_60) TextView textRoom60;
    @BindView(R.id.seek_room_60) SeekBar seekRoom60;
    @BindView(R.id.text_room_30) TextView textRoom30;
    @BindView(R.id.seek_room_30) SeekBar seekRoom30;
    @BindView(R.id.text_room_20) TextView textRoom20;
    @BindView(R.id.seek_room_20) SeekBar seekRoom20;
    @BindView(R.id.text_room_10) TextView textRoom10;
    @BindView(R.id.seek_room_10) SeekBar seekRoom10;
    @BindView(R.id.swipe_layout) SwipeLayout swipeLayout;
    @BindView(R.id.text_bday_total) TextView textBdayTotal;
    @BindView(R.id.panel_room_expand2) LinearLayout panelRoomExpand2;
    @BindView(R.id.input_bday_50) EditText inputBday50;
    @BindView(R.id.input_bday_10) EditText inputBday10;
    @BindView(R.id.input_bday_30) EditText inputBday30;
    @BindView(R.id.text_bday_50) TextView textBday50;
    @BindView(R.id.seek_bday_50) SeekBar seekBday50;
    @BindView(R.id.text_bday_10) TextView textBday10;
    @BindView(R.id.seek_bday_10) SeekBar seekBday10;
    @BindView(R.id.text_bday_30) TextView textBday30;
    @BindView(R.id.seek_bday_30) SeekBar seekBday30;
    @BindView(R.id.text_bday_mk_done) TextView textBdayMk;
    @BindView(R.id.seek_bday_mk_done) SeekBar seekBdayMk;
    @BindView(R.id.swipe_layout2) SwipeLayout swipeLayout2;
    @BindView(R.id.text_mk_name) TextView textMkName;
    @BindView(R.id.button_choose_mk) Button buttonChooseMk;
    @BindView(R.id.text_mk_total) TextView textMkTotal;
    @BindView(R.id.switch_my_mk) Switch switchMyMk;
    @BindView(R.id.switch_half_salary) Switch switchHalfSalary;
    @BindView(R.id.panel_room_expand3) LinearLayout panelRoomExpand3;
    @BindView(R.id.input_mk_1) EditText inputMk1;
    @BindView(R.id.input_mk_2) EditText inputMk2;
    @BindView(R.id.text_mk_t1) TextView textMkT1;
    @BindView(R.id.seek_mk_t1) SeekBar seekMkT1;
    @BindView(R.id.text_mk_t2) TextView textMkT2;
    @BindView(R.id.seek_mk_t2) SeekBar seekMkT2;
    @BindView(R.id.text_mk_1) TextView textMk1;
    @BindView(R.id.seek_mk_1) SeekBar seekMk1;
    @BindView(R.id.text_mk_2) TextView textMk2;
    @BindView(R.id.seek_mk_2) SeekBar seekMk2;
    @BindView(R.id.swipe_layout3) SwipeLayout swipeLayout3;
    @BindView(R.id.edit_comment) EditText editComment;
    @BindView(R.id.text_check_list_time) TextView mTextCheckListTime;
    @BindView(R.id.panel_check_list) LinearLayout mPanelCheckList;
    @BindView(R.id.panel_check_list_expand) LinearLayout mPanelCheckListExpand;
    @BindView(R.id.button_check_list_done) Button mButtonCheckListDone;
    @BindView(R.id.checkbox_check_list_1) CheckBox mCheckboxCheckList1;
    @BindView(R.id.checkbox_check_list_2) CheckBox mCheckboxCheckList2;
    @BindView(R.id.checkbox_check_list_3) CheckBox mCheckboxCheckList3;
    @BindView(R.id.checkbox_check_list_4) CheckBox mCheckboxCheckList4;
    @BindView(R.id.checkbox_check_list_5) CheckBox mCheckboxCheckList5;
    @BindView(R.id.panel_report_values) LinearLayout mPanelReportValues;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private String mUId, mUserName;
    private Calendar mDate;
    private String mDateStr, mDateMonth, mDateYear, mDateDay;
    private Report mReport;
    private SimpleDateFormat mSdf = new SimpleDateFormat("d-M-yyyy", Locale.US);
    private boolean isChanged;
    private List<Mk> mkList = new ArrayList<>();
    private boolean mIsFuture;
    private LocationHandler mLocationHandler;
    private LatLng mLocation;

    public static Intent getLaunchIntent(final Activity launchIntent) {
        return new Intent(launchIntent, ReportActivity.class);
    }

    public static Intent getLaunchIntent(final Context context, String reportDate, String userName, String userId) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_DATE, reportDate);
        intent.putExtra(Keys.Extras.EXTRA_USER_NAME, userName);
        intent.putExtra(Keys.Extras.EXTRA_UID, userId);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_report;
    }

    @Override
    protected void parseExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            if (mUser != null)
                mUId = mUser.getUid();
            if (mUser != null)
                mUserName = mUser.getDisplayName();
            setDate(Calendar.getInstance());
        } else {
            mUId = bundle.getString(Keys.Extras.EXTRA_UID, mUser.getUid());
            mUserName = bundle.getString(Keys.Extras.EXTRA_USER_NAME, mUser.getDisplayName());
            String date = bundle.getString(Keys.Extras.EXTRA_DATE);
            if (date != null)
                setDate(date);
            else
                setDate(Calendar.getInstance());
        }
    }

    @Override
    protected void setupView() {
        initDatePicker();
        initSwipesAndExpandPanels();
        initSeeks();
        loadReportFromDB();
        loadMK();
        buttonChooseMk.setOnClickListener(v -> chooseMkDialog());
        panelPhoto.setOnClickListener(v -> {
            String uri = mReport.imageUri;
            if (uri != null && !uri.isEmpty()) {
//                if (uri.startsWith("http"))
//                    startActivity(EnlargedImageActivity.getLaunchIntent(this, uri));
//                else
                startActivity(PhotoPreviewActivity.getLaunchIntent(
                        ReportActivity.this, uri, mReport.getUserName(), DefaultConfigurations.STORAGE_REPORT));
            } else {
                startCamera();
            }
        });
        panelPhoto.setOnLongClickListener(v -> {
            startCamera();
            return false;
        });
        if (!FirebaseUtils.isAdmin()) {
            mLocationHandler = new LocationHandler(this);
            mLocationHandler.getLocation(new LocationHandler.OnLocationRequest() {
                @Override
                public void onFindLocation(final Location location) {
                    mLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }

                @Override
                public void onError(final Exception e) {
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        switch (id) {
            case R.id.action_add: {
                saveReportToDB();
                break;
            }
            case R.id.action_today: {
                setDate(Calendar.getInstance());
                break;
            }
            case R.id.action_clear_report: {
                clearReport(true);
                break;
            }
            case R.id.action_delete_report: {
                deleteReport();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (progressBarUpload.getVisibility() == View.VISIBLE) {
            Toast.makeText(this, R.string.photo_uploading, Toast.LENGTH_SHORT).show();
            return;
        }
        // Show Dialog if we have not saved data
        if (isChanged) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
            builder.setTitle(R.string.dialog_report_save_before_close_title)
                    .setMessage(R.string.dialog_report_save_before_close_text)
                    .setPositiveButton(R.string.action_save, (dialog, which) -> {
                        if (saveReportToDB())
                            onBackPressed();
                    })
                    .setNegativeButton(R.string.action_no, (dialog, which) -> {
                        isChanged = false;
                        onBackPressed();
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private void setDate(Calendar calendar) {
        mDate = calendar;
        mDateStr = mSdf.format(mDate.getTime());
        fillDateStr();
        mIsFuture = DateUtils.future(mDateStr);
    }

    private void setDate(String dateStr) {
        mDateStr = dateStr;
        mDate = getDateFromStr(mDateStr);
        fillDateStr();
        mIsFuture = DateUtils.future(mDateStr);
    }

    private void fillDateStr() {
        int first = mDateStr.indexOf('-');
        int last = mDateStr.lastIndexOf('-');
        mDateDay = mDateStr.substring(0, first);
        mDateMonth = mDateStr.substring(first + 1, last);
        mDateYear = mDateStr.substring(last + 1);
        textDate.setText(DateUtils.toString(mDate.getTime(), "d-M-yyyy EEEE").toUpperCase());
        if (mReport != null)
            mReport.date = mDateStr;
    }

    private Calendar getDateFromStr(String dateStr) {
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(mSdf.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
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

    void startCamera() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            EasyImage.openChooserWithGallery(this, getString(R.string.choose_images), 0);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.image_permissions), REQUEST_IMAGE_PERMISSION, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mLocationHandler != null)
            mLocationHandler.handleActivityResult(requestCode, resultCode, data);
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
        showProgressBarUpload();
        isChanged = true;
        String imageFileName = DateUtils.getCurrentTimeStamp() + "_original" + ".jpg";
        uploadFile(imageFile, imageFileName, 95).addOnSuccessListener(taskSnapshot -> {
            if (taskSnapshot.getDownloadUrl() != null)
                mReport.imageUri = taskSnapshot.getDownloadUrl().toString();
            hideProgressBarUpload();
        }).addOnFailureListener(exception -> hideProgressBarUpload());
    }

    void showProgressBarUpload() {
        progressBarUpload.setVisibility(View.VISIBLE);
        textPhoto.setText("Загрузка...");
    }

    private UploadTask uploadFile(File file, String fileName, int quality) {
        Bitmap bitmapOriginal = BitmapFactory.decodeFile(file.toString());//= imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        // Meta data for imageView
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("Date", mReport.getDate())
                .setCustomMetadata("UserName", mReport.getUserName())
                .build();
        // name of file in Storage
        return FirebaseStorage.getInstance()
                .getReference(DefaultConfigurations.STORAGE_REPORT)
                .child(fileName)
                .putBytes(bytes, metadata);
    }

    void hideProgressBarUpload() {
        progressBarUpload.setVisibility(View.GONE);
        textPhoto.setText("Фото загружено!");
    }

    @Override
    protected void onPause() {
        if (mLocationHandler != null)
            mLocationHandler.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationHandler != null && !FirebaseUtils.isAdmin() && mReport != null && !mReport.getCheckedListDone())
            mLocationHandler.onResume();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mLocationHandler != null)
            mLocationHandler.handleRequestPermissionResult(requestCode, permissions, grantResults);
    }

    private void loadMK() {
        mkList.clear();
        FirebaseDatabase.getInstance().getReference(DefaultConfigurations.DB_MK).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mk mk = dataSnapshot.getValue(Mk.class);
                if (mk != null) {
                    mkList.add(0, mk);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void chooseMkDialog() {
        final ArrayList<String> mkNames = new ArrayList<>();
        for (Mk mk : mkList) {
            mkNames.add(mk.getTitle1());
        }
        mkNames.add("[не обрано]");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_choose_mk)
                .setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, mkNames),
                        (dialog, which) -> {
                            if (which == mkList.size()) {
                                mReport.mkRef = "";
                                mReport.mkName = "";
                            } else {
                                mReport.mkRef = mkList.get(which).getKey();
                                mReport.mkName = mkList.get(which).getTitle1();
                            }
                            isChanged = true;
                            updateMkName();
                        });
        builder.create();
        builder.show();
    }

    private void initDatePicker() {
        panelDate.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(ReportActivity.this, (view, year, monthOfYear, dayOfMonth) ->
                    setDate(year, monthOfYear, dayOfMonth),
                    mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH),
                    mDate.get(Calendar.DAY_OF_MONTH));
            if (!FirebaseUtils.isAdmin()) {
                dpd.getDatePicker().setMinDate(new Date().getTime());
            }
            dpd.show();
        });
    }

    /**
     * Set OnSeekBarChanged and TextChanged Listeners for inputs
     */
    private void initSeeks() {
        seekRoom60.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.r60 = progress;
                updateRoomTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekRoom30.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.r30 = progress;
                updateRoomTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekRoom20.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.r20 = progress;
                updateRoomTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekRoom10.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.r10 = progress;
                updateRoomTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBday50.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.b50 = progress;
                updateBdayTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBday10.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.b10 = progress;
                updateBdayTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBday30.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.b30 = progress;
                if (mReport.b30 > 0)
                    mReport.bMk = 1;
                else
                    mReport.bMk = 0;
                if (mReport.b30 > 10)
                    mReport.bMk = 2;
                if (mReport.b30 > 20)
                    mReport.bMk = 3;
                seekBdayMk.setProgress(mReport.bMk);
                updateBdayTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBdayMk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.bMk = progress;
                String mkDone = getString(R.string.mk_done) + mReport.bMk;
                textBdayMk.setText(mkDone);
                updateBdayTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        // MK
        seekMkT1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.mkt1 = progress;
                updateMkTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if (hasFocus) {
                EditText ed = (EditText) v;
                ed.setSelection(ed.getText().length());
            }
        };
        seekMkT2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.mkt2 = progress;
                updateMkTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekMk1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.mk1 = progress;
                updateMkTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekMk2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReport.mk2 = progress;
                updateMkTotal();
                if (fromUser)
                    closeSoftKeyBoard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        inputRoom60.setOnFocusChangeListener(focusChangeListener);
        inputRoom60.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = 0;
                try {
                    value = Integer.parseInt(String.valueOf(s));
                } catch (Exception e) {
                    //eat it
                }
                mReport.r60 = value;
                updateSeekBars();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().isEmpty())
                    return;
                String sub = s.toString().substring(0, 1);
                if (s.length() == 2 && sub.equals("0"))
                    s.delete(0, 1);
            }
        });
        inputRoom30.setOnFocusChangeListener(focusChangeListener);
        inputRoom30.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = 0;
                try {
                    value = Integer.parseInt(String.valueOf(s));
                } catch (Exception e) {
                    //eat it
                }
                mReport.r30 = value;
                updateSeekBars();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().isEmpty())
                    return;
                String sub = s.toString().substring(0, 1);
                if (s.length() == 2 && sub.equals("0"))
                    s.delete(0, 1);
            }
        });
        inputRoom20.setOnFocusChangeListener(focusChangeListener);
        inputRoom20.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = 0;
                try {
                    value = Integer.parseInt(String.valueOf(s));
                } catch (Exception e) {
                    //eat it
                }
                mReport.r20 = value;
                updateSeekBars();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().isEmpty())
                    return;
                String sub = s.toString().substring(0, 1);
                if (s.length() == 2 && sub.equals("0"))
                    s.delete(0, 1);
            }
        });
        inputRoom10.setOnFocusChangeListener(focusChangeListener);
        inputRoom10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = 0;
                try {
                    value = Integer.parseInt(String.valueOf(s));
                } catch (Exception e) {
                    //eat it
                }
                mReport.r10 = value;
                updateSeekBars();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().isEmpty())
                    return;
                String sub = s.toString().substring(0, 1);
                if (s.length() == 2 && sub.equals("0"))
                    s.delete(0, 1);
            }
        });
        inputBday50.setOnFocusChangeListener(focusChangeListener);
        inputBday50.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = 0;
                try {
                    value = Integer.parseInt(String.valueOf(s));
                } catch (Exception e) {
                    //eat it
                }
                mReport.b50 = value;
                updateSeekBars();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().isEmpty())
                    return;
                String sub = s.toString().substring(0, 1);
                if (s.length() == 2 && sub.equals("0"))
                    s.delete(0, 1);
            }
        });
        inputBday10.setOnFocusChangeListener(focusChangeListener);
        inputBday10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = 0;
                try {
                    value = Integer.parseInt(String.valueOf(s));
                } catch (Exception e) {
                    //eat it
                }
                mReport.b10 = value;
                updateSeekBars();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().isEmpty())
                    return;
                String sub = s.toString().substring(0, 1);
                if (s.length() == 2 && sub.equals("0"))
                    s.delete(0, 1);
            }
        });
        inputBday30.setOnFocusChangeListener(focusChangeListener);
        inputBday30.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = 0;
                try {
                    value = Integer.parseInt(String.valueOf(s));
                } catch (Exception e) {
                    //eat it
                }
                mReport.b30 = value;
                updateSeekBars();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().isEmpty())
                    return;
                String sub = s.toString().substring(0, 1);
                if (s.length() == 2 && sub.equals("0"))
                    s.delete(0, 1);
            }
        });
        inputMk1.setOnFocusChangeListener(focusChangeListener);
        inputMk1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = 0;
                try {
                    value = Integer.parseInt(String.valueOf(s));
                } catch (Exception e) {
                    //eat it
                }
                mReport.mk1 = value;
                updateSeekBars();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().isEmpty())
                    return;
                String sub = s.toString().substring(0, 1);
                if (s.length() == 2 && sub.equals("0"))
                    s.delete(0, 1);
            }
        });
        inputMk2.setOnFocusChangeListener(focusChangeListener);
        inputMk2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = 0;
                try {
                    value = Integer.parseInt(String.valueOf(s));
                } catch (Exception e) {
                    //eat it
                }
                mReport.mk2 = value;
                updateSeekBars();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().isEmpty())
                    return;
                String sub = s.toString().substring(0, 1);
                if (s.length() == 2 && sub.equals("0"))
                    s.delete(0, 1);
            }
        });
        switchMyMk.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mReport.mkMy = isChecked;
            isChanged = true;
        });
        switchHalfSalary.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mReport.setHalfSalary(isChecked);
            isChanged = true;
        });
        editComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mReport.comment = editComment.getText().toString();
                isChanged = true;
            }
        });
    }

    /**
     * Clear focus on focused View and hide Soft Keyboard
     */
    private void closeSoftKeyBoard() {
        // Clear Focus
        inputRoom60.clearFocus();
        inputRoom30.clearFocus();
        inputRoom20.clearFocus();
        inputRoom10.clearFocus();
        inputBday50.clearFocus();
        inputBday30.clearFocus();
        inputMk1.clearFocus();
        inputMk2.clearFocus();
        // Check if no view has focus:
        View view = ReportActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Initialize Swipe panels and Expanded panels
     */
    private void initSwipesAndExpandPanels() {
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, R.id.swipe_menu);
        swipeLayout.setLeftSwipeEnabled(true);
        swipeLayout.setBottomSwipeEnabled(false);
        swipeLayout2.addDrag(SwipeLayout.DragEdge.Left, R.id.swipe_menu2);
        swipeLayout2.setLeftSwipeEnabled(true);
        swipeLayout2.setBottomSwipeEnabled(false);
        swipeLayout3.addDrag(SwipeLayout.DragEdge.Left, R.id.swipe_menu3);
        swipeLayout3.setLeftSwipeEnabled(true);
        swipeLayout3.setBottomSwipeEnabled(false);
        panelRoomExpand.setOnClickListener(v -> {
            if (mIsFuture) return;
            swipeLayout.setVisibility((swipeLayout.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            swipeLayout2.setVisibility(View.GONE);
            swipeLayout3.setVisibility(View.GONE);
        });
        panelRoomExpand2.setOnClickListener(v -> {
            if (mIsFuture) return;
            swipeLayout2.setVisibility((swipeLayout2.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            swipeLayout.setVisibility(View.GONE);
            swipeLayout3.setVisibility(View.GONE);
        });
        panelRoomExpand3.setOnClickListener(v -> {
            if (mIsFuture) return;
            swipeLayout3.setVisibility((swipeLayout3.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            swipeLayout2.setVisibility(View.GONE);
            swipeLayout.setVisibility(View.GONE);
        });
    }

    /**
     * If exist load report from DB for selected day, otherwise create new report
     */
    private void loadReportFromDB() {
        mDatabase.getReference(DefaultConfigurations.DB_REPORTS)
                .child(mDateYear).child(mDateMonth).child(mDateDay).child(mUId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (isDestroyed()) return;
                        mReport = dataSnapshot.getValue(Report.class);
                        if (mReport == null) {
                            mReport = new Report(mUId, mUserName, mDateStr);
                        }
                        updateSeekBars();
                        updateMkName();
                        updateComments();
                        updatePhoto();
                        updateChecklist();
                        isChanged = false;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void updatePhoto() {
        String uri = mReport.imageUri;
        if (uri != null && !uri.isEmpty()) {
            textPhoto.setText(R.string.photo_uploaded);
        } else
            textPhoto.setText(R.string.text_add_photo);
    }

    private void deleteReport() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportActivity.this);
        builder.setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton("Видалити", (dialog, which) -> {
                    // delete from DB
                    cancelAlarm();
                    deleteReportFromDB();
                    loadReportFromDB();
                })
                .setNegativeButton("Повернутись", null)
                .show();
    }

    private void deleteReportFromDB() {
        // Delete item from DB
        isChanged = false;
        mDatabase.getReference(DefaultConfigurations.DB_REPORTS)
                .child(mDateYear).child(mDateMonth).child(mDateDay)
                .child(mUId)
                .removeValue().addOnCompleteListener(task ->
                Snackbar.make(textRoom60, getString(R.string.toast_report_deleted), Snackbar.LENGTH_SHORT).show());
    }

    private void clearReport(boolean clearMK) {
        mReport.clearReport(clearMK);
        updateSeekBars();
        updateMkName();
        updatePhoto();
        updateComments();
        updateTitle();
        updateChecklist();
        if (clearMK)
            Snackbar.make(textRoom60, getString(R.string.toast_report_cleared), Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Save current report to DB
     */
    private boolean saveReportToDB() {
        if (mReport.getTotal() > 0 && (mReport.getImageUri() == null || mReport.getImageUri().isEmpty())) {
            Toast.makeText(this, getString(R.string.report_toast_no_image), Toast.LENGTH_SHORT).show();
            return false;
        }
        isChanged = false;
        mDatabase.getReference(DefaultConfigurations.DB_REPORTS)
                .child(mDateYear).child(mDateMonth).child(mDateDay)
                .child(mUId)
                .setValue(mReport).addOnCompleteListener(task -> Snackbar.make(textRoom60, getString(R.string.toast_report_saved), Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.action_go_to_calendar), v -> {
                    // show calendar activity
                    Intent intent = new Intent(ReportActivity.this, CalendarActivity.class);
                    startActivity(intent);
                })
                .show());
        setupAlarm();
        return true;
    }

    private void cancelAlarm() {
        if (mReport == null) return;
        if (!mReport.getUserId().equals(mUser.getUid())) return;
        if (!DateUtils.future(mReport.getDate())) return;
        //Create a new PendingIntent and add it to the AlarmManager
        Intent intent = new Intent(this, AlarmReceiverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                getAlarmRequestCode(mReport.getDate()), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        if (am != null) am.cancel(pendingIntent);
    }

    private int getAlarmRequestCode(final String date) {
        try {
            return Integer.parseInt(date.replace("-", ""));
        } catch (Exception e) {
            return 12345;
        }
    }

    private Calendar getAlarmTime(final Calendar time) {
//        time.setTime(new Date());
//        time.add(Calendar.MINUTE, 1);
        int hour = 10;
        if (time.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) hour = 11;
        if (time.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) hour = 12;
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, 5);
        return time;
    }

    private void setupAlarm() {
        if (mReport == null) return;
        if (!mReport.getUserId().equals(mUser.getUid())) return;
        if (!DateUtils.future(mReport.getDate())) return;
        Calendar alarmTime = getAlarmTime(mDate);
        //Create a new PendingIntent and add it to the AlarmManager
        Intent intent = new Intent(this, AlarmReceiverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                getAlarmRequestCode(mReport.getDate()), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        if (am != null)
            am.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        String toastText = "Нагадування встановлено: \n" +
                DateUtils.toString(alarmTime.getTime(), "EEEE d-M-yyyy HH:mm").toUpperCase();
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }

    /**
     * Update View with Mk name if exist
     */
    private void updateMkName() {
        //notification
        if (mReport.getMkName() != null && !mReport.getMkName().isEmpty())
            textMkName.setText(mReport.getMkName());
        else
            textMkName.setText(R.string.text_mk_full);
    }

    private void updateComments() {
        // comment
        if (mReport.getComment() != null && !mReport.getComment().isEmpty())
            editComment.setText(mReport.getComment());
        else
            editComment.setText("");
    }

    /**
     * Update all text views on Room Panel
     * Update Report with new values
     * Update Title of Activity
     */
    @SuppressLint("SetTextI18n")
    void updateRoomTotal() {
        textRoom60.setText("60 грн х " + mReport.r60 + " = " + (mReport.r60 * 60) + " ГРН");
        textRoom30.setText("30 грн х " + mReport.r30 + " = " + (mReport.r30 * 30) + " ГРН");
        textRoom20.setText("20 грн х " + mReport.r20 + " = " + (mReport.r20 * 20) + " ГРН");
        textRoom10.setText("10 грн х " + mReport.r10 + " = " + (mReport.r10 * 10) + " ГРН");
        if (!inputRoom60.hasFocus())
            inputRoom60.setText(String.valueOf(mReport.r60));
        if (!inputRoom30.hasFocus())
            inputRoom30.setText(String.valueOf(mReport.r30));
        if (!inputRoom20.hasFocus())
            inputRoom20.setText(String.valueOf(mReport.r20));
        if (!inputRoom10.hasFocus())
            inputRoom10.setText(String.valueOf(mReport.r10));
        mReport.totalRoom = mReport.r60 * 60 + mReport.r30 * 30 + mReport.r20 * 20 + mReport.r10 * 10;
        String total = DateUtils.getIntWithSpace(mReport.totalRoom) + " ГРН";
        textRoomTotal.setText(total);
        updateTitle();
    }

    /**
     * Update all text views on BirthDayPanel
     * Update Report with new values
     * Update Title of Activity
     */
    @SuppressLint("SetTextI18n")
    void updateBdayTotal() {
        textBday50.setText("Кімната: 50 грн х " + mReport.b50 + " = " + (mReport.b50 * 50) + " ГРН");
        textBday10.setText("Кімната: 10 грн х " + mReport.b10 + " = " + (mReport.b10 * 10) + " ГРН");
        textBday30.setText("МК: 30 грн х " + mReport.b30 + " = " + (mReport.b30 * 30) + " ГРН");
        String mkDone = getString(R.string.mk_done) + mReport.bMk;
        textBdayMk.setText(mkDone);
        if (!inputBday50.hasFocus())
            inputBday50.setText(String.valueOf(mReport.b50));
        if (!inputBday10.hasFocus())
            inputBday10.setText(String.valueOf(mReport.b10));
        if (!inputBday30.hasFocus())
            inputBday30.setText(String.valueOf(mReport.b30));
        mReport.totalBday = mReport.b50 * 50 + mReport.b10 * 10 + mReport.b30 * 30;
        String total = DateUtils.getIntWithSpace(mReport.totalBday) + " ГРН";
        textBdayTotal.setText(total);
        updateTitle();
    }

    /**
     * Update all text views on Mk Panel
     * Update Report with new values
     * Update Title of Activity
     */
    @SuppressLint("SetTextI18n")
    void updateMkTotal() {
        int tar1 = 30 + mReport.mkt1 * 10;
        int tar2 = 30 + mReport.mkt2 * 10;
        mReport.totalMk = tar1 * mReport.mk1 + tar2 * mReport.mk2;
        textMkT1.setText("Тариф 1: " + tar1 + " грн x");
        textMkT2.setText("Тариф 2: " + tar2 + " грн x");
        textMk1.setText("  " + mReport.mk1 + " = " + (tar1 * mReport.mk1) + " ГРН");
        textMk2.setText("  " + mReport.mk2 + " = " + (tar2 * mReport.mk2) + " ГРН");
        if (!inputMk1.hasFocus())
            inputMk1.setText(String.valueOf(mReport.mk1));
        if (!inputMk2.hasFocus())
            inputMk2.setText(String.valueOf(mReport.mk2));
        String total = DateUtils.getIntWithSpace(mReport.totalMk) + " ГРН";
        textMkTotal.setText(total);
        updateTitle();
    }

    /**
     * Update Title of Activity
     */
    void updateTitle() {
        mReport.total = (mReport.totalRoom + mReport.totalBday + mReport.totalMk);
        setTitle((mReport.getUserId().equals(mUser.getUid())
                ? getString(R.string.title_activity_report) : getAbbreviation(mReport.getUserName()))
                + " (" + DateUtils.getIntWithSpace(mReport.total) + " ГРН)");
        isChanged = true;
    }

    private String getAbbreviation(final String userName) {
        return userName.charAt(0) + "." + userName.charAt(userName.lastIndexOf(" ") + 1) + ".";
    }

    /**
     * Load values from Report to Seek Bars
     */
    void updateSeekBars() {
        seekRoom60.setProgress(mReport.r60);
        seekRoom30.setProgress(mReport.r30);
        seekRoom20.setProgress(mReport.r20);
        seekRoom10.setProgress(mReport.r10);
        seekBday50.setProgress(mReport.b50);
        seekBday10.setProgress(mReport.b10);
        seekBday30.setProgress(mReport.b30);
        seekBdayMk.setProgress(mReport.bMk);
        seekMk1.setProgress(mReport.mk1);
        seekMk2.setProgress(mReport.mk2);
        seekMkT1.setProgress(mReport.mkt1);
        seekMkT2.setProgress(mReport.mkt2);
        switchMyMk.setChecked(mReport.mkMy);
        switchHalfSalary.setChecked(mReport.getHalfSalary());
        updateTitle();
    }

    private void setDate(int year, int monthOfYear, int dayOfMonth) {
        mDate.set(Calendar.YEAR, year);
        mDate.set(Calendar.MONTH, monthOfYear);
        mDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mDateStr = mSdf.format(mDate.getTime());
        fillDateStr();
        mIsFuture = DateUtils.future(mDateStr);
        if (mIsFuture) {
            swipeLayout.setVisibility(View.GONE);
            swipeLayout2.setVisibility(View.GONE);
            swipeLayout3.setVisibility(View.GONE);
            clearReport(false);
        } else {
            loadReportFromDB();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_report, menu);
        return true;
    }

    @OnClick(R.id.button_check_list_done)
    public void onCheckListDoneClicked() {
        if (mCheckboxCheckList1.isChecked()
                && mCheckboxCheckList2.isChecked()
                && mCheckboxCheckList3.isChecked()
                && mCheckboxCheckList4.isChecked()
                && mCheckboxCheckList5.isChecked()) {
            if (mReport != null) {
                mReport.setCheckedListDone(true);
                mReport.setCheckedListTime(new Date());
                if (mLocation != null) {
                    mReport.setCheckedListLatitude(mLocation.latitude);
                    mReport.setCheckedListLongitude(mLocation.longitude);
                } else {
                    mReport.setCheckedListLatitude(-1);
                    mReport.setCheckedListLongitude(-1);
                }
                isChanged = true;
                updateChecklist();
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_check_list_not_all_checked), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateChecklist() {
        if (mReport == null) return;
        toggleCheckList(!mReport.getCheckedListDone() && !FirebaseUtils.isAdmin() && !DateUtils.future(mDate.getTime()));
        if (mReport.getCheckedListTime() != null)
            mTextCheckListTime.setText(DateUtils.toString(mReport.getCheckedListTime(), "HH:mm"));
        else
            mTextCheckListTime.setText("");
    }

    void toggleCheckList(boolean show) {
        if (show) {
            if (mPanelCheckListExpand.getVisibility() != View.VISIBLE)
                ViewUtils.expand(mPanelCheckListExpand);
            mPanelReportValues.setVisibility(View.GONE);
            mCheckboxCheckList1.setChecked(false);
            mCheckboxCheckList2.setChecked(false);
            mCheckboxCheckList3.setChecked(false);
            mCheckboxCheckList4.setChecked(false);
            mCheckboxCheckList5.setChecked(false);
            if (mLocationHandler != null) mLocationHandler.onResume();
        } else {
            mPanelCheckListExpand.setVisibility(View.GONE);
            if (mPanelReportValues.getVisibility() != View.VISIBLE)
                ViewUtils.expand(mPanelReportValues);
            if (mLocationHandler != null) mLocationHandler.onPause();
        }
    }

    @OnClick(R.id.panel_check_list)
    public void onPanelCheckListClicked() {
        if (mReport == null) return;
        if (FirebaseUtils.isAdmin()) {
            if (mReport.getCheckedListLongitude() != 0 && mReport.getCheckedListLongitude() != 0) {
                startActivity(MapsActivity.getLaunchIntent(this, new LatLng(mReport.getCheckedListLatitude(), mReport.getCheckedListLongitude())));
            }
        }
    }
}
