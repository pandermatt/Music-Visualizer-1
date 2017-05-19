package com.MVisualizer;

import com.soundcloud.api.ApiWrapper;
import de.voidplus.soundcloud.SoundCloud;
import de.voidplus.soundcloud.Track;

import java.io.IOException;

public class SoundCloudWrapper {
    private SoundCloud soundCloudSource = null;
    private ApiWrapper apiWrapper = null;

    public SoundCloudWrapper(String clientID, String clientSecret, String username, String pass) throws IOException {
        soundCloudSource = new SoundCloud(clientID, clientSecret, username, pass);
        apiWrapper = new ApiWrapper(clientID, clientSecret, null, null);
        apiWrapper.login(username, pass);
    }

    public long getTrackID(String url) throws IOException {
        return apiWrapper.resolve(url);
    }

    public Track getTrackFromURL(String url) throws IOException {
        return soundCloudSource.getTrack((int) getTrackID(url));
    }
}
