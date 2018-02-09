package fr.epicture.epicture.interfaces;

public interface AuthentificationInterface {

    void onUserPermissionRequest(String url, String grantWord);
    void onUserPermissionGranted();

}
