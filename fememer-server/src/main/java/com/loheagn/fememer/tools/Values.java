package com.loheagn.fememer.tools;

/**
 * Values
 */
public class Values {

    private static String webappPath = "/usr/local/Cellar/tomcat/9.0.20/libexec/webapps/fememer";

    /**
     * @param webappPath the webappPath to set
     */
    public void setWebappPath(String _webappPath) {
        webappPath = _webappPath;
    }

    /**
     * @return the webappPath
     */
    public static String getWebappPath() {
        return webappPath;
    }
}