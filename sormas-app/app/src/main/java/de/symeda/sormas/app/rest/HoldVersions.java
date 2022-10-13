package de.symeda.sormas.app.rest;

public class HoldVersions {
    private static String ServerVersionFromURL;
    private static String AppVersionFromUrl;


    public HoldVersions(String serverVersionFromURL, String appVersionFromUrl) {
        ServerVersionFromURL = serverVersionFromURL;
        AppVersionFromUrl = appVersionFromUrl;
    }

    public static String getServerVersionFromURL() {
        return ServerVersionFromURL;
    }

    public void setServerVersionFromURL(String serverVersionFromURL) {
        ServerVersionFromURL = serverVersionFromURL;
    }


    public static String getAppVersionFromUrl() {
        return AppVersionFromUrl;
    }

    public void setAppVersionFromUrl(String appVersionFromUrl) {
        AppVersionFromUrl = appVersionFromUrl;
    }
}
