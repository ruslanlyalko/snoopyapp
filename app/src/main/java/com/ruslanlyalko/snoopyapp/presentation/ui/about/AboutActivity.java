package com.ruslanlyalko.snoopyapp.presentation.ui.about;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.text_version) TextView textVersion;
    @BindView(R.id.text_about) TextView textAbout;
    @BindView(R.id.edit_about) EditText editAbout;
    @BindView(R.id.fab) FloatingActionButton fab;

    public static Intent getLaunchIntent(Context context, String text) {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.putExtra(Keys.Extras.EXTRA_ABOUT, text);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initToolbar();
        parseExtras();
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = getString(R.string.dialog_about_message) + "" + (pInfo != null ? pInfo.versionName : "");
        textVersion.setText(version);
        fab.setVisibility(FirebaseUtils.isAdmin() ? View.VISIBLE : View.GONE);
        if (FirebaseUtils.isAdmin()) {
            textAbout.setVisibility(View.GONE);
            editAbout.setVisibility(View.VISIBLE);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void parseExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String aboutText = bundle.getString(Keys.Extras.EXTRA_ABOUT, getString(R.string.text_about_company));
            textAbout.setText(aboutText);
            editAbout.setText(aboutText);
        }
    }

    @OnClick(R.id.fab)
    void onFabClicked() {
        if (FirebaseUtils.isAdmin())
            FirebaseDatabase.getInstance().getReference(DefaultConfigurations.DB_INFO)
                    .child("aboutText")
                    .setValue(editAbout.getText().toString().trim())
                    .addOnCompleteListener(task ->
                            Toast.makeText(AboutActivity.this, R.string.mk_updated, Toast.LENGTH_SHORT).show());
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
