package mygames.lineball.GameLogic;

import android.os.CountDownTimer;

public class RoundTimer {

    private CountDownTimer timer;

    private final int ONE_SECOND   = 1000;
    private String timeLeft;
    private BallTracker ballTracker;

    public RoundTimer(final BallTracker ballTracker, int initialRoundTIme) {
        this.ballTracker = ballTracker;
        setTimer(initialRoundTIme);
    }

    public void setTimer(final int roundTime) {
        timer = new CountDownTimer(roundTime, ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                int remainingTime = (int) Math.floor(millisUntilFinished / ONE_SECOND);
                timeLeft = String.valueOf(remainingTime);
            }

            @Override
            public void onFinish() {
                timeLeft = "0";
                ballTracker.timeOut();
            }
        };
        timer.start();
    }

    public String getTimeLeft() {
        return timeLeft;
    }
}

