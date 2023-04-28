package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProfileController",urlPatterns = {"/profile","/profile-task-update"})
public class ProfileController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        switch (servletPath){
            case "/profile":
                req.getRequestDispatcher("profile.jsp").forward(req,resp);
                break;
            case "/profile-task-update":
                req.getRequestDispatcher("profile-task-update.jsp").forward(req,resp);
                break;
            default:
                break;
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
