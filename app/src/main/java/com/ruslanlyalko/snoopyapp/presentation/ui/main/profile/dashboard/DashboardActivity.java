package com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Constants;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.common.ViewUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Credit;
import com.ruslanlyalko.snoopyapp.data.models.Expense;
import com.ruslanlyalko.snoopyapp.data.models.Report;
import com.ruslanlyalko.snoopyapp.data.models.Result;
import com.ruslanlyalko.snoopyapp.data.models.User;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.expenses.ExpensesActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.dashboard.adapter.CreditsAdapter;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.dashboard.adapter.OnCreditClickListener;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.dashboard.adapter.UsersSalaryAdapter;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.dashboard.credit.CreditEditActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.salary.SalaryActivity;
import com.ruslanlyalko.snoopyapp.presentation.widget.OnItemClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardActivity extends AppCompatActivity implements OnItemClickListener, OnCreditClickListener {

    @BindView(R.id.calendar_view) CompactCalendarView mCompactCalendarView;
    @BindView(R.id.text_total) TextView textTotal;
    @BindView(R.id.text_room_total) TextView textRoom;
    @BindView(R.id.text_bday_total) TextView textBday;
    @BindView(R.id.text_mk_total) TextView textMk;
    @BindView(R.id.text_month) TextView textMonth;
    @BindView(R.id.text_cost_total) TextView textCostTotal;
    @BindView(R.id.text_cost_common) TextView textCostCommon;
    @BindView(R.id.text_cost_mk) TextView textCostMk;
    @BindView(R.id.text_salary_total) TextView textSalaryTotal;
    @BindView(R.id.text_stavka_total) TextView textSalaryStavka;
    @BindView(R.id.text_percent_total) TextView textSalaryPercent;
    @BindView(R.id.text_salary_mk_total) TextView textSalaryMk;
    @BindView(R.id.text_birthdays) TextView textBirthdays;
    @BindView(R.id.edit_comment) EditText editComment;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.progress_bar_cost) ProgressBar progressBarCost;
    @BindView(R.id.progress_bar_salary) ProgressBar progressBarSalary;
    @BindView(R.id.list_users_salary) RecyclerView mListUsersSalary;
    @BindView(R.id.list_credits) RecyclerView mListCredits;
    @BindView(R.id.layout_collapsing) NestedScrollView mLayoutCollapsing;
    @BindView(R.id.nested_income) NestedScrollView mNestedIncome;
    @BindView(R.id.text_credit_total) TextView mTextCreditTotal;
    @BindView(R.id.image_expand) ImageView mImageView;
    @BindView(R.id.image_income_expand) ImageView mImageAction;
    @BindView(R.id.bar_chart) BarChart mBarChart;
    @BindView(R.id.bar_chart_income) BarChart mBarChartIncome;
    @BindView(R.id.card_expenses) CardView mCardExpenses;
    private UsersSalaryAdapter mUsersSalaryAdapter = new UsersSalaryAdapter(this);
    private CreditsAdapter mCreditsAdapter = new CreditsAdapter(this);
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private List<Report> reportList = new ArrayList<>();
    private List<Expense> mExpenseList = new ArrayList<>();
    private List<Credit> mCreditList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<Result> mResults;
    private String yearStr;
    private String monthStr;
    private int incomeTotal;
    private int costTotal;
    private int salaryTotal;
    private String mComment;
    private int mCreditTotal;
    private int mMaxMoney;

    public static Intent getLaunchIntent(final AppCompatActivity launchActivity) {
        return new Intent(launchActivity, DashboardActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        initCalendar();
        initBarChart();
        initBarChartIncome();
        initRecycler();
        textMonth.setText(Constants.MONTH_FULL[Calendar.getInstance().get(Calendar.MONTH)]);
        yearStr = new SimpleDateFormat("yyyy", Locale.US).format(new Date());
        monthStr = new SimpleDateFormat("M", Locale.US).format(new Date());
        loadReports(yearStr, monthStr);
        loadCosts(yearStr, monthStr);
        loadUsers();
        loadComment(yearStr, monthStr);
        loadCredits(yearStr, monthStr);
        loadResults();
    }

    private void initRecycler() {
        mListUsersSalary.setLayoutManager(new LinearLayoutManager(this));
        mListUsersSalary.setAdapter(mUsersSalaryAdapter);
        mListCredits.setLayoutManager(new LinearLayoutManager(this));
        mListCredits.setAdapter(mCreditsAdapter);
    }

    private void loadResults() {
        mDatabase.getReference(DefaultConfigurations.DB_RESULTS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        List<Result> results = new ArrayList<>();
                        for (DataSnapshot year : dataSnapshot.getChildren()) {
                            for (DataSnapshot month : year.getChildren()) {
                                Result result = month.getValue(Result.class);
                                if (result != null) {
                                    results.add(result);
                                }
                            }
                            updateBarChart(results);
                            updateBarChartIncome(results);
                        }
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    private void initBarChart() {
        mBarChart.setDrawGridBackground(false);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.getXAxis().setEnabled(true);
        mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);
        mBarChart.getAxisLeft().enableGridDashedLine(1, 1, 10);
        mBarChart.setMaxVisibleValueCount(150);
        mBarChart.setDrawValueAboveBar(true);
        mBarChart.setDoubleTapToZoomEnabled(false);
        mBarChart.setPinchZoom(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.setTouchEnabled(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.getAxisLeft().setAxisMinimum(0);
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mResults.get((int) value).getMonth();
            }
        });
    }

    private void initBarChartIncome() {
        mBarChartIncome.setDrawGridBackground(false);
        mBarChartIncome.getLegend().setEnabled(false);
        mBarChartIncome.getDescription().setEnabled(false);
        mBarChartIncome.getXAxis().setEnabled(true);
        mBarChartIncome.getAxisRight().setEnabled(false);
        mBarChartIncome.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);
        mBarChartIncome.getAxisLeft().enableGridDashedLine(1, 1, 10);
        mBarChartIncome.setMaxVisibleValueCount(150);
        mBarChartIncome.setDrawValueAboveBar(true);
        mBarChartIncome.setDoubleTapToZoomEnabled(false);
        mBarChartIncome.setPinchZoom(false);
        mBarChartIncome.setScaleEnabled(false);
        mBarChartIncome.setTouchEnabled(false);
        mBarChartIncome.setDrawBarShadow(false);
        mBarChartIncome.getAxisLeft().setAxisMinimum(0);
        XAxis xAxis = mBarChartIncome.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mResults.get((int) value).getMonth();
            }
        });
    }

    private void initCalendar() {
        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
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
                yearStr = new SimpleDateFormat("yyyy", Locale.US).format(firstDayOfNewMonth);
                monthStr = new SimpleDateFormat("M", Locale.US).format(firstDayOfNewMonth);
                loadReports(yearStr, monthStr);
                loadCosts(yearStr, monthStr);
                loadUsers();
                loadComment(yearStr, monthStr);
                loadCredits(yearStr, monthStr);
            }
        });
    }

    @OnClick(R.id.layout_income_action)
    void onIncomeActionClicked() {
        if (mNestedIncome.getVisibility() == View.VISIBLE) {
            mImageAction.setImageResource(R.drawable.ic_action_expand_more);
            ViewUtils.collapse(mNestedIncome);
        } else {
            ViewUtils.expand(mNestedIncome);
            mImageAction.setImageResource(R.drawable.ic_action_expand_less);
        }
    }

    @OnClick(R.id.text_credit_total)
    void onAddCreditClicked() {
        startActivityForResult(CreditEditActivity.getLaunchIntent(this, yearStr, monthStr, mMaxMoney), 0);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadCredits(yearStr, monthStr);
    }

    private void loadCredits(String yearStr, String monthStr) {
        mDatabase.getReference(DefaultConfigurations.DB_CREDITS)
                .child(yearStr)
                .child(monthStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        mCreditList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Credit credit = data.getValue(Credit.class);
                            if (credit != null) {
                                mCreditList.add(credit);
                            }
                        }
                        mCreditsAdapter.setData(mCreditList);
                        calcCreditTotal();
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    private void calcCreditTotal() {
        mCreditTotal = 0;
        for (int i = 0; i < mCreditList.size(); i++) {
            Credit credit = mCreditList.get(i);
            mCreditTotal += credit.getMoney();
        }
        mMaxMoney = (int) (incomeTotal * 0.8 - mCreditTotal);
        mTextCreditTotal.setText(String.format(getString(R.string.text_credit),
                DateUtils.getIntWithSpace(mCreditTotal), DateUtils.getIntWithSpace(mMaxMoney)));
    }

    @Override
    public void onBackPressed() {
        // save comments before exit
        saveCommentToDB(editComment.getText().toString());
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        calcIncome();
        calcCostTotal();
    }

    private void calcIncome() {
        int room = 0;
        int bday = 0;
        int mk = 0;
        incomeTotal = room + bday + mk;
        textRoom.setText(String.format(getString(R.string.hrn), DateUtils.getIntWithSpace(room)));
        textBday.setText(String.format(getString(R.string.hrn), DateUtils.getIntWithSpace(bday)));
        textMk.setText(String.format(getString(R.string.hrn), DateUtils.getIntWithSpace(mk)));
        String income100Str = DateUtils.getIntWithSpace(incomeTotal);
        String income80Str = DateUtils.getIntWithSpace(incomeTotal * 80 / 100);
        textTotal.setText(String.format(getString(R.string.income), income100Str, income80Str));
        progressBar.setMax(incomeTotal);
        progressBar.setProgress(room);
        progressBar.setSecondaryProgress(room + bday);
        updateNetIncome();
    }

    private void calcCostTotal() {
        int common = 0;
        int mk = 0;
        for (Expense expense : mExpenseList) {
            if (expense.getType().equalsIgnoreCase(getString(R.string.text_cost_common)))
                common += expense.getPrice();
            if (expense.getType().equalsIgnoreCase(getString(R.string.text_cost_mk)))
                mk += expense.getPrice();
        }
        costTotal = common + mk;
        progressBarCost.setMax(costTotal);
        progressBarCost.setProgress(common);
        progressBarCost.setSecondaryProgress(common + mk);
        textCostCommon.setText(String.format(getString(R.string.hrn), DateUtils.getIntWithSpace(common)));
        textCostMk.setText(String.format(getString(R.string.hrn), DateUtils.getIntWithSpace(mk)));
        textCostTotal.setText(String.format(getString(R.string.HRN), DateUtils.getIntWithSpace(costTotal)));
        updateNetIncome();
    }

    private void updateNetIncome() {
        int income80 = (int) (incomeTotal * 0.8);
        int netIncome = income80 - costTotal - salaryTotal;
        setTitle(String.format(getString(R.string.title_activity_dashboard), DateUtils.getIntWithSpace(netIncome)));
        Result result = new Result(incomeTotal, income80, salaryTotal, costTotal, netIncome, yearStr, monthStr);
        if (incomeTotal != 0 && salaryTotal != 0)
            FirebaseDatabase.getInstance()
                    .getReference(DefaultConfigurations.DB_RESULTS)
                    .child(yearStr)
                    .child(monthStr)
                    .setValue(result);
    }

    @OnClick(R.id.panel_action)
    void onExpandClicked() {
        if (mLayoutCollapsing.getVisibility() == View.VISIBLE) {
            mImageView.setImageResource(R.drawable.ic_action_expand_more);
            ViewUtils.collapse(mLayoutCollapsing);
        } else {
            ViewUtils.expand(mLayoutCollapsing);
            mImageView.setImageResource(R.drawable.ic_action_expand_less);
        }
    }

    @OnClick(R.id.button_prev)
    void onPrevClicked() {
        saveCommentToDB(editComment.getText().toString());
        mCompactCalendarView.showPreviousMonth();
    }

    private void saveCommentToDB(String s) {
        if (!s.equals(mComment))
            mDatabase.getReference(DefaultConfigurations.DB_COMMENTS)
                    .child(yearStr)
                    .child(monthStr).setValue(s);
    }

    @OnClick(R.id.button_next)
    void onNextClicked() {
        saveCommentToDB(editComment.getText().toString());
        mCompactCalendarView.showNextMonth();
    }

    private void loadReports(String yearStr, String monthStr) {
        mDatabase.getReference(DefaultConfigurations.DB_REPORTS)
                .child(yearStr)
                .child(monthStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        reportList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            for (DataSnapshot ds : data.getChildren()) {
                                Report report = ds.getValue(Report.class);
                                if (report != null) {
                                    reportList.add(report);
                                }
                            }
                        }
                        calcIncome();
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    private void loadCosts(String yearStr, String monthStr) {
        mDatabase.getReference(DefaultConfigurations.DB_EXPENSES)
                .child(yearStr)
                .child(monthStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        mExpenseList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Expense expense = data.getValue(Expense.class);
                            if (expense != null) {
                                mExpenseList.add(0, expense);
                            }
                        }
                        calcCostTotal();
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    private void loadUsers() {
        mDatabase.getReference(DefaultConfigurations.DB_USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        userList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            User user = data.getValue(User.class);
                            if (user != null) {
                                userList.add(0, user);
                            }
                        }
                        calcSalaryForUsers();
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    private void loadComment(String yearStr, String monthStr) {
        //editComment.setText("");
        mDatabase.getReference(DefaultConfigurations.DB_COMMENTS)
                .child(yearStr)
                .child(monthStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String s = dataSnapshot.getValue().toString();
                    mComment = s;
                    editComment.setText(s);
                } else {
                    mComment = "";
                    editComment.setText("");
                }
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

    private void calcSalaryForUsers() {
    }

    private void updateBarChart(List<Result> resultList) {
        mResults = resultList;
        ArrayList<BarEntry> values = new ArrayList<>();
        int[] colors = new int[resultList.size()];
        String yearStr = DateFormat.format("yyyy", Calendar.getInstance()).toString();
        for (int i = 0; i < resultList.size(); i++) {
            Result current = resultList.get(i);
            float val = current.getProfit();
            colors[i] = current.getYear().equals(yearStr)
                    ? ContextCompat.getColor(this, R.color.colorAccent)
                    : ContextCompat.getColor(this, R.color.colorPrimary);
            values.add(new BarEntry(i, val, val));
        }
        BarDataSet set1;
        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
        } else {
            set1 = new BarDataSet(values, "");
            set1.setColors(colors);
            set1.setDrawIcons(true);
            set1.setDrawValues(true);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setBarWidth(0.9f);
            data.setDrawValues(true);
            // data.setValueFormatter(new LabelValueFormatter());
            data.setValueTextColor(Color.BLACK);
            set1.setValueTextSize(10f);
            mBarChart.setData(data);
        }
        mBarChart.getData().notifyDataChanged();
        mBarChart.notifyDataSetChanged();
        mBarChart.invalidate();
    }

    private void updateBarChartIncome(List<Result> resultList) {
        mResults = resultList;
        int[] colors = new int[resultList.size()];
        int[] colors80 = new int[resultList.size()];
        int[] colorsProfit = new int[resultList.size()];
        ArrayList<BarEntry> values = new ArrayList<>();
        ArrayList<BarEntry> values80 = new ArrayList<>();
        ArrayList<BarEntry> valuesProfit = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            Result current = resultList.get(i);
            float val = current.getIncome();
            float val80 = current.getIncome80();
            float valProfit = current.getProfit();
            colors[i] = ContextCompat.getColor(this, R.color.colorProfit);
            colors80[i] = ContextCompat.getColor(this, R.color.colorAccent);
            colorsProfit[i] = ContextCompat.getColor(this, R.color.colorPrimary);
            values.add(new BarEntry(i, val, val));
            values80.add(new BarEntry(i, val80, val80));
            valuesProfit.add(new BarEntry(i, valProfit, valProfit));
        }
        BarDataSet set1;
        BarDataSet set180;
        BarDataSet set1Profit;
        if (mBarChartIncome.getData() != null &&
                mBarChartIncome.getData().getDataSetCount() == 1) {
            set1 = (BarDataSet) mBarChartIncome.getData().getDataSetByIndex(0);
            set1.setValues(values);
        } else if (mBarChartIncome.getData() != null &&
                mBarChartIncome.getData().getDataSetCount() == 2) {
            set1 = (BarDataSet) mBarChartIncome.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set180 = (BarDataSet) mBarChartIncome.getData().getDataSetByIndex(1);
            set180.setValues(values80);
        } else if (mBarChartIncome.getData() != null &&
                mBarChartIncome.getData().getDataSetCount() == 3) {
            set1 = (BarDataSet) mBarChartIncome.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set180 = (BarDataSet) mBarChartIncome.getData().getDataSetByIndex(1);
            set180.setValues(values80);
            set1Profit = (BarDataSet) mBarChartIncome.getData().getDataSetByIndex(2);
            set1Profit.setValues(valuesProfit);
        } else {
            set1 = new BarDataSet(values, "");
            set1.setColors(colors);
            set1.setDrawIcons(true);
            set1.setDrawValues(true);
            set180 = new BarDataSet(values, "");
            set180.setColors(colors80);
            set180.setDrawIcons(true);
            set180.setDrawValues(true);
            set1Profit = new BarDataSet(values, "");
            set1Profit.setColors(colorsProfit);
            set1Profit.setDrawIcons(true);
            set1Profit.setDrawValues(true);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set180);
            dataSets.add(set1Profit);
            BarData data = new BarData(dataSets);
            data.setBarWidth(0.90f);
            data.setDrawValues(true);
            // data.setValueFormatter(new LabelValueFormatter());
            data.setValueTextColor(Color.BLACK);
            set1.setValueTextSize(10f);
            set180.setValueTextSize(9f);
            set1Profit.setValueTextSize(9f);
            mBarChartIncome.setData(data);
        }
        mBarChartIncome.getData().notifyDataChanged();
        mBarChartIncome.notifyDataSetChanged();
        mBarChartIncome.invalidate();
    }

    @Override
    public void onItemClicked(final int position) {
        User user = mUsersSalaryAdapter.getItemAtPosition(position);
        startActivity(SalaryActivity.getLaunchIntent(this,
                user.getId(),
                user));
    }

    @OnClick(R.id.card_expenses)
    public void onExpensesClicked() {
        startActivity(ExpensesActivity.getLaunchIntent(this));
    }

    @Override
    public void onRemoveClicked(final Credit credit) {
        mDatabase.getReference(DefaultConfigurations.DB_CREDITS)
                .child(credit.getYear())
                .child(credit.getMonth())
                .child(credit.getKey()).removeValue();
        loadCredits(yearStr, monthStr);
    }

    @Override
    public void onEditClicked(final Credit credit) {
        startActivityForResult(CreditEditActivity.getLaunchIntent(this, credit), 0);
    }
}
