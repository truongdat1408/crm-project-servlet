package api;

import com.google.gson.Gson;
import filter.AuthenHanding;
import filter.AuthorList;
import model.*;
import payload.BasicResponse;
import service.ProjectService;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ProjectApi",urlPatterns = {"/api/project","/api/project-add","/api/project-edit","/api/project-detail"})
public class ProjectApi extends HttpServlet {
    UserModel user = new UserModel();
    String projectID = null;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<BasicResponse> responseList = new ArrayList<>();
        BasicResponse basicResponse = new BasicResponse();

        String servletPath = req.getServletPath();
        switch (servletPath){
            case "/api/project":
                responseList = doGetOfProject(req);
                break;
            case "/api/project-add":
                responseList = doGetOfAddProject(req);
                break;
            case "/api/project-edit":
                responseList = doGetOfEditProject(req);
                break;
            case "/api/project-detail":
                responseList = doGetOfProjectDetail(req);
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

    private List<BasicResponse> doGetOfProject(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy toàn bộ danh sách user
        basicResponse = getAllProject();
        responseList.add(basicResponse);

        return responseList;
    }

    private BasicResponse getAllProject(){
        BasicResponse basicResponse = new BasicResponse();
        ProjectService projectService = new ProjectService();
        List<ProjectModel> listProject;

        if(user.getRole().getId() == AuthorList.ADMIN.getValue()){
            listProject = projectService.getAllProject();
        } else {
            listProject = projectService.getAllProjectByLeaderId(user.getId());
        }

        if(listProject.size()>0){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách dự án thành công");
            basicResponse.setData(listProject);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách dự án thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private List<BasicResponse> doGetOfAddProject(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy danh sách leader
        basicResponse = getLeaderList();
        responseList.add(basicResponse);

        return responseList;
    }

    private BasicResponse getLeaderList(){
        BasicResponse basicResponse = new BasicResponse();
        UserService userService = new UserService();
        List<UserModel> userList = userService.getLeaderList();

        if(userList.size()>0){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách quản lý thành công");
            basicResponse.setData(userList);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách quản lý thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private List<BasicResponse> doGetOfEditProject(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user đang đăng nhập
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy thông tin dự án để edit
        basicResponse = getProject(projectID,false);
        responseList.add(basicResponse);

        // Lấy danh sách leader
        basicResponse = getLeaderList();
        responseList.add(basicResponse);

        // Quản lý việc chỉnh sửa người quản lý của dự án
        basicResponse = manageLeaderEdit();
        responseList.add(basicResponse);

        projectID = null;
        return responseList;
    }

    private BasicResponse getProject(String projectId, boolean changeDateFormat){
        BasicResponse basicResponse = new BasicResponse();

        if(projectId != null && !"".equals(projectId)){
            ProjectService projectService = new ProjectService();
            ProjectModel project = projectService.getProject(Integer.parseInt(projectId),changeDateFormat);
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy thông tin dự án thành công");
            basicResponse.setData(project);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy thông tin dự án thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse manageLeaderEdit(){
        BasicResponse basicResponse = new BasicResponse();

        if(user.getRole().getId() == AuthorList.ADMIN.getValue()){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Có quyền thay đổi người quản lý của dự án");
            basicResponse.setData(true);
        } else {
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Không có quyền thay đổi người quản lý của dự án");
            basicResponse.setData(false);
        }

        return basicResponse;
    }

    private List<BasicResponse> doGetOfProjectDetail(HttpServletRequest req){
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user đang đăng nhập
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Lấy thông tin dự án để hiển thị
        basicResponse = getProject(projectID,true);
        responseList.add(basicResponse);

        // Lấy thông tin thống kê của dự án
        basicResponse = getTaskStatics(projectID);
        responseList.add(basicResponse);

        // Lấy thông tin công việc của dự án
        basicResponse = getAllTaskOfProject(projectID);
        responseList.add(basicResponse);

        projectID = null;
        return responseList;
    }

    private BasicResponse getTaskStatics(String projectId){
        BasicResponse basicResponse = new BasicResponse();
        int[] staticsList = {0,0,0};   //1-unbegun num, 2-doing, 3-finish

        if(projectId != null && !"".equals(projectId)){
            ProjectService projectService = new ProjectService();
            List<Integer> taskStatusList = projectService.getTaskStatics(Integer.parseInt(projectId));
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
            basicResponse.setMessage("Lấy danh sách thống kê công việc của dự án thành công");
            basicResponse.setData(staticsList);
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách thống công việc của dự án thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse getAllTaskOfProject(String projectId){
        BasicResponse basicResponse = new BasicResponse();

        if(projectId != null && !"".equals(projectId)){
            ProjectService projectService = new ProjectService();
            List<TaskModel> taskList = projectService.getTaskListOfProject(Integer.parseInt(projectId));
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Lấy danh sách công việc của dự án thành công");
            basicResponse.setData(arrangeTaskByMember(taskList));
        } else {
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách công việc của dự án thất bại");
            basicResponse.setData(null);
        }
        return basicResponse;
    }

    private List<TaskByMemberModel> arrangeTaskByMember(List<TaskModel> inputList){
        List<TaskByMemberModel> resultList = new ArrayList<>();

        if(inputList.size() > 0){
            for(int i=0;i<inputList.size();i++){
                boolean isDuplicate = false;
                if(i != 0){
                    for(int k=0;k<resultList.size();k++){
                        if(inputList.get(i).getUser().getId() == resultList.get(k).getUser().getId()){
                            resultList.get(k).getTaskList().add(inputList.get(i));
                            isDuplicate = true;
                            break;
                        }
                    }
                }
                if(!isDuplicate){
                    TaskByMemberModel taskByMember = new TaskByMemberModel();

                    UserModel user = inputList.get(i).getUser();
                    taskByMember.setUser(user);

                    List<TaskModel> taskList = new ArrayList<>();
                    taskList.add(inputList.get(i));
                    taskByMember.setTaskList(taskList);

                    resultList.add(taskByMember);
                }
            }
        } else {
            return null;
        }

        return resultList;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicResponse basicResponse = new BasicResponse();

        String servletPath = req.getServletPath();
        String function = req.getParameter("function");;
        switch (servletPath){
            case "/api/project":
                basicResponse = doPostOfProject(req,resp,function);
                break;
            case "/api/project-add":
                basicResponse = doPostOfAddProject(req,resp,function);
                break;
            case "/api/project-edit":
                basicResponse = doPostOfEditProject(req,resp,function);
                break;
            case "/api/project-detail":
                basicResponse = doPostOfProjectDetail(req,resp,function);
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

    private BasicResponse doPostOfProject(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "goToAddProject":
                basicResponse = goToAddProject();
                break;
            case "deleteProject":
                int projectId = Integer.parseInt(req.getParameter("projectID"));
                basicResponse = deleteProject(projectId);
                break;
            case "goToEditProject":
                projectID = req.getParameter("projectID");
                basicResponse = goToEditProject(projectID);
                break;
            case "goToProjectDetail":
                projectID = req.getParameter("projectID") ;
                basicResponse = goToProjectDetail(projectID);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse goToAddProject(){
        BasicResponse basicResponse = new BasicResponse();

        basicResponse.setStatusCode(200);
        basicResponse.setMessage("Truy cập vào trang thêm dự án");
        basicResponse.setData("/project-add");

        return basicResponse;
    }

    private BasicResponse deleteProject(int projectId){
        BasicResponse basicResponse = new BasicResponse();
        ProjectService projectService = new ProjectService();

        if(!projectService.checkExistingOfTaskByProjectId(projectId)){
            if(projectService.deleteProject(projectId)){
                basicResponse.setStatusCode(200);
                basicResponse.setMessage("Xóa dự án thành công");
                basicResponse.setData(1);
            }else {
                basicResponse.setStatusCode(400);
                basicResponse.setMessage("Xóa dự án thất bại");
                basicResponse.setData(0);
            }
        } else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không thể xóa dự án này");
            basicResponse.setData(-1);
        }

        return basicResponse;
    }

    private BasicResponse goToEditProject(String projectId){
        BasicResponse basicResponse = new BasicResponse();

        if(projectId != null && !"".equals(projectId)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Truy cập vào trang chỉnh sửa dự án");
            basicResponse.setData("/project-edit");
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy ID của dự án muốn chỉnh sửa");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse goToProjectDetail(String projectId){
        BasicResponse basicResponse = new BasicResponse();

        if(projectId != null && !"".equals(projectId)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Truy cập vào trang chi tiết dự án");
            basicResponse.setData("/project-detail");
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy ID của dự án muốn xem");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

    private BasicResponse doPostOfAddProject(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "addProject":
                basicResponse = addProject(req);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse addProject(HttpServletRequest req){
        BasicResponse basicResponse = new BasicResponse();

        ProjectModel newProject = new ProjectModel();
        newProject.setName(req.getParameter("name"));
        String leaderId = req.getParameter("leaderId");
        newProject.setStart_date(req.getParameter("start-date"));
        newProject.setEnd_date(req.getParameter("end-date"));

        if("".equals(newProject.getName()) || "".equals(newProject.getStart_date()) || "".equals(newProject.getEnd_date())
                || "".equals(leaderId) || leaderId == null) {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Dữ liệu chưa được nhập đủ");
            basicResponse.setData(-1);

            return basicResponse;
        }

        UserModel user = new UserModel();
        user.setId(Integer.parseInt(leaderId));
        newProject.setLeader(user);

        ProjectService projectService = new ProjectService();
        if(projectService.addProject(newProject)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Thêm dự án thành công");
            basicResponse.setData(1);
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Thêm dự án thất bại");
            basicResponse.setData(0);
        }

        return basicResponse;
    }

    private BasicResponse doPostOfEditProject(HttpServletRequest req, HttpServletResponse resp, String function){
        BasicResponse basicResponse = new BasicResponse();

        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            case "editProject":
                basicResponse = editProject(req);
                break;
            default:
                break;
        }

        return basicResponse;
    }

    private BasicResponse editProject(HttpServletRequest req){
        BasicResponse basicResponse = new BasicResponse();

        String projectId = req.getParameter("id");
        if("0".equals(projectId)){
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Không tìm thấy dữ liệu dự án");
            basicResponse.setData(-2);

            return basicResponse;
        }

        ProjectModel project = new ProjectModel();
        project.setId(Integer.parseInt(req.getParameter("id")));
        project.setName(req.getParameter("name"));
        project.setStart_date(req.getParameter("start-date"));
        project.setEnd_date(req.getParameter("end-date"));
        String leaderId = req.getParameter("leaderId");

        if("".equals(project.getName()) || "".equals(project.getStart_date())
                || "".equals(project.getEnd_date()) || "".equals(leaderId) || leaderId == null) {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Chưa nhập đủ dữ liệu");
            basicResponse.setData(-1);

            return basicResponse;
        }
        UserModel user = new UserModel();
        user.setId(Integer.parseInt(leaderId));
        project.setLeader(user);

        ProjectService projectService = new ProjectService();
        if(projectService.editProject(project)){
            basicResponse.setStatusCode(200);
            basicResponse.setMessage("Chỉnh sửa thông tin dự án thành công");
            basicResponse.setData(1);
        }else {
            basicResponse.setStatusCode(400);
            basicResponse.setMessage("Chỉnh sửa thông tin dự án thất bại");
            basicResponse.setData(0);
        }

        return basicResponse;
    }

    private BasicResponse doPostOfProjectDetail(HttpServletRequest req, HttpServletResponse resp, String function){
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
