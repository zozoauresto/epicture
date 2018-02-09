package fr.epicture.epicture.asynctasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import fr.epicture.epicture.R;
import fr.epicture.epicture.adapters.ShareAppsAdapter;
import fr.epicture.epicture.api.API;
import fr.epicture.epicture.api.APIImageElement;
import fr.epicture.epicture.api.APIManager;
import fr.epicture.epicture.interfaces.LoadBitmapInterface;

public class ShareAsyncTask extends AsyncTask<Void, Void, Void> {

    private Activity activity;
    private APIImageElement element;

    public ShareAsyncTask(Activity activity, APIImageElement element) {
        this.activity = activity;
        this.element = element;
    }

    @Override
    protected Void doInBackground(Void... params) {
        element.setSize(APIImageElement.SIZE_PREVIEW);

        API api = APIManager.getSelectedAPI();
        api.loadImage(activity, element, new LoadBitmapInterface() {
            @Override
            public void onFinish(Bitmap bitmap) {

            }
        });
        return null;
    }

    @Override
    public void onPostExecute(Void result) {
        ShareAsyncTask.this.onFinish();
    }

    private void onFinish() {
        ShareAppsAdapter adapter = new ShareAppsAdapter(activity, element.getFile(activity), 0);
        new AlertDialog.Builder(activity)
                .setTitle(R.string.share)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        activity.startActivity(adapter.getIntent(index));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

}
