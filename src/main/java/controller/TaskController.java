package controller;

import filter.AuthenHanding;
import filter.AuthorList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "TaskController",urlPatterns = {"/task","/task-add","/task-edit"})
public class TaskController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthenHanding authenHanding = new AuthenHanding();
        int roleId = authenHanding.getRoleOfUser(req);

        if(roleId == AuthorList.STAFF.getValue()){
            req.getRequestDispatcher("403.jsp").forward(req,resp);
        } else {
            String servletPath = req.getServletPath();
            switch (servletPath){
                case "/task":
                    req.getRequestDispatcher("task.jsp").forward(req,resp);
                    break;
                case "/task-add":
                    req.getRequestDispatcher("task-add.jsp").forward(req,resp);
                    break;
                case "/task-edit":
                    req.getRequestDispatcher("task-edit.jsp").forward(req,resp);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
