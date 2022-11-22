package com.cinemamod.api.theater;

import com.cinemamod.api.PlayingVideo;
import com.cinemamod.api.TheaterPlayer;
import com.cinemamod.api.VideoQueue;

import java.util.Set;

public interface Theater {

    String getId();

    String getHumanName();

    PlayingVideo getVideo();

    Set<Screen> getScreens();

    Set<Screen> getPreviewScreens();

    VideoQueue getQueue();

    Set<TheaterPlayer> getViewers();

    boolean isViewer(TheaterPlayer player);

    Set<TheaterPlayer> getVoteSkips();

    default int getRequiredVoteSkipCount() {
        if (getViewers().size() < 2) return 1;
        else if (getViewers().size() == 2) return 2;
        return (int) ((2D / 3D) * getViewers().size());
    }

    void reset();

}
