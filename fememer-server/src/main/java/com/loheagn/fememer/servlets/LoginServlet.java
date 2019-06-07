package com.loheagn.fememer.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;

import com.loheagn.fememer.dbop.*;

/**
 * LoginServlat
 */
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 3167196807814576347L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("userName");
        Map<String, Object> responseData = new HashMap<String, Object>();
        responseData.clear();
        if (!new Dbop().thereIsThisUser(name)) {
            responseData.put("flag", "404");
        } else {
            String password = request.getParameter("password");
            User user = new Dbop().getUserByName(name);
            if (user == null) {
                responseData.put("flag", "500");
            } else if (password.equals(user.getPassword())) {
                responseData.put("flag", "ok");
                responseData.put("id", user.getId());
            } else {
                responseData.put("flag", "false");
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(JSON.toJSONString(responseData).getBytes("UTF-8"));
        outputStream.close();
    }

    public static void main(String[] args) {
        System.out.println(new Dbop().getUserByName("15131049"));
    }
}