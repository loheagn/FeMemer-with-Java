package com.loheagn.fememer.dbop;

/**
 * Article
 */
public class Article {

    private int id;
    private String url;
    private String title;
    private String webUrl;
    private String localUrl;
    private boolean downloaded;
    private long addTime;
    private String tag;

    public Article(int id, String url, String title, String webUrl, String localUrl, boolean downloaded, long addTime,
            String tag) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.webUrl = webUrl;
        this.localUrl = localUrl;
        this.downloaded = downloaded;
        this.addTime = addTime;
        this.tag = tag;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebUrl() {
        return this.webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getLocalUrl() {
        return this.localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public boolean isDownloaded() {
        return this.downloaded;
    }

    public boolean getDownloaded() {
        return this.downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}