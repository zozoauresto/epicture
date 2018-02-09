package fr.epicture.epicture.api.flickr.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import java.net.URLEncoder;

import fr.epicture.epicture.api.flickr.FlickrAccount;
import fr.epicture.epicture.api.flickr.FlickrClient;
import fr.epicture.epicture.api.flickr.utils.FlickrUtils;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.requests.TextPostRequest;
import fr.epicture.epicture.utils.RequestIdentifierGenerator;
import fr.epicture.epicture.utils.StaticTools;

public class AddFavoriteRequest extends TextPostRequest {

    private static final String URL = "https://www.flickr.com/services/rest";
    private static final String METHOD = "flickr.favorites.add";

    private FlickrAccount user;
    private String photoid;

    public AddFavoriteRequest(@NonNull Context context, FlickrAccount user, String photoid, LoadTextInterface listener) {
        super(context, URL, listener);

        this.user = user;
        this.photoid = photoid;

        addHeader();
        addParam();

        execute();
    }

    private void addHeader() {
        String random = RequestIdentifierGenerator.Generate();
        long unixTime = StaticTools.GetCurrentUnixTime();

        try {
            addHeader("Authorization", "OAuth oauth_nonce=\"" + random + "\""
                    + ",oauth_timestamp=\"" + unixTime + "\""
                    + ",oauth_consumer_key=\"" + FlickrClient.CONSUMER_KEY + "\""
                    + ",oauth_signature_method=\"HMAC-SHA1\""
                    + ",oauth_signature=\"" + URLEncoder.encode(getSignature(random, unixTime), "UTF-8") + "\""
                    + ",oauth_token=\"" + user.token + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addParam() {
        addParam(new ParamBody("nojsoncallback", "1"));
        addParam(new ParamBody("format", "json"));
        addParam(new ParamBody("method", METHOD));
        addParam(new ParamBody("photo_id", photoid));
    }

    private String getSignature(String random, long unixTime) {
        String part1 = "POST";
        String part2 = URL;

        String[] params = new String[] {
                "oauth_nonce=" + random,
                "oauth_consumer_key=" + FlickrClient.CONSUMER_KEY,
                "oauth_timestamp=" + unixTime,
                "oauth_signature_method=HMAC-SHA1",
                "oauth_token=" + user.token,
                "method=" + METHOD,
                "photo_id=" + photoid,
                "nojsoncallback=1",
                "format=json"
        };

        return FlickrUtils.getPOSTSignature(part1, part2, params, user.tokenSecret);
    }
}
