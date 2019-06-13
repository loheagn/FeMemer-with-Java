package com.loheagn.fememer.downLoader;

import java.io.*;
import java.net.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

public class ChromeHeadless {
    private String chromeDriverPath;
    private String chromePath;
    private WebDriver driver;
    private ChromeOptions options;

    public ChromeHeadless() {
        setChromeDriverPath("/usr/local/bin/chromedriver");
        setChromePath("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
        options.setBinary(chromePath);
    }

    public ChromeHeadless(String chromeDriverPath, String chromePath) {
        this.chromeDriverPath = chromeDriverPath;
        this.chromePath = chromePath;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
        options.setBinary(chromePath);
    }

    public String getPageSource(String url) {
        try {
            driver = new ChromeDriver(options);
            driver.get(url);
            return driver.getPageSource();
        } catch (Exception e) {
            return "抓取网页源代码错误！";
        } finally {
            if (driver != null) {
                driver.close();
            }
        }
    }

    /**
     * 下载图片到本地，保存到指定位置
     * 
     * @param urlString 图片的url
     * @param fileName  本地存放文件的位置，这是个全路径，包含路径和文件名
     * @return 返回图片文件对象
     */
    public File downImageFile(String urlString, String fileName) {
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(5000);
            InputStream inputStream = urlConnection.getInputStream();
            File imageFile = new File(fileName);
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] bytes = new byte[4096];
            int length;
            while ((length = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return imageFile;
        } catch (Exception e) {
            System.err.println("下载图片" + fileName + "失败");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return the chromeDriverPath
     */
    public String getChromeDriverPath() {
        return chromeDriverPath;
    }

    /**
     * @return the chromePath
     */
    public String getChromePath() {
        return chromePath;
    }

    /**
     * @param chromeDriverPath the chromeDriverPath to set
     */
    public void setChromeDriverPath(String chromeDriverPath) {
        this.chromeDriverPath = chromeDriverPath;
    }

    /**
     * @param chromePath the chromePath to set
     */
    public void setChromePath(String chromePath) {
        this.chromePath = chromePath;
    }

    /**
     * @return the driver
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * @param driver the driver to set
     */
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public static void main(String[] args) throws IOException {
        ChromeHeadless chromeHeadless = new ChromeHeadless();
        File file = new File("ttt.html");
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(chromeHeadless.getPageSource(
                "https://mp.weixin.qq.com/s?__biz=MjM5MTA0MzIxNg==&mid=2651030558&idx=1&sn=7eb09446fafd34468004ae9c8da94109&chksm=bd4cbfe18a3b36f78b146b6ef5e0f30a3eb7b890a26aafbaf75aac2df8ca2ff8c9a9f269d7c1&bizpsid=0&scene=126&ascene=3&devicetype=android-27&version=2700043c&nettype=WIFI&abtest_cookie=BQABAAoACwASABMAFQAHACOXHgBWmR4Aw5keANyZHgDxmR4AA5oeAAmaHgAAAA%3D%3D&lang=zh_CN&pass_ticket=SV7qt9KJ0b4PpnqsOxx3nzTmXJZo9iib3DKkBtE%2Brh%2Bs9%2BmXW7xlHRAzBT%2BvmVcz&wx_header=1")
                .getBytes());
        outputStream.close();
        // chromeHeadless.downImageFile("https://img3.doubanio.com/view/thing_review/l/public/p2811970.webp",
        // "test.png");
    }
}