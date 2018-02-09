package fr.epicture.epicture.api.flickr;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.epicture.epicture.R;
import fr.epicture.epicture.api.API;
import fr.epicture.epicture.api.APIAccount;
import fr.epicture.epicture.api.APICommentElement;
import fr.epicture.epicture.api.APIImageElement;
import fr.epicture.epicture.api.flickr.database.FlickrDatabase;
import fr.epicture.epicture.api.flickr.modele.TokenAccess;
import fr.epicture.epicture.api.flickr.modele.TokenRequest;
import fr.epicture.epicture.api.flickr.requests.AddCommentRequest;
import fr.epicture.epicture.api.flickr.requests.AddFavoriteRequest;
import fr.epicture.epicture.api.flickr.requests.DeleteFavoriteRequest;
import fr.epicture.epicture.api.flickr.requests.DeletePhotoRequest;
import fr.epicture.epicture.api.flickr.requests.GetAccessTokenRequest;
import fr.epicture.epicture.api.flickr.requests.GetCommentsRequest;
import fr.epicture.epicture.api.flickr.requests.GetRequestTokenRequest;
import fr.epicture.epicture.api.flickr.requests.GetUserFavoritesRequest;
import fr.epicture.epicture.api.flickr.requests.InterestingnessRequest;
import fr.epicture.epicture.api.flickr.requests.PhotoIsInFavoritesRequest;
import fr.epicture.epicture.api.flickr.requests.SearchRequest;
import fr.epicture.epicture.api.flickr.requests.UploadPictureRequest;
import fr.epicture.epicture.api.flickr.requests.UserInformationRequest;
import fr.epicture.epicture.api.flickr.requests.UserPhotosRequest;
import fr.epicture.epicture.api.flickr.utils.FlickrUtils;
import fr.epicture.epicture.interfaces.AddCommentInterface;
import fr.epicture.epicture.interfaces.AuthentificationInterface;
import fr.epicture.epicture.interfaces.ImageDiskCacheInterface;
import fr.epicture.epicture.interfaces.LoadBitmapInterface;
import fr.epicture.epicture.interfaces.LoadCommentElementInterface;
import fr.epicture.epicture.interfaces.LoadImageElementInterface;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.interfaces.LoadUserInfoInterface;
import fr.epicture.epicture.interfaces.PhotoIsInFavoritesInterface;
import fr.epicture.epicture.utils.ImageDiskCache;

public class Flickr implements API {

    private static final String authorizeLink = "https://www.flickr.com/services/oauth/authorize?oauth_token=%s&perms=delete";
    private static final String name = "Flickr";

    private static final Map<String, FlickrAccount> Accounts = new HashMap<>();

    private FlickrDatabase database;

    private GetRequestTokenRequest getRequestTokenRequest;
    private GetAccessTokenRequest getAccessTokenRequest;
    private InterestingnessRequest interestingnessRequest;
    private UserPhotosRequest userPhotosRequest;
    private UploadPictureRequest uploadPictureRequest;
    private SearchRequest searchRequest;
    private AddCommentRequest addCommentRequest;
    private AddFavoriteRequest addFavoriteRequest;
    private PhotoIsInFavoritesRequest photoIsInFavoritesRequest;
    private GetUserFavoritesRequest getUserFavoritesRequest;
    private DeleteFavoriteRequest deleteFavoriteRequest;

    private AuthentificationInterface authListener;
    private TokenRequest tokenRequest;

    private FlickrAccount currentAccount;

    public Flickr(Context context) {
        database = new FlickrDatabase(context);
        database.open();
        loadAccounts();
    }

    @Override
    public void setAuthentificationListener(AuthentificationInterface listener) {
        this.authListener = listener;
    }

