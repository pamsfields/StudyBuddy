package com.pam.studybuddy.State;

import com.pam.studybuddy.MainActivity;

/**
 * Created by Pam on 12/3/2017.
 */


public class LongBreakState extends State {

    private LongBreakState() {
        this.stateName = states[2];
        this.duration = 15;
        this.isBreak = true;
    }
    private static LongBreakState INSTANCE;
    public static State getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LongBreakState();
        }
        return INSTANCE;
    }


    @Override
    public void next(MainActivity context) {
        context.setState(PomodoroState.getInstance());
    }

    @Override
    public String toString() {
        return "LongBreakState{}";
    }


}
