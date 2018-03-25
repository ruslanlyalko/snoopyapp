package com.ruslanlyalko.snoopyapp.presentation.ui.main.messages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Message;
import com.ruslanlyalko.snoopyapp.data.models.Notification;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.messages.adapter.MessagesAdapter;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.messages.edit.MessageEditActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessagesActivity extends AppCompatActivity {

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.recycler_view) RecyclerView mMessagesList;

    private MessagesAdapter mMessagesAdapter;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public static Intent getLaunchIntent(final Activity launchIntent) {
        return new Intent(launchIntent, MessagesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        initRecycler();
        loadMessages();
        fab.setVisibility(FirebaseUtils.isAdmin() ? View.VISIBLE : View.GONE);
        loadBadge();
    }

    private void initRecycler() {
        mMessagesAdapter = new MessagesAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mMessagesList.setLayoutManager(mLayoutManager);
        mMessagesList.setItemAnimator(new DefaultItemAnimator());
        mMessagesList.setAdapter(mMessagesAdapter);
    }

    private void loadMessages() {
        FirebaseDatabase.getInstance().getReference(DefaultConfigurations.DB_DIALOGS)
                .orderByChild("updatedAt/time")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        List<Message> list = new ArrayList<>();
                        for (DataSnapshot messageSS : dataSnapshot.getChildren()) {
                            if (FirebaseUtils.isAdmin() || messageSS.child("Members").child(mUser.getUid()).exists()) {
                                Message message = messageSS.getValue(Message.class);
                                if (message != null) {
                                    list.add(0, message);
                                }
                            }
                        }
                        mMessagesAdapter.setData(list);
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    private void loadBadge() {
        mDatabase.getReference(DefaultConfigurations.DB_USERS_NOTIFICATIONS)
                .child(mUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        List<Notification> notifications = new ArrayList<>();
                        for (DataSnapshot notifSS : dataSnapshot.getChildren()) {
                            Notification notification = notifSS.getValue(Notification.class);
                            notifications.add(notification);
                        }
                        mMessagesAdapter.updateNotifications(notifications);
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    @OnClick(R.id.fab)
    void onFabCLicked() {
        addNotification();
    }

    private void addNotification() {
        Intent intent = new Intent(MessagesActivity.this, MessageEditActivity.class);
        startActivity(intent);
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
