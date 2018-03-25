package com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.birthdays;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Birthday;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.birthdays.adapter.BirthdaysAdapter;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.birthdays.adapter.OnBirthdaysClickListener;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.birthdays.edit.BirthdaysEditActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts.details.ContactDetailsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ruslan Lyalko
 * on 29.01.2018.
 */
public class BirthdaysFragment extends Fragment implements OnBirthdaysClickListener {

    @BindView(R.id.text_filter_date1) TextView mTextFilterDate1;
    @BindView(R.id.text_filter_date2) TextView mTextFilterDate2;
    @BindView(R.id.list_birthdays) RecyclerView mListBirthdays;

    private BirthdaysAdapter mBirthdaysAdapter = new BirthdaysAdapter(this);
    private Calendar mDateFrom = Calendar.getInstance();
    private Calendar mDateTo = Calendar.getInstance();

    public BirthdaysFragment() {
    }

    public static BirthdaysFragment newInstance() {
        BirthdaysFragment fragment = new BirthdaysFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_birthdays, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        setupRecycler();
        setupFilter();
        loadBirthdays();
    }

    private void setupRecycler() {
        mListBirthdays.setLayoutManager(new LinearLayoutManager(getContext()));
        mListBirthdays.setAdapter(mBirthdaysAdapter);
    }

    private void setupFilter() {
        mDateTo.add(Calendar.DAY_OF_MONTH, 14);
        onFilterTextChanged();
    }

    private void loadBirthdays() {
        Query ref = FirebaseDatabase.getInstance()
                .getReference(DefaultConfigurations.DB_BIRTHDAYS).orderByChild("bdDate/time");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                List<Birthday> birthdays = new ArrayList<>();
                for (DataSnapshot birthdaySS : dataSnapshot.getChildren()) {
                    Birthday birthday = birthdaySS.getValue(Birthday.class);
                    if (birthday != null) {
                        birthdays.add(birthday);
                    }
                }
                mBirthdaysAdapter.setData(birthdays);
                onFilterTextChanged();
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
            }
        });
    }

    private void onFilterTextChanged() {
        mTextFilterDate1.setText(DateUtils.toString(mDateFrom.getTime(), "dd.MM.yyyy"));
        mTextFilterDate2.setText(DateUtils.toString(mDateTo.getTime(), "dd.MM.yyyy"));
        String filter = DateUtils.toString(mDateFrom.getTime(), "dd.MM.yyyy") + "/" +
                DateUtils.toString(mDateTo.getTime(), "dd.MM.yyyy");
        mBirthdaysAdapter.getFilter().filter(filter);
    }

    @Override
    public void onEditClicked(final int position) {
        startActivity(BirthdaysEditActivity.getLaunchIntent(getContext(), mBirthdaysAdapter.getItem(position)));
    }

    @Override
    public void onItemClicked(final int position) {
        startActivity(ContactDetailsActivity.getLaunchIntent(getContext(), mBirthdaysAdapter.getItem(position).getContactKey()));
    }

    @OnClick({R.id.text_filter_date1, R.id.text_filter_date2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_filter_date1:
                new DatePickerDialog(getContext(), (datePicker, i, i1, i2)
                        -> {
                    mDateFrom.set(i, i1, i2);
                    onFilterTextChanged();
                },
                        mDateFrom.get(Calendar.YEAR),
                        mDateFrom.get(Calendar.MONTH),
                        mDateFrom.get(Calendar.DAY_OF_MONTH)
                ).show();
                break;
            case R.id.text_filter_date2:
                new DatePickerDialog(getContext(), (datePicker, i, i1, i2)
                        -> {
                    mDateTo.set(i, i1, i2);
                    onFilterTextChanged();
                },
                        mDateTo.get(Calendar.YEAR),
                        mDateTo.get(Calendar.MONTH),
                        mDateTo.get(Calendar.DAY_OF_MONTH)
                ).show();
                break;
        }
    }
}
