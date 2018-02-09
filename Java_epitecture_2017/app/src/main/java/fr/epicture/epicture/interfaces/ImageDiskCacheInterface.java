package fr.epicture.epicture.interfaces;

import android.graphics.Bitmap;

import fr.epicture.epicture.api.APIImageElement;

public interface ImageDiskCacheInterface {

    void onFinish(APIImageElement element, Bitmap bitmap);

}
