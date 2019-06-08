package com.loheagn.fememer.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DeleteUserServlet
 */
public class DeleteUserServlet extends MyServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        response.setContentType("text/html");
        if (dbop.deleteUserByID(id))
            response.getWriter().print("ok");
        else
            response.getWriter().print("error");
    }
}