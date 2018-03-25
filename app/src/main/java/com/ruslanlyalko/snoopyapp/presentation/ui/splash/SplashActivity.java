package com.ruslanlyalko.snoopyapp.presentation.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.presentation.base.BaseActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.login.LoginActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.MainActivity;

import butterknife.BindView;

/**
 * Created by Ruslan Lyalko
 * on 11.11.2017.
 */

public class SplashActivity extends BaseActivity {

    @BindView(R.id.text_app_name) TextView mTextAppName;
    //@BindView(R.id.image_logo) ImageView mImageLogo;

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, SplashActivity.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void setupView() {
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.splash);
        mTextAppName.startAnimation(loadAnimation);
        loadAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                startMainActivity();
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });
    }

    private void startMainActivity() {
        Intent intent;
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            intent = LoginActivity.getLaunchIntent(this);
        else
            intent = MainActivity.getLaunchIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.nothing, R.anim.nothing);
    }
}
