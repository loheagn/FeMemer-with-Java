package com.loheagn.fememer.servlets;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * UpdateTagArticle
 */
public class UpdateTagServlet extends MyServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String title = request.getParameter("title");
        title = URLDecoder.decode(title, "UTF-8");
        String tag = request.getParameter("tag");
        tag = URLDecoder.decode(tag, "UTF-8");
        response.setContentType("text/html");
        if (dbop.updateTagBytitleAndTitle(id, title, tag))
            response.getWriter().print("ok");
        else
            response.getWriter().print("error");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}