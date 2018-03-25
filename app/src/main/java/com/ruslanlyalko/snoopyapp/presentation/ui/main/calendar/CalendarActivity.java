package com.ruslanlyalko.snoopyapp.presentation.ui.main.calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Constants;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Report;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.calendar.adapter.OnReportClickListener;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.calendar.adapter.ReportsAdapter;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.report.ReportActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalendarActivity extends AppCompatActivity implements OnReportClickListener {

    @BindView(R.id.recycler_view) RecyclerView mReportsList;
    @BindView(R.id.calendar_view) CompactCalendarView mCompactCalendarView;
    @BindView(R.id.swipere_fresh) SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.button_prev) ImageButton mButtonPrev;
    @BindView(R.id.button_next) ImageButton mButtonNext;
    @BindView(R.id.text_month) TextView mTextMonth;
    @BindView(R.id.button_add_report) TextView mTextAddReport;

    private ReportsAdapter mReportsAdapter;
    private List<Report> mReportList = new ArrayList<>();
    private ArrayList<String> mUsersList = new ArrayList<>();
    private Date mCurrentDate;
    private String mUserId;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    //private String mDay, mMonth, mYear;
    private SimpleDateFormat mSdf = new SimpleDateFormat("d-M-yyyy", Locale.US);
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public static Intent getLaunchIntent(final Activity launchIntent) {
        return new Intent(launchIntent, CalendarActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        mUserId = FirebaseAuth.getInstance().getUid();
        initRecycle();
        initCalendarView();
        showReportsOnCalendar();
        Date today = Calendar.getInstance().getTime();
        showReportsForDate(today);
    }

    private void initRecycle() {
        mReportList = new ArrayList<>();
        mReportsAdapter = new ReportsAdapter(this, mReportList);
        mReportsList.setLayoutManager(new LinearLayoutManager(this));
        mReportsList.setAdapter(mReportsAdapter);
    }

    private void initCalendarView() {
        mCompactCalendarView.setUseThreeLetterAbbreviation(true);
        mCompactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        mCompactCalendarView.shouldScrollMonth(false);
        mCompactCalendarView.displayOtherMonthDays(true);
        Calendar month = Calendar.getInstance();
        mTextMonth.setText(Constants.MONTH_FULL[month.get(Calendar.MONTH)]);
        // define a listener to receive callbacks when certain events happen.
        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                showReportsForDate(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Calendar month = Calendar.getInstance();
                month.setTime(firstDayOfNewMonth);
                String yearSimple = new SimpleDateFormat("yy", Locale.US).format(firstDayOfNewMonth);
                String str = Constants.MONTH_FULL[month.get(Calendar.MONTH)];
                if (!DateUtils.isCurrentYear(firstDayOfNewMonth))
                    str = str + "'" + yearSimple;
                mTextMonth.setText(str);
            }
        });
        mSwipeRefresh.setOnRefreshListener(() -> {
            showReportsOnCalendar();
            reloadReportsForDate();
        });
    }

    private void showReportsOnCalendar() {
        mSwipeRefresh.setRefreshing(true);
        mDatabase.getReference(DefaultConfigurations.DB_REPORTS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        mUsersList.clear();
                        mCompactCalendarView.removeAllEvents();
                        for (DataSnapshot datYears : dataSnapshot.getChildren()) {
                            for (DataSnapshot datYear : datYears.getChildren()) {
                                for (DataSnapshot datMonth : datYear.getChildren()) {
                                    for (DataSnapshot datDay : datMonth.getChildren()) {
                                        Report report = datDay.getValue(Report.class);
//                                        if (report != null && (FirebaseUtils.isAdmin() || report.getUserId().equals(mUserId) || DateUtils.future(report.getReportDate()))) {
//                                            int color = getUserColor(report.getUserId());
//                                            long date = getDateLongFromStr(report.getReportDate());
//                                            String uId = report.getUserId();
//                                            mCompactCalendarView.addEvent(
//                                                    new Event(color, date, uId), true);
//                                        }
                                    }
                                }
                            }
                        }
                        mSwipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    private void showReportsForDate(Date date) {
        mCurrentDate = date;
        mTextAddReport.setVisibility((FirebaseUtils.isAdmin() || DateUtils.isTodayOrFuture(date))
                ? View.VISIBLE : View.GONE);
        String mDay = DateFormat.format("d", date).toString();
        String mMonth = DateFormat.format("M", date).toString();
        String mYear = DateFormat.format("yyyy", date).toString();
        mDatabase.getReference(DefaultConfigurations.DB_REPORTS)
                .child(mYear)
                .child(mMonth)
                .child(mDay)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        mReportList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Report report = data.getValue(Report.class);
                            if (report != null) {
//                                if (FirebaseUtils.isAdmin() || report.getUserId().equals(mUserId)) {
//                                    mReportList.add(report);
//                                }
                            }
                        }
                        mReportsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    private void reloadReportsForDate() {
        showReportsForDate(mCurrentDate);
    }

    @OnClick(R.id.button_prev)
    void onPrevClicked() {
        mCompactCalendarView.showPreviousMonth();
    }

    @OnClick(R.id.button_next)
    void onNextClicked() {
        mCompactCalendarView.showNextMonth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showReportsOnCalendar();
        reloadReportsForDate();
    }

    private int getUserColor(String userId) {
        if (!mUsersList.contains(userId)) {
            mUsersList.add(userId);
        }
        int index = mUsersList.indexOf(userId);
        int[] colors = getResources().getIntArray(R.array.colors);
        if (index < 6)
            return colors[index];
        else
            return Color.GREEN;
    }

    private long getDateLongFromStr(String dateStr) {
        long dateLong = Calendar.getInstance().getTime().getTime();
        Date date;
        try {
            date = mSdf.parse(dateStr);
            dateLong = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateLong;
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
    public void onCommentClicked(final Report report) {
//        final boolean commentsExist = report.getComment() != null & !report.getComment().isEmpty();
//        if (commentsExist)
//            Toast.makeText(this, report.getComment(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMkClicked(final Report report) {
//        if (report.getMkRef() != null && !report.getMkRef().isEmpty()) {
//            Intent intent = new Intent(this, MkDetailsActivity.class);
//            intent.putExtra(Keys.Extras.EXTRA_ITEM_ID, report.getMkRef());
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, R.string.toast_mk_not_set, Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onEditClicked(final Report report) {
//        if (FirebaseUtils.isAdmin() || DateUtils.todayOrFuture(report.getReportDate()))
        {
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra(Keys.Extras.EXTRA_DATE, report.getReportDate());
            intent.putExtra(Keys.Extras.EXTRA_UID, report.getCreatedBy());
            intent.putExtra(Keys.Extras.EXTRA_USER_NAME, report.getCreatedById());
            startActivityForResult(intent, 0);
        }
//        else{
//            Toast.makeText(this, R.string.toast_edit_impossible, Toast.LENGTH_SHORT).show();
//        }
    }

    @OnClick(R.id.button_add_report)
    void onAddReportClicked() {
        startActivity(ReportActivity.getLaunchIntent(this, DateUtils.toString(mCurrentDate, "d-M-yyyy"),
                mUser.getDisplayName(), mUser.getUid()));
    }
}
