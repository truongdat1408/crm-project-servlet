package api;

import com.google.gson.Gson;
import filter.AuthenHanding;
import filter.AuthorList;
import model.*;
import payload.BasicResponse;
import service.StatusService;
import service.TaskService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "TaskApi",urlPatterns = {"/api/task","/api/task-add","/api/task-edit"})
public class TaskApi extends HttpServlet {
    UserModel user = new UserModel();
    String taskID = null;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<BasicResponse> responseList = new ArrayList<>();
        BasicResponse basicResponse = new BasicResponse();

        String servletPath = req.getServletPath();
        switch (servletPath){
            case "/api/task":
                responseList = doGetOfTask(req);
                break;
            case "/api/task-add":
                responseList = doGetOfAddTask(req);
                break;
            case "/api/task-edit":
                responseList = doGetOfEditTask(req);
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

    private List<BasicResponse> doGetOfTask(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy toàn bộ danh sách task
        basicResponse = getAllTask();
        responseList.add(basicResponse);

        return responseList;
    }

    private BasicResponse getAllTask(){
        BasicResponse basicResponse = new BasicResponse();
        TaskService taskService = new TaskService();
        List<TaskModel> taskList;

        if(user.getRole().getId() == AuthorList.ADMIN.getValue()){
            taskList = taskService.getAllTask();
        } else {
            taskList = taskService.getAllTaskByLeaderId(user.getId());
        }

        if(taskList.size()>0){
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

    private List<BasicResponse> doGetOfAddTask(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy danh sách project
        basicResponse = getProjectList();
        responseList.add(basicResponse);

        // Lấy danh sách user
        basicResponse = getMemberList();
        responseList.add(basicResponse);

        return responseList;
    }

    private BasicResponse getProjectList(){
        BasicResponse basicResponse = new BasicResponse();
        TaskService taskService = new TaskService();
        List<ProjectModel> projectList = taskService.getProjectList();

        if(projectList.size()>0){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách dự án trong thêm/chỉnh sửa công việc thành công");
            basicResponse.setData(projectList);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách dự án trong thêm/chỉnh sửa công việc thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse getMemberList(){
        BasicResponse basicResponse = new BasicResponse();
        TaskService taskService = new TaskService();
        List<UserModel> userList = taskService.getMemberList();

        if(userList.size()>0){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách thành viên trong thêm/chỉnh sửa công việc thành công");
            basicResponse.setData(userList);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách thành viên trong thêm/chỉnh sửa công việc thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private List<BasicResponse> doGetOfEditTask(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy thông tin công việc
        basicResponse = getTaskInfo(taskID);
        responseList.add(basicResponse);

        // Lấy danh sách project
        basicResponse = getProjectList();
        responseList.add(basicResponse);

        // Lấy danh sách user
        basicResponse = getMemberList();
        responseList.add(basicResponse);

        // Lấy danh sách status
        basicResponse = getStatusList();
        responseList.add(basicResponse);

        taskID = null;
        return responseList;
    }

    private BasicResponse getTaskInfo(String taskId){
        BasicResponse basicResponse = new BasicResponse();

        if(taskId != null && !"".equals(taskId)){
            TaskService taskService = new TaskService();
            TaskModel task = taskService.getTaskById(Integer.parseInt(taskId));
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy thông tin công việc thành công");
            basicResponse.setData(task);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy thông tin công việc thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse getStatusList(){
        BasicResponse basicResponse = new BasicResponse();
        StatusService statusService = new StatusService();
        List<StatusModel> statusList = statusService.getAllStatus();

        if(statusList.size()>0){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách trạng thái trong chỉnh sửa công việc thành công");
            basicResponse.setData(statusList);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách trạng thái trong chỉnh sửa công việc thất bại");
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
            case "/api/task":
                basicResponse = doPostOfTask(req,resp,function);
                break;
            case "/api/task-add":
                basicResponse = doPostOfAddTask(req,resp,function);
                break;
            case "/api/task-edit":
                basicResponse = doPostOfEditTask(req,resp,function);
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

    private BasicResponse doPostOfTask(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "goToAddTask":
                basicResponse = goToAddTask();
                break;
            case "deleteTask":
                int taskId = Integer.parseInt(req.getParameter("taskID"));
                basicResponse = deleteTask(taskId);
                break;
            case "goToEditTask":
                taskID = req.getParameter("taskID");
                basicResponse = goToEditTask(taskID);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse goToAddTask(){
        BasicResponse basicResponse = new BasicResponse();

        basicResponse.setStatusCode(200);
        basicResponse.setMessage("Truy cập vào trang thêm dự án");
        basicResponse.setData("/task-add");

        return basicResponse;
    }

    private BasicResponse deleteTask(int taskId){
        BasicResponse basicResponse = new BasicResponse();
        TaskService taskService = new TaskService();

        if(taskService.deleteTask(taskId)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Xóa công việc thành công");
            basicResponse.setData(true);
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Xóa công việc thất bại");
            basicResponse.setData(false);
        }

        return basicResponse;
    }

    private BasicResponse goToEditTask(String taskId) {
        BasicResponse basicResponse = new BasicResponse();

        if (taskId != null && !"".equals(taskId)) {
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Truy cập vào trang chỉnh sửa công việc");
            basicResponse.setData("/task-edit");
        } else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy ID của công việc muốn chỉnh sửa");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse doPostOfAddTask(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "addTask":
                basicResponse = addTask(req);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse addTask(HttpServletRequest req){
        BasicResponse basicResponse = new BasicResponse();

        TaskModel newTask = checkMissingInputAdd(req);
        if(newTask == null) {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Dữ liệu chưa được nhập đủ");
            basicResponse.setData(-1);

            return basicResponse;
        }

        TaskService taskService = new TaskService();
        if(taskService.addTask(newTask)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Thêm công việc thành công");
            basicResponse.setData(1);
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Thêm công việc thất bại");
            basicResponse.setData(0);
        }

        return basicResponse;
    }

    private TaskModel checkMissingInputAdd(HttpServletRequest req){
        boolean missingInput = false;

        TaskModel task = new TaskModel();
        task.setName(req.getParameter("taskName"));
        task.setStart_date(req.getParameter("start-date"));
        task.setEnd_date(req.getParameter("end-date"));
        if("".equals(task.getName()) || "".equals(task.getStart_date())
                || "".equals(task.getEnd_date())){
            missingInput = true;
        }

        String projectId = req.getParameter("projectID");
        if("".equals(projectId) || projectId == null){
            missingInput = true;
        } else {
            ProjectModel project = new ProjectModel();
            project.setId(Integer.parseInt(projectId));
            task.setProject(project);
        }

        String memberId = req.getParameter("memberID");
        if("".equals(memberId) || memberId == null){
            missingInput = true;
        } else {
            UserModel member = new UserModel();
            member.setId(Integer.parseInt(memberId));
            task.setUser(member);
        }

        StatusModel status = new StatusModel();
        status.setId(StatusList.UNBEGUN.getValue());
        task.setStatus(status);

        if(missingInput){
            return null;
        } else {
            return task;
        }
    }

    private BasicResponse doPostOfEditTask(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "editTask":
                basicResponse = editTask(req);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse editTask(HttpServletRequest req){
        BasicResponse basicResponse = new BasicResponse();

        String taskId = req.getParameter("id");
        if("0".equals(taskId)){
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy dữ liệu công việc");
            basicResponse.setData(-2);

            return basicResponse;
        }

        TaskModel task = checkMissingInputEdit(req);
        if(task == null) {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Dữ liệu chưa được nhập đủ");
            basicResponse.setData(-1);

            return basicResponse;
        }

        TaskService taskService = new TaskService();
        if(taskService.editTask(task)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Chỉnh sửa công việc thành công");
            basicResponse.setData(1);
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Chỉnh sửa công việc thất bại");
            basicResponse.setData(0);
        }

        return basicResponse;
    }

    private TaskModel checkMissingInputEdit(HttpServletRequest req){
        boolean missingInput = false;

        TaskModel task = new TaskModel();
        task.setId(Integer.parseInt(req.getParameter("id")));
        task.setName(req.getParameter("taskName"));
        task.setStart_date(req.getParameter("start-date"));
        task.setEnd_date(req.getParameter("end-date"));
        if("".equals(task.getName()) || "".equals(task.getStart_date())
                || "".equals(task.getEnd_date())){
            missingInput = true;
        }

        String projectId = req.getParameter("projectID");
        if("".equals(projectId) || projectId == null){
            missingInput = true;
        } else {
            ProjectModel project = new ProjectModel();
            project.setId(Integer.parseInt(projectId));
            task.setProject(project);
        }

        String memberId = req.getParameter("memberID");
        if("".equals(memberId) || memberId == null){
            missingInput = true;
        } else {
            UserModel member = new UserModel();
            member.setId(Integer.parseInt(memberId));
            task.setUser(member);
        }

        String statusId = req.getParameter("statusID");
        if("".equals(statusId) || statusId == null){
            missingInput = true;
        } else {
            StatusModel status = new StatusModel();
            status.setId(Integer.parseInt(statusId));
            task.setStatus(status);
        }

        if(missingInput){
            return null;
        } else {
            return task;
        }
    }
}
