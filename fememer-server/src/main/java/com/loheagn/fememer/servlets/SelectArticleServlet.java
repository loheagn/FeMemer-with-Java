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

import com.loheagn.fememer.dbop.*;

/**
 * SelectArticleServlet
 */
public class SelectArticleServlet extends MyServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String source = request.getParameter("source");
        List<Article> responseData = null;
        if (source.equals("all")) {
            responseData = dbop.selectAllArticlesByID(id);
        } else {
            responseData = dbop.selectArticlesByIDAndSource(id, source);
        }
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}