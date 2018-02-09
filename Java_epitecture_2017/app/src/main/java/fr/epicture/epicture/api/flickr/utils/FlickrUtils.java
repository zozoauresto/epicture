package fr.epicture.epicture.api.flickr.utils;

import android.text.TextUtils;
import android.util.Log;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.epicture.epicture.api.flickr.FlickrClient;
import fr.epicture.epicture.api.flickr.modele.TokenAccess;
import fr.epicture.epicture.api.flickr.modele.TokenRequest;
import fr.epicture.epicture.utils.StaticTools;

public class FlickrUtils {

    public static TokenRequest retrieveTokenRequest(String response) throws Exception {
        TokenRequest tokenRequest = new TokenRequest();
        String[] datas = response.split("&");

        if (datas.length == 3) {
            tokenRequest.callbackConfirmed = datas[0].substring(datas[0].indexOf('=') + 1).equals("true");
            tokenRequest.token = datas[1].substring(datas[1].indexOf('=') + 1);
            tokenRequest.tokenSecret = datas[2].substring(datas[2].indexOf('=') + 1);
        }

        Log.i("token request", tokenRequest.callbackConfirmed + " " + tokenRequest.token + " " + tokenRequest.tokenSecret);

        return tokenRequest;
    }

    public static TokenAccess retrieveTokenAccess(String response) throws Exception {
        TokenAccess tokenAccess = new TokenAccess();
        String[] datas = response.split("&");
        if (datas.length == 5) {
            tokenAccess.fullname = URLDecoder.decode(datas[0].substring(datas[0].indexOf('=') + 1), "UTF-8");
            tokenAccess.token = datas[1].substring(datas[1].indexOf('=') + 1);
            tokenAccess.tokenSecret = datas[2].substring(datas[2].indexOf('=') + 1);
            tokenAccess.nsid = datas[3].substring(datas[3].indexOf('=') + 1);
            tokenAccess.username = URLDecoder.decode(datas[4].substring(datas[4].indexOf('=') + 1), "UTF-8");
        }
        return tokenAccess;
    }

    public static String getURLSignature(String method, String url, String[] params, String tokenSecret) {
        List<String> encodedParams = new ArrayList<>();
        String signature = "";

        try {
            Arrays.sort(params);

            for (int i = 0; i < params.length; i++) {
                String name = params[i].substring(0, params[i].indexOf('='));
                String data = params[i].substring(params[i].indexOf('=') + 1);
                String encoded = StaticTools.OAuthEncode(name + "=") + StaticTools.OAuthEncode(StaticTools.OAuthEncode(data));
                if (i < params.length - 1) {
                    encoded += StaticTools.OAuthEncode("&");
                }
                encodedParams.add(encoded);
            }

            String part1Encoded = StaticTools.OAuthEncode(method);
            String part2Encoded = StaticTools.OAuthEncode(url);
            String part3Encoded = TextUtils.join("", encodedParams);
            String encoded = part1Encoded + "&" + part2Encoded + "&" + part3Encoded;

            Log.i("encodedsignature", encoded);

            signature = StaticTools.OAuthEncode(StaticTools.hmacsha1(encoded, FlickrClient.CONSUMER_SECRET + "&" + tokenSecret));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    public static String getPOSTSignature(String method, String url, String[] params, String tokenSecret) {
        List<String> encodedParams = new ArrayList<>();
        String signature = "";

        try {
            Arrays.sort(params);

            for (int i = 0; i < params.length; i++) {
                String name = params[i].substring(0, params[i].indexOf('='));
                String data = params[i].substring(params[i].indexOf('=') + 1);
                String encoded = StaticTools.OAuthEncode(name + "=") + StaticTools.OAuthEncode(StaticTools.OAuthEncode(data));
                if (i < params.length - 1) {
                    encoded += StaticTools.OAuthEncode("&");
                }
                encodedParams.add(encoded);
            }

            String part1Encoded = StaticTools.OAuthEncode(method);
            String part2Encoded = StaticTools.OAuthEncode(url);
            String part3Encoded = TextUtils.join("", encodedParams);
            String encoded = part1Encoded + "&" + part2Encoded + "&" + part3Encoded;

            Log.i("encodedsignature", encoded);

            signature = StaticTools.hmacsha1(encoded, FlickrClient.CONSUMER_SECRET + "&" + tokenSecret);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

}
