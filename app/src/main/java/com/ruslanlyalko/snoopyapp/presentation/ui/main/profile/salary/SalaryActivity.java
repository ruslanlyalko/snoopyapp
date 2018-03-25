package com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.salary;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Constants;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.common.ViewUtils;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Report;
import com.ruslanlyalko.snoopyapp.data.models.User;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.salary.edit.SalaryEditActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SalaryActivity extends AppCompatActivity {

    @BindView(R.id.calendar_view) CompactCalendarView mCompactCalendarView;
    @BindView(R.id.text_salary_stavka) TextView mTextSalaryStavka;
    @BindView(R.id.text_salary_percent) TextView mTextSalaryPercent;
    @BindView(R.id.text_salary_art) TextView mTextSalaryArt;
    @BindView(R.id.text_salary_mk1) TextView mTextSalaryMk;
    @BindView(R.id.text_salary_mk_children) TextView mTextSalaryMkChildren;
    @BindView(R.id.text_percent_total) TextView mTextPercent;
    @BindView(R.id.text_stavka_total) TextView mTextStavka;
    @BindView(R.id.text_mk_total) TextView mTextMk;
    @BindView(R.id.text_month) TextView mTextMonth;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.text_card) TextView mTextCard;
    @BindView(R.id.text_name) TextView mTextName;
    @BindView(R.id.text_salary_expand) TextView mTextExpand;
    @BindView(R.id.image_expand) ImageView mImageView;
    @BindView(R.id.text_total) TextSwitcher mTotalSwitcher;

    private List<Report> mReports = new ArrayList<>();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private User mUser = new User();
    private String mUId;
    private Date mCurrentMonth;

    public static Intent getLaunchIntent(final AppCompatActivity launchActivity, String userId, User user) {
        Intent intent = new Intent(launchActivity, SalaryActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_UID, userId);
        intent.putExtra(Keys.Extras.EXTRA_USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);
        ButterKnife.bind(this);
        parseExtras();
        initSwitcher();
        initCalendar();
        initUserData();
        mTextMonth.setText(Constants.MONTH_FULL[Calendar.getInstance().get(Calendar.MONTH)]);
        String yearStr = new SimpleDateFormat("yyyy", Locale.US).format(new Date());
        String monthStr = new SimpleDateFormat("M", Locale.US).format(new Date());
        loadReports(yearStr, monthStr);
        mCurrentMonth = DateUtils.getCurrentMonthFirstDate();
        updateConditionUI(mCurrentMonth);
    }

    private void parseExtras() {
        Bundle extras;
        if ((extras = getIntent().getExtras()) != null) {
            mUser = (User) extras.getSerializable(Keys.Extras.EXTRA_USER);
            mUId = extras.getString(Keys.Extras.EXTRA_UID);
        }
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
                mTextMonth.setText(str);
                String yearStr = new SimpleDateFormat("yyyy", Locale.US).format(firstDayOfNewMonth);
                String monthStr = new SimpleDateFormat("M", Locale.US).format(firstDayOfNewMonth);
                loadReports(yearStr, monthStr);
                mCurrentMonth = firstDayOfNewMonth;
                updateConditionUI(firstDayOfNewMonth);
            }
        });
    }

    private void initSwitcher() {
        mTotalSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView myText = new TextView(SalaryActivity.this);
                myText.setTextSize(32);
                myText.setTextColor(Color.BLACK);
                return myText;
            }
        });
    }

    @OnClick(R.id.button_prev)
    void onPrevClicked() {
        setSwitcherAnim(true);
        mCompactCalendarView.showPreviousMonth();
    }

    private void setSwitcherAnim(final boolean right) {
        Animation in;
        Animation out;
        if (right) {
            in = AnimationUtils.loadAnimation(this, R.anim.trans_right_in);
            out = AnimationUtils.loadAnimation(this, R.anim.trans_right_out);
        } else {
            in = AnimationUtils.loadAnimation(this, R.anim.trans_left_in);
            out = AnimationUtils.loadAnimation(this, R.anim.trans_left_out);
        }
        mTotalSwitcher.setInAnimation(in);
        mTotalSwitcher.setOutAnimation(out);
    }

    @OnClick(R.id.button_next)
    void onNextClicked() {
        setSwitcherAnim(false);
        mCompactCalendarView.showNextMonth();
    }

    private void loadReports(String yearStr, String monthStr) {
        mDatabase.getReference(DefaultConfigurations.DB_REPORTS)
                .child(yearStr)
                .child(monthStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        mReports.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Report report = data.child(mUId).getValue(Report.class);
                            if (report != null) {
                                mReports.add(report);
                            }
                        }
                        calcSalary();
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    @OnClick(R.id.panel_copy)
    void onCardClicked() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(mTextCard.getText().toString(), mTextCard.getText().toString());
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(SalaryActivity.this, getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.panel_action)
    void onExpandClicked() {
        if (mTextExpand.getVisibility() == View.VISIBLE) {
            mImageView.setImageResource(R.drawable.ic_action_expand_more);
            ViewUtils.collapse(mTextExpand);
        } else {
            ViewUtils.expand(mTextExpand);
            mImageView.setImageResource(R.drawable.ic_action_expand_less);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (FirebaseUtils.isAdmin())
            getMenuInflater().inflate(R.menu.menu_salary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, SalaryEditActivity.class);
            intent.putExtra(Keys.Extras.EXTRA_UID, mUId);
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initUserData();
        updateConditionUI(mCurrentMonth);
    }

    private void initUserData() {
        mTextName.setText(mUser.getUserName());
        mTextCard.setText(mUser.getUserCard());
    }

    private void updateConditionUI(Date firstDayOfNewMonth) {
        String date = new SimpleDateFormat("d-M-yyyy", Locale.US).format(firstDayOfNewMonth);
        boolean isSpecCalc = mUser.getMkSpecCalc() && DateUtils.isTodayOrFuture(date, mUser.getMkSpecCalcDate());
        if (isSpecCalc) {
            mTextSalaryStavka.setText(String.format(getString(R.string.hrn_day), String.valueOf(mUser.getUserStavka())));
            mTextSalaryPercent.setText(String.format(getString(R.string.hrn_percent), String.valueOf(mUser.getUserPercent())));
            mTextSalaryMk.setText(String.format(getString(R.string.hrn), String.valueOf(mUser.getMkBd())));
            mTextSalaryMkChildren.setText(String.format(getString(R.string.hrn_child), String.valueOf(mUser.getMkBdChild())));
            mTextSalaryArt.setText(String.format(getString(R.string.hrn_child), String.valueOf(mUser.getMkArtChild())));
            return;
        }
        mTextSalaryStavka.setText(String.format(getString(R.string.hrn_day), String.valueOf(Constants.SALARY_DEFAULT_STAVKA)));
        mTextSalaryPercent.setText(String.format(getString(R.string.hrn_percent), String.valueOf(Constants.SALARY_DEFAULT_PERCENT)));
        mTextSalaryMk.setText(String.format(getString(R.string.hrn), String.valueOf(Constants.SALARY_DEFAULT_MK)));
        mTextSalaryMkChildren.setText(String.format(getString(R.string.hrn_child), String.valueOf(Constants.SALARY_DEFAULT_MK_CHILD)));
        mTextSalaryArt.setText(String.format(getString(R.string.hrn_child), String.valueOf(Constants.SALARY_DEFAULT_ART_MK_CHILD)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        calcSalary();
    }

    private void calcSalary() {
        //init hrn
        int userStavka = Constants.SALARY_DEFAULT_STAVKA;
        int userPercent = Constants.SALARY_DEFAULT_PERCENT;
        int userMkBirthday = Constants.SALARY_DEFAULT_MK;
        int userMkBdChild = Constants.SALARY_DEFAULT_MK_CHILD;
        int userMkArtChild = Constants.SALARY_DEFAULT_ART_MK_CHILD;
        //check what exactly we have
        int stavka = 0;
        int percentTotal = 0;
        int percent;
        int mkBirthday = 0;
        int mkBirthdayCount = 0;
        int mkBirthdayChildren = 0;
        int mkArt = 0;
        int mkArtCount = 0;
        int mkArtChildren = 0;
        if (mUser.getMkSpecCalc()
                && mReports.size() > 0
                && DateUtils.isTodayOrFuture(mReports.get(0).getDate(), mUser.getMkSpecCalcDate())) {
            userStavka = mUser.getUserStavka();
            userPercent = mUser.getUserPercent();
            userMkBirthday = mUser.getMkBd();
            userMkBdChild = mUser.getMkBdChild();
            userMkArtChild = mUser.getMkArtChild();
        }
        for (Report rep : mReports) {
            if (DateUtils.future(rep.getDate())) continue;
            // stavka
            if (rep.getHalfSalary())
                stavka += (userStavka / 2);
            else
                stavka += userStavka;
            // percent
            percentTotal += rep.total;
            //Birthdays Mk
            mkBirthday += rep.bMk * userMkBirthday;
            mkBirthday += rep.b30 * userMkBdChild;
            mkBirthdayCount += rep.bMk;
            mkBirthdayChildren += rep.b30;
            // Art MK
            if (rep.mkMy) {
                mkArt += (rep.mk1 + rep.mk2) * userMkArtChild;
                if (rep.mk1 != 0 || rep.mk2 != 0)
                    mkArtCount += 1;
                mkArtChildren += rep.mk1;
                mkArtChildren += rep.mk2;
            }
        }
        percent = (percentTotal * userPercent / 100);
        int total = stavka + percent + mkBirthday + mkArt;
        mTextStavka.setText(String.format(getString(R.string.hrn), DateUtils.getIntWithSpace(stavka)));
        mTextPercent.setText(String.format(getString(R.string.hrn), DateUtils.getIntWithSpace(percent)));
        mTextMk.setText(String.format(getString(R.string.hrn), DateUtils.getIntWithSpace(mkBirthday + mkArt)));
        mTotalSwitcher.setText(String.format(getString(R.string.HRN), DateUtils.getIntWithSpace(total)));
        String text1 = "В цьому місяці було " + mReports.size() + " робочих днів \n";
        text1 += "Загальна виручка " + percentTotal + " грн \n\n";
        text1 += "Проведено " + mkBirthdayCount + " МК на Днях Народженнях \n";
        text1 += "На яких було присутньо " + mkBirthdayChildren + " дітей \n";
        text1 += "Зароблено " + mkBirthday + " грн\n\n";
        text1 += "Проведено " + mkArtCount + " Творчих та Кулінарних МК \n";
        text1 += "На яких було присутньо " + mkArtChildren + " дітей \n";
        text1 += "Зароблено " + mkArt + " грн\n\n";
        text1 += "Аванс: до 20-го чиса;  ЗП: до 5-го числа\n";
        mTextExpand.setText(text1);
        mProgressBar.setMax(total);
        mProgressBar.setProgress(stavka);
        mProgressBar.setSecondaryProgress(stavka + percent);
    }
}
