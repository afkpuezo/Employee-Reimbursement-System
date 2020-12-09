/**
 * This servlet displays the menu of actions available when no user is logged in.
 */
package com.revature.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoUserMenuServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public NoUserMenuServlet() {
        super();
    }

    /**
     * Serves up no_user_menu.html
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("no_user_menu.html").forward(request, response);
    }
}