    @Override
    public void authentification(Context context) {
        if (!isGetRequestTokenRequestRunning()) {
            getRequestTokenRequest = new GetRequestTokenRequest(context, new LoadTextInterface() {
                @Override
                public void onFinish(String text) {
                    getRequestTokenRequest = null;
                    try {
                        tokenRequest = FlickrUtils.retrieveTokenRequest(text);
                        authListener.onUserPermissionRequest(String.format(authorizeLink, tokenRequest.token), "oauth_verifier");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void afterUserPermissionRequest(Context context, String urlResponse) {
        if (!isGetAccessTokenRequestRunning()) {
            tokenRequest.verifier = Uri.parse(urlResponse).getQueryParameter("oauth_verifier");
            getAccessTokenRequest = new GetAccessTokenRequest(context, tokenRequest, new LoadTextInterface() {
                @Override
                public void onFinish(String text) {
                    getAccessTokenRequest = null;
                    try {
                        TokenAccess tokenAccess = FlickrUtils.retrieveTokenAccess(text);
                        FlickrAccount user = new FlickrAccount(tokenAccess);
                        database.insertAccount(user);
                        Accounts.put(user.getUsername(), user);
                        authListener.onUserPermissionGranted();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void loadUserInformation(Context context, LoadUserInfoInterface callback) {
        for (Map.Entry<String, FlickrAccount> entry : Accounts.entrySet()) {
            FlickrAccount user = entry.getValue();
            new UserInformationRequest(context, user.nsid, new LoadTextInterface() {
                @Override
                public void onFinish(String text) {
                    try {
                        user.setData(new JSONObject(text));
                        callback.onFinish(user.getUsername(), user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void loadUserInformation(Context context, String id, LoadUserInfoInterface callback) {
        new UserInformationRequest(context, id, new LoadTextInterface() {
            @Override
            public void onFinish(String text) {
                try {
                    FlickrAccount user = new FlickrAccount(new JSONObject(text));
                    callback.onFinish(user.getUsername(), user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void loadUserAvatar(Context context, APIAccount account, LoadBitmapInterface callback) {
        FlickrAccount user = Accounts.get(account.getID());
        if (user != null) {
            if (user.iconserver != null) {
                FlickyAvatarElement element = new FlickyAvatarElement(user.nsid, user.iconserver, user.iconfarm);
                loadImage(context, element, callback);
            }
        } else {
            FlickyAvatarElement element = new FlickyAvatarElement(account.getNSID(), account.getIconServer(), account.getFarm());
            loadImage(context, element, callback);
        }
    }

    @Override
    public void loadUserAvatar(Context context, APICommentElement commentElement, LoadBitmapInterface callback) {
        FlickyAvatarElement element = new FlickyAvatarElement(commentElement.getNSID(), commentElement.getIconServer(), commentElement.getIconFarm());
        loadImage(context, element, callback);
    }

    @Override
    public void loadImage(Context context, APIImageElement element, LoadBitmapInterface callback) {
        ImageDiskCache.load(context, element, new ImageDiskCacheInterface() {
            @Override
            public void onFinish(APIImageElement element, Bitmap bitmap) {
                callback.onFinish(bitmap);
            }
        });
    }

    @Override
    public void uploadImage(Context context, APIAccount user, String path, String title, String description, String tags, LoadTextInterface callback) {
        if (!isRequestingUploadPicture()) {
            FlickrAccount flickrAccount = Accounts.get(user.getUsername());
            FlickrImageElement element = new FlickrImageElement(path, title, description, tags);
            uploadPictureRequest = new UploadPictureRequest(context, flickrAccount, element, new LoadTextInterface() {
                @Override
                public void onFinish(String text) {
                    uploadPictureRequest = null;
                    callback.onFinish(text);
                }
            });
        }
    }

    @Override
    public void getInterestingnessList(Context context, int page, LoadImageElementInterface callback) {
        if (!isInterestingnessRequestRunning()) {
            interestingnessRequest = new InterestingnessRequest(context, page, new LoadTextInterface() {
                @Override
                public void onFinish(String text) {
                    interestingnessRequest = null;
                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        int maxPage = jsonObject.getJSONObject("photos").getInt("pages");
                        if (maxPage < page) {
                            callback.onFinish(null, true);
                        }
                        List<APIImageElement> datas = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONObject("photos").getJSONArray("photo");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject current = jsonArray.getJSONObject(i);
                            datas.add(new FlickrImageElement(current, APIImageElement.SIZE_PREVIEW));
                        }
                        callback.onFinish(datas, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void getMyPictures(Context context, int page, LoadImageElementInterface callback) {
        if (!isRequestingImageList()) {
            userPhotosRequest = new UserPhotosRequest(context, page, currentAccount, new LoadTextInterface() {
                @Override
                public void onFinish(String text) {
                    userPhotosRequest = null;
                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        int maxPage = jsonObject.getJSONObject("photos").getInt("pages");
                        if (maxPage < page) {
                            callback.onFinish(null, true);
                        }
                        List<APIImageElement> datas = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONObject("photos").getJSONArray("photo");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject current = jsonArray.getJSONObject(i);
                            datas.add(new FlickrImageElement(current, APIImageElement.SIZE_PREVIEW));
                        }
                        callback.onFinish(datas, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void addComment(Context context, String userid, String photoid, String comment, AddCommentInterface callback) {
        if (!isRequestingAddComment()) {
            FlickrAccount user = Accounts.get(userid);
            addCommentRequest = new AddCommentRequest(context, user, photoid, comment, new LoadTextInterface() {
                @Override
                public void onFinish(String text) {
                    addCommentRequest = null;
                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        callback.onFinish(new FlickrCommentElement(jsonObject.getJSONObject("comment")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void getComments(Context context, String userid, String photoid, LoadCommentElementInterface callback) {
        FlickrAccount account = Accounts.get(userid);
        new GetCommentsRequest(context, account, photoid, new LoadTextInterface() {
            @Override
            public void onFinish(String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    List<APICommentElement> datas = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONObject("comments").getJSONArray("comment");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject current = jsonArray.getJSONObject(i);
                        datas.add(new FlickrCommentElement(current));
                    }
                    callback.onFinish(datas, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFinish(null, true);
                }
            }
        });
    }

    @Override
    public void addFavorite(Context context, String userid, String photoid, LoadTextInterface callback) {
        if (!isRequestingAddFavorite()) {
            FlickrAccount account = Accounts.get(userid);
            addFavoriteRequest = new AddFavoriteRequest(context, account, photoid, new LoadTextInterface() {
                @Override
                public void onFinish(String text) {
                    addFavoriteRequest = null;
                    callback.onFinish(text);
                }
            });
        }
    }

    @Override
    public void deleteFavorite(Context context, String userid, String photoid, LoadTextInterface callback) {
        FlickrAccount account = Accounts.get(userid);
        deleteFavoriteRequest = new DeleteFavoriteRequest(context, account, photoid, new LoadTextInterface() {
            @Override
            public void onFinish(String text) {
                callback.onFinish(text);
            }
        });
    }

    @Override
    public void isFavorite(Context context, String userid, String photoid, PhotoIsInFavoritesInterface callback) {
        FlickrAccount account = Accounts.get(userid);
        photoIsInFavoritesRequest = new PhotoIsInFavoritesRequest(context, account.id, photoid, new LoadTextInterface() {
            @Override
            public void onFinish(String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    callback.onFinish(!jsonObject.getString("stat").equals("fail"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void getFavorites(Context context, String userid, int page, LoadImageElementInterface callback) {
        FlickrAccount account = Accounts.get(userid);
        getUserFavoritesRequest = new GetUserFavoritesRequest(context, account.id, page, new LoadTextInterface() {
            @Override
            public void onFinish(String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    int maxPage = jsonObject.getJSONObject("photos").getInt("pages");
                    if (maxPage < page) {
                        callback.onFinish(null, true);
                    }
                    List<APIImageElement> datas = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONObject("photos").getJSONArray("photo");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject current = jsonArray.getJSONObject(i);
                        datas.add(new FlickrImageElement(current, APIImageElement.SIZE_PREVIEW));
                    }
                    callback.onFinish(datas, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void search(Context context, String search, String userid, int page, LoadImageElementInterface callback) {
        if (!isRequestingSearch()) {
            FlickrAccount account = Accounts.get(userid);
            searchRequest = new SearchRequest(context, account, search, page, new LoadTextInterface() {
                @Override
                public void onFinish(String text) {
                    searchRequest = null;
                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        int maxPage = jsonObject.getJSONObject("photos").getInt("pages");
                        if (maxPage < page) {
                            callback.onFinish(null, true);
                        }
                        List<APIImageElement> datas = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONObject("photos").getJSONArray("photo");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject current = jsonArray.getJSONObject(i);
                            datas.add(new FlickrImageElement(current, APIImageElement.SIZE_PREVIEW));
                        }
                        callback.onFinish(datas, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void deletePhoto(Context context, String photoid, String userid, LoadTextInterface callback) {
        FlickrAccount account = Accounts.get(userid);
        new DeletePhotoRequest(context, account, photoid, callback);
    }

    @Override
    public void setCurrentAccount(APIAccount account) {
        currentAccount = Accounts.get(account.getUsername());
    }

    @Override
    public APIAccount getCurrentAccount() {
        return currentAccount;
    }

    @Override
    public int getLogo() {
        return R.drawable.flickr_logo;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<APIAccount> getAccounts() {
        Collection<APIAccount> ret = new ArrayList<>();
        List<FlickrAccount> accounts = database.getAccounts();
        for (FlickrAccount account : accounts) {
            ret.add(account);
        }
        return (ret);
    }

    private void loadAccounts() {
        List<FlickrAccount> accounts = database.getAccounts();
        for (FlickrAccount account : accounts) {
            Accounts.put(account.getUsername(), account);
        }
    }

    private boolean isGetAccessTokenRequestRunning() {
        return (getAccessTokenRequest != null);
    }

    private boolean isGetRequestTokenRequestRunning() {
        return (getRequestTokenRequest != null);
    }

    private boolean isInterestingnessRequestRunning() {
        return (interestingnessRequest != null);
    }

    private boolean isRequestingImageList() {
        return (userPhotosRequest != null);
    }

    private boolean isRequestingUploadPicture() {
        return (uploadPictureRequest != null);
    }

    private boolean isRequestingSearch() {
        return searchRequest != null;
    }

    private boolean isRequestingAddComment() {
        return addCommentRequest != null;
    }

    private boolean isRequestingAddFavorite() {
        return addFavoriteRequest != null;
    }

    private boolean isRequestingIsInFavoritesRequest() {
        return photoIsInFavoritesRequest != null;
    }

    private boolean isRequestingGetUserFavoritesRequest() {
        return getUserFavoritesRequest != null;
    }

}
