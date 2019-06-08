package com.loheagn.fememer.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * deleteArticleServlet
 */
public class DeleteArticleServlet extends MyServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String title = request.getParameter("title");
        response.setContentType("text/html");
        if (dbop.deleteArticleByIDAndTitle(id, title))
            response.getWriter().print("ok");
        else
            response.getWriter().print("error");
    }
}