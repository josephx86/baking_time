package io.github.josephx86.bakingtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {
    protected int currentStep = 0;
    protected long playerPosition = 0;
    protected int playerWindow = 0;
    protected boolean playWhenReady = true; // Auto play by default.

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Recipe will be resubmitted with intent when activity is started.
        // However, current step may change as user navigates through the steps.
        // Save current step
        String key = getString(R.string.step_number_key);
        outState.putInt(key, currentStep);

        // Save video position
        String windowKey = getString(R.string.player_window_key);
        outState.putInt(windowKey, playerWindow);
        String positionKey = getString(R.string.player_position_key);
        outState.putLong(positionKey, playerPosition);
        String playwhenReadyKey = getString(R.string.player_play_when_ready_key);
        outState.putBoolean(playwhenReadyKey, playWhenReady);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String key = getString(R.string.step_number_key);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(key)) {
                currentStep = savedInstanceState.getInt(key);
            }
            String playwhenReadyKey = getString(R.string.player_play_when_ready_key);
            if (savedInstanceState.containsKey(playwhenReadyKey)) {
                playWhenReady = savedInstanceState.getBoolean(playwhenReadyKey);
            }
            String positionKey = getString(R.string.player_position_key);
            if (savedInstanceState.containsKey(positionKey)) {
                playerPosition = savedInstanceState.getLong(positionKey);
            }
            String windowKey = getString(R.string.player_window_key);
            if (savedInstanceState.containsKey(windowKey)) {
                playerWindow = savedInstanceState.getInt(windowKey);
            }
        }
    }

    public void setPlayerPosition(long position, int window, boolean autoPlay) {
        playerPosition = position;
        playerWindow = window;
        playWhenReady = autoPlay;
    }
}
