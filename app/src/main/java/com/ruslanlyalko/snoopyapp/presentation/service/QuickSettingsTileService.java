package com.ruslanlyalko.snoopyapp.presentation.service;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Report;
import com.ruslanlyalko.snoopyapp.data.models.User;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.report.ReportActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.splash.SplashActivity;

import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 13.03.2018.
 */
@TargetApi(Build.VERSION_CODES.N)
public class QuickSettingsTileService extends TileService {

    private String mUId;
    private String mDateYear;
    private String mDateMonth;
    private String mDateDay;
    private Report mReport;

    public QuickSettingsTileService() {
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        update();
    }

    @Override
    public void onClick() {
        if (mReport != null)
            startActivityAndCollapse(ReportActivity.getLaunchIntent(getApplicationContext(),
                    mReport.getDate(), mReport.getUserName(), mReport.getUserId()));
        else
            startActivityAndCollapse(SplashActivity.getLaunchIntent(getApplicationContext()));
    }

    public void update() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Tile tile = getQsTile();
            if (tile != null) {
                tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_tile_24dp));
                tile.setLabel("Ввійдіть в додаток!");
                tile.updateTile();
            }
            return;
        }
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_tile_load_24dp));
            tile.setLabel("Оновлення");
            tile.updateTile();
        }
        mReport = null;
        mUId = FirebaseAuth.getInstance().getUid();
        mDateDay = DateUtils.toString(new Date(), "d");
        mDateMonth = DateUtils.toString(new Date(), "M");
        mDateYear = DateUtils.toString(new Date(), "yyyy");
        FirebaseDatabase.getInstance().getReference(DefaultConfigurations.DB_USERS)
                .child(mUId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null && user.getUserIsAdmin()) {
                            showReportsData();
                        } else {
                            Tile tile = getQsTile();
                            if (tile != null) {
                                tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_tile_24dp));
                                tile.setLabel("Відкритии KIDS APP");
                                tile.updateTile();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    void showReportsData() {
        FirebaseDatabase.getInstance().getReference(DefaultConfigurations.DB_REPORTS)
                .child(mDateYear).child(mDateMonth).child(mDateDay)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            Tile tile = getQsTile();
                            if (tile != null) {
                                tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_tile_empty_24dp));
                                tile.setState(Tile.STATE_INACTIVE);
                                tile.setLabel("Немає звітів за сьогодні!");
                                tile.updateTile();
                            }
                        } else {
                            int total = 0;
                            Date date = null;
                            String names = "";
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                mReport = dataSnapshot1.getValue(Report.class);
                                if (mReport != null) {
                                    total += mReport.getTotal();
                                    if (mReport.getCheckedListDone() && mReport.getCheckedListTime() != null)
                                        date = mReport.getCheckedListTime();
                                    if (!names.isEmpty()) names += " ";
                                    names += mReport.getUserName();
                                }
                            }
                            Tile tile = getQsTile();
                            if (tile != null) {
                                tile.setState(Tile.STATE_ACTIVE);
                                if (total == 0) {
                                    if (date == null) {
                                        tile.setLabel("Чек-лист не пройдено (" + names + ")");
                                        tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_tile_24dp));
                                    } else {
                                        tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_tile_check_24dp));
                                        tile.setLabel("Чек-лист: " +
                                                DateUtils.toString(mReport.getCheckedListTime(), "HH:mm") + " (" + names + ")");
                                    }
                                } else {
                                    tile.setLabel("Виручка: " + total + "грн (" + names + ")");
                                    tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_tile_done_24dp));
                                }
                                tile.updateTile();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }
}
