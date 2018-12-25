package org.javaee7.servlet.simple;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import d.nrtest.nrcommon.PerformanceMeter;

@WebServlet("/SimpleServlet")
public class SimpleServlet extends HttpServlet {

    private String responseHtml;
    private PerformanceMeter performanceMeter;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            responseHtml = new String(Files.readAllBytes( Paths.get("../../../nodered_resources/d_resources/benchmarkHtmlExample.html")), StandardCharsets.UTF_8);
            performanceMeter = new PerformanceMeter(60000);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.getWriter().print(responseHtml);
        performanceMeter.notifyEvent();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.getWriter().print("my POST");
    }
}
