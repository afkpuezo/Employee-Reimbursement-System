/**
 * This servlet displays the menu of actions available to a manager.
 * 
 * @author Andrew Curry
 */
package com.revature.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revature.model.UserProfile.UserRole;

public class ManagerMenuServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public ManagerMenuServlet() {
        super();
    }

    /**
     * Serves the manager_menu.html
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (getCurrentUserRole(request) != UserRole.MANAGER){
            redirectToMenu(response);
            return;
        }

        request.getRequestDispatcher("manager_menu.html").forward(request, response);
    }
}
