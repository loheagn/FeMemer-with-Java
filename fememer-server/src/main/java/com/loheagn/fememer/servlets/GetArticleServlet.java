package com.loheagn.fememer.servlets;

import java.io.*;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.loheagn.fememer.dbop.*;

/**
 * GetArticleServlet
 */
public class GetArticleServlet extends MyServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("\n\n\n\n\n\noooookkkkk");
        int id = Integer.parseInt(request.getParameter("id"));
        String title = request.getParameter("title");
        title = URLDecoder.decode(title, "UTF-8");
        System.out.println(title);
        Article responseData = dbop.getArticleByIDAndTitle(id, title);
        if (responseData != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(JSON.toJSONString(responseData.convertToMap()).getBytes("UTF-8"));
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