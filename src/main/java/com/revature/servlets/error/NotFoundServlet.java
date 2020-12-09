/**
 * Handles 404 errors
 */
package com.revature.servlets.error;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revature.servlets.ERSServlet;

public class NotFoundServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;
    
    public NotFoundServlet() {
        super();
    }

    /**
     * Send the user to the problem page, then to the menu
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        handleProblem(response, request, "Quoth the server, \"404\"", "menu");
    }
}
