package com.loheagn.fememer;

import java.io.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

public class ChromeHeadless {
    private String chromeDriverPath = "/usr/local/bin/chromedriver";
    private String chromePath = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
    WebDriver driver;

    public ChromeHeadless() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
        options.setBinary(chromePath);
        driver = new ChromeDriver(options);
    }

    public String getPageSource(String url) {
        try {
            driver.get(url);
            return driver.getPageSource();
        } catch (Exception e) {
            return "抓取网页错误！";
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

    public static void main(String[] args) throws IOException {
        ChromeHeadless chromeHeadless = new ChromeHeadless();
        File file = new File("HtmlDemo.html");
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(
                chromeHeadless.getPageSource("https://www.zhihu.com/question/327780925/answer/704644555").getBytes());
        System.out.println(chromeHeadless.getPageSource("https://www.zhihu.com/question/327780925/answer/704644555"));
    }
}