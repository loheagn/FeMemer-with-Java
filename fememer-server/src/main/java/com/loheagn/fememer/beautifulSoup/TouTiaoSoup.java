package com.loheagn.fememer.beautifulSoup;

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import com.loheagn.fememer.tools.*;
import com.loheagn.fememer.render.*;

/**
 * TouTiaoSoup
 */
public class TouTiaoSoup extends BeautifulSoup {

    public TouTiaoSoup() {
        super();
        toutiaoPath = Values.getWebappPath() + toutiaoPath;
        File dir = new File(toutiaoPath);
        if (!dir.exists())
            dir.mkdir();
    }

    @Override
    Element dealwithImgElement(String dirName, Element imgElement) {
        // 如果图片标签里没有这个属性，那么这个标签没有处理的必要
        if (imgElement.attr("src").equals("")) {
            return imgElement;
        }

        // 获取完整的文件名
        String shortFileName;
        img_count++;
        String imgName = img_count.toString();
        // 处理一下文件名的后缀

        imgName = imgName + ".gif";

        shortFileName = imgName;
        imgName = dirName + imgName;

        // 获取图片的url
        String url = imgElement.attr("src");

        // 下载图片
        chromeHeadless.downImageFile(url, imgName);

        // 清空全部属性
        imgElement = (Element) imgElement.clearAttributes();

        // 添加src属性和width属性

        imgElement = imgElement.attr("src", "./images/" + shortFileName);
        imgElement = imgElement.attr("width", "97%");

        return imgElement;
    }

    @Override
    public Map<String, String> getAndStoreArticle(String url) {
        File dir = new File(toutiaoPath + System.currentTimeMillis());
        dir.mkdir();
        File imageDir = new File(toutiaoPath + dir.getName() + "/images");
        imageDir.mkdir();
        System.out.println("toutiao");
        Document document = Jsoup.parse(chromeHeadless.getPageSource(url));

        // 获取文章标题
        Element titleElement = document.select("h1[class=article-title]").first();
        String title = titleElement.text();
        // System.out.println(title);

        // 获取文章来源的公众号名称
        Element sourceElement = document.select("div[class=article-sub]").first();
        String tmp = sourceElement.text();
        int flag = tmp.indexOf("原创");
        int digit = tmp.indexOf("2");
        String origin = "";
        String source = null;
        if (flag == -1) {
            source = tmp.substring(0, digit);
        } else {
            origin = "(原创)";
            source = tmp.substring(2, digit);
        }
        // System.out.println(origin);
        // System.out.println(source);
        String author = "";
        author += origin;

        // 获取文章的正文
        Element contentElement = document.select("div[class=article-content]").first();

        // 处理正文中的所有图片标签
        Elements imgElements = contentElement.getElementsByTag("img");
        for (Element imgElement : imgElements) {
            dealwithImgElement(toutiaoPath + dir.getName() + "/images/", imgElement);
        }

        // 渲染整个html页面
        String wholeHtmlString = Render.renderArticle(title, author, "今日头条", "默认", url, source, contentElement.html());

        File htmlFile = new File(toutiaoPath + dir.getName() + "/index.html");
        OutputStream outputStream = null;
        try {
            htmlFile.createNewFile();
            outputStream = new FileOutputStream(htmlFile);
            outputStream.write(wholeHtmlString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnArticleMap("toutiao/" + dir.getName(), "toutiao", title);
    }

    public static void main(String[] args) {
        System.out.println(new TouTiaoSoup().getAndStoreArticle(
                "https://m.toutiaocdn.com/group/6700152093967122957/?app=news_article&timestamp=1560067576&req_id=20190609160616010152021220726BD04&group_id=6700152093967122957"));
    }
}