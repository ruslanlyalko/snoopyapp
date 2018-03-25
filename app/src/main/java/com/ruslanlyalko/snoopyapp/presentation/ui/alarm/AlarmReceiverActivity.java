package com.ruslanlyalko.snoopyapp.presentation.ui.alarm;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.report.ReportActivity;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlarmReceiverActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_receiver);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = null;
        if (pm != null) {
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "wakeup");
            wl.acquire(10 * 60 * 1000L /*10 minutes*/);
            new Handler().postDelayed(wl::release, 10 * 60 * 1000);
        }
        shakeItBaby();
        playSound(this, getAlarmUri());
    }

    private void shakeItBaby() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator == null) return;
        //long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        if (Build.VERSION.SDK_INT >= 26) {
            mVibrator.vibrate(VibrationEffect.createOneShot(120 * 1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            mVibrator.vibrate(120 * 1000);
        }
    }

    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null && audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    @OnClick(R.id.button_stop_alarm)
    public void onButtonStopClicked() {
        mMediaPlayer.stop();
        mVibrator.cancel();
        startActivity(ReportActivity.getLaunchIntent(this));
        finishAffinity();
    }
}
