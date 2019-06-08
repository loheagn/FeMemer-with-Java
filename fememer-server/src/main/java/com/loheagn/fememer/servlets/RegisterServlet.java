package com.loheagn.fememer.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;

/**
 * RegisterServlet
 */
public class RegisterServlet extends MyServlet {

    private static final long serialVersionUID = -6106782208986631383L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        values.setWebappPath(request.getSession().getServletContext().getRealPath("/"));
        String name = request.getParameter("userName");
        Map<String, Object> responseData = new HashMap<String, Object>();
        responseData.clear();
        if (dbop.thereIsThisUser(name)) {
            responseData.put("flag", "alreadyhave");
        } else {
            String password = request.getParameter("password");
            int id = dbop.insertUser(name, password);
            if (id > 0) {
                responseData.put("flag", "ok");
                responseData.put("id", id);
            } else {
                responseData.put("flag", "500");
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(JSON.toJSONString(responseData).getBytes("UTF-8"));
        outputStream.close();
    }
}