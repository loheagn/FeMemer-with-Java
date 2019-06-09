package com.loheagn.fememer.beautifulSoup;

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import com.loheagn.fememer.tools.*;
import com.loheagn.fememer.render.*;

/**
 * BiliBiliSoup
 */
public class BiliBiliSoup extends BeautifulSoup {

    public BiliBiliSoup() {
        super();
        biliPath = Values.getWebappPath() + biliPath;
        File dir = new File(biliPath);
        if (!dir.exists())
            dir.mkdir();
    }

    @Override
    Element dealwithImgElement(String dirName, Element imgElement) {
        // 如果图片标签里没有这个属性，那么这个标签没有处理的必要
        if (imgElement.attr("data-src").equals("")) {
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

        System.out.println(imgElement);

        // 获取图片的url
        String url = imgElement.attr("data-src");
        if (url.indexOf("https:") == -1)
            url = "https:" + url;

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
        File dir = new File(biliPath + System.currentTimeMillis());
        dir.mkdir();
        File imageDir = new File(biliPath + dir.getName() + "/images");
        imageDir.mkdir();
        System.out.println("bili");
        Document document = Jsoup.parse(chromeHeadless.getPageSource(url));

        // 获取文章标题
        Element titleElement = document.select("h1[class=title]").first();
        String title = titleElement.text();
        System.out.println(title);

        // 获取文章来源的公众号名称

        String source = "";

        Element authorElement = document.select("a[class~=up-name*]").first();
        String author = authorElement.text();
        System.out.println(author);

        // 获取文章的正文
        Element contentElement = document.select("div[class=article-holder unable-reprint]").first();

        // 处理正文中的所有图片标签
        Elements imgElements = contentElement.getElementsByTag("img");
        for (Element imgElement : imgElements) {
            dealwithImgElement(biliPath + dir.getName() + "/images/", imgElement);
        }

        // 渲染整个html页面
        String wholeHtmlString = Render.renderArticle(title, author, "bilibili", "默认", url, source,
                contentElement.html());

        File htmlFile = new File(biliPath + dir.getName() + "/index.html");
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
        return returnArticleMap("bili/" + dir.getName(), "bili", title);
    }

    public static void main(String[] args) {
        System.out.println(new BiliBiliSoup().getAndStoreArticle("https://www.bilibili.com/read/cv2796551"));
    }
}
