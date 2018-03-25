package com.ruslanlyalko.snoopyapp.presentation.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.text_email) AutoCompleteTextView inputEmail;
    @BindView(R.id.text_password) EditText inputPassword;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    public static Intent getLaunchIntent(final AppCompatActivity launchActivity) {
        return new Intent(launchActivity, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_login)
    void onLoginClicked() {
        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Введіть електронну скриньку!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Введіть пароль!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (!task.isSuccessful()) {
                        if (password.length() < 6) {
                            inputPassword.setError(getString(R.string.toast_minimum_password));
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.toast_auth_failed), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @OnClick(R.id.button_reset_password)
    void onResetClicked() {
        startActivity(ResetPasswordActivity.getLaunchIntent(this));
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

