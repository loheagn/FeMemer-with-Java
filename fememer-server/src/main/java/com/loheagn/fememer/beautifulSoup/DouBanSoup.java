package com.loheagn.fememer.beautifulSoup;

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import com.loheagn.fememer.tools.*;
import com.loheagn.fememer.render.*;

/**
 * DouBanSoup
 */
public class DouBanSoup extends BeautifulSoup {

    public DouBanSoup() {
        super();
        douBanPath = Values.getWebappPath() + douBanPath;
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
        System.out.println(url);

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
        File dir = new File(douBanPath + System.currentTimeMillis());
        dir.mkdir();
        File imageDir = new File(douBanPath + dir.getName() + "/images");
        imageDir.mkdir();
        System.out.println("DouBan");

        Document document = Jsoup.parse(chromeHeadless.getPageSource(url));

        // 获取文章标题
        Element titleElement = document.select("h1").first();
        String title = titleElement.text();

        Element contentElement = document.select("div[class=review-content clearfix]").first();
        String author = contentElement.attr("data-author");

        // 处理正文中的所有图片标签
        Elements imgElements = contentElement.getElementsByTag("img");
        for (Element imgElement : imgElements) {
            dealwithImgElement(douBanPath + dir.getName() + "/images/", imgElement);
        }

        // 渲染整个html页面
        String wholeHtmlString = Render.renderArticle(title, author, "豆瓣长影评", "默认", url, " ", contentElement.html());

        File htmlFile = new File(douBanPath + dir.getName() + "/index.html");
        OutputStream outputStream = null;
        try {
            htmlFile.createNewFile();
            System.out.println(htmlFile.getAbsolutePath());
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
        return returnArticleMap("douban/" + dir.getName(), "douban", title);
    }

    public static void main(String[] args) {
        System.out.println(
                new DouBanSoup().getAndStoreArticle("https://www.douban.com/doubanapp/dispatch/review/10163255"));
    }
}