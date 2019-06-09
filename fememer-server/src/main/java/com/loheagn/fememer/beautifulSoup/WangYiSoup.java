package com.loheagn.fememer.beautifulSoup;

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import com.loheagn.fememer.tools.*;
import com.loheagn.fememer.render.*;

/**
 * WangYiSoup
 */
public class WangYiSoup extends BeautifulSoup {

    public WangYiSoup() {
        super();
        wangyiPath = Values.getWebappPath() + wangyiPath;
    }

    @Override
    Element dealwithImgElement(String dirName, Element imgElement) {
        return null;
    }

    @Override
    public Map<String, String> getAndStoreArticle(String url) {
        File dir = new File(wangyiPath + System.currentTimeMillis());
        dir.mkdir();
        File imageDir = new File(wangyiPath + dir.getName() + "/images");
        imageDir.mkdir();
        System.out.println("wangyi");
        Document document = Jsoup.parse(chromeHeadless.getPageSource(url));
        // 获取文章标题
        Element titleElement = document.select("h1[class=g-title]").first();
        String title = titleElement.text();
        System.out.println(title);

        // 获取文章来源的公众号名称
        Element sourceElement = document.select("div[class=g-subtitle]").first().selectFirst("b");
        String source = sourceElement.text();
        System.out.println(source);

        // 获取文章的正文
        Element contentElement = document.select("article").first();
        Element remove1 = contentElement.select("div[class=m-newsapplite-redpacket-card]").first();
        remove1.html(" ");
        Element remove2 = contentElement.select("span[class=like js-like]").first();
        remove2.html(" ");
        // 渲染整个html页面
        String wholeHtmlString = Render.renderArticle(title, "", "网易新闻网", "默认", url, source, contentElement.html());

        File htmlFile = new File(wangyiPath + dir.getName() + "/index.html");
        OutputStream outputStream = null;
        try {
            htmlFile.createNewFile();
            outputStream = new FileOutputStream(htmlFile);
            outputStream.write(wholeHtmlString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnArticleMap("wangyi/" + dir.getName(), "wangyi", title);
    }
}