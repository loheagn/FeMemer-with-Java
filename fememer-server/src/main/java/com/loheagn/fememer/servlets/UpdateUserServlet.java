package com.loheagn.fememer.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * UpdateUserServlet
 */
public class UpdateUserServlet extends MyServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        response.setContentType("text/html");
        if (dbop.updateUser(name, password))
            response.getWriter().print("ok");
        else
            response.getWriter().print("error");
    }
}