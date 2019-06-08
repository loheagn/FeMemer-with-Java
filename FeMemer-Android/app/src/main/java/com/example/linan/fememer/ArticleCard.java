package com.example.linan.fememer;

/**
 * 用了安卓的cardview控件，这是每个card对应的类，存着这个card
 * 文章的相关信息
 *
 */

public class ArticleCard {
    private int source_img;
    private String title;

    public ArticleCard(int source, String title) {
        this.source_img = source;
        this.title = title;
    }

    public int getSource() {
        return source_img;
    }

    public String getTitle() {
        return title;
    }

    public void setSource(int source) {
        this.source_img = source;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
