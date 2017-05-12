package com.MVisualizer;

import com.soundcloud.api.ApiWrapper;
import de.voidplus.soundcloud.SoundCloud;
import de.voidplus.soundcloud.Track;

import java.io.IOException;

public class SoundCloudWrapper {
    private SoundCloud soundCloudSource = null;
    private ApiWrapper apiWrapper = null;
    private StringBuilder clientID, clientSecret;

    public SoundCloudWrapper(StringBuilder clientID, StringBuilder clientSecret, String username, String pass) throws IOException {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        soundCloudSource = new SoundCloud(this.clientID.toString(), this.clientSecret.toString(), username, pass);
        apiWrapper = new ApiWrapper(this.clientID.toString(), this.clientSecret.toString(), null, null);
        apiWrapper.login(username, pass);
    }

    public long getTrackID(String url) throws IOException {
        return apiWrapper.resolve(url);
    }

    public Track getTrackFromURL(String url) throws IOException {
        return soundCloudSource.getTrack((int) getTrackID(url));
    }
}
