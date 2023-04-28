package controller;

import filter.AuthenHanding;
import filter.AuthorList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProjectController",urlPatterns = {"/project","/project-add","/project-edit","/project-detail"})
public class ProjectController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthenHanding authenHanding = new AuthenHanding();
        int roleId = authenHanding.getRoleOfUser(req);

        if(roleId == AuthorList.STAFF.getValue()){
            req.getRequestDispatcher("403.jsp").forward(req,resp);
        } else {
            String servletPath = req.getServletPath();
            switch (servletPath){
                case "/project":
                    req.getRequestDispatcher("project.jsp").forward(req,resp);
                    break;
                case "/project-detail":
                    req.getRequestDispatcher("project-detail.jsp").forward(req,resp);
                    break;
                case "/project-add":
                    req.getRequestDispatcher("project-add.jsp").forward(req,resp);
                    break;
                case "/project-edit":
                    req.getRequestDispatcher("project-edit.jsp").forward(req,resp);
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
