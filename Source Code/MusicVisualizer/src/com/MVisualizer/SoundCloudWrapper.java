package com.MVisualizer;

import com.soundcloud.api.ApiWrapper;
import de.voidplus.soundcloud.SoundCloud;
import de.voidplus.soundcloud.Track;

import java.io.IOException;

public class SoundCloudWrapper {
    private SoundCloud soundCloudSource = null;
    private ApiWrapper apiWrapper = null;
    private String clientID, clientSecret;

    public SoundCloudWrapper(String clientID, String clientSecret, String username, String pass) throws IOException {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        soundCloudSource = new SoundCloud(this.clientID, this.clientSecret, username, pass);
        apiWrapper = new ApiWrapper(this.clientID, this.clientSecret, null, null);
        apiWrapper.login(username, pass);
    }

    public long getTrackID(String url) throws IOException {
        return apiWrapper.resolve(url);
    }

    public Track getTrackFromURL(String url) throws IOException {
        return soundCloudSource.getTrack((int) getTrackID(url));
    }
}
