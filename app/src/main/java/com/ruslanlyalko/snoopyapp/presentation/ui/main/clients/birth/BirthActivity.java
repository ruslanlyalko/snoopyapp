package com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.birth;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Constants;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Contact;
import com.ruslanlyalko.snoopyapp.presentation.base.BaseActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.birth.adapter.BirthContactsAdapter;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts.adapter.OnContactClickListener;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts.details.ContactDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class BirthActivity extends BaseActivity implements OnContactClickListener {

    @BindView(R.id.calendar_view) CompactCalendarView mCalendarView;
    @BindView(R.id.text_month) TextView mTextMonth;
    @BindView(R.id.list_kids) RecyclerView mListKids;
    private BirthContactsAdapter mBirthContactsAdapter;
    private Date mLastDate = new Date();

    public static Intent getLaunchIntent(final Context launchIntent) {
        return new Intent(launchIntent, BirthActivity.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_birth;
    }

    @Override
    protected void setupView() {
        setupRecycler();
        loadContacts();
    }

    private void setupRecycler() {
        mBirthContactsAdapter = new BirthContactsAdapter(this, this);
        mListKids.setLayoutManager(new LinearLayoutManager(this));
        mListKids.setAdapter(mBirthContactsAdapter);
        onFilterTextChanged(new Date());
        mCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                onFilterTextChanged(firstDayOfNewMonth);
            }
        });
    }

    private void loadContacts() {
        Query ref = FirebaseDatabase.getInstance()
                .getReference(DefaultConfigurations.DB_CONTACTS)
                .orderByChild("name");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                List<Contact> contacts = new ArrayList<>();
                for (DataSnapshot clientSS : dataSnapshot.getChildren()) {
                    Contact contact = clientSS.getValue(Contact.class);
                    if (contact != null) {
                        contacts.add(contact);
                    }
                }
                mBirthContactsAdapter.setData(contacts);
                onFilterTextChanged(mLastDate);
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
            }
        });
    }

    private void onFilterTextChanged(Date date) {
        mLastDate = date;
        Calendar month = Calendar.getInstance();
        month.setTime(date);
        String str = Constants.MONTH_FULL[month.get(Calendar.MONTH)];
        String yearSimple = new SimpleDateFormat("yy", Locale.US).format(date);
        if (!DateUtils.isCurrentYear(date))
            str = str + "'" + yearSimple;
        mTextMonth.setText(str);
        mBirthContactsAdapter.getFilter().filter(DateUtils.toString(date, "MM"));
    }

    @OnClick({R.id.button_prev, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_prev:
                mCalendarView.showPreviousMonth();
                break;
            case R.id.button_next:
                mCalendarView.showNextMonth();
                break;
        }
    }

    @Override
    public void onItemClicked(final int position, final ActivityOptionsCompat options) {
        startActivity(ContactDetailsActivity.getLaunchIntent(this, mBirthContactsAdapter.getItem(position)), options.toBundle());
    }
}
