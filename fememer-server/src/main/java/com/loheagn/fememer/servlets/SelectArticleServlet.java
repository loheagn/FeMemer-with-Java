package com.loheagn.fememer.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        OutputStream outputStream = response.getOutputStream();
        if (source.equals("all")) {
            outputStream.write(JSON.toJSONString(dbop.selectAllArticlesByID(id)).getBytes("UTF-8"));
        } else {
            outputStream.write(JSON.toJSONString(dbop.selectArticlesByIDAndSource(id, source)).getBytes("UTF-8"));
        }
        outputStream.close();
    }
}