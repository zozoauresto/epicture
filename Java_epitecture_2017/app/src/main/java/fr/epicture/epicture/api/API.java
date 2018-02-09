package fr.epicture.epicture.api;

import android.content.Context;

import java.util.Collection;

import fr.epicture.epicture.interfaces.AddCommentInterface;
import fr.epicture.epicture.interfaces.AuthentificationInterface;
import fr.epicture.epicture.interfaces.LoadBitmapInterface;
import fr.epicture.epicture.interfaces.LoadCommentElementInterface;
import fr.epicture.epicture.interfaces.LoadImageElementInterface;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.interfaces.LoadUserInfoInterface;
import fr.epicture.epicture.interfaces.PhotoIsInFavoritesInterface;

public interface API {
    int getLogo();
    String getName();
    void setAuthentificationListener(AuthentificationInterface listener);
    void authentification(Context context);
    void afterUserPermissionRequest(Context context, String urlResponse);
    void loadUserInformation(Context context, LoadUserInfoInterface callback);
    void loadUserInformation(Context context, String id, LoadUserInfoInterface callback);
    void loadUserAvatar(Context context, APIAccount account, LoadBitmapInterface callback);
    void loadUserAvatar(Context context, APICommentElement commentElement, LoadBitmapInterface callback);
    void loadImage(Context context, APIImageElement element, LoadBitmapInterface callback);
    void uploadImage(Context context, APIAccount user, String path, String title, String description, String tags, LoadTextInterface callback);
    void deletePhoto(Context context, String photoid, String userid, LoadTextInterface callback);
    void getInterestingnessList(Context context, int page, LoadImageElementInterface callback);
    void getMyPictures(Context context, int page, LoadImageElementInterface callback);
    void search(Context context, String search, String userid, int page, LoadImageElementInterface callback);
    void addComment(Context context, String userid, String photoid, String comment, AddCommentInterface callback);
    void getComments(Context context, String userid, String photoid, LoadCommentElementInterface callback);
    void addFavorite(Context context, String userid, String photoid, LoadTextInterface callback);
    void deleteFavorite(Context context, String userid, String photoid, LoadTextInterface callback);
    void isFavorite(Context context, String userid, String photoid, PhotoIsInFavoritesInterface callback);
    void getFavorites(Context context, String userid, int page, LoadImageElementInterface callback);
    void setCurrentAccount(APIAccount account);
    APIAccount getCurrentAccount();
    Collection<APIAccount> getAccounts();
}
