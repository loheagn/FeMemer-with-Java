package com.loheagn.fememer.dbop;

import java.util.HashMap;
import java.util.Map;

/**
 * Article
 */
public class Article {

    private int id;
    private String source;
    private String title;
    private String webUrl;
    private String localUrl;
    private String downloaded;
    private long addTime;
    private String tag;

    public Article(int id, String source, String title, String webUrl, String localUrl, String downloaded, long addTime,
            String tag) {
        this.id = id;
        this.source = source;
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

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
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

    public String getDownloaded() {
        return this.downloaded;
    }

    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }

    /**
     * @param addTime the addTime to set
     */
    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    /**
     * @return the addTime
     */
    public long getAddTime() {
        return addTime;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return Long.toString(addTime);
    }

    /**
     * 将Article对象转化为map变量，主要是结局转化成json时候的问题
     * 
     * @return
     */
    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.clear();
        map.put("id", id);
        map.put("source", source);
        map.put("title", title);
        map.put("web_url", webUrl);
        map.put("local_url", localUrl);
        map.put("is_downloaded", downloaded);
        map.put("add_time", addTime);
        map.put("tag", tag);
        return map;
    }

}