package io.github.josephx86.bakingtime;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerEventListener implements Player.EventListener {

    @BindView(R.id.exo_buffering)
    ProgressBar progressBar;

    @BindView(R.id.exo_error_message)
    TextView statusTextview;

    PlayerEventListener(PlayerView view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case Player.STATE_BUFFERING:
                progressBar.setVisibility(View.VISIBLE);
                statusTextview.setVisibility(View.VISIBLE);
                statusTextview.setText(R.string.buffering);
                break;

            case Player.STATE_ENDED:
            case Player.STATE_IDLE:
            case Player.STATE_READY:
            default:
                progressBar.setVisibility(View.GONE);
                statusTextview.setVisibility(View.GONE);
                statusTextview.setText("");
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
