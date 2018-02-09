package fr.epicture.epicture.api.flickr;

import org.json.JSONObject;

import fr.epicture.epicture.api.APICommentElement;

public class FlickrCommentElement extends APICommentElement {

    private String iconServer;
    private String iconFarm;

    public FlickrCommentElement(JSONObject jsonObject) {
        try {
            id = jsonObject.getString("id");
            authorId = jsonObject.getString("author");
            authorName = jsonObject.getString("authorname");
            dateCreate = jsonObject.getLong("datecreate");
            content = jsonObject.getString("_content");
            iconServer = jsonObject.optString("iconserver");
            iconFarm = jsonObject.optString("iconfarm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getIconServer() {
        return iconServer;
    }

    @Override
    public String getIconFarm() {
        return iconFarm;
    }

    @Override
    public String getNSID() {
        return authorId;
    }
}
