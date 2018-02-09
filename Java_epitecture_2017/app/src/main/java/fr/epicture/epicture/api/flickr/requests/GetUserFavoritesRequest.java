package fr.epicture.epicture.api.flickr.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import java.net.URLEncoder;

import fr.epicture.epicture.api.flickr.FlickrClient;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.requests.TextRequest;

public class GetUserFavoritesRequest extends TextRequest {

    private static final String URL = "https://www.flickr.com/services/rest";
    private static final String METHOD = "flickr.favorites.getList";

    public GetUserFavoritesRequest(@NonNull Context context, String userid, int page, LoadTextInterface listener) {
        super(context, listener);
        try {
            setUrl(URL + "?nojsoncallback=1"
                    + "&format=json"
                    + "&api_key=" + FlickrClient.CONSUMER_KEY
                    + "&user_id=" + URLEncoder.encode(userid, "UTF-8")
                    + "&method=" + METHOD
                    + "&page=" + page
                    + "&per_page=20"
                    + "&extras=description,date_upload,tags,owner_name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        execute();
    }
}
