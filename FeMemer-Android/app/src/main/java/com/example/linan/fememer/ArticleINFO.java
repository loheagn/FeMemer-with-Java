package com.example.linan.fememer;

public class ArticleINFO {
    private int id;
    private String source ;
    private String title;
    private String web_url;
    private String local_url;
    private String is_downloaded;
    private String add_time;
    private String tag;

    public ArticleINFO(int id, String source, String title, String web_url, String local_url, String is_downloaded, String add_time, String tag) {
        this.id = id;
        this.source = source;
        this.title = title;
        this.web_url = web_url;
        this.local_url = local_url;
        this.is_downloaded = is_downloaded;
        this.add_time = add_time;
        this.tag = tag;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    public String getLocal_url() {
        return local_url;
    }

    public void setLocal_url(String local_url) {
        this.local_url = local_url;
    }

    public String getIs_downloaded() {
        return is_downloaded;
    }

    public void setIs_downloaded(String is_downloaded) {
        this.is_downloaded = is_downloaded;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
