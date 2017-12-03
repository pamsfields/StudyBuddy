package com.pam.studybuddy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pam.studybuddy.State.PomodoroState;
import com.pam.studybuddy.State.State;

public class MainActivity extends AppCompatActivity {

    RelativeLayout mRelativeLayout;
    TextView mCountTextView;
    TextView mClockTextView;
    Button mStartButton;
    State mState = PomodoroState.getInstance();

    ClockService mClockService;
    private boolean isBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ClockService.ClockServiceBinder binder = (ClockService.ClockServiceBinder)service;
            mClockService = binder.getService();
            isBound = true;
            mClockService.setBoundActivity(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.container);
        mCountTextView = (TextView) findViewById(R.id.text_view_count);
        mClockTextView = (TextView) findViewById(R.id.text_view_count_down);
        mStartButton = (Button) findViewById(R.id.button_start);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBound) return;
                if (mStartButton.getText().toString().toLowerCase().equals("start")) {
                    mClockService.start(mState);
                    mStartButton.setText("pause");
                } else {
                    mClockService.pause();
                    mStartButton.setText("start");
                }
            }
        });
        if (savedInstanceState != null) {
            restore(savedInstanceState);
        }
        bindService(new Intent(this, ClockService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void setState(State s) {
        this.mState = s;
    }

    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == ClockService.FINISHED) {
            mClockTextView.setText("0 : 00");
            mState.next(MainActivity.this);
            setTitle(mState.getStateName());
            mStartButton.setText("start");
            mClockTextView.setText(mState.getDuration()+" : 00");
            mCountTextView.setText(String.valueOf(mState.getCompletedPomodoro()));
            ActionBar actionBar = getSupportActionBar();
            if (mState.isBreak()) {
                if (actionBar != null) {
                    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.idle)));
                }
                mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.idle));
                mStartButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                if (actionBar != null) {
                    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                }
                mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mStartButton.setBackgroundColor(getResources().getColor(R.color.idle));
            }
        } else if (resultCode == ClockService.UPDATE_TIME) {
            long timeRemaining = resultData.getLong(ClockService.TIME_REMAINING);
            mClockTextView.setText(String.valueOf(formatTime(timeRemaining)));
        }
    }

    private String formatTime(long millisUntilFinished) {
        int totalSecondsRemaining = (int) (millisUntilFinished / 1000);
        int minutes =  totalSecondsRemaining / 60;
        int seconds = totalSecondsRemaining % 60;
        String result = String.format("%d : %d", minutes, seconds);
        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("MainActivity", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putLong("timeRemaining", mClockService.getTimeRemaining());
        outState.putBoolean("isPaused", mClockService.isPaused());
        outState.putInt("pomodoroCount", mState.getCompletedPomodoro());


    }

    public void restore(Bundle savedInstanceState) {
        Log.d("MainActivity", "onRestore");
        boolean isPaused = savedInstanceState.getBoolean("isPaused");
        long timeRemaining = savedInstanceState.getLong("timeRemaining");
        int pomodoroCompletedCount = savedInstanceState.getInt("pomodoroCount");
        mCountTextView.setText(pomodoroCompletedCount);
        if (isPaused) {
            mStartButton.setText("start");
            mClockTextView.setText(formatTime(timeRemaining));
        } else {
            mStartButton.setText("pause");
            mClockTextView.setText(formatTime(mClockService.getTimeRemaining()));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClockService.stopSelf();
        unbindService(mConnection);
    }
}