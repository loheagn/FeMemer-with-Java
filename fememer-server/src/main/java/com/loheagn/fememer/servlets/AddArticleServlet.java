package com.loheagn.fememer.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.*;

import com.loheagn.fememer.dbop.*;

import com.loheagn.fememer.beautifulSoup.*;

import java.net.URLDecoder;

/**
 * AddArticle
 */
public class AddArticleServlet extends MyServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        values.setWebappPath(request.getSession().getServletContext().getRealPath("/"));
        int id = Integer.parseInt(request.getParameter("id"));
        String webUrl = request.getParameter("url");
        webUrl = URLDecoder.decode(webUrl, "UTF-8");
        BeautifulSoup beautifulSoup = null;
        if (webUrl.indexOf("mp.weixin.qq.com") != -1) {
            beautifulSoup = new WeiXinSoup();
        } else if (webUrl.indexOf("www.zhihu.com") != -1) {
            beautifulSoup = new ZhiHuSoup();
        } else if (webUrl.indexOf("zhuanlan.zhihu.com") != -1) {
            beautifulSoup = new ZhiHuZhuanLanSoup();
        } else if (webUrl.indexOf("www.bilibili.com") != -1) {
            beautifulSoup = new BiliBiliSoup();
        } else if (webUrl.indexOf("www.douban.com") != -1) {
            beautifulSoup = new DouBanSoup();
        } else if (webUrl.indexOf("ifeng.com") != -1) {
            beautifulSoup = new FengHuangSoup();
        } else {
            beautifulSoup = new TouTiaoSoup();
        }
        System.out.println("\n\n\n" + webUrl);
        Map<String, String> articleMap = beautifulSoup.getAndStoreArticle(webUrl);
        Article article = dbop.insertArticle(id, articleMap.get("source"), articleMap.get("title"), webUrl,
                articleMap.get("localUrl"));

        if (article != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(JSON.toJSONString(article.convertToMap()).getBytes("UTF-8"));
            outputStream.close();
        } else {
            response.setContentType("text/html");
            response.getWriter().print("error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}