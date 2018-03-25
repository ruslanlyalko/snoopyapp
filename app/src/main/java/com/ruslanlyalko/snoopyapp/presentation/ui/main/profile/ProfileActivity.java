package com.ruslanlyalko.snoopyapp.presentation.ui.main.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.User;
import com.ruslanlyalko.snoopyapp.presentation.ui.login.LoginActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.login.SignupActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.adapter.UsersAdapter;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.dashboard.DashboardActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.salary.SalaryActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.settings.ProfileSettingsActivity;
import com.ruslanlyalko.snoopyapp.presentation.widget.OnItemClickListener;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity implements OnItemClickListener {

    @BindView(R.id.text_email) TextView mEmailText;
    @BindView(R.id.text_phone) TextView mPhoneText;
    @BindView(R.id.text_bday) TextView mBDayText;
    @BindView(R.id.text_card) TextView mCardText;
    @BindView(R.id.text_position_title) TextView mTitlePositionText;
    @BindView(R.id.text_time) TextView mTimeText;
    @BindView(R.id.text_first_date) TextView mFirstDateText;
    @BindView(R.id.panel_first_date) LinearLayout mFirsDateLayout;
    @BindView(R.id.panel_phone) LinearLayout mPhoneLayout;
    @BindView(R.id.panel_email) LinearLayout mEmailLayout;
    @BindView(R.id.panel_card) LinearLayout mCardLayout;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.card_friends) CardView mCardView;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.image_view_ava) ImageView mAvaImageView;
    @BindView(R.id.image_view_back) ImageView mBackImageView;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.text_available) TextView mTextAvailable;
    @BindView(R.id.text_last_online) TextView mTextLastOnline;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private String mUID;
    private User mUser;
    private UsersAdapter mUsersAdapter;
    private boolean needLoadFriends;
    private Date mLastOnline = new Date();
    private Boolean mConnected;

    public static Intent getLaunchIntent(final Activity launchIntent) {
        return new Intent(launchIntent, ProfileActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        parseExtras();
        initToolbar();
        initRecycle();
        loadUsers();
        checkConnection();
    }

    private void parseExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUID = bundle.getString(Keys.Extras.EXTRA_UID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else
            mUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        needLoadFriends = mUID.equals(mAuth.getCurrentUser().getUid());
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecycle() {
        if (needLoadFriends) {
            mCardView.setVisibility(View.VISIBLE);
            mUsersAdapter = new UsersAdapter(this);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mUsersAdapter);
        } else {
            mCardView.setVisibility(View.GONE);
        }
    }

    private void loadUsers() {
        if (needLoadFriends)
            mUsersAdapter.notifyDataSetChanged();
        mDatabase.getReference(DefaultConfigurations.DB_USERS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            if (user.getId().equals(mUID)) {
                                mUser = user;
                                updateUI(user);
                            } else if (needLoadFriends) {
                                mUsersAdapter.add(user);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            if (user.getId().equals(mUID)) {
                                mUser = user;
                                updateUI(user);
                            } else if (needLoadFriends) {
                                mUsersAdapter.update(user);
                            }
                        }
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

    private void checkConnection() {
        if (needLoadFriends) {
            mDatabase.getReference(".info/connected")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            mConnected = snapshot.getValue(Boolean.class);
                            updateLastOnline();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            System.err.println("Listener was cancelled");
                        }
                    });
        } else {
            mDatabase.getReference(DefaultConfigurations.DB_USERS)
                    .child(mUID)
                    .child("isOnline")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            mConnected = dataSnapshot.getValue(Boolean.class);
                            updateLastOnline();
                        }

                        @Override
                        public void onCancelled(final DatabaseError databaseError) {
                        }
                    });
            mDatabase.getReference(DefaultConfigurations.DB_USERS)
                    .child(mUID)
                    .child("lastOnline")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            mLastOnline = snapshot.getValue(Date.class);
                            updateLastOnline();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            System.err.println("Listener was cancelled");
                        }
                    });
        }
    }

    private void updateUI(User user) {
        if (user == null || mAuth.getCurrentUser() == null) return;
        final boolean myPage = mUser.getId().equals(mAuth.getCurrentUser().getUid());
        // if current user is admin or open his friends
        fab.setVisibility(FirebaseUtils.isAdmin() || myPage ? View.VISIBLE : View.GONE);
        if (mUser.getIsAdmin() && myPage)
            fab.setImageResource(R.drawable.ic_action_money);
        mTitlePositionText.setText(user.getPositionTitle());
        collapsingToolbar.setTitle(user.getFullName());
        mPhoneText.setText(user.getPhone());
        mEmailText.setText(user.getEmail());
        mBDayText.setText(user.getBirthdayDate());
        mCardText.setText(user.getCard());
        mTimeText.setText(user.getWorkingStartTime() + " - " + user.getWorkingEndTime());
        mFirstDateText.setText(user.getWorkingFromDate());
        final String phone = user.getPhone();
        mPhoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        });
        final String email = user.getEmail();
        mEmailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(email, email);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ProfileActivity.this, getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
            }
        });
        final String card = user.getCard();
        mCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(card, card);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ProfileActivity.this, getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
            }
        });
        if (FirebaseUtils.isAdmin() && !user.getId().equals(mAuth.getCurrentUser().getUid())) {
            mFirsDateLayout.setVisibility(View.VISIBLE);
        }
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            mAvaImageView.setVisibility(View.VISIBLE);
            mBackImageView.setVisibility(View.VISIBLE);
            if (!isDestroyed())
                Glide.with(this).load(mUser.getAvatar()).into(mAvaImageView);
        } else {
            mAvaImageView.setVisibility(View.GONE);
            mBackImageView.setVisibility(View.GONE);
        }
    }

    private void updateLastOnline() {
        if (mConnected != null && mConnected) {
            mTextAvailable.setVisibility(View.VISIBLE);
            mTextLastOnline.setText(R.string.online);
        } else {
            mTextAvailable.setVisibility(View.INVISIBLE);
            if (mLastOnline != null)
                mTextLastOnline.setText(getString(R.string.last_online, DateUtils.getUpdatedAt(mLastOnline)));
            else
                mTextLastOnline.setText(getString(R.string.last_online_long_time_ago));
        }
    }

    @OnClick(R.id.fab)
    void onFabClicked() {
        final boolean myPage = mUser.getId().equals(mAuth.getCurrentUser().getUid());
        if (FirebaseUtils.isAdmin() && myPage) {
            startActivity(DashboardActivity.getLaunchIntent(ProfileActivity.this));
        } else {
            startActivity(SalaryActivity.getLaunchIntent(ProfileActivity.this, mUID, mUser));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);
        boolean isCurrentUserPage = mUID.equals(mAuth.getCurrentUser().getUid());
        menu.findItem(R.id.action_add_user).setVisible(FirebaseUtils.isAdmin() && isCurrentUserPage);
        menu.findItem(R.id.action_settings).setVisible(FirebaseUtils.isAdmin() || isCurrentUserPage);
        menu.findItem(R.id.action_logout).setVisible(isCurrentUserPage);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        switch (id) {
            case R.id.action_add_user: {
                startActivity(new Intent(this, SignupActivity.class));
                return true;
            }
            case R.id.action_settings: {
                Intent intent = new Intent(this, ProfileSettingsActivity.class);
                intent.putExtra(Keys.Extras.EXTRA_UID, mUID);
                startActivity(intent);
                return true;
            }
            case R.id.action_logout: {
                logout();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_message)
                .setPositiveButton("Вийти", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUtils.clearPushToken();
                        mAuth.signOut();
                        Intent intent = new Intent(ProfileActivity.this,
                                LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Повернутись", null)
                .show();
    }

    @Override
    public void onItemClicked(final int position) {
        startActivity(ProfileActivity.getLaunchIntent(this, mUsersAdapter.getItemAtPosition(position).getId()));
    }

    public static Intent getLaunchIntent(final Activity launchIntent, final String userId) {
        Intent intent = new Intent(launchIntent, ProfileActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_UID, userId);
        return intent;
    }
}