package com.loheagn.fememer.servlets;

import java.util.List;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

/**
 * GetAllTagsServlet
 */
public class GetAllTagsServlet extends MyServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        List<String> tags = dbop.getAllTags(id);
        if (tags != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(JSON.toJSONString(tags).getBytes("UTF-8"));
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