package com.pam.studybuddy.State;

import com.pam.studybuddy.MainActivity;
/**
 * Created by Pam on 12/3/2017.
 */

public class PomodoroState extends State {

    private PomodoroState() {
        this.stateName = states[0];
        this.duration = 25;
        this.isBreak = false;
    }
    private static PomodoroState INSTANCE;
    public static State getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PomodoroState();
        }
        return INSTANCE;
    }


    @Override
    public void next(MainActivity context) {
        this.completedPomodoroCount++;
        if (completedPomodoroCount % 4 == 0) {
            context.setState(LongBreakState.getInstance());
        } else {
            context.setState(ShortBreakState.getInstance());
        }

    }

    @Override
    public String toString() {
        return "PomodoroState{}";
    }
}
