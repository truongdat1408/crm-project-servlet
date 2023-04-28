package filter;

import model.UserModel;
import payload.BasicResponse;
import service.AuthService;
import service.LoginService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenHanding {
    final String COOKIE_NAME = "email";

    // Lấy thông tin người dùng đặng nhập từ cookie
    public BasicResponse getUserInfo(HttpServletRequest req) {
        BasicResponse basicResponse = new BasicResponse();
        CookieHandling cookieHandling = new CookieHandling();
        Cookie cookie = cookieHandling.getCookie(req);

        if(cookie != null){
            AuthService authService = new AuthService();
            UserModel user = authService.getUser(cookie.getValue());
            if(user != null){
                basicResponse.setStatusCode(200);
                basicResponse.setMessage("Lấy thông tin user thành công");
                basicResponse.setData(user);

                return basicResponse;
            } else {
                basicResponse.setMessage("Không tìm thấy user");
            }
        } else {
            basicResponse.setMessage("Không tìm thấy cookie");
        }
        basicResponse.setStatusCode(404);
        basicResponse.setData(null);

        return basicResponse;
    }

    // Đăng xuất
    public BasicResponse logOut(HttpServletRequest req, HttpServletResponse resp){
        BasicResponse basicResponse = new BasicResponse();
        CookieHandling cookieHandling = new CookieHandling();
        Cookie cookie = cookieHandling.getCookie(req);

        if(cookie != null){
            basicResponse.setStatusCode(200);
            cookieHandling.deleteCookie(resp,cookie);
            basicResponse.setMessage("Đăng xuất thành công");
            basicResponse.setData(true);
        } else{
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Đăng xuất thất bại");
            basicResponse.setData(false);
        }

        return basicResponse;
    }

    // Xác minh tài khoản đăng nhập
    public BasicResponse verifyLoginAccount(HttpServletResponse resp, String email, String password){
        BasicResponse basicResponse = new BasicResponse();
        LoginService loginService = new LoginService();

        if(loginService.checkLogin(email,password)){
            CookieHandling cookieHandling = new CookieHandling();
            cookieHandling.addCookie(resp,email);

            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Đăng nhập thành công");
            basicResponse.setData(true);
        } else{
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Đăng nhập thất bại");
            basicResponse.setData(false);
        }

        return basicResponse;
    }

    // Kiểm tra đã login hay chưa
    public boolean isLoggedIn(Cookie[] cookies){
        boolean isLogged = false;
        for (Cookie ck: cookies) {
            if(COOKIE_NAME.equals(ck.getName()) && !("".equals(ck.getValue()))){
                isLogged = true;
                break;
            }
        }

        return isLogged;
    }


    // Kiểm tra phân quyền - lấy quyền của user
    public int getRoleOfUser(HttpServletRequest req){
        CookieHandling cookieHandling = new CookieHandling();
        Cookie cookie = cookieHandling.getCookie(req);
        String email = cookie.getValue();

        AuthService authService = new AuthService();
        return authService.getRoleByEmail(email);
    }
}
