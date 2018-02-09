package fr.epicture.epicture.interfaces;

import fr.epicture.epicture.api.APIImageElement;

public interface ImageListInterface extends RetractableToolbarInterface {

    void onRequestImageList(int page, String search, String userID);
    void onImageClick(APIImageElement element);
    void onCommentClick(APIImageElement element);

}
