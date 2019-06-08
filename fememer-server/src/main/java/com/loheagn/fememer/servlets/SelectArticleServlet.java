package com.loheagn.fememer.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(JSON.toJSONString(responseData).getBytes("UTF-8"));
            outputStream.close();
        } else {
            response.setContentType("text/html");
            response.getWriter().print("error");
        }
    }
}