package com.ruslanlyalko.snoopyapp.presentation.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.AppInfo;
import com.ruslanlyalko.snoopyapp.data.models.Notification;
import com.ruslanlyalko.snoopyapp.data.models.User;
import com.ruslanlyalko.snoopyapp.presentation.base.BaseActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.about.AboutActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.calendar.CalendarActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.ClientsTabActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.expenses.ExpensesActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.messages.MessagesActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.mk.MkTabActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.ProfileActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.report.ReportActivity;
import com.ruslanlyalko.snoopyapp.presentation.widget.SwipeLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ruslanlyalko.snoopyapp.BuildConfig.DEBUG;
import static com.ruslanlyalko.snoopyapp.common.ViewUtils.viewToDrawable;

public class MainActivity extends BaseActivity {

    private static final int MIN_DISTANCE = 150;

    @BindView(R.id.text_app_name) TextView mAppNameText;
    @BindView(R.id.text_link) TextView mLinkText;
    @BindView(R.id.text_link_details) TextView mLinkDetailsText;
    @BindView(R.id.button_arrow) Button mArrowButton;
    @BindView(R.id.swipe_layout) SwipeLayout mSwipeLayout;
    @BindView(R.id.button_events) ImageView mEventsButton;
    @BindView(R.id.layout_clients) LinearLayout mLayoutClients;

    boolean mDoubleBackToExitPressedOnce = false;
    boolean mSwipeOpened = false;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private float x1;
    private float y1;
    private List<Notification> mNotifications = new ArrayList<>();
    private AppInfo mAppInfo = new AppInfo();

