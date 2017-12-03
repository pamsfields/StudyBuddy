package com.pam.studybuddy.State;


import com.pam.studybuddy.MainActivity;
/**
 * Created by Pam on 12/3/2017.
 */

public class ShortBreakState extends State {

    private ShortBreakState() {
        this.stateName = states[1];
        this.duration = 5;
        this.isBreak = true;
    }
    private static ShortBreakState INSTANCE;
    public static State getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShortBreakState();
        }
        return INSTANCE;
    }


    @Override
    public void next(MainActivity context) {
        context.setState(PomodoroState.getInstance());
    }

    @Override
    public String toString() {
        return "ShortBreakState{}";
    }
}