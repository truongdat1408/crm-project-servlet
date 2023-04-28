package api;

import com.google.gson.Gson;
import filter.AuthenHanding;
import model.StatusList;
import model.StatusModel;
import model.TaskModel;
import model.UserModel;
import payload.BasicResponse;
import service.HomeService;
import service.ProfileService;
import service.StatusService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ProfileApi",urlPatterns = {"/api/profile","/api/profile-task-update"})
public class ProfileApi extends HttpServlet {
    UserModel user = new UserModel();
    String taskID = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<BasicResponse> responseList = new ArrayList<>();
        BasicResponse basicResponse = new BasicResponse();

        String servletPath = req.getServletPath();
        switch (servletPath){
            case "/api/profile":
                responseList = doGetOfProfile(req);
                break;
            case "/api/profile-task-update":
                responseList = doGetOfTaskUpdate(req);
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

    private List<BasicResponse> doGetOfProfile(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Thống kê số lượng task của user
        basicResponse = getTaskStatics();
        responseList.add(basicResponse);

        // Lấy toàn bộ task mà user phụ trách
        basicResponse = getAllTaskOfUser();
        responseList.add(basicResponse);

        return responseList;
    }

    private BasicResponse getTaskStatics(){
        BasicResponse basicResponse = new BasicResponse();

        int[] statusList = {0,0,0};   // 0-unbegun qty, 1-doing qty, 2-finish qty

        if(user != null){
            HomeService homeService = new HomeService();
            List<Integer> taskStatusList = homeService.getTaskStatus(user.getId());

            for (int i: taskStatusList) {
                if(i == StatusList.UNBEGUN.getValue()){
                    statusList[0]++;
                } else if(i == StatusList.DOING.getValue()){
                    statusList[1]++;
                } else if(i == StatusList.FINISH.getValue()){
                    statusList[2]++;
                }
            }
            if(taskStatusList.size()>0) {
                statusList[0] = Math.round(statusList[0] * 100.0f / taskStatusList.size());
                statusList[1] = Math.round(statusList[1] * 100.0f / taskStatusList.size());
                statusList[2] = Math.round(statusList[2] * 100.0f / taskStatusList.size());
            }
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách thống kê công việc của người dùng thành công");
            basicResponse.setData(statusList);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách thống kê công việc của người dùng thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse getAllTaskOfUser(){
        BasicResponse basicResponse = new BasicResponse();

        if(user != null){
            basicResponse.setStatusCode(200);
            ProfileService profileService = new ProfileService();
            List<TaskModel> listTask = profileService.getAllTaskOfUser(user.getId());
            basicResponse.setMessage("Lấy danh sách công việc thành công");
            basicResponse.setData(listTask);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách công việc thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private List<BasicResponse> doGetOfTaskUpdate(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy dữ liệu công việc từ taskID có được
        basicResponse = getTask(taskID);
        responseList.add(basicResponse);

        // Lấy danh sách trạng thái công việc
        basicResponse = getAllStatus();
        responseList.add(basicResponse);

        taskID = null;

        return responseList;
    }

    private BasicResponse getAllStatus(){
        BasicResponse basicResponse = new BasicResponse();
        StatusService statusService = new StatusService();
        List<StatusModel> list = statusService.getAllStatus();

        if(list.size()>0){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách trạng thái công việc thành công");
            basicResponse.setData(list);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách trạng thái công việc thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse getTask(String taskId){
        BasicResponse basicResponse = new BasicResponse();

        if(taskId != null && !"".equals(taskId)){
            basicResponse.setStatusCode(200);
            ProfileService profileService = new ProfileService();
            TaskModel task = profileService.getTaskById(Integer.parseInt(taskId));
            basicResponse.setMessage("Lấy dữ liệu công việc thành công");
            basicResponse.setData(task);
        }else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy dữ liệu công việc thất bại");
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
            case "/api/profile":
                basicResponse = doPostOfProfile(req,resp,function);
                break;
            case "/api/profile-task-update":
                basicResponse = doPostOfTaskUpdate(req,resp,function);
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

    private BasicResponse doPostOfProfile(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "goToTaskUpdate":
                taskID = req.getParameter("taskID");
                basicResponse = goToTaskUpdate(taskID);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse goToTaskUpdate(String taskId){
        BasicResponse basicResponse = new BasicResponse();

        if(taskId != null && !"".equals(taskId) ){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Truy cập vào trang cập nhập công việc");
            basicResponse.setData("/profile-task-update");
        } else{
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy ID của công việc muốn cập nhập");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse doPostOfTaskUpdate(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "saveStatus":

                basicResponse = updateTaskStatus(req);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse updateTaskStatus(HttpServletRequest req){
        BasicResponse basicResponse = new BasicResponse();

        String taskId = req.getParameter("taskId");
        String statusID = req.getParameter("statusId");

        if("0".equals(taskId)){
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy dữ liệu công việc");
            basicResponse.setData(-1);

            return basicResponse;
        }

        ProfileService profileService = new ProfileService();
        boolean result = profileService.updateStatusTask(Integer.parseInt(taskId),Integer.parseInt(statusID));

        if(result){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Cập nhập trạng thái công việc thành công");
            basicResponse.setData(1);
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Cập nhập trạng thái công việc thất bại");
            basicResponse.setData(0);
        }

        return basicResponse;
    }

}
