package com.loheagn.fememer.servlets;

import javax.servlet.http.HttpServlet;
import com.loheagn.fememer.dbop.Dbop;
import com.loheagn.fememer.tools.Values;

import com.loheagn.fememer.beautifulSoup.*;

/**
 * MyServlet
 */
public class MyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    Dbop dbop = new Dbop();
    Values values = new Values();
    BeautifulSoup beautifulSoup = new BeautifulSoup();
}