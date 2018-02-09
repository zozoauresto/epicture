package fr.epicture.epicture.interfaces;

import java.util.List;

import fr.epicture.epicture.api.APICommentElement;

public interface LoadCommentElementInterface {

    void onFinish(List<APICommentElement> datas, boolean error);

}
