package io.github.josephx86.bakingtime;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class InstructionsFragment extends Fragment {
    private FragmentParent parent;
    private String instructions = "", title = "";
    private boolean showPlayer = false;
    private String videoUrl = "";
    private ExoPlayer player;

    @BindView(R.id.instructions_tv)
    TextView instructionsTextView;

    @BindView(R.id.title_tv)
    TextView titleTextView;

    @BindView(R.id.exoplayer_ui)
    PlayerView playerView;

    @BindView(R.id.root)
    ScrollView rootView;

    public InstructionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_instructions, container, false);
        ButterKnife.bind(this, view);
        setInstructionss(instructions);
        setTitle(title);
        setPlayerVisible(showPlayer, videoUrl);
        return view;
    }

    private void setupPlayer() {
        if (videoUrl.isEmpty()) {
            return;
        }
        if (playerView != null) {
            // Media
            Context context = playerView.getContext();
            RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
            TrackSelector selector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, selector, loadControl);
            playerView.setPlayer(player);
            String userAgent = context.getString(R.string.app_name);
            DataSource.Factory dataSource = new DefaultDataSourceFactory(context, userAgent);
            ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(dataSource);
            Uri videoUri = Uri.parse(videoUrl);
            ExtractorMediaSource mediaSource = factory.createMediaSource(videoUri);
            player.prepare(mediaSource);
            player.setPlayWhenReady(true);

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
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public void setInstructionss(String s) {
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

    public void moveNext() {
        if (parent != null) {
            parent.moveNext();
        }
    }

    public void setPlayerVisible(boolean b, String video) {
        showPlayer = b;
        videoUrl = video;
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
