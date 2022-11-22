package com.cinemamod.api;

public interface VideoInfo {

    String getKey();

    String getTitle();

    String getRequesterUsername();

    String getThumbnailUrl();

    long getDurationSeconds();

}
