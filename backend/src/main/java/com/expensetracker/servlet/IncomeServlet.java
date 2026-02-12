package com.expensetracker.servlet;

import com.expensetracker.dao.IncomeDAO;
import com.expensetracker.model.Income;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
/**
 * Servlet for handling income operations
 */
public class IncomeServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");
        
        String action = req.getParameter("action");
        
        try {
            if ("add".equals(action)) {
                addIncome(req, resp);
            } else if ("update".equals(action)) {
                updateIncome(req, resp);
            } else if ("delete".equals(action)) {
                deleteIncome(req, resp);
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
                getAllIncome(resp);
            } else if ("getByMonth".equals(action)) {
                getByMonth(req, resp);
            } else if ("getTotalIncome".equals(action)) {
                getTotalIncome(resp);
            } else if ("getMonthlyTotal".equals(action)) {
                getMonthlyTotal(req, resp);
            } else if ("getSourcesSummary".equals(action)) {
                getSourcesSummary(req, resp);
            } else {
                sendError(resp, "Invalid action");
            }
        } catch (Exception e) {
            sendError(resp, "Error: " + e.getMessage());
        }
    }
    
    private void addIncome(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Income income = new Income();
        income.setAmount(Double.parseDouble(req.getParameter("amount")));
        income.setSource(req.getParameter("source"));
        income.setIncomeDate(dateFormat.parse(req.getParameter("date")));
        income.setNotes(req.getParameter("notes"));
        
        int id = IncomeDAO.addIncome(income);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", id > 0);
        json.addProperty("id", id);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void updateIncome(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Income income = new Income();
        income.setId(Integer.parseInt(req.getParameter("id")));
        income.setAmount(Double.parseDouble(req.getParameter("amount")));
        income.setSource(req.getParameter("source"));
        income.setIncomeDate(dateFormat.parse(req.getParameter("date")));
        income.setNotes(req.getParameter("notes"));
        
        boolean success = IncomeDAO.updateIncome(income);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void deleteIncome(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        boolean success = IncomeDAO.deleteIncome(id);
        
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void getAllIncome(HttpServletResponse resp) throws Exception {
        resp.getWriter().write(gson.toJson(IncomeDAO.getAllIncome()));
    }
    
    private void getByMonth(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int month = Integer.parseInt(req.getParameter("month"));
        int year = Integer.parseInt(req.getParameter("year"));
        try {
            List<Income> incomes = IncomeDAO.getIncomeByMonth(month, year);
            String json = gson.toJson(incomes);
            resp.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
    
    private void getTotalIncome(HttpServletResponse resp) throws Exception {
        JsonObject json = new JsonObject();
        json.addProperty("total", IncomeDAO.getTotalIncome());
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void getMonthlyTotal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int month = Integer.parseInt(req.getParameter("month"));
        int year = Integer.parseInt(req.getParameter("year"));
        JsonObject json = new JsonObject();
        json.addProperty("total", IncomeDAO.getTotalIncomeByMonth(month, year));
        resp.getWriter().write(gson.toJson(json));
    }
    
    private void getSourcesSummary(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int month = Integer.parseInt(req.getParameter("month"));
        int year = Integer.parseInt(req.getParameter("year"));
        resp.getWriter().write(gson.toJson(IncomeDAO.getIncomeSourcesSummary(month, year)));
    }
    
    private void sendError(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        resp.getWriter().write(gson.toJson(json));
    }
}
