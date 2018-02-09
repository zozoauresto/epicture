package fr.epicture.epicture.interfaces;

import java.util.List;

import fr.epicture.epicture.api.APIImageElement;

public interface LoadImageElementInterface {

    void onFinish(List<APIImageElement> datas, boolean error);

}
