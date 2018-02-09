package fr.epicture.epicture.api.flickr.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import fr.epicture.epicture.api.flickr.FlickrClient;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.requests.TextRequest;

public class UserInformationRequest extends TextRequest {

    private static final String URL = "https://www.flickr.com/services/rest";
    private static final String METHOD = "flickr.people.getInfo";

    public UserInformationRequest(@NonNull Context context, String userID, LoadTextInterface listener) {
        super(context,
                URL + "?nojsoncallback=1"
                + "&format=json"
                + "&api_key=" + FlickrClient.CONSUMER_KEY
                + "&user_id=" + userID
                + "&method=" + METHOD
                , listener);
        execute();
    }
}
