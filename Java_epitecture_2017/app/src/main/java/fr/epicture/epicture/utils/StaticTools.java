package fr.epicture.epicture.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class StaticTools {

    public static int GetCurrentUnixTime() {
        Calendar calendar = Calendar.getInstance();
        return (int)(calendar.getTimeInMillis());
    }

    public static String OAuthEncode(String input) {
        Map<String, String> oauthEncodeMap = new HashMap<>();
        oauthEncodeMap.put("\\*", "%2A");
        oauthEncodeMap.put("\\+", "%20");
        oauthEncodeMap.put("%7E", "~");
        String encoded = "";
        try {
            encoded = URLEncoder.encode(input, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, String> entry : oauthEncodeMap.entrySet()) {
            encoded = encoded.replaceAll(entry.getKey(), entry.getValue());
        }
        return encoded;
    }

    public static String hmacsha1(String data, String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac macInstance = Mac.getInstance("HmacSHA1");
        macInstance.init(keySpec);
        byte[] signedBytes = macInstance.doFinal(data.getBytes());
        return (new String(Base64.encodeBase64(signedBytes)));
    }

    public static boolean isJSON(String content) {
        try {
            new JSONObject(content);
        } catch (Exception e) {
            try {
                new JSONArray(content);
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    public static boolean saveBitmapToJpegFile(@NonNull Bitmap bitmap, @NonNull File target) {
        try {
            target.getParentFile().mkdirs();
            FileOutputStream output = new FileOutputStream(target);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output);
            output.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static float convertDpToPixel(float dp, Context context){
        float px = 0.0f;
        try {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            px = dp * (metrics.densityDpi / 160f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return px;
    }

    public static boolean copyStreamToFile(InputStream input, File target) {
        try {
            target.getParentFile().mkdirs();
            FileOutputStream output = new FileOutputStream(target);
            return copyStreamToStream(input, output);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyStreamToStream(InputStream input, OutputStream output) {
        boolean result = false;
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) > 0) {
                output.write(buffer, 0, bytesRead);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Context context, View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

}
