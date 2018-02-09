package fr.epicture.epicture.api.flickr.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import fr.epicture.epicture.api.flickr.FlickrClient;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.requests.TextRequest;

public class InterestingnessRequest extends TextRequest {

    private static final String URL = "https://www.flickr.com/services/rest";
    private static final String METHOD = "flickr.interestingness.getList";

    public InterestingnessRequest(@NonNull Context context, int page, LoadTextInterface listener) {
        super(context,
                URL + "?nojsoncallback=1"
                + "&format=json"
                + "&page=" + page
                + "&api_key=" + FlickrClient.CONSUMER_KEY
                + "&method=" + METHOD
                + "&per_page=20"
                + "&extras=description,date_upload,tags,owner_name",
                listener);
        execute();
    }
}
