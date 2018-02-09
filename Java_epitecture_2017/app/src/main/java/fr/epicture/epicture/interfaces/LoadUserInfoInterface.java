package fr.epicture.epicture.interfaces;

import fr.epicture.epicture.api.APIAccount;

public interface LoadUserInfoInterface {

    void onFinish(String id, APIAccount result);
}
