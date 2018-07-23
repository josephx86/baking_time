package io.github.josephx86.bakingtime;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class InstructionsFragment extends Fragment {
    private FragmentParent parent;
    private String instructions = "", title = "";
    private boolean showPlayer = false;
    private String videoUrl = "", thumbnailUrl = "";
    private SimpleExoPlayer player;
    private int playerWin;
    private long playerPos;
    private boolean playWhenReady = true; // Autoplay by default.
    private boolean showNextButton = false;

    @BindView(R.id.instructions_tv)
    TextView instructionsTextView;

    @BindView(R.id.title_tv)
    TextView titleTextView;

    @BindView(R.id.exoplayer_ui)
    PlayerView playerView;

    @BindView(R.id.root)
    ScrollView rootView;

    @BindView(R.id.next_b)
    Button nextButton;


    public InstructionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_instructions, container, false);
        ButterKnife.bind(this, view);
        setInstructionsa(instructions);
        setTitle(title);
        setPlayerVisible(showPlayer, videoUrl, thumbnailUrl, playerPos, playerWin, playWhenReady);
        showNextButton(showNextButton);
        return view;
    }

    private void setupPlayer() {

        // Delete old player
        releasePlayer(false);

        if (videoUrl.isEmpty()) {
            return;
        }
        if (playerView != null) {
            Context context = playerView.getContext();

            // Set thumbnail
            Bitmap thumbnailBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_circle_64dp);
            playerView.setDefaultArtwork(thumbnailBitmap);

            // Media
            RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
            TrackSelector selector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, selector, loadControl);
            player.addListener(new PlayerEventListener(playerView));

            PlayerActivity parent = (PlayerActivity) getActivity();
            if (parent != null) {
                player.addVideoListener(new VideoEventListener(playerView, parent, parent instanceof InstructionsActivity));
            }

            playerView.setPlayer(player);
            String userAgent = context.getString(R.string.app_name);
            DataSource.Factory dataSource = new DefaultDataSourceFactory(context, userAgent);
            ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(dataSource);
            Uri videoUri = Uri.parse(videoUrl);
            ExtractorMediaSource mediaSource = factory.createMediaSource(videoUri);
            player.prepare(mediaSource);
            if (playerPos > 0) {
                player.seekTo(playerWin, playerPos);
            }

            player.setPlayWhenReady(playWhenReady);

            // Set full screen in landscape mode and NOT 2-pane mode
            boolean isTwoPaneMode = getResources().getBoolean(R.bool.two_panes);
            if (!isTwoPaneMode) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Resources resources = playerView.getContext().getResources();
                    DisplayMetrics metrics = resources.getDisplayMetrics();
                    playerView.getLayoutParams().width = metrics.widthPixels;
                    playerView.getLayoutParams().height = metrics.heightPixels;
                }
            }
        }
    }

    public void setTitle(String s) {
        if (s == null) {
            s = "";
        }
        title = s.trim();

        // If textview is null, the text will be set when onCreateView is called.
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer(true);
    }

    public void releasePlayer(boolean save) {
        if (player != null) {
            // Save player position first
            PlayerActivity parent = (PlayerActivity) getActivity();
            if ((parent != null) && save) {
                parent.setPlayerPosition(player.getCurrentPosition(), player.getCurrentWindowIndex(), player.getPlayWhenReady());
            }

            player.stop();
            player.release();
            player = null;
        }
    }

    public void setInstructionsa(String s) {
        if (s == null) {
            s = "";
        }
        instructions = s.trim();

        // If textview is null, the text will be set when onCreateView is called.
        if (instructionsTextView != null) {
            instructionsTextView.setText(instructions);
        }
    }

    public void setParent(FragmentParent instructionsFragmentParent) {
        parent = instructionsFragmentParent;
    }

    @OnClick(R.id.next_b)
    public void nextStepButton_Click(View view) {
        if (parent != null) {
            parent.moveNext();
        }
    }

    public void showNextButton(boolean b) {
        showNextButton = b;
        if (nextButton != null) {
            if (showNextButton) {
                nextButton.setVisibility(View.VISIBLE);
            } else {
                nextButton.setVisibility(View.GONE);
            }
        }
    }

    public void setPlayerVisible(boolean b, String video, String thumbnail, long playerPosition, int playerWindow, boolean autoplay) {
        showPlayer = b;
        videoUrl = video;
        thumbnailUrl = thumbnail;
        playerPos = playerPosition;
        playerWin = playerWindow;
        playWhenReady = autoplay;
        if (playerView != null) {
            if (showPlayer) {
                playerView.setVisibility(View.VISIBLE);
                setupPlayer();
            } else {
                playerView.setVisibility(View.GONE);
            }
        }
    }
}
