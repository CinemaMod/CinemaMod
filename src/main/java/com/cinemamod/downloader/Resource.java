package com.cinemamod.downloader;

public final class Resource {

    public static final String CINEMAMOD_VERSIONS_URL = "https://cinemamod-libraries.ewr1.vultrobjects.com/versions.txt";
    public static final String CINEMAMOD_JCEF_URL_FORMAT = "https://ewr1.vultrobjects.com/cinemamod-libraries/jcef/%s/%s";

    public static String getCinemamodJcefUrl(String cefBranch, String platform) {
        return CINEMAMOD_JCEF_URL_FORMAT.formatted(cefBranch, platform);
    }

}
