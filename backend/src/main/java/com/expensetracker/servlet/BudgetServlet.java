package com.expensetracker.servlet;

import com.expensetracker.dao.BudgetDAO;
import com.expensetracker.model.Budget;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for handling budget operations
 */
public class BudgetServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");
        
        String action = req.getParameter("action");
        
        try {
            if ("add".equals(action)) {
                addBudget(req, resp);
            } else if ("update".equals(action)) {
                updateBudget(req, resp);
            } else if ("delete".equals(action)) {
                deleteBudget(req, resp);
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
                getAllBudgets(resp);
            } else if ("getByMonth".equals(action)) {
                getByMonth(req, resp);
            } else if ("getAlerts".equals(action)) {
                getAlerts(req, resp);
            } else {
                sendError(resp, "Invalid action");
            }
        } catch (Exception e) {
            sendError(resp, "Error: " + e.getMessage());
        }
    }
    
    private void addBudget(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Budget budget = new Budget();
        budget.setCategory(req.getParameter("category"));
        budget.setAmount(Double.parseDouble(req.getParameter("amount")));
        budget.setMonth(Integer.parseInt(req.getParameter("month")));
        budget.setYear(Integer.parseInt(req.getParameter("year")));
        
        int id = BudgetDAO.addBudget(budget);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", id > 0);
        json.addProperty("id", id);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void updateBudget(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Budget budget = new Budget();
        budget.setId(Integer.parseInt(req.getParameter("id")));
        budget.setAmount(Double.parseDouble(req.getParameter("amount")));
        
        boolean success = BudgetDAO.updateBudget(budget);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void deleteBudget(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        boolean success = BudgetDAO.deleteBudget(id);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void getAllBudgets(HttpServletResponse resp) throws Exception {
        resp.getWriter().write(gson.toJson(BudgetDAO.getAllBudgets()));
    }
    
    private void getByMonth(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int month = Integer.parseInt(req.getParameter("month"));
        int year = Integer.parseInt(req.getParameter("year"));
        resp.getWriter().write(gson.toJson(BudgetDAO.getBudgetsByMonth(month, year)));
    }
    
    private void getAlerts(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int month = Integer.parseInt(req.getParameter("month"));
        int year = Integer.parseInt(req.getParameter("year"));
        resp.getWriter().write(gson.toJson(BudgetDAO.getBudgetAlerts(month, year)));
    }
    
    private void sendError(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        resp.getWriter().write(gson.toJson(json));
    }
}
