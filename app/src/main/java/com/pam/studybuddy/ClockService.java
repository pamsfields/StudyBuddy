package com.pam.studybuddy;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Pam on 12/3/2017.
 */

import com.pam.studybuddy.State.State;


public class ClockService extends Service {

    /** result codes*/
    public static final int UPDATE_TIME = 17;
    public static final int FINISHED = 19;

    public static final String TIME_REMAINING = "com.junzew.pomodoro.TIMEREMAINING";

    private CountDownTimer mCountDownTimer;
    private MainActivity mMainActivity;
    public void setBoundActivity(MainActivity activity) {
        mMainActivity = activity;
    }

    private boolean isTimerRunning = false;
    private long timeRemaining = 0;
    private boolean isPaused = false;

    public class ClockServiceBinder extends Binder {
        ClockService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ClockService.this;
        }
    }
    private IBinder mBinder = new ClockServiceBinder();

    public ClockService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void pause() {
        handleActionPause();
    }

    public void start(State state) {
        handleActionStart(state.getStateName());
    }

    private void handleActionStart(String stateName) {
        State currentState = State.getStateFromName(stateName);
        long duration = currentState.getDuration() * 60 * 1000; // milliseconds to minutes
        isTimerRunning = true;
        if (isPaused) {
            duration = timeRemaining;
            isPaused = false;
        }
        mCountDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("ClockService", "onTick" + millisUntilFinished);
                timeRemaining = millisUntilFinished;
                Bundle bundle = new Bundle();
                bundle.putLong(TIME_REMAINING, millisUntilFinished);
                mMainActivity.onReceiveResult(UPDATE_TIME, bundle);
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                Bundle bundle = new Bundle();
                mMainActivity.onReceiveResult(FINISHED, bundle);
                alarm();

            }
        };
        mCountDownTimer.start();
    }

    private void handleActionPause() {
        if (isTimerRunning) {
            mCountDownTimer.cancel();
            isPaused = true;
        }
    }

    // set off alarm
    public void alarm() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null)
            vibrator.vibrate(400);
    }

}