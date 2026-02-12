package com.expensetracker.servlet;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Servlet for handling expense operations
 */
public class ExpenseServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");
        
        String action = req.getParameter("action");
        
        try {
            if ("add".equals(action)) {
                addExpense(req, resp);
            } else if ("update".equals(action)) {
                updateExpense(req, resp);
            } else if ("delete".equals(action)) {
                deleteExpense(req, resp);
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
                getAllExpenses(resp);
            } else if ("getByCategory".equals(action)) {
                getByCategory(req, resp);
            } else if ("getByMonth".equals(action)) {
                getByMonth(req, resp);
            } else if ("getCategoryBreakdown".equals(action)) {
                getCategoryBreakdown(req, resp);
            } else if ("getMonthlySummary".equals(action)) {
                getMonthlySummary(req, resp);
            } else if ("getTotalExpenses".equals(action)) {
                getTotalExpenses(resp);
            } else if ("getDailyAverage".equals(action)) {
                getDailyAverage(req, resp);
            } else {
                sendError(resp, "Invalid action");
            }
        } catch (Exception e) {
            sendError(resp, "Error: " + e.getMessage());
        }
    }
    
    private void addExpense(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Expense expense = new Expense();
        expense.setAmount(Double.parseDouble(req.getParameter("amount")));
        expense.setCategory(req.getParameter("category"));
        expense.setExpenseDate(dateFormat.parse(req.getParameter("date")));
        expense.setDescription(req.getParameter("description"));
        
        int id = ExpenseDAO.addExpense(expense);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", id > 0);
        json.addProperty("id", id);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void updateExpense(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Expense expense = new Expense();
        expense.setId(Integer.parseInt(req.getParameter("id")));
        expense.setAmount(Double.parseDouble(req.getParameter("amount")));
        expense.setCategory(req.getParameter("category"));
        expense.setExpenseDate(dateFormat.parse(req.getParameter("date")));
        expense.setDescription(req.getParameter("description"));
        
        boolean success = ExpenseDAO.updateExpense(expense);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void deleteExpense(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        boolean success = ExpenseDAO.deleteExpense(id);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void getAllExpenses(HttpServletResponse resp) throws Exception {
        resp.getWriter().write(gson.toJson(ExpenseDAO.getAllExpenses()));
    }
    
    private void getByCategory(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String category = req.getParameter("category");
        resp.getWriter().write(gson.toJson(ExpenseDAO.getExpensesByCategory(category)));
    }
    
    private void getByMonth(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int month = Integer.parseInt(req.getParameter("month"));
        int year = Integer.parseInt(req.getParameter("year"));
        try {
            List<Expense> expenses = ExpenseDAO.getExpensesByMonth(month, year);
            String json = gson.toJson(expenses);
            resp.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
    
    private void getCategoryBreakdown(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int month = Integer.parseInt(req.getParameter("month"));
        int year = Integer.parseInt(req.getParameter("year"));
        resp.getWriter().write(gson.toJson(ExpenseDAO.getCategoryBreakdown(month, year)));
    }
    
    private void getMonthlySummary(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int year = Integer.parseInt(req.getParameter("year"));
        resp.getWriter().write(gson.toJson(ExpenseDAO.getMonthlySummary(year)));
    }
    
    private void getTotalExpenses(HttpServletResponse resp) throws Exception {
        JsonObject json = new JsonObject();
        json.addProperty("total", ExpenseDAO.getTotalExpenses());
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void getDailyAverage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int month = Integer.parseInt(req.getParameter("month"));
        int year = Integer.parseInt(req.getParameter("year"));
        JsonObject json = new JsonObject();
        json.addProperty("average", ExpenseDAO.getDailyAverageExpense(month, year));
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void sendError(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        resp.getWriter().write(gson.toJson(json));
    }
}