    public static Intent getLaunchIntent(final AppCompatActivity launchActivity) {
        return new Intent(launchActivity, MainActivity.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void setupView() {
        initCurrentUser();
        initSwipes();
        loadBadge();
        sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());
    }

    private void initCurrentUser() {
        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }
        mDatabase.getReference(DefaultConfigurations.DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            FirebaseUtils.setIsAdmin(user.getUserIsAdmin());
                            FirebaseUtils.setUser(user);
                            if(isDestroyed())return;
                            mLayoutClients.setVisibility(user.getUserIsAdmin() || user.getShowClients() ? View.VISIBLE : View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
        mDatabase.getReference(DefaultConfigurations.DB_INFO)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mAppInfo = dataSnapshot.getValue(AppInfo.class);
                        checkVersion();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void initSwipes() {
        // Swipe
        mSwipeLayout.setSwipeEnabled(false);
        mSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                mArrowButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_down3, 0, 0);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                mSwipeOpened = true;
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                mArrowButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_up3, 0, 0);
            }

            @Override
            public void onClose(SwipeLayout layout) {
                mSwipeOpened = false;
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
            }
        });
    }

    private void loadBadge() {
        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }
        mDatabase.getReference(DefaultConfigurations.DB_USERS_NOTIFICATIONS)
                .child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        mNotifications.clear();
                        for (DataSnapshot notifSS : dataSnapshot.getChildren()) {
                            Notification notification = notifSS.getValue(Notification.class);
                            mNotifications.add(notification);
                        }
                        redrawCountOfNotifications(mNotifications.size());
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    private void sendRegistrationToServer(final String refreshedToken) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = (pInfo != null ? pInfo.versionName : "");
        if (user != null) {
            DatabaseReference ref = mDatabase.getReference(DefaultConfigurations.DB_USERS)
                    .child(user.getUid());
            ref.child("token").setValue(refreshedToken);
            ref.child("appVersion").setValue(version);
            ref.child("lastOnline").onDisconnect().setValue(new Date());
            ref.child("isOnline").setValue(Boolean.TRUE);
            ref.child("isOnline").onDisconnect().removeValue();
        }
    }

    private void checkVersion() {
        if (isDestroyed()) return;
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int myVersion = (pInfo != null ? pInfo.versionCode : 0);
        if (myVersion < mAppInfo.getLatestVersion() || mAppInfo.getShowAnyWay()) {
            showVersionLink(true);
        } else {
            showVersionLink(false);
            if (myVersion > mAppInfo.getLatestVersion() && !DEBUG) {
                mDatabase.getReference(DefaultConfigurations.DB_INFO)
                        .child("latestVersion").setValue(myVersion);
            }
        }
    }

    public void redrawCountOfNotifications(int count) {
        if (isDestroyed()) return;
        if (count == 0) {
            mEventsButton.setImageDrawable(getDrawable(R.drawable.ic_event1));
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.drawable_event_badge, null);
        TextView badgeCount = view.findViewById(R.id.badge);
        badgeCount.setVisibility(View.VISIBLE);
        badgeCount.setText(String.valueOf(count));
        Bitmap bitmap = viewToDrawable(view);
        Drawable icon = new BitmapDrawable(getResources(), bitmap);
        mEventsButton.setImageDrawable(icon);
    }

    void showVersionLink(boolean show) {
        mLinkText.setText(mAppInfo.getTitle1());
        mLinkDetailsText.setText(mAppInfo.getTitle2());
        mLinkText.setVisibility(show ? View.VISIBLE : View.GONE);
        mLinkDetailsText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (mSwipeOpened) {
            mSwipeLayout.close();
            return;
        }
        if (mDoubleBackToExitPressedOnce) {
            finish();
            return;
        }
        mDoubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.hint_double_press, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> mDoubleBackToExitPressedOnce = false, 2000);
    }

    void setupShortCuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()) {
                // create ShortcutInfo with details of shortcut
                // if 'uniqueShortcutId' already existed, we could just access it using the Id
                if (shortcutManager.getPinnedShortcuts().size() > 0) return;
                ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this, "calendarId")
                        .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                        .setShortLabel(getString(R.string.shortcut_calendar_short_label))
                        .setLongLabel(getString(R.string.shortcut_calendar_long_label))
                        .setIntent(new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, CalendarActivity.class))
                        .build();
                shortcutManager.requestPinShortcut(shortcutInfo, null);
            }
        }
    }

    @OnClick({R.id.text_link, R.id.text_link_details})
    void onTopLinkClicked() {
        openBrowser(mAppInfo.getLink());
    }

    private void openBrowser(String url) {
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.button_arrow)
    void onArrowClicked() {
        if (mSwipeOpened)
            mSwipeLayout.close();
        else
            mSwipeLayout.open();
    }

    @OnClick(R.id.button_profile)
    void onProfileClicked() {
        startActivity(ProfileActivity.getLaunchIntent(this));
    }

    @OnClick(R.id.button_events)
    void onEventsClicked() {
        startActivity(MessagesActivity.getLaunchIntent(this));
    }

    @OnClick(R.id.button_report)
    void onReportClicked() {
        startActivity(ReportActivity.getLaunchIntent(this));
    }

    @OnClick(R.id.button_calendar)
    void onCalendarClicked() {
        startActivity(CalendarActivity.getLaunchIntent(this));
    }

    @OnClick(R.id.button_mk)
    void onMkClicked() {
        startActivity(MkTabActivity.getLaunchIntent(this));
    }

    @OnClick(R.id.button_expenses)
    void onExpensesClicked() {
        startActivity(ExpensesActivity.getLaunchIntent(this));
    }

    @OnClick(R.id.button_about)
    void onAboutClicked() {
        startActivity(AboutActivity.getLaunchIntent(this, mAppInfo.getAboutText()));
    }

    @OnClick(R.id.button_fb)
    void onFbClicked() {
        openBrowser(mAppInfo.getLinkFb());
    }

    @OnClick(R.id.button_clients)
    void onClientsClicked() {
        startActivity(ClientsTabActivity.getLaunchIntent(this));
    }

    @OnClick(R.id.button_link)
    void onShareClicked() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_TEXT, mAppInfo.getLink());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float deltaX = x2 - x1;
                if (deltaX > MIN_DISTANCE) {
                    // left2right
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    break;
                } else if (deltaX < (0 - MIN_DISTANCE)) {
                    // right2left
                    Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                    startActivity(intent);
                    break;
                }
// else {
//                    // consider as something else - a screen tap for example
//                }
                float y2 = event.getY();
                float deltaY = y2 - y1;
                if (deltaY > MIN_DISTANCE) {
                    // top2bottom
                    mSwipeLayout.close();
                } else if (deltaY < (0 - MIN_DISTANCE)) {
                    // bottom2top
                    mSwipeLayout.open();
                }
//                else {
//                    // consider as something else - a screen tap for example
//                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
    }
}
