/**
 * This servlet displays the menu of actions available to an employee
 * 
 * @author Andrew Curry
 */
package com.revature.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revature.model.UserProfile.UserRole;

public class EmployeeMenuServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public EmployeeMenuServlet() {
        super();
    }

    /**
     * Serves up the employee_menu.html file
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (getCurrentUserRole(request) != UserRole.EMPLOYEE){
            redirectToMenu(response);
            return;
        }
        
        //response.getWriter().write("DEBUG: is this changing?");
        request.getRequestDispatcher("employee_menu.html").forward(request, response);
    }

}
