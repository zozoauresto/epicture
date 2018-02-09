package fr.epicture.epicture.api.flickr.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import fr.epicture.epicture.api.flickr.FlickrClient;
import fr.epicture.epicture.api.flickr.modele.TokenRequest;
import fr.epicture.epicture.api.flickr.utils.FlickrUtils;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.requests.TextRequest;
import fr.epicture.epicture.utils.RequestIdentifierGenerator;
import fr.epicture.epicture.utils.StaticTools;

public class GetAccessTokenRequest extends TextRequest {

    private static final String BASE_URL = "https://www.flickr.com/services";
    private static final String URL = "/oauth/access_token";

    private TokenRequest tokenRequest;

    public GetAccessTokenRequest(@NonNull Context context, TokenRequest tokenRequest, LoadTextInterface listener) {
        super(context, listener);
        this.tokenRequest = tokenRequest;

        try {
            setUrl(getURL());
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getURL() throws Exception {
        String part1 = "GET";
        String part2 = BASE_URL + URL;

        String random = RequestIdentifierGenerator.Generate();
        long unixTime = StaticTools.GetCurrentUnixTime();

        String[] params = new String[] {
                "oauth_nonce=" + random,
                "oauth_timestamp=" + unixTime,
                "oauth_verifier=" + tokenRequest.verifier,
                "oauth_consumer_key=" + FlickrClient.CONSUMER_KEY,
                "oauth_signature_method=HMAC-SHA1",
                "oauth_token=" + tokenRequest.token
        };

        return BASE_URL + URL + "?oauth_nonce=" + random
                + "&oauth_timestamp=" + unixTime
                + "&oauth_verifier=" + tokenRequest.verifier
                + "&oauth_consumer_key=" + FlickrClient.CONSUMER_KEY
                + "&oauth_signature_method=HMAC-SHA1"
                + "&oauth_token=" + tokenRequest.token
                + "&oauth_signature=" + FlickrUtils.getURLSignature(part1, part2, params, tokenRequest.tokenSecret);
    }



}
