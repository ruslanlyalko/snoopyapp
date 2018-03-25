package com.ruslanlyalko.snoopyapp.presentation.ui.main.mk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Constants;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Report;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.mk.adapters.MkPlanAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MkPlanActivity extends AppCompatActivity {

    @BindView(R.id.text_month) TextView textMonth;
    @BindView(R.id.button_next) ImageButton buttonNext;
    @BindView(R.id.button_prev) ImageButton buttonPrev;
    @BindView(R.id.calendar_view) CompactCalendarView compactCalendarView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private MkPlanAdapter mPlanAdapter;
    private List<Report> reportList = new ArrayList<>();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mk_plan);
        ButterKnife.bind(this);
        initRecycler();
        initCalendar();
        showReportsForDate(Calendar.getInstance().getTime());
    }

    private void initRecycler() {
        reportList = new ArrayList<>();
        mPlanAdapter = new MkPlanAdapter(this, reportList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mPlanAdapter);
    }

    private void initCalendar() {
        Calendar month = Calendar.getInstance();
        textMonth.setText(Constants.MONTH_FULL[month.get(Calendar.MONTH)]);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showNextMonth();
            }
        });
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showPreviousMonth();
            }
        });
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Calendar month = Calendar.getInstance();
                month.setTime(firstDayOfNewMonth);
                String yearSimple = new SimpleDateFormat("yy", Locale.US).format(firstDayOfNewMonth);
                String str = Constants.MONTH_FULL[month.get(Calendar.MONTH)];
                if (!DateUtils.isCurrentYear(firstDayOfNewMonth))
                    str = str + "'" + yearSimple;
                textMonth.setText(str);
                showReportsForDate(firstDayOfNewMonth);
            }
        });
    }

    private void showReportsForDate(Date date) {
        String mMonth = DateFormat.format("M", date).toString();
        String mYear = DateFormat.format("yyyy", date).toString();
        final String uId = mUser.getUid();
        reportList.clear();
        mPlanAdapter.notifyDataSetChanged();
        DatabaseReference listOfUsersReports = mDatabase.getReference(DefaultConfigurations.DB_REPORTS)
                .child(mYear).child(mMonth);
        listOfUsersReports.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Report report = data.getValue(Report.class);
                    // Add to list only current user reports
                    // But if user role - Admin then add all reports
                    if (report != null) {
                        if (FirebaseUtils.isAdmin() || report.getUserId().equals(uId)) {
                            // check if has MK name
                            if (report.getTotalMk() > 0 || (report.getMkRef() != null && !report.getMkRef().isEmpty())) {
                                reportList.add(report);
                                mPlanAdapter.notifyDataSetChanged();
                            }
                        }
                    }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
