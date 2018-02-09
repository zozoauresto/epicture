package fr.epicture.epicture.api.flickr.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import fr.epicture.epicture.api.flickr.FlickrAccount;
import fr.epicture.epicture.api.flickr.FlickrClient;
import fr.epicture.epicture.api.flickr.utils.FlickrUtils;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.requests.TextRequest;
import fr.epicture.epicture.utils.RequestIdentifierGenerator;
import fr.epicture.epicture.utils.StaticTools;

public class SearchRequest extends TextRequest {

    private static final String URL = "https://www.flickr.com/services/rest";
    private static final String METHOD = "flickr.photos.search";

    private FlickrAccount user;
    private String search;
    private int page;

    public SearchRequest(@NonNull Context context, FlickrAccount user, String search, int page, LoadTextInterface listener) {
        super(context, listener);

        this.user = user;
        this.search = search;
        this.page = page;

        try {
            setUrl(getURL());
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *  URL + "?nojsoncallback=1"
     + "&format=json"
     + "&text=" + search
     + "&user_id=" + user.nsid
     + "&page=" + page
     + "&per_page=20"
     + "&api_key=" + FlickrClient.CONSUMER_KEY
     + "&method=" + METHOD
     */

    private String getURL() throws Exception {
        String part1 = "GET";
        String part2 = URL;

        String random = RequestIdentifierGenerator.Generate();
        long unixTime = StaticTools.GetCurrentUnixTime();

        List<String> list = new ArrayList<>();
        if (user != null) {
            list.add("oauth_nonce=" + random);
            list.add("oauth_consumer_key=" + FlickrClient.CONSUMER_KEY);
            list.add("oauth_timestamp=" + unixTime);
            list.add("oauth_signature_method=HMAC-SHA1");
            list.add("oauth_token=" + user.token);
            list.add("user_id=" + user.id);
            list.add("nojsoncallback=1");
            list.add("format=json");
            list.add("method=" + METHOD);
            list.add("page=" + page);
            list.add("per_page=20");
            list.add("text=" + search);
            list.add("extras=description,date_upload,tags,owner_name");
            list.add("api_key=" + FlickrClient.CONSUMER_KEY);
        }

        String ret = URL
                + "?nojsoncallback=1"
                + "&api_key=" + FlickrClient.CONSUMER_KEY
                + "&format=json"
                + "&text=" + URLEncoder.encode(search, "UTF-8")
                + "&method=" + METHOD
                + "&page=" + page
                + "&extras=description,date_upload,tags,owner_name"
                + "&per_page=20";

        if (user != null) {
            String[] array = new String[list.size()];
            array = list.toArray(array);
            ret += "&oauth_nonce=" + random
                    + "&oauth_consumer_key=" + FlickrClient.CONSUMER_KEY
                    + "&oauth_timestamp=" + unixTime
                    + "&oauth_signature_method=HMAC-SHA1"
                    + "&oauth_token=" + user.token
                    + "&oauth_signature=" + FlickrUtils.getURLSignature(part1, part2, array, user.tokenSecret)
                    + "&user_id=" + user.id;
        }
        return ret;
    }

}
