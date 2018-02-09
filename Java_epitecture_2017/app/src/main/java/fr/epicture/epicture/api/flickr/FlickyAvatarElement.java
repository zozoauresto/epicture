package fr.epicture.epicture.api.flickr;

import android.os.Parcel;
import android.os.Parcelable;

import fr.epicture.epicture.api.APIImageElement;

public class FlickyAvatarElement extends APIImageElement {

    private static final String URL_DEFAULT = "https://www.flickr.com/images/buddyicon.gif";
    private static final String URL = "https://farm%1$s.staticflickr.com/%2$s/buddyicons/%3$s.jpg";

    private String server;
    private String farm;

    public FlickyAvatarElement(String id, String server, String farm) {
        super(id, SIZE_THUMBNAIL);
        this.farm = farm;
        this.server = server;
    }

    @Override
    public String getURL() {
        String ret;

        if (server.equals("0")) {
            ret = URL_DEFAULT;
        } else {
            ret = String.format(URL, farm, server, getID());
        }
        return ret;
    }

    @Override
    public float getWidthSize() {
        return 0;
    }

    @Override
    public float getHeightSize() {
        return 0;
    }

    @Override
    public boolean isFavorite() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getID());
        dest.writeInt(getSize());
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(server);
        dest.writeString(farm);

    }

    public static final Parcelable.Creator<FlickyAvatarElement> CREATOR = new Parcelable.Creator<FlickyAvatarElement>() {

        @Override
        public FlickyAvatarElement createFromParcel(Parcel in) {
            return new FlickyAvatarElement(in);
        }

        @Override
        public FlickyAvatarElement[] newArray(int size) {
            return new FlickyAvatarElement[size];
        }
    };

    private FlickyAvatarElement(Parcel in) {
        setID(in.readString());
        setSize(in.readInt());
        path = in.readString();
        title = in.readString();
        description = in.readString();
        server = in.readString();
        farm = in.readString();
    }
}
