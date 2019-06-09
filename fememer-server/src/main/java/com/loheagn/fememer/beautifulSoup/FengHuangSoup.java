package com.loheagn.fememer.beautifulSoup;

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import com.loheagn.fememer.tools.*;
import com.loheagn.fememer.render.*;

/**
 * FengHuangSoup
 */
public class FengHuangSoup extends BeautifulSoup {

    public FengHuangSoup() {
        super();
        fenghuangPath = Values.getWebappPath() + fenghuangPath;
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
        String url = imgElement.attr("data-lazyload");
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
        File dir = new File(fenghuangPath + System.currentTimeMillis());
        dir.mkdir();
        File imageDir = new File(fenghuangPath + dir.getName() + "/images");
        imageDir.mkdir();
        System.out.println("fenghuang");
        Document document = Jsoup.parse(chromeHeadless.getPageSource(url));

        // 获取文章标题
        Element titleElement = document.selectFirst("section[class~=container*]").selectFirst("h2[class~=title*]");
        String title = titleElement.text();
        System.out.println(title);

        // 获取文章来源的公众号名称
        Element sourceElement = document.selectFirst("span[class~=source*]");
        String source = sourceElement.text();
        String author = "";
        System.out.println(source);

        // 获取文章的正文
        Element contentElement = document.select("div[class~=smallFont*]").first();

        // 处理正文中的所有图片标签
        Elements imgElements = contentElement.getElementsByTag("img");
        for (Element imgElement : imgElements) {
            dealwithImgElement(fenghuangPath + dir.getName() + "/images/", imgElement);
        }

        // 渲染整个html页面
        String wholeHtmlString = Render.renderArticle(title, author, "凤凰网", "默认", url, source, contentElement.html());

        File htmlFile = new File(fenghuangPath + dir.getName() + "/index.html");
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
        return returnArticleMap("fenghuang/" + dir.getName(), "fenghuang", title);
    }

    public static void main(String[] args) {
        System.out.println(new FengHuangSoup()
                .getAndStoreArticle("https://ishare.ifeng.com/c/s/7nMNggdhVB2?aman=6hc00L5286c94N6acb970"));
    }
}