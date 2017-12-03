package com.pam.studybuddy.State;

/**
 * Created by Pam on 12/3/2017.
 */

import com.pam.studybuddy.MainActivity;

import java.util.Arrays;

public abstract class State {
    protected static int completedPomodoroCount = 0;
    protected String stateName;
    protected int duration = 0;
    protected boolean isBreak = false;

    public boolean isBreak() {
        return isBreak;
    }

    public static final String POMODORO = "Pomodoro";
    public static final String LONG_BREAK = "Long break";
    public static final String SHORT_BREAK = "Short break";

    protected String[] states = new String[] {POMODORO, SHORT_BREAK, LONG_BREAK};

    public String getStateName() {
        return this.stateName;
    }
    public abstract void next(MainActivity context);

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public int getCompletedPomodoro() {
        return completedPomodoroCount;
    }

    public void setCompletedPomodoro(int completedPomodoro) {
        this.completedPomodoroCount = completedPomodoro;
    }

    public static State getStateFromName(String stateName) {
        State result = null;
        switch (stateName) {
            case POMODORO:
                result = PomodoroState.getInstance();
                break;
            case SHORT_BREAK:
                result = ShortBreakState.getInstance();
                break;
            case LONG_BREAK:
                result = LongBreakState.getInstance();
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;

        State state = (State) o;

        if (completedPomodoroCount != state.completedPomodoroCount) return false;
        if (duration != state.duration) return false;
        if (isBreak != state.isBreak) return false;
        if (!stateName.equals(state.stateName)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(states, state.states);

    }

    @Override
    public int hashCode() {
        int result = completedPomodoroCount;
        result = 31 * result + stateName.hashCode();
        result = 31 * result + duration;
        result = 31 * result + (isBreak ? 1 : 0);
        result = 31 * result + Arrays.hashCode(states);
        return result;
    }
}