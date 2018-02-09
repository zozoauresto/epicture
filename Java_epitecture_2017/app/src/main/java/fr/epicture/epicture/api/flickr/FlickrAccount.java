package fr.epicture.epicture.api.flickr;

import org.json.JSONObject;

import fr.epicture.epicture.api.APIAccount;
import fr.epicture.epicture.api.flickr.modele.TokenAccess;

public class FlickrAccount extends APIAccount {

    public int dbid;
    public String nsid;
    public String token;
    public String tokenSecret;
    public String fullname;
    public String iconserver;
    public String iconfarm;
    public String location;
    public String description;

    public FlickrAccount(TokenAccess tokenAccess) {
        this.fullname = tokenAccess.fullname;
        this.token = tokenAccess.token;
        this.tokenSecret = tokenAccess.tokenSecret;
        this.nsid = tokenAccess.nsid;
        this.username = tokenAccess.username;
    }

    public FlickrAccount(JSONObject jsonObject) {
        setData(jsonObject);
    }

    public void setData(JSONObject jsonObject) {
        try {
            id = jsonObject.getJSONObject("person").getString("id");
            username = jsonObject.getJSONObject("person").getJSONObject("username").getString("_content");

            nsid = jsonObject.getJSONObject("person").getString("nsid");
            iconserver = jsonObject.getJSONObject("person").getString("iconserver");
            iconfarm = jsonObject.optJSONObject("person").getString("iconfarm");
            try {location = jsonObject.optJSONObject("person").optJSONObject("location").optString("_content");} catch (Exception e) {}
            try {description = jsonObject.optJSONObject("person").getJSONObject("description").getString("_content");} catch (Exception e) {}
            try {realname = jsonObject.optJSONObject("person").optJSONObject("realname").optString("_content");} catch (Exception e) {}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getID() {
        return username;
    }

    @Override
    public String getNSID() {
        return nsid;
    }

    @Override
    public String getIconServer() {
        return iconserver;
    }

    @Override
    public String getFarm() {
        return iconfarm;
    }
}
