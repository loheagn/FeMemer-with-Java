package com.loheagn.fememer;

import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

/**
 * BeautifulSoup
 */
public class BeautifulSoup {

    static String weixinPath = "src/main/webapp/weixin/";
    static String zhihuPath = "zhihu/";
    Long img_count = 0L;
    ChromeHeadless chromeHeadless = new ChromeHeadless();

    public BeautifulSoup() {
    }

    public BeautifulSoup(String chromeDriverPath, String chromePath) {
        chromeHeadless = new ChromeHeadless(chromeDriverPath, chromePath);
    }

    /**
     * 爬取微信页面的时候处理img标签的方法，主要是把把图片下载下来， 保存到相应的位置，然后修改img标签的各个属性，使其指向本机的图片
     * 
     * @param dirName    存放图片的目录，对于微信，一般应该是/weixin/时间戳字符串/images/
     * @param imgElement 要修改的图片标签
     * @return
     */
    private Element dealwithImgElement(String dirName, Element imgElement) {
        // 如果图片标签里没有这个属性，那么这个标签没有处理的必要
        if (imgElement.attr("data-src").equals("")) {
            return imgElement;
        }

        // 获取完整的文件名
        String shortFileName;
        img_count++;
        String imgName = img_count.toString();
        // 处理一下文件名的后缀
        if (!imgElement.attr("data-type").equals("")) {
            imgName = imgName + "." + imgElement.attr("data-type");
        } else {
            imgName = imgName + ".gif";
        }
        shortFileName = imgName;
        imgName = dirName + imgName;

        // 获取图片的url
        String url = imgElement.attr("data-src");

        // 下载图片
        chromeHeadless.downImageFile(url, imgName);

        // 清空全部属性
        imgElement = (Element) imgElement.clearAttributes();

        // 添加src属性和width属性
        imgElement = imgElement.attr("src", "./images/" + shortFileName);
        imgElement = imgElement.attr("width", "97%");

        return imgElement;
    }

    /**
     * 根据url爬取微信文章，并存储在合适的位置。
     * 
     * @param url
     */
    public void getAndStoreWeixinArticle(String url) {
        File dir = new File(weixinPath + System.currentTimeMillis());
        dir.mkdir();
        File imageDir = new File(weixinPath + dir.getName() + "/images");
        imageDir.mkdir();
        Document document = Jsoup.parse(chromeHeadless.getPageSource(url));

        // 获取文章标题
        Element titleElement = document.select("h2[class=rich_media_title]").first();
        String title = titleElement.text();

        // 获取文章作者
        Element authorElement = document.select("span[class=rich_media_meta rich_media_meta_text]").first();
        String author = authorElement.text();
        // 判断是不是原创
        if (document.select("div[id=meta_content]").first().text().indexOf("原创") != -1) {
            author = "（原创）" + author;
        }

        // 获取文章来源的公众号名称
        Element sourceElement = document.select("strong[class=profile_nickname]").first();
        String source = sourceElement.text();

        // 获取文章的正文
        Element contentElement = document.select("div[class=rich_media_content]").first();

        // 处理正文中的所有图片标签
        Elements imgElements = contentElement.getElementsByTag("img");
        for (Element imgElement : imgElements) {
            dealwithImgElement(weixinPath + dir.getName() + "/images/", imgElement);
        }

        // 渲染整个html页面
        String wholeHtmlString = Render.renderArticle(title, author, "微信", "默认", url, source, contentElement.html());

        File htmlFile = new File(weixinPath + dir.getName() + "/index.html");
        OutputStream outputStream = null;
        try {
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
    }

    public static void main(String[] args) throws Exception {
        String url = "https://mp.weixin.qq.com/s/nroHdViXC7tLgJioojupWw";
        BeautifulSoup beautifulSoup = new BeautifulSoup();

        beautifulSoup.getAndStoreWeixinArticle(url);
    }
}