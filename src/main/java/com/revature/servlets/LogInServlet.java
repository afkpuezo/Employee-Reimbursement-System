/**
 * Handles logging in to a new user account.
 * 
 * @author Andrew Curry
 */
package com.revature.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.revature.service.comms.ERSRequest;
import com.revature.service.comms.ERSRequest.ERSRequestType;
import com.revature.model.UserProfile;
import com.revature.model.UserProfile.UserRole;
import com.revature.service.comms.ERSResponse;

public class LogInServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public LogInServlet() {
        super();
    }

    /**
     * Displays the log in page.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (getCurrentUserRole(request) != UserRole.LOGGED_OUT){
            redirectToMenu(response);
            return;
        }
        
        request.getRequestDispatcher("log_in.html").forward(request, response);
    }
    
    /**
     * Carries out the actual process of logging in.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (getCurrentUserRole(request) != UserRole.LOGGED_OUT){
            redirectToMenu(response);
            return;
        }
        
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!isUsernameValid(username)){
            handleProblem(response, session, INVALID_USERNAME_MESSAGE, "log_in");
            return;
        }

        if (!isUsernameValid(password)){
            handleProblem(response, session, INVALID_PASSWORD_MESSAGE, "log_in");
            return;
        }

        // now that it's valid we can try to log in
        ERSRequest ereq = makeERSRequest(ERSRequestType.LOG_IN, session);
        ereq.putParameter(ERSRequest.USERNAME_KEY, username);
        ereq.putParameter(ERSRequest.PASSWORD_KEY, password);
        ERSResponse eres = getResponse(ereq);

        // was there a problem?
        if (isFailure(eres)){
            handleProblem(response, session, eres.getMessage(), "log_in");
            return;
        } 

        // get info about the account
        UserProfile up = eres.getReturnedUserProfiles().get(0); // assume proper response
        // remember to actually update the session...
        session.setAttribute("userID", up.getID());
        session.setAttribute("role", up.getRole());

        String message = "Logged into " + up.getRole() + " account with username: " 
                + up.getUsername();
        handleSuccess(response, session, message, "menu");
	}
}
