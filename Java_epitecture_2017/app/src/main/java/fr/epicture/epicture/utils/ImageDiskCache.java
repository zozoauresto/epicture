package fr.epicture.epicture.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.epicture.epicture.api.APIImageElement;
import fr.epicture.epicture.interfaces.ImageDiskCacheInterface;
import fr.epicture.epicture.interfaces.LoadBitmapInterface;
import fr.epicture.epicture.requests.DownloadImageRequest;

public class ImageDiskCache {

    public final static String CACHE_TAG = "imagediskcache";

    private static final Map<APIImageElement, WebImageRequestTask> WEBTASKS = new HashMap<>();
    private static final Map<APIImageElement, DiskTask> DISKTASKS = new HashMap<>();
    private static final Map<APIImageElement, Set<ImageDiskCacheInterface>> LISTENERS = new HashMap<>();

    public static void load(@NonNull final Context context,
                            @NonNull final APIImageElement element,
                            @NonNull final ImageDiskCacheInterface listener) {

        Bitmap bitmap = BitmapCache.getInCache(CACHE_TAG + element.getID() + element.getSize());
        if (bitmap != null && !bitmap.isRecycled()) {
            listener.onFinish(element, bitmap);
            return;
        }

        Set<ImageDiskCacheInterface> listeners = LISTENERS.get(element);
        if (listeners == null) {
            listeners = new HashSet<>();
        }
        listeners.add(listener);
        LISTENERS.put(element, listeners);
        WebImageRequestTask webtask = WEBTASKS.get(element);
        if (webtask != null) {
            return;
        }
        DiskTask disktask = DISKTASKS.get(element);
        if (disktask != null) {
            return;
        }
        File file = element.getFile(context);
        if (file != null && file.exists()) {
            disktask = new DiskTask(context, element, new ImageDiskCacheInterface() {
                @Override
                public void onFinish(APIImageElement element, Bitmap bitmap) {
                    notifyResult(element, bitmap);
                }
            });
            DISKTASKS.put(element, disktask);
            disktask.execute();
        } else {
            webtask = new WebImageRequestTask(context, element, new ImageDiskCacheInterface() {
                @Override
                public void onFinish(@NonNull APIImageElement element, @Nullable Bitmap bitmap) {
                    notifyResult(element, bitmap);
                }

            });
            WEBTASKS.put(element, webtask);
            webtask.execute();
        }
    }

    private static void notifyResult(
            @NonNull APIImageElement element,
            @Nullable Bitmap bitmap) {
        if (bitmap != null) {
            BitmapCache.putInCache(CACHE_TAG + element.getID() + element.getSize(), bitmap);
        }
        WebImageRequestTask webtask = WEBTASKS.remove(element);
        if (webtask != null) {
            webtask.cancel(true);
        }
        DiskTask disktask = DISKTASKS.remove(element);
        if (disktask != null) {
            disktask.cancel(true);
        }
        Set<ImageDiskCacheInterface> listeners = LISTENERS.remove(element);
        if (listeners != null) {
            for (ImageDiskCacheInterface listener : listeners) {
                listener.onFinish(element, bitmap);
            }
        }
    }

    private static class WebImageRequestTask extends DownloadImageRequest {
        WebImageRequestTask(@NonNull final Context context,
                            @NonNull final APIImageElement element,
                            @NonNull final ImageDiskCacheInterface listener) {
            super(context, element, new LoadBitmapInterface() {
                @Override
                public void onFinish(Bitmap bitmap) {
                    listener.onFinish(element, bitmap);
                }
            });
        }
    }

    private static class DiskTask extends AsyncTask<Void, Void, Void> {

        private final Context context;
        private final APIImageElement element;
        private final ImageDiskCacheInterface listener;
        private Bitmap bitmap;

        public DiskTask(@NonNull Context context, @NonNull APIImageElement element, @NonNull ImageDiskCacheInterface listener) {
            this.context = context;
            this.element = element;
            this.listener = listener;
        }

        public void execute() {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        protected Void doInBackground(@Nullable Void... params) {
            File file = element.getFile(context);
            if (file != null) {
                String path = file.getAbsolutePath();
                bitmap = BitmapFactory.decodeFile(path);
            }
            return null;
        }

        @Override
        protected void onPostExecute(@Nullable Void result) {
            listener.onFinish(element, bitmap);
        }

    }

}
