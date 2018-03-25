package com.ruslanlyalko.snoopyapp.presentation.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.text_email) EditText inputEmail;
    @BindView(R.id.text_password) EditText inputPassword;
    @BindView(R.id.text_name) EditText inputName;
    @BindView(R.id.text_phone) EditText inputPhone;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.sign_up_button)
    void onSignUpClicked() {
        final String name = inputName.getText().toString().trim();
        final String phone = inputPhone.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            //Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
            inputName.setError("Enter name!");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            //Toast.makeText(getApplicationContext(), "Enter phone!", Toast.LENGTH_SHORT).show();
            inputPhone.setError("Enter phone!");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Enter email!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Enter password!");
            return;
        }
        if (password.length() < 6) {
            inputPassword.setError("Password too short, enter minimum 6 characters!");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        //create user
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the mFirebaseAuth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            FirebaseUtils.setIsAdmin(false);
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            mUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    createUserData(name, phone, email, mUser.getUid());
                                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.toast_user_created)
                                            + " " + email, Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                }
                            });
                        } else {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUserData(String name, String phone, String email, String uId) {
        DatabaseReference databaseRefCurrentUser = mDatabase.getReference(DefaultConfigurations.DB_USERS).child(uId);
        User user = new User(uId, name, phone, email, "01.06.1991", "01.06.1991", "", false);
        databaseRefCurrentUser.setValue(user);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.button_reset_password)
    void onResetClicked() {
        startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
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