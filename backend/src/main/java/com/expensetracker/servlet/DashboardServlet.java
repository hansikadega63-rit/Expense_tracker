package com.expensetracker.servlet;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.dao.IncomeDAO;
import com.expensetracker.dao.BudgetDAO;
import com.expensetracker.dao.SavingsGoalDAO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

/**
 * Servlet for Dashboard and Report operations
 */
public class DashboardServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");
        
        String action = req.getParameter("action");
        
        try {
            if ("getDashboardData".equals(action)) {
                getDashboardData(req, resp);
            } else if ("getMonthlyReport".equals(action)) {
                getMonthlyReport(req, resp);
            } else if ("getExpenseInsights".equals(action)) {
                getExpenseInsights(req, resp);
            } else {
                sendError(resp, "Invalid action");
            }
        } catch (Exception e) {
            sendError(resp, "Error: " + e.getMessage());
        }
    }
    
    private void getDashboardData(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        
        double totalExpenses = ExpenseDAO.getTotalExpensesByMonth(month, year);
        double totalIncome = IncomeDAO.getTotalIncomeByMonth(month, year);
        double savings = totalIncome - totalExpenses;
        
        JsonObject dashboardData = new JsonObject();
        dashboardData.addProperty("totalExpenses", totalExpenses);
        dashboardData.addProperty("totalIncome", totalIncome);
        dashboardData.addProperty("savings", savings);
        dashboardData.add("categoryBreakdown", gson.toJsonTree(ExpenseDAO.getCategoryBreakdown(month, year)));
        dashboardData.add("budgetAlerts", gson.toJsonTree(BudgetDAO.getBudgetAlerts(month, year)));
        dashboardData.add("savingsGoals", gson.toJsonTree(SavingsGoalDAO.getGoalsStatus()));
        
        resp.getWriter().write(gson.toJson(dashboardData));
    }
    
    private void getMonthlyReport(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int month = Integer.parseInt(req.getParameter("month"));
        int year = Integer.parseInt(req.getParameter("year"));
        
        double totalExpenses = ExpenseDAO.getTotalExpensesByMonth(month, year);
        double totalIncome = IncomeDAO.getTotalIncomeByMonth(month, year);
        double savings = totalIncome - totalExpenses;
        double savingsPercentage = totalIncome > 0 ? (savings / totalIncome) * 100 : 0;
        
        JsonObject report = new JsonObject();
        report.addProperty("month", month);
        report.addProperty("year", year);
        report.addProperty("totalIncome", totalIncome);
        report.addProperty("totalExpenses", totalExpenses);
        report.addProperty("savings", savings);
        report.addProperty("savingsPercentage", savingsPercentage);
        report.add("categoryBreakdown", gson.toJsonTree(ExpenseDAO.getCategoryBreakdown(month, year)));
        report.add("budgetStatus", gson.toJsonTree(BudgetDAO.getBudgetsByMonth(month, year)));
        report.add("incomeSourcesSummary", gson.toJsonTree(IncomeDAO.getIncomeSourcesSummary(month, year)));
        
        resp.getWriter().write(gson.toJson(report));
    }
    
    private void getExpenseInsights(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        
        double totalExpenses = ExpenseDAO.getTotalExpensesByMonth(month, year);
        double dailyAverage = ExpenseDAO.getDailyAverageExpense(month, year);
        
        JsonObject insights = new JsonObject();
        insights.addProperty("monthlyTotal", totalExpenses);
        insights.addProperty("dailyAverage", dailyAverage);
        insights.add("categoryBreakdown", gson.toJsonTree(ExpenseDAO.getCategoryBreakdown(month, year)));
        
        resp.getWriter().write(gson.toJson(insights));
    }
    
    private void sendError(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        resp.getWriter().write(gson.toJson(json));
    }
}
