package fr.epicture.epicture.api;

import java.util.ArrayList;
import java.util.List;

public class APIManager {

    private static final List<API> APIs = new ArrayList<>();
    private static API selectedAPI = null;

    public static void addAPI(API api) {
        boolean found = false;
        for (API it : APIs) {
            found = it.getName().equals(api.getName());
            if (found) {
                break;
            }
        }
        if (!found) {
            APIs.add(api);
        }

    }

    public static List<API> getAPIList() {
        return APIs;
    }
    public static API getAPIByIndex(int idx) {
        return APIs.get(idx);
    }

    public static void setSelectedAPI(API api) {
        selectedAPI = api;
    }
    public static API getSelectedAPI() {
        return selectedAPI;
    }
}
