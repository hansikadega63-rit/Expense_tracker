package com.expensetracker.servlet;

import com.expensetracker.dao.SavingsGoalDAO;
import com.expensetracker.model.SavingsGoal;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Servlet for handling savings goal operations
 */
public class SavingsGoalServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");
        
        String action = req.getParameter("action");
        
        try {
            if ("add".equals(action)) {
                addGoal(req, resp);
            } else if ("update".equals(action)) {
                updateGoal(req, resp);
            } else if ("delete".equals(action)) {
                deleteGoal(req, resp);
            } else if ("updateProgress".equals(action)) {
                updateProgress(req, resp);
            } else {
                sendError(resp, "Invalid action");
            }
        } catch (Exception e) {
            sendError(resp, "Error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");
        
        String action = req.getParameter("action");
        
        try {
            if ("getAll".equals(action)) {
                getAllGoals(resp);
            } else if ("getStatus".equals(action)) {
                getStatus(resp);
            } else {
                sendError(resp, "Invalid action");
            }
        } catch (Exception e) {
            sendError(resp, "Error: " + e.getMessage());
        }
    }
    
    private void addGoal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        SavingsGoal goal = new SavingsGoal();
        goal.setName(req.getParameter("name"));
        goal.setTargetAmount(Double.parseDouble(req.getParameter("targetAmount")));
        goal.setCurrentAmount(Double.parseDouble(req.getParameter("currentAmount")));
        goal.setDeadline(dateFormat.parse(req.getParameter("deadline")));
        goal.setPriority(req.getParameter("priority"));
        goal.setDescription(req.getParameter("description"));
        
        int id = SavingsGoalDAO.addSavingsGoal(goal);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", id > 0);
        json.addProperty("id", id);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void updateGoal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        SavingsGoal goal = new SavingsGoal();
        goal.setId(Integer.parseInt(req.getParameter("id")));
        goal.setName(req.getParameter("name"));
        goal.setTargetAmount(Double.parseDouble(req.getParameter("targetAmount")));
        goal.setCurrentAmount(Double.parseDouble(req.getParameter("currentAmount")));
        goal.setDeadline(dateFormat.parse(req.getParameter("deadline")));
        goal.setPriority(req.getParameter("priority"));
        goal.setDescription(req.getParameter("description"));
        
        boolean success = SavingsGoalDAO.updateGoal(goal);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void deleteGoal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        boolean success = SavingsGoalDAO.deleteGoal(id);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void updateProgress(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        double currentAmount = Double.parseDouble(req.getParameter("currentAmount"));
        boolean success = SavingsGoalDAO.updateGoalProgress(id, currentAmount);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void getAllGoals(HttpServletResponse resp) throws Exception {
        resp.getWriter().write(gson.toJson(SavingsGoalDAO.getAllGoals()));
    }
    
    private void getStatus(HttpServletResponse resp) throws Exception {
        resp.getWriter().write(gson.toJson(SavingsGoalDAO.getGoalsStatus()));
    }
    
    private void sendError(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        resp.getWriter().write(gson.toJson(json));
    }
}
