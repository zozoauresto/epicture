package fr.epicture.epicture.api;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

public abstract class APIImageElement implements Parcelable {

    private static final String DIR_THUMBNAIL = "thumbnail";
    private static final String DIR_PREVIEW = "preview";
    private static final String DIR_ORIGINAL = "original";

    public static final int SIZE_THUMBNAIL = 1;
    public static final int SIZE_PREVIEW = 2;
    public static final int SIZE_ORIGINAL = 3;

    private String id;
    private int size;

    public String path;
    public String title;
    public String description;
    public String tags;
    public String ownerid;
    public String ownername;
    public int commentCount;
    public long date;
    public boolean favorite;

    protected APIImageElement() {
        this.id = "undefined";
        this.size = 0;
    }

    protected APIImageElement(String id, int size) {
        this.id = id;
        this.size = size;
        favorite = false;
    }

    protected APIImageElement(String path, String title, String description, String tags) {
        this.path = path;
        this.title = title;
        this.description = description;
        this.tags = tags;
        favorite = false;
    }

    public String getID() {
        return id;
    }

    public int getSize() {
        return size;
    }

    protected void setID(String id) {
        this.id = id;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public File getFile(@NonNull Context context) {
        File dir = context.getExternalCacheDir();
        String path = getFilePath(context);
        if (!path.isEmpty()) {
            return new File(getFilePath(context));
        }
        return null;
    }

    public String getFilePath(@NonNull Context context) {
        String ret = "";
        File dir = context.getExternalCacheDir();
        if (dir != null) {
            String path = dir.getAbsolutePath();
            switch (size) {
                case SIZE_PREVIEW:
                    ret = path + File.separator + DIR_PREVIEW + id + ".jpg";
                    break;
                case SIZE_ORIGINAL:
                    ret = path + File.separator + DIR_ORIGINAL + id + ".jpg";
                    break;
                default:
                    ret = path + File.separator + DIR_THUMBNAIL + id + ".jpg";
                    break;
            }
        }
        return ret;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return object instanceof APIImageElement
                && id.equals(((APIImageElement) object).getID())
                && size == ((APIImageElement) object).getSize();
    }


    @Override
    public int hashCode() {
        return id.hashCode() + size;
    }

    public abstract String getURL();
    public abstract float getWidthSize();
    public abstract float getHeightSize();
    public abstract boolean isFavorite();
}
