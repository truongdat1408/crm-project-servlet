package api;

import com.google.gson.Gson;
import model.RoleModel;
import model.UserModel;
import payload.BasicResponse;
import service.RoleService;
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

@WebServlet(name = "RoleApi",urlPatterns = {"/api/role","/api/role-add","/api/role-edit"})
public class RoleApi extends HttpServlet {
    UserModel user = new UserModel();
    String roleID = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<BasicResponse> responseList = new ArrayList<>();
        BasicResponse basicResponse = new BasicResponse();

        String servletPath = req.getServletPath();
        switch (servletPath){
            case "/api/role":
                responseList = doGetOfRole(req);
                break;
            case "/api/role-add":
                responseList = doGetOfAddRole(req);
                break;
            case "/api/role-edit":
                responseList = doGetOfEditRole(req);
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

    private List<BasicResponse> doGetOfRole(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy toàn bộ danh sách role
        basicResponse = getAllRole();
        responseList.add(basicResponse);

        return responseList;
    }

    private BasicResponse getAllRole(){
        BasicResponse basicResponse = new BasicResponse();
        RoleService roleService = new RoleService();
        List<RoleModel> listRole = roleService.getAllRole();

        if(listRole.size()>0){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách tất cả role thành công");
            basicResponse.setData(listRole);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách tất cả role thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private List<BasicResponse> doGetOfAddRole(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        return responseList;
    }



    private List<BasicResponse> doGetOfEditRole(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy thông tin role để edit
        basicResponse = getRole(roleID);
        responseList.add(basicResponse);

        roleID = null;

        return responseList;
    }

    private BasicResponse getRole(String roleId){
        BasicResponse basicResponse = new BasicResponse();

        if(roleId != null && !"".equals(roleId)){
            RoleService roleService = new RoleService();
            RoleModel role = roleService.getRole(Integer.parseInt(roleId));
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy thông tin role thành công");
            basicResponse.setData(role);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy thông tin role thất bại");
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
            case "/api/role":
                basicResponse = doPostOfRole(req,resp,function);
                break;
            case "/api/role-add":
                basicResponse = doPostOfAddRole(req,resp,function);
                break;
            case "/api/role-edit":
                basicResponse = doPostOfEditRole(req,resp,function);
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

    private BasicResponse doPostOfRole(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "goToAddRole":
                basicResponse = goToAddRole();
                break;
            case "deleteRole":
                int roleId = Integer.parseInt(req.getParameter("roleID"));
                basicResponse = deleteRole(roleId);
                break;
            case "goToEditRole":
                roleID = req.getParameter("roleID");
                basicResponse = goToEditRole(roleID);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse goToAddRole(){
        BasicResponse basicResponse = new BasicResponse();

        basicResponse.setStatusCode(200);
        basicResponse.setMessage("Truy cập vào trang thêm thành viên");
        basicResponse.setData("/role-add");

        return basicResponse;
    }

    private BasicResponse deleteRole(int roleId){
        BasicResponse basicResponse = new BasicResponse();
        RoleService roleService = new RoleService();

        if(!roleService.checkExistingOfUserByRoleId(roleId)){
            if(roleService.delelteRole(roleId)){
                basicResponse.setStatusCode(200);
                basicResponse.setMessage("Xóa quyền thành công");
                basicResponse.setData(1);
            }else {
                basicResponse.setStatusCode(400);
                basicResponse.setMessage("Xóa quyền thất bại");
                basicResponse.setData(0);
            }
        } else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không thể xóa quyền này");
            basicResponse.setData(-1);
        }


        return basicResponse;
    }

    private BasicResponse goToEditRole(String roleId){
        BasicResponse basicResponse = new BasicResponse();

        if(roleId != null && !"".equals(roleId)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Truy cập vào trang chỉnh sửa quyền");
            basicResponse.setData("/role-edit");
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy ID của quyền muốn chỉnh sửa");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse doPostOfAddRole(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "addRole":
                basicResponse = addRole(req);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse addRole(HttpServletRequest req){
        BasicResponse basicResponse = new BasicResponse();

        RoleModel newRole = new RoleModel();
        newRole.setName(req.getParameter("name"));
        newRole.setDescription(req.getParameter("description"));

        if("".equals(newRole.getName())) {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Tên quyền chưa được nhập!");
            basicResponse.setData(-1);

            return basicResponse;
        }

        RoleService roleService = new RoleService();
        if(roleService.addRole(newRole)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Thêm quyền thành công");
            basicResponse.setData(1);
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Thêm quyền thất bại");
            basicResponse.setData(0);
        }

        return basicResponse;
    }

    private BasicResponse doPostOfEditRole(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "editRole":
                basicResponse = editRole(req);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse editRole(HttpServletRequest req){
        BasicResponse basicResponse = new BasicResponse();

        String roleId = req.getParameter("id");
        if("0".equals(roleId)){
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy dữ liệu quyền");
            basicResponse.setData(-2);

            return basicResponse;
        }

        RoleModel role = new RoleModel();
        role.setId(Integer.parseInt(roleId));
        role.setName(req.getParameter("name"));
        role.setDescription(req.getParameter("description"));

        if("".equals(role.getName())) {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Tên quyền trống");
            basicResponse.setData(-1);

            return basicResponse;
        }

        RoleService roleService = new RoleService();
        if(roleService.editRole(role)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Chỉnh sửa quyền thành công");
            basicResponse.setData(1);
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Chỉnh sửa quyền thất bại");
            basicResponse.setData(0);
        }

        return basicResponse;
    }
}
