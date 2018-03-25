package com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.salary.edit;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SalaryEditActivity extends AppCompatActivity {

    private EditText editStavka;
    private EditText editPercent;
    private EditText editMkBirthday;
    private EditText editMkChild;
    private EditText editMkArt;
    private TextView textDate;
    private Switch switchSalary;

    private Calendar mDate = Calendar.getInstance();
    LinearLayout panelDate;
    private SimpleDateFormat mSdf = new SimpleDateFormat("d-M-yyyy", Locale.US);

    // VARIABLES
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String key;
    User user = new User();
    boolean needToSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.nothing);
        setContentView(R.layout.activity_salary_edit);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString(Keys.Extras.EXTRA_UID);
        }
        initRef();
        initDatePicker();
        loadUserItem();
        updateUI();
        switchSalary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateEditsEnabled(isChecked);
            }
        });
    }

    private void initRef() {
        textDate = findViewById(R.id.text_date);
        switchSalary = (Switch) findViewById(R.id.switch_salary);
        editStavka = findViewById(R.id.edit_stavka);
        editPercent = findViewById(R.id.edit_percent);
        editMkBirthday = findViewById(R.id.edit_mk_birthday);
        editMkChild = findViewById(R.id.edit_mk_children);
        editMkArt = findViewById(R.id.edit_mk_art);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                needToSave = true;
            }
        };
        editStavka.addTextChangedListener(watcher);
        editPercent.addTextChangedListener(watcher);
        editMkBirthday.addTextChangedListener(watcher);
        editMkArt.addTextChangedListener(watcher);
        editMkChild.addTextChangedListener(watcher);
        panelDate = findViewById(R.id.panel_date);
    }

    private void initDatePicker() {
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDate.set(Calendar.YEAR, year);
                mDate.set(Calendar.MONTH, monthOfYear);
                mDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String mDateStr = mSdf.format(mDate.getTime());
                textDate.setText(mDateStr);
            }
        };
        // Pop up the Date Picker after user clicked on editText
        panelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(SalaryEditActivity.this, dateSetListener,
                        mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH),
                        mDate.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
    }

    private void loadUserItem() {
        DatabaseReference userRef = database.getReference(DefaultConfigurations.DB_USERS).child(key);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateUI() {
        setTitle(R.string.title_activity_edit);
        if (user != null) {
            editStavka.setText(String.valueOf(user.getUserStavka()));
            editPercent.setText(String.valueOf(user.getUserPercent()));
            editMkBirthday.setText(String.valueOf(user.getMkBd()));
            editMkChild.setText(String.valueOf(user.getMkBdChild()));
            editMkArt.setText(String.valueOf(user.getMkArtChild()));
            textDate.setText(String.valueOf(user.getMkSpecCalcDate()));
            switchSalary.setChecked(user.getMkSpecCalc());
            updateEditsEnabled(user.getMkSpecCalc());
            try {
                mDate.setTime(mSdf.parse(user.getMkSpecCalcDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        needToSave = false;
    }

    private void updateEditsEnabled(boolean isChecked) {
        editStavka.setEnabled(isChecked);
        editPercent.setEnabled(isChecked);
        editMkBirthday.setEnabled(isChecked);
        editMkChild.setEnabled(isChecked);
        editMkArt.setEnabled(isChecked);
    }

    private void updateUserModel() {
        String stavka = editStavka.getText().toString().trim();
        String percent = editPercent.getText().toString().trim();
        String mk = editMkBirthday.getText().toString().trim();
        String mkChild = editMkChild.getText().toString().trim();
        String art = editMkArt.getText().toString().trim();
        user.setMkSpecCalc(switchSalary.isChecked());
        user.setMkSpecCalcDate(textDate.getText().toString());
        try {
            user.setUserStavka(Integer.parseInt(stavka));
        } catch (Exception e) {
        }
        try {
            user.setUserPercent(Integer.parseInt(percent));
        } catch (Exception e) {
        }
        try {
            user.setMkArtChild(Integer.parseInt(art));
        } catch (Exception e) {
        }
        try {
            user.setMkBd(Integer.parseInt(mk));
        } catch (Exception e) {
        }
        try {
            user.setMkBdChild(Integer.parseInt(mkChild));
        } catch (Exception e) {
        }
    }

    private void saveToDb() {
        updateUserModel();
        database.getReference(DefaultConfigurations.DB_USERS)
                .child(user.getUserId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SalaryEditActivity.this, getString(R.string.mk_updated), Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
        needToSave = false;
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
            saveToDb();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (needToSave) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SalaryEditActivity.this);
            builder.setTitle(R.string.dialog_report_save_before_close_title)
                    .setMessage(R.string.dialog_mk_edit_text)
                    .setPositiveButton(R.string.action_save_changes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            saveToDb();
                            onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.action_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            needToSave = false;
                            onBackPressed();
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.nothing, R.anim.fadeout);
        }
    }
}
