package api;

import com.google.gson.Gson;
import model.StatusList;
import model.UserModel;
import payload.BasicResponse;
import service.HomeService;
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

@WebServlet(name = "HomeApi",urlPatterns = {"/api/home"})
public class HomeApi extends HttpServlet {
    UserModel user = new UserModel();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<BasicResponse> responseList = new ArrayList<>();

        // Lấy thông tin user
        AuthenHanding auth = new AuthenHanding();
        BasicResponse basicResponse = auth.getUserInfo(req);
        responseList.add(basicResponse);
        user = (UserModel) basicResponse.getData();

        // Thống kê số lượng task của user
        basicResponse = getTaskStatus();
        responseList.add(basicResponse);

        Gson gson = new Gson();
        String dataJson = gson.toJson(responseList);

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        PrintWriter printWriter = resp.getWriter();
        printWriter.print(dataJson);
        printWriter.flush();
        printWriter.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicResponse basicResponse = new BasicResponse();
        String function = req.getParameter("function");
        switch (function){
            case "logout":
                AuthenHanding authenHanding = new AuthenHanding();
                basicResponse = authenHanding.logOut(req,resp);
                break;
            default:
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

    private BasicResponse getTaskStatus(){
        BasicResponse basicResponse = new BasicResponse();

        int[] statusList = {0,0,0,0};   // 0-task qty sum, 1-unbegun qty, 2-doing qty, 3-finish qty

        if(user != null){
            HomeService homeService = new HomeService();
            List<Integer> taskStatusList = homeService.getTaskStatus(user.getId());
            if(taskStatusList.size()>0){
                statusList[0] = taskStatusList.size();
                for (int i: taskStatusList) {
                    if(i == StatusList.UNBEGUN.getValue()){
                        statusList[1]++;
                    } else if(i == StatusList.DOING.getValue()){
                        statusList[2]++;
                    } else if(i == StatusList.FINISH.getValue()){
                        statusList[3]++;
                    }
                }
                basicResponse.setStatusCode(200);
                basicResponse.setMessage("Lấy danh sách thống kê công việc của người dùng thành công");
                basicResponse.setData(statusList);
            }
        } else{
            basicResponse.setStatusCode(404);
            basicResponse.setMessage("Lấy danh sách thống kê công việc của người dùng thất bại");
            basicResponse.setData(null);
        }

        return basicResponse;
    }

}
