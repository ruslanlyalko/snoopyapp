package com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Contact;
import com.ruslanlyalko.snoopyapp.presentation.base.BaseActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

public class ContactEditActivity extends BaseActivity {

    @BindView(R.id.edit_name) EditText mEditName;
    @BindView(R.id.edit_phone1) EditText mEditPhone1;
    @BindView(R.id.edit_phone2) EditText mEditPhone2;
    @BindView(R.id.edit_child_name1) EditText mEditChildName1;
    @BindView(R.id.edit_child_date1) TextView mEditChildDate1;
    @BindView(R.id.edit_child_name2) EditText mEditChildName2;
    @BindView(R.id.edit_child_date2) TextView mEditChildDate2;
    @BindView(R.id.edit_child_name3) EditText mEditChildName3;
    @BindView(R.id.edit_child_date3) TextView mEditChildDate3;
    @BindView(R.id.edit_description) EditText mEditDescription;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private Contact mContact = new Contact();
    private String mClientName;
    private String mClientPhone;
    private boolean mNeedToSave = false;
    private boolean mIsNew = false;

    public static Intent getLaunchIntent(final Context launchIntent, final Contact contact) {
        Intent intent = new Intent(launchIntent, ContactEditActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_ITEM_ID, contact);
        return intent;
    }

    public static Intent getLaunchIntent(final Context launchIntent, String name, String phone) {
        Intent intent = new Intent(launchIntent, ContactEditActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_CLIENT_NAME, name);
        intent.putExtra(Keys.Extras.EXTRA_CLIENT_PHONE, phone);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_contact_edit;
    }

    @Override
    protected void parseExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mContact = (Contact) bundle.getSerializable(Keys.Extras.EXTRA_ITEM_ID);
            mClientPhone = bundle.getString(Keys.Extras.EXTRA_CLIENT_PHONE);
            mClientName = bundle.getString(Keys.Extras.EXTRA_CLIENT_NAME);
        }
        mIsNew = mContact == null;
        if (mIsNew) {
            mContact = new Contact();
            mEditPhone1.setText(mClientPhone);
            mEditName.setText(mClientName);
        }
    }

    @Override
    protected void setupView() {
        setupChangeWatcher();
        if (mIsNew) {
            setTitle(R.string.title_activity_add);
            mEditChildDate1.setText(DateUtils.toString(mContact.getChildBd1(), "dd.MM.yyyy"));
            mEditChildDate2.setText(DateUtils.toString(mContact.getChildBd2(), "dd.MM.yyyy"));
            mEditChildDate3.setText(DateUtils.toString(mContact.getChildBd3(), "dd.MM.yyyy"));
        } else {
            setTitle(R.string.title_activity_edit);
            mEditName.setText(mContact.getName());
            mEditChildName1.setText(mContact.getChildName1());
            mEditChildName2.setText(mContact.getChildName2());
            mEditChildName3.setText(mContact.getChildName3());
            mEditChildDate1.setText(DateUtils.toString(mContact.getChildBd1(), "dd.MM.yyyy"));
            mEditChildDate2.setText(DateUtils.toString(mContact.getChildBd2(), "dd.MM.yyyy"));
            mEditChildDate3.setText(DateUtils.toString(mContact.getChildBd3(), "dd.MM.yyyy"));
            mEditPhone1.setText(mContact.getPhone());
            mEditPhone2.setText(mContact.getPhone2());
            mEditDescription.setText(mContact.getDescription());
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
        mEditName.addTextChangedListener(watcher);
        mEditChildName1.addTextChangedListener(watcher);
        mEditChildName2.addTextChangedListener(watcher);
        mEditChildName3.addTextChangedListener(watcher);
        mEditChildDate1.addTextChangedListener(watcher);
        mEditChildDate2.addTextChangedListener(watcher);
        mEditChildDate3.addTextChangedListener(watcher);
        mEditPhone1.addTextChangedListener(watcher);
        mEditPhone2.addTextChangedListener(watcher);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (mIsNew)
                addClient();
            else
                updateClient();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNeedToSave) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ContactEditActivity.this);
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

    private void updateModel() {
        mContact.setName(mEditName.getText().toString().trim());
        mContact.setChildName1(mEditChildName1.getText().toString().trim());
        mContact.setChildName2(mEditChildName2.getText().toString().trim());
        mContact.setChildName3(mEditChildName3.getText().toString().trim());
        mContact.setPhone(mEditPhone1.getText().toString().trim());
        mContact.setPhone2(mEditPhone2.getText().toString().trim());
        mContact.setDescription(mEditDescription.getText().toString().trim());
    }

    private void addClient() {
        updateModel();
        mIsNew = false;
        DatabaseReference ref = database.getReference(DefaultConfigurations.DB_CONTACTS)
                .push();
        mContact.setKey(ref.getKey());
        ref.setValue(mContact).addOnCompleteListener(task -> {
            Snackbar.make(mEditName, getString(R.string.client_added), Snackbar.LENGTH_SHORT).show();
            mNeedToSave = false;
            onBackPressed();
        });
    }

    private void updateClient() {
        updateModel();
        database.getReference(DefaultConfigurations.DB_CONTACTS)
                .child(mContact.getKey())
                .setValue(mContact)
                .addOnCompleteListener(task -> {
                    Toast.makeText(ContactEditActivity.this, getString(R.string.mk_updated), Toast.LENGTH_SHORT).show();
                    mNeedToSave = false;
                    onBackPressed();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @OnClick(R.id.edit_child_date1)
    public void onDate1Clicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mContact.getChildBd1());
        DatePickerDialog dialog = DatePickerDialog.newInstance((datePicker, year, month, day)
                        -> {
                    mContact.setChildBd1(DateUtils.getDate(year, month, day));
                    mEditChildDate1.setText(DateUtils.toString(mContact.getChildBd1(), "dd.MM.yyyy"));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.showYearPickerFirst(true);
        dialog.show(getFragmentManager(), "child1");
    }

    @OnClick(R.id.edit_child_date2)
    public void onDate2Clicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mContact.getChildBd2());
        DatePickerDialog dialog = DatePickerDialog.newInstance((datePicker, year, month, day)
                        -> {
                    mContact.setChildBd2(DateUtils.getDate(year, month, day));
                    mEditChildDate2.setText(DateUtils.toString(mContact.getChildBd2(), "dd.MM.yyyy"));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.showYearPickerFirst(true);
        dialog.show(getFragmentManager(), "child2");
    }

    @OnClick(R.id.edit_child_date3)
    public void onDate3Clicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mContact.getChildBd3());
        DatePickerDialog dialog = DatePickerDialog.newInstance((datePicker, year, month, day)
                        -> {
                    mContact.setChildBd3(DateUtils.getDate(year, month, day));
                    mEditChildDate3.setText(DateUtils.toString(mContact.getChildBd3(), "dd.MM.yyyy"));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.showYearPickerFirst(true);
        dialog.show(getFragmentManager(), "child3");
    }
}
