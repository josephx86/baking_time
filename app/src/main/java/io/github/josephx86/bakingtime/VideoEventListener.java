package io.github.josephx86.bakingtime;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.exoplayer2.ui.PlayerView;

public class VideoEventListener implements com.google.android.exoplayer2.video.VideoListener {
    private PlayerView playerView;
    private Activity activity;
    private boolean playingInInstructionsActivity;

    VideoEventListener(PlayerView playerView, Activity activity, boolean fromInstructionsActivity) {
        this.playerView = playerView;
        this.activity = activity;
        playingInInstructionsActivity = fromInstructionsActivity;
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if ((playerView == null) || (activity == null)) {
            return;
        }


        // Full screen video in landscape but NOT 2-pane mode
        // Note that some phones with big screens will trigger two-pane mode.
        // Show full-screen as long as video is being viewed from a InstructionsActivity.
        // InstructionsActivity is used for devices than use a single pane in portrait mode.
        // A device might switch to 2 pane mode when switched to landscape mode, resulting in a
        // video that is not full-screen, when  fullscreen was actually expected on the device.
        Context context = playerView.getContext();
        boolean twoPaneMode = context.getResources().getBoolean(R.bool.two_panes);
        if (playingInInstructionsActivity) {
            // To enable scrolling to down to view instructions, just set the player view to be the
            // same size as the parent scrollview (scrollview covers entire viewport i.e kind of full screen.
            // An alternative is to set the player view to be full screen but that means scrolling the rest of the
            // UI become impossible.
            int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ViewGroup.LayoutParams playerViewLayoutParams = playerView.getLayoutParams();
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (windowManager != null) {
                    // Don't show the notification bar.
                    View decorView = activity.getWindow().getDecorView();
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

                    // Resize the player
                    Display defaultDisplay = windowManager.getDefaultDisplay();
                    Point displaySize = new Point();
                    defaultDisplay.getSize(displaySize);
                    playerViewLayoutParams.height = displaySize.y;
                    playerViewLayoutParams.width = displaySize.x;
                    playerView.setLayoutParams(playerViewLayoutParams);
                }
            }
        } else if (twoPaneMode) {
            // In 2 pane mode, the detail pane is 2/3 of the width of the screen.
            // Use 60% of the width. the other 6.66% will compensate margins and padding.
            // Adjust player size to fit by width of the detail pane side.
            ViewGroup.LayoutParams playerViewLayoutParams = playerView.getLayoutParams();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                // Resize the player
                Display defaultDisplay = windowManager.getDefaultDisplay();
                Point displaySize = new Point();
                defaultDisplay.getSize(displaySize);
                double deviceWidth = displaySize.x;
                double detailPanewidth = deviceWidth * 2.0 / 3.0;
                double desiredVideoWidth = detailPanewidth;

                // Adjust width for margins
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) playerViewLayoutParams;
                if (marginLayoutParams != null) {
                    desiredVideoWidth = detailPanewidth - marginLayoutParams.leftMargin - marginLayoutParams.rightMargin;

                    // The fragment parent also has margins.
                    float parentMargins = context.getResources().getDimension(R.dimen.two_pane_margin) * 2;
                    desiredVideoWidth -= parentMargins;
                }
                double desiredVideoHeight = desiredVideoWidth / width * height;
                playerViewLayoutParams.height = (int) desiredVideoHeight;
                playerViewLayoutParams.width = (int) desiredVideoWidth;
                playerView.setLayoutParams(playerViewLayoutParams);
            }
        } else {

        }
    }

    @Override
    public void onRenderedFirstFrame() {

    }
}
