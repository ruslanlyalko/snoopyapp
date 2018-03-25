package com.ruslanlyalko.snoopyapp.presentation.ui.main.report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.presentation.base.BaseActivity;

public class ReportActivity extends BaseActivity {

    private boolean mIsChanged;

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
        } else {
        }
    }

    @Override
    protected void setupView() {
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
            case R.id.action_delete_report: {
                deleteReport();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mIsChanged) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
            builder.setTitle(R.string.dialog_report_save_before_close_title)
                    .setMessage(R.string.dialog_report_save_before_close_text)
                    .setPositiveButton(R.string.action_save, (dialog, which) -> {
                        saveReportToDB();
                        onBackPressed();
                    })
                    .setNegativeButton(R.string.action_no, (dialog, which) -> {
                        super.onBackPressed();
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private void deleteReport() {
    }

    private void saveReportToDB() {
    }
}
