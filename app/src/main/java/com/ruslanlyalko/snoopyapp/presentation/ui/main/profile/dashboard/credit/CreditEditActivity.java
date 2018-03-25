package com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.dashboard.credit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Credit;
import com.ruslanlyalko.snoopyapp.presentation.base.BaseActivity;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class CreditEditActivity extends BaseActivity {

    @BindView(R.id.edit_title1) EditText mEditTitle1;
    @BindView(R.id.edit_price) EditText mEditPrice;
    @BindView(R.id.text_date) TextView mTextDate;
    @BindView(R.id.check_box_max) CheckBox mCheckBoxMax;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Credit mCredit;//= new Credit();
    private boolean mNeedToSave = false;
    private boolean mIsNew = false;
    private int mMaxMoney;

    public static Intent getLaunchIntent(final Activity launchIntent, final Credit expense) {
        Intent intent = new Intent(launchIntent, CreditEditActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_ITEM_ID, expense);
        return intent;
    }

    public static Intent getLaunchIntent(final Activity launchIntent, String year, String month, int max) {
        Intent intent = new Intent(launchIntent, CreditEditActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_YEAR, year);
        intent.putExtra(Keys.Extras.EXTRA_MONTH, month);
        intent.putExtra(Keys.Extras.EXTRA_MAX, max);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_credit_edit;
    }

    @Override
    protected void parseExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mCredit = (Credit) bundle.getSerializable(Keys.Extras.EXTRA_ITEM_ID);
            mIsNew = mCredit == null;
            if (mIsNew) {
                mTextDate.setText(DateUtils.toString(new Date(), "d-M-yyyy"));
                mCheckBoxMax.setVisibility(View.VISIBLE);
                mMaxMoney = bundle.getInt(Keys.Extras.EXTRA_MAX);
                mCheckBoxMax.setText(getString(R.string.text_max_money, DateUtils.getIntWithSpace(mMaxMoney)));
                mCredit = new Credit(0, "", new Date(),
                        bundle.getString(Keys.Extras.EXTRA_MONTH, "1"),
                        bundle.getString(Keys.Extras.EXTRA_YEAR, "2018"));
            }
        }
    }

    @Override
    public void setupView() {
        setupChangeWatcher();
        if (mIsNew) {
            setTitle(R.string.title_activity_add);
        } else {
            setTitle(R.string.title_activity_edit);
            mTextDate.setText(DateUtils.toString(mCredit.getDate(), "d-M-yyyy"));
            mEditTitle1.setText(mCredit.getDescription());
            mEditPrice.setText(String.valueOf(mCredit.getMoney()));
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
            hideKeyboard();
            if (mIsNew)
                addCredit();
            else
                updateCredit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNeedToSave) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreditEditActivity.this);
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

    @OnCheckedChanged(R.id.check_box_max)
    void onCheckedChanged() {
        if (mCheckBoxMax.isChecked()) {
            mEditPrice.setText(String.valueOf(mMaxMoney));
            mEditPrice.setEnabled(false);
        } else {
            mEditPrice.setEnabled(true);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void updateModel() {
        if (mEditPrice.getText().toString().isEmpty())
            mEditPrice.setText("0");
        mCredit.setDescription(mEditTitle1.getText().toString());
        mCredit.setMoney(Integer.parseInt(mEditPrice.getText().toString()));
    }

    private void addCredit() {
        updateModel();
        mIsNew = false;
        DatabaseReference ref = database.getReference(DefaultConfigurations.DB_CREDITS)
                .child(mCredit.getYear())
                .child(mCredit.getMonth())
                .push();
        mCredit.setKey(ref.getKey());
        ref.setValue(mCredit).addOnCompleteListener(task -> {
            Snackbar.make(mEditPrice, getString(R.string.expense_added), Snackbar.LENGTH_SHORT).show();
            mNeedToSave = false;
            onBackPressed();
        });
    }

    private void updateCredit() {
        updateModel();
        database.getReference(DefaultConfigurations.DB_CREDITS)
                .child(mCredit.getYear())
                .child(mCredit.getMonth())
                .child(mCredit.getKey())
                .setValue(mCredit)
                .addOnCompleteListener(task -> {
                    Toast.makeText(CreditEditActivity.this, getString(R.string.mk_updated), Toast.LENGTH_SHORT).show();
                    mNeedToSave = false;
                    onBackPressed();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @OnClick(R.id.text_date)
    public void onDateClicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCredit.getDate());
        new DatePickerDialog(CreditEditActivity.this, (datePicker, year, month, day)
                -> {
            mCredit.setDate(DateUtils.getDate(year, month, day));
            mTextDate.setText(DateUtils.toString(mCredit.getDate(), "d-M-yyyy"));
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
