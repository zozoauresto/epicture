package fr.epicture.epicture.api.flickr.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import fr.epicture.epicture.api.flickr.FlickrAccount;
import fr.epicture.epicture.api.flickr.FlickrClient;
import fr.epicture.epicture.api.flickr.utils.FlickrUtils;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.requests.TextRequest;
import fr.epicture.epicture.utils.RequestIdentifierGenerator;
import fr.epicture.epicture.utils.StaticTools;

public class GetCommentsRequest extends TextRequest {

    private static final String URL = "https://www.flickr.com/services/rest";
    private static final String METHOD = "flickr.photos.comments.getList";

    private FlickrAccount user;
    private String photoid;

    public GetCommentsRequest(@NonNull Context context, FlickrAccount user, String photoid, LoadTextInterface listener) {
        super(context, listener);/*URL + "?nojsoncallback=1"
                + "&format=json"
                + "&api_key=" + FlickrClient.CONSUMER_KEY
                + "&photo_id=" + photoid
                + "&method=" + METHOD,
                listener);*/

        this.user = user;
        this.photoid = photoid;

        try {
            setUrl(getURL());
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getURL() throws Exception {
        String part1 = "GET";
        String part2 = URL;

        String random = RequestIdentifierGenerator.Generate();
        long unixTime = StaticTools.GetCurrentUnixTime();

        String[] params = new String[] {
                "nojsoncallback=1",
                "format=json",
                "oauth_nonce=" + random,
                "oauth_consumer_key=" + FlickrClient.CONSUMER_KEY,
                "oauth_timestamp=" + unixTime,
                "oauth_signature_method=HMAC-SHA1",
                "oauth_token=" + user.token,
                "method=" + METHOD,
                "user_id=" + user.id,
                "photo_id=" + photoid
        };

        return URL + "?nojsoncallback=1"
                + "&format=json"
                + "&oauth_nonce=" + random
                + "&oauth_consumer_key=" + FlickrClient.CONSUMER_KEY
                + "&oauth_timestamp=" + unixTime
                + "&oauth_signature_method=HMAC-SHA1"
                + "&oauth_token=" + user.token
                + "&oauth_signature=" + FlickrUtils.getURLSignature(part1, part2, params, user.tokenSecret)
                + "&method=" + METHOD
                + "&user_id=" + user.id
                + "&photo_id=" + photoid;
    }
}
