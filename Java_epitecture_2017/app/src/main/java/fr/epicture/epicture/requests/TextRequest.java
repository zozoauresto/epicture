package fr.epicture.epicture.requests;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import fr.epicture.epicture.asynctasks.RequestAsyncTask;
import fr.epicture.epicture.interfaces.LoadTextInterface;

public class TextRequest extends RequestAsyncTask {

    private String url;
    private LoadTextInterface listener;

    public TextRequest(@NonNull Context context, String url, LoadTextInterface listener) {
        super(context);
        this.url = url;
        this.listener = listener;
    }

    protected TextRequest(@NonNull Context context, LoadTextInterface listener) {
        super(context);
        this.listener = listener;
    }

    protected void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            GET(url);
            Log.i("TextRequest", "GET " + url);
            Log.i("TextRequest", "BODY " + response);
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
