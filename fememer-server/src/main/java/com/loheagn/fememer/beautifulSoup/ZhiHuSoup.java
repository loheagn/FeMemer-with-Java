package com.loheagn.fememer.beautifulSoup;

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import com.loheagn.fememer.tools.*;
import com.loheagn.fememer.render.*;

/**
 * ZhiHuSoup
 */
public class ZhiHuSoup extends BeautifulSoup {

    public ZhiHuSoup() {
        super();
        zhihuPath = Values.getWebappPath() + zhihuPath;
        File dir = new File(zhihuPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    @Override
    Element dealwithImgElement(String dirName, Element imgElement) {
        Element tmp = imgElement.selectFirst("img");
        String url = "";
        if (tmp != null) {
            if (tmp.attr("src").equals("")) {
                return imgElement;
            }
            url = tmp.attr("src");
        } else {
            tmp = imgElement.selectFirst("div[class~=VagueImage origin_image*]");

            if (tmp == null || tmp.attr("data-src").equals("")) {
                return imgElement;
            }
            // 获取图片的url
            url = tmp.attr("data-src");
        }
        // 获取完整的文件名
        String shortFileName;
        img_count++;
        String imgName = img_count.toString();

        // 处理一下文件名的后缀
        imgName = imgName + ".gif";
        shortFileName = imgName;
        imgName = dirName + imgName;
        // 下载图片
        chromeHeadless.downImageFile(url, imgName);

        // 清空全部属性
        String src = "src=\"" + "./images/" + shortFileName + "\"";
        String width = " width=\"97%\"";
        String aim = "<img " + src + width + ">";
        imgElement = imgElement.html(aim);
        return imgElement;
    }

    @Override
    public Map<String, String> getAndStoreArticle(String url) {
        System.out.println("getAndStorezhihuArticle");

        File dir = new File(zhihuPath + System.currentTimeMillis());
        dir.mkdir();
        File imageDir = new File(zhihuPath + dir.getName() + "/images");
        imageDir.mkdir();

        Document document = Jsoup.parse(chromeHeadless.getPageSource(url));

        // 获取文章标题
        Element titleElement = document.select("h1[class=QuestionHeader-title]").first();
        String title = titleElement.text();

        // 获取文章作者
        Element authorElement = document.select("div[class=AuthorInfo-badge]").first();
        String author = authorElement.text();

        // 获取回答
        Element contentElement = document.select("div[class=RichContent-inner]").first();
        String contentElementtext = "";

        // 处理noscript
        Elements removes = contentElement.select("noscript");
        for (Element r : removes)
            r.html(" ");

        // 处理正文中的所有图片标签
        Elements imgElements = contentElement.getElementsByTag("figure");
        System.out.println(imgElements.size());

        for (Element imgElement : imgElements) {
            dealwithImgElement(zhihuPath + dir.getName() + "/images/", imgElement);
        }

        // 渲染整个html页面
        String wholeHtmlString = Render.renderArticle(title, author, "知乎问答", "默认", url, " ", contentElement.html());

        File htmlFile = new File(zhihuPath + dir.getName() + "/index.html");
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
        return returnArticleMap("zhihu/" + dir.getName(), "zhihu", title);
    }

    public static void main(String[] args) {
        System.out.println(
                new ZhiHuSoup().getAndStoreArticle("https://www.zhihu.com/question/290849728/answer/708829331"));
    }
}