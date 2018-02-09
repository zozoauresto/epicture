package fr.epicture.epicture.requests;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import fr.epicture.epicture.asynctasks.RequestAsyncTask;
import fr.epicture.epicture.interfaces.LoadTextInterface;

public class TextPostRequest extends RequestAsyncTask {

    private String url;
    private LoadTextInterface listener;

    public TextPostRequest(@NonNull Context context, String url, LoadTextInterface listener) {
        super(context);
        this.url = url;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            POST(url);
            Log.i("TextPostRequest", "POST " + url);
            Log.i("TextPostRequest", "BODY " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        try {
            listener.onFinish(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
