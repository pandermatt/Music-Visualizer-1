package com.MVisualizer;

import com.google.gson.Gson;
import com.soundcloud.api.ApiWrapper;
import de.voidplus.soundcloud.SoundCloud;
import de.voidplus.soundcloud.Track;
import org.json.simple.parser.JSONParser;
import java.io.IOException;

public class ISoundCloud extends SoundCloud {
    public ISoundCloud(String _app_client_id, String _app_client_secret, String _login_name, String _login_password) {
        super(_app_client_id, _app_client_secret, _login_name, _login_password);
    }

    public long getTrackID(String url) throws IOException {return wrapper.resolve(url);}
    public Track getTrackFromURL(String url) throws IOException {return getTrack((int) getTrackID(url));}

    public ApiWrapper getApiWrapper(){return wrapper;}
    public JSONParser getJSONParser(){return parser;}
    public Gson getGson(){return gson;}
}
