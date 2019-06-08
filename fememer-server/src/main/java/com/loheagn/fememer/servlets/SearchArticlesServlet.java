package com.loheagn.fememer.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.loheagn.fememer.dbop.Article;

/**
 * SearchArticlesServlet
 */
public class SearchArticlesServlet extends MyServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String word = request.getParameter("searchData");
        List<Article> responseData = dbop.searchArticles(id, word);
        if (responseData != null) {
            List<Map<String, Object>> returnMap = new ArrayList<Map<String, Object>>();
            returnMap.clear();
            for (Article article : responseData) {
                returnMap.add(article.convertToMap());
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(JSON.toJSONString(returnMap).getBytes("UTF-8"));
            outputStream.close();
        } else {
            response.setContentType("text/html");
            response.getWriter().print("error");
        }
    }
}