package fr.epicture.epicture.requests;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;

import fr.epicture.epicture.R;
import fr.epicture.epicture.api.APIImageElement;
import fr.epicture.epicture.asynctasks.RequestAsyncTask;
import fr.epicture.epicture.interfaces.LoadBitmapInterface;
import fr.epicture.epicture.utils.StaticTools;

public class DownloadImageRequest extends RequestAsyncTask {

    private APIImageElement element;
    private LoadBitmapInterface listener;
    private Bitmap bitmap;

    public DownloadImageRequest(@NonNull Context context, APIImageElement element, LoadBitmapInterface listener) {
        super(context);
        this.element = element;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            GETImage(element.getURL());

            Log.i("imageRequest", "GET " + element.getURL());
            Log.i("imageRequest", "BODY " + response);
            Log.i("imageRequest", "RESPONSE CODE " + httpResponseCode);

            makeBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        try {
            listener.onFinish(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeBitmap () {
        File file = element.getFile(getContext());
        if (file != null && image != null) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Point outSize = new Point();
            wm.getDefaultDisplay().getSize(outSize);
            float screenWidth = outSize.x;
            float w = 0.0f, h = 0.0f;

            try {
                switch (element.getSize()) {
                    case APIImageElement.SIZE_THUMBNAIL:
                        int size = getContext().getResources().getDimensionPixelSize(R.dimen.image_size_thumbnail);
                        bitmap = Bitmap.createScaledBitmap(image, size, size, true);
                        StaticTools.saveBitmapToJpegFile(bitmap, file);
                        image.recycle();
                        break;
                    case APIImageElement.SIZE_PREVIEW:
                        w = screenWidth;
                        h = image.getHeight() * ( screenWidth / image.getWidth() );
                        if (h > screenWidth) {
                            w = screenWidth * ( screenWidth / h );
                            h = screenWidth;
                        }
                        bitmap = Bitmap.createScaledBitmap(image, (int)w, (int)h, true);
                        StaticTools.saveBitmapToJpegFile(bitmap, file);
                        image.recycle();
                        break;
                    default:
                        StaticTools.saveBitmapToJpegFile(image, file);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
