package com.bankrupted.tradein.service.Servlet;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet{
    protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,
            IOException{
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username+":"+password);
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        String msg = null;
        if(username != null && username.equals("antkingwei") && password != null && password.equals("123")){
            msg="登录成功";
        }
        else {
            msg = "登录失败";
        }
        out.print(msg);
        out.flush();
        out.close();

    }
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        doGet(request,response);
    }
}