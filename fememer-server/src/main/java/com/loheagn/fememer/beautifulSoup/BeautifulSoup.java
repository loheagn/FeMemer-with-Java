package com.loheagn.fememer.beautifulSoup;

import java.util.*;
import org.jsoup.nodes.*;
import com.loheagn.fememer.downLoader.*;

/**
 * beautifulSoup
 */
public abstract class BeautifulSoup {

    static String weixinPath = "/weixin/";
    static String douBanPath = "douban\\";
    static String zhihuPath = "zhihu/";
    static Long img_count = 0L;
    ChromeHeadless chromeHeadless;

    public BeautifulSoup() {
        chromeHeadless = new ChromeHeadless();
    }

    public BeautifulSoup(String chromeDriverPath, String chromePath) {
        chromeHeadless = new ChromeHeadless(chromeDriverPath, chromePath);
    }

    /**
     * 根据传入的数据构造一个hashmap
     * 
     * @param localUrl 文章本地的位置
     * @param source   文章的来源类别
     * @param title    文章的标题
     * @return
     */
    Map<String, String> returnArticleMap(String localUrl, String source, String title) {
        Map<String, String> returnData = new HashMap<String, String>();
        returnData.clear();
        returnData.put("source", source);
        returnData.put("title", title);
        returnData.put("localUrl", localUrl);
        return returnData;
    }

    /**
     * 爬取微信页面的时候处理img标签的方法，主要是把把图片下载下来， 保存到相应的位置，然后修改img标签的各个属性，使其指向本机的图片
     * 
     * @param dirName    存放图片的目录，对于微信，一般应该是/weixin/时间戳字符串/images/
     * @param imgElement 要修改的图片标签
     * @return
     */
    abstract Element dealwithImgElement(String dirName, Element imgElement);

    /**
     * 根据url爬取文章，并存储在合适的位置。
     * 
     * @param url 用户传入要爬取的文章的链接
     */
    abstract public Map<String, String> getAndStoreArticle(String url);
}