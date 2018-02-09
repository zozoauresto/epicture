package fr.epicture.epicture.api.flickr.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import fr.epicture.epicture.api.flickr.FlickrClient;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.requests.TextRequest;

public class PhotoIsInFavoritesRequest extends TextRequest {

    private static final String URL = "https://www.flickr.com/services/rest";
    private static final String METHOD = "flickr.favorites.getContext";

    public PhotoIsInFavoritesRequest(@NonNull Context context, String userid, String photoid, LoadTextInterface listener) {
        super(context,
                URL + "?nojsoncallback=1"
                + "&format=json"
                + "&api_key=" + FlickrClient.CONSUMER_KEY
                + "&method=" + METHOD
                + "&user_id=" + userid
                + "&photo_id=" + photoid
                , listener);
        execute();
    }

}
