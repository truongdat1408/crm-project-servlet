package api;

import com.google.gson.Gson;
import model.StatusList;
import model.TaskModel;
import service.HomeService;
import filter.AuthorList;
import model.RoleModel;
import model.UserModel;
import payload.BasicResponse;
import service.UserService;
import filter.AuthenHanding;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "UserApi",urlPatterns = {"/api/user","/api/user-detail","/api/user-add","/api/user-edit"})
public class UserApi extends HttpServlet {
    UserModel user = new UserModel();
    String memberID = null;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<BasicResponse> responseList = new ArrayList<>();
        BasicResponse basicResponse = new BasicResponse();

        String servletPath = req.getServletPath();
        switch (servletPath){
            case "/api/user":
                responseList = doGetOfUser(req);
                break;
            case "/api/user-add":
                responseList = doGetOfAddUser(req);
                break;
            case "/api/user-edit":
                responseList = doGetOfEditUser(req);
                break;
            case "/api/user-detail":
                responseList = doGetOfUserDetail(req);
                break;
            default:
                basicResponse.setStatusCode(404);
                basicResponse.setMessage("Không tồn tại URL");

                responseList.add(basicResponse);
                break;
        }

        Gson gson = new Gson();
        String dataJson = gson.toJson(responseList);

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        PrintWriter printWriter = resp.getWriter();
        printWriter.print(dataJson);
        printWriter.flush();
        printWriter.close();
    }

    private List<BasicResponse> doGetOfUser(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy toàn bộ danh sách user
        basicResponse = getAllUser();
        responseList.add(basicResponse);

        return responseList;
    }

    private BasicResponse getAllUser(){
        BasicResponse basicResponse = new BasicResponse();
        UserService userService = new UserService();
        List<UserModel> listUser = userService.getAllUser();

        if(listUser.size()>0){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách thành viên thành công");
            basicResponse.setData(listUser);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách thành viên thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private List<BasicResponse> doGetOfAddUser(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy danh sách role
        basicResponse = getRoleList();
        responseList.add(basicResponse);

        return responseList;
    }

    private BasicResponse getRoleList(){
        BasicResponse basicResponse = new BasicResponse();
        UserService userService = new UserService();

        List<RoleModel> listRole = userService.getAllRole();

        if(listRole.size()>0){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách quyền thành công");
            basicResponse.setData(listRole);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách quyền thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private List<BasicResponse> doGetOfEditUser(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user đang đăng nhập
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy thông tin user để edit
        basicResponse = getMember(memberID);
        responseList.add(basicResponse);

        // Lấy danh sách role để edit
        basicResponse = getRoleList();
        responseList.add(basicResponse);

        memberID = null;
        return responseList;
    }

    private BasicResponse getMember(String memberId){
        BasicResponse basicResponse = new BasicResponse();

        if(memberId != null && !"".equals(memberId)){
            UserService userService = new UserService();
            UserModel user = userService.getMember(Integer.parseInt(memberId));
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy thông tin thành viên thành công");
            basicResponse.setData(user);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy thông tin thành viên thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private List<BasicResponse> doGetOfUserDetail(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user đang đăng nhập
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy thông tin chi tiết của thành viên
        basicResponse = getMember(memberID);
        responseList.add(basicResponse);

        // Thống kê task của thành viên
        basicResponse = getTaskStatics(memberID);
        responseList.add(basicResponse);

        // Lấy danh sách chi tiết task của thành viên
        basicResponse = getTaskList(memberID);
        responseList.add(basicResponse);

        memberID = null;
        return responseList;
    }

    private BasicResponse getTaskStatics(String memberId){
        BasicResponse basicResponse = new BasicResponse();
        int[] staticsList = {0,0,0};   //1-unbegun num, 2-doing, 3-finish

        if(memberId != null && !"".equals(memberId)){
            HomeService homeService = new HomeService();
            List<Integer> taskStatusList = homeService.getTaskStatus(Integer.parseInt(memberId));
            if(taskStatusList.size()>0) {
                for (int i: taskStatusList) {
                    if(i == StatusList.UNBEGUN.getValue()){
                        staticsList[0]++;
                    } else if(i == StatusList.DOING.getValue()){
                        staticsList[1]++;
                    } else if(i == StatusList.FINISH.getValue()){
                        staticsList[2]++;
                    }
                }
                staticsList[0] = Math.round(staticsList[0] * 100.0f / taskStatusList.size());
                staticsList[1] = Math.round(staticsList[1] * 100.0f / taskStatusList.size());
                staticsList[2] = Math.round(staticsList[2] * 100.0f / taskStatusList.size());
            }
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách thống kê công việc của thành viên thành công");
            basicResponse.setData(staticsList);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách thống kê công việc của thành viên thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse getTaskList(String memberId){
        BasicResponse basicResponse = new BasicResponse();

        if(memberId != null && !"".equals(memberId)){
            UserService userService = new UserService();
            List<TaskModel> taskList = userService.getTaskListOfMember(Integer.parseInt(memberId));
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách công việc thành công");
            basicResponse.setData(taskList);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách công việc thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicResponse basicResponse = new BasicResponse();

        String servletPath = req.getServletPath();
        String function = req.getParameter("function");;
        switch (servletPath){
            case "/api/user":
                basicResponse = doPostOfUser(req,resp,function);
                break;
            case "/api/user-add":
                basicResponse = doPostOfAddUser(req,resp,function);
                break;
            case "/api/user-edit":
                basicResponse = doPostOfEditUser(req,resp,function);
                break;
            case "/api/user-detail":
                basicResponse = doPostOfUserDetail(req,resp,function);
                break;
            default:
                basicResponse.setStatusCode(404);
                basicResponse.setMessage("Không tồn tại URL");
                break;
        }

        Gson gson = new Gson();
        String dataJson = gson.toJson(basicResponse);

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        PrintWriter printWriter = resp.getWriter();
        printWriter.print(dataJson);
        printWriter.flush();
        printWriter.close();
    }

    private BasicResponse doPostOfUser(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "goToAddUser":
                basicResponse = goToAddUser();
                break;
            case "deleteUser":
                int memberId = Integer.parseInt(req.getParameter("memberID"));
                basicResponse = deleteUser(memberId);
                break;
            case "goToEditUser":
                memberID = req.getParameter("memberID");
                basicResponse = goToEditUser(memberID);
                break;
            case "goToUserDetail":
                memberID = req.getParameter("memberID") ;
                basicResponse = goToUserDetail(memberID);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse goToAddUser(){
        BasicResponse basicResponse = new BasicResponse();

        basicResponse.setStatusCode(200);
        basicResponse.setMessage("Truy cập vào trang thêm thành viên");
        basicResponse.setData("/user-add");

        return basicResponse;
    }

    private BasicResponse deleteUser(int memberId){
        BasicResponse basicResponse = new BasicResponse();
        UserService userService = new UserService();

        if(user.getRole().getId() == AuthorList.ADMIN.getValue()){
            if(userService.checkExistingOfTaskByUserId(memberId)){
                basicResponse.setStatusCode(400);
                basicResponse.setMessage("Không thể xóa thành viên này");
                basicResponse.setData(-1);
            } else if(userService.checkExistingOfProjectByLeaderId(memberId)){
                basicResponse.setStatusCode(400);
                basicResponse.setMessage("Không thể xóa thành viên này");
                basicResponse.setData(-2);
            }
            else {
                if(userService.deleteUser(memberId)){
                    basicResponse.setStatusCode(200);
                    basicResponse.setMessage("Xóa thành viên thành công");
                    basicResponse.setData(1);
                }else {
                    basicResponse.setStatusCode(400);
                    basicResponse.setMessage("Xóa thành viên thất bại");
                    basicResponse.setData(0);
                }
            }
        } else {
            basicResponse.setStatusCode(403);
            basicResponse.setMessage("Người dùng không có quyền xóa");
            basicResponse.setData(403);
        }

        return basicResponse;
    }

    private BasicResponse goToEditUser(String memberId){
        BasicResponse basicResponse = new BasicResponse();

        if(memberId != null && !"".equals(memberId)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Truy cập vào trang chỉnh sửa thành viên");
            basicResponse.setData("/user-edit");
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy ID của thành viên muốn chỉnh sửa");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse goToUserDetail(String memberId){
        BasicResponse basicResponse = new BasicResponse();

        if(memberId != null && !"".equals(memberId)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Truy cập vào trang chi tiết thành viên");
            basicResponse.setData("/user-detail");
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy ID của thành viên muốn xem");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse doPostOfAddUser(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "addUser":
                basicResponse = addUser(req);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse addUser(HttpServletRequest req){
        BasicResponse basicResponse = new BasicResponse();

        UserModel newUser = new UserModel();
        newUser.setEmail(req.getParameter("email"));
        newUser.setPassword(req.getParameter("password"));
        newUser.setFullname(req.getParameter("fullname"));
        newUser.setAvatar(req.getParameter("avatar"));
        RoleModel role = new RoleModel();
        role.setId(Integer.parseInt(req.getParameter("role-id")));
        newUser.setRole(role);

        String confirmPassword = req.getParameter("confirm-password");

        if(!newUser.getPassword().equals(confirmPassword)){
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Xác nhận mật khẩu không khớp");
            basicResponse.setData(-1);

            return basicResponse;
        } else if("".equals(newUser.getEmail())) {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Email chưa được nhập!");
            basicResponse.setData(-2);

            return basicResponse;
        }

        UserService userService = new UserService();
        if(userService.addUser(newUser)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Thêm thành viên thành công");
            basicResponse.setData(1);
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Thêm thành viên thất bại");
            basicResponse.setData(0);
        }

        return basicResponse;
    }

    private BasicResponse doPostOfEditUser(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "editUser":
                basicResponse = editMember(req);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse editMember(HttpServletRequest req){
        BasicResponse basicResponse = new BasicResponse();

        String userId = req.getParameter("id");
        if("0".equals(userId)){
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy dữ liệu thành viên");
            basicResponse.setData(-3);

            return basicResponse;
        }

        UserModel user = new UserModel();
        user.setId(Integer.parseInt(userId));
        user.setEmail(req.getParameter("email"));
        user.setPassword(req.getParameter("password"));
        user.setFullname(req.getParameter("fullname"));
        user.setAvatar(req.getParameter("avatar"));
        RoleModel role = new RoleModel();
        role.setId(Integer.parseInt(req.getParameter("role-id")));
        user.setRole(role);

        String confirmPassword = req.getParameter("confirm-password");

        if(!user.getPassword().equals(confirmPassword)){
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Xác nhận mật khẩu không khớp");
            basicResponse.setData(-1);

            return basicResponse;
        } else if("".equals(user.getEmail())) {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Email bị bỏ trống");
            basicResponse.setData(-2);

            return basicResponse;
        }

        UserService userService = new UserService();
        if(userService.updateUser(user)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Chỉnh sửa thông tin thành viên thành công");
            basicResponse.setData(1);
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Chỉnh sửa thông tin thành viên thất bại");
            basicResponse.setData(0);
        }

        return basicResponse;
    }

    private BasicResponse doPostOfUserDetail(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            default:
                break;
        }

        return basicResponse;
    }




}
