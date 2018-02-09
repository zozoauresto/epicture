package fr.epicture.epicture.api.flickr.modele;

import android.os.Parcel;
import android.os.Parcelable;

public class TokenRequest implements Parcelable {

    public boolean callbackConfirmed;
    public String token;
    public String tokenSecret;
    public String verifier;

    public TokenRequest() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(callbackConfirmed?1:0);
        dest.writeString(token);
        dest.writeString(tokenSecret);
        dest.writeString(verifier);
    }

    public static final Parcelable.Creator<TokenRequest> CREATOR = new Parcelable.Creator<TokenRequest>() {

        @Override
        public TokenRequest createFromParcel(Parcel in) {
            return new TokenRequest(in);
        }

        @Override
        public TokenRequest[] newArray(int size) {
            return new TokenRequest[size];
        }
    };

    private TokenRequest(Parcel in) {
        callbackConfirmed = in.readInt() == 1;
        token = in.readString();
        tokenSecret = in.readString();
        verifier = in.readString();
    }
}
