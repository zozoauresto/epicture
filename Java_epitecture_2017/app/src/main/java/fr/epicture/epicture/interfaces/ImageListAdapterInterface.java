package fr.epicture.epicture.interfaces;

import fr.epicture.epicture.api.APIImageElement;

public interface ImageListAdapterInterface {

    void onImageClick(APIImageElement element);
    void onCommentClick(APIImageElement element);
    void onImageDelete(APIImageElement element);

}
