package com.ruslanlyalko.snoopyapp.presentation.ui.main.mk;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Constants;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Mk;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MkDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.text_description) TextView textDescription;
    @BindView(R.id.text_title2) TextView textTitle2;
    @BindView(R.id.image_view) ImageView imageView;
    @BindView(R.id.fab) FloatingActionButton fab;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private Mk mk;
    private String mkKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mk_details);
        ButterKnife.bind(this);
        parseExtras();
        initToolbar();
        loadMkFromDB();
    }

    private void parseExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mkKey = bundle.getString(Keys.Extras.EXTRA_ITEM_ID);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadMkFromDB() {
        if (mkKey == null || mkKey.isEmpty()) return;
        DatabaseReference ref = database.getReference(DefaultConfigurations.DB_MK).child(mkKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mk = dataSnapshot.getValue(Mk.class);
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateUI() {
        if (mk != null) {
            invalidateOptionsMenu();
            toolbarLayout.setTitle(mk.getTitle1());
            textTitle2.setText(mk.getTitle2());
            textDescription.setText(mk.getDescription());
            fab.setVisibility((mk.getLink() != null && !mk.getLink().isEmpty())
                    ? View.VISIBLE : View.GONE);
            if (mk.getImageUri() != null && !mk.getImageUri().isEmpty()) {
                Glide.with(MkDetailsActivity.this).load(mk.getImageUri()).into(imageView);
            }
        } else {
            // notification == null
            toolbar.setTitle(R.string.title_activity_mk_item);
        }
    }

    @OnClick(R.id.fab)
    void onFabClicked() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setToolbarColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        customTabsIntent.launchUrl(MkDetailsActivity.this, Uri.parse(mk.getLink()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_EDIT) {
            loadMkFromDB();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mk != null) {
            menu.findItem(R.id.action_delete).setVisible(FirebaseUtils.isAdmin()
                    || mk.getUserId().equals(mUser.getUid()));
            menu.findItem(R.id.action_edit).setVisible(FirebaseUtils.isAdmin()
                    || mk.getUserId().equals(mUser.getUid()));
        }
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
            if (FirebaseUtils.isAdmin()
                    || mk.getUserId().equals(mUser.getUid())) {
                editMk();
            }
        }
        if (id == R.id.action_delete) {
            if (FirebaseUtils.isAdmin()
                    || mk.getUserId().equals(mUser.getUid())) {
                deleteMk();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void editMk() {
        startActivityForResult(MkEditActivity.getLaunchIntent(this, mkKey), Constants.REQUEST_CODE_EDIT);
    }

    private void deleteMk() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MkDetailsActivity.this);
        AlertDialog dialog = builder.setTitle(R.string.dialog_delete_mk_title)
                .setMessage(R.string.dialog_delete_mk_title)
                .setPositiveButton("Видалити", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        database.getReference(DefaultConfigurations.DB_MK).child(mk.getKey()).removeValue();
                        onBackPressed();
                    }
                })
                .setNegativeButton("Повернутись", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).create();
        dialog.show();
    }
}
