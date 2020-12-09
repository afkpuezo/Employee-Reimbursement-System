/**
 * This servlet handles the menu pages - that is, it sends the user to appropriate menu.
 */
package com.revature.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revature.model.UserProfile.UserRole;

public class MenuServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public MenuServlet() {
        super();
    }

    /**
     * Detemrines the role of the user currently logged in (if any), and sends them to the
     * appropriate menu page.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {       
        
        UserRole role = getCurrentUserRole(request);

        switch(role){
            case EMPLOYEE:
                response.sendRedirect("employee_menu");
                break;
            case MANAGER:
                response.sendRedirect("manager_menu");
                break;
            default:
                response.sendRedirect("no_user_menu");
                break;
        }
    }
}
