/**
 * Handles logging out.
 * 
 * @author Andrew Curry
 */
package com.revature.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.revature.model.UserProfile.UserRole;
import com.revature.service.comms.ERSRequest;
import com.revature.service.comms.ERSRequest.ERSRequestType;

import com.revature.service.comms.ERSResponse;

public class LogOutServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public LogOutServlet() {
        super();
    }

    /**
     * Carries out the actual logging out.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (getCurrentUserRole(request) == UserRole.LOGGED_OUT){
            redirectToMenu(response);
            return;
        }
        
        HttpSession session = request.getSession();
        ERSRequest ereq = makeERSRequest(ERSRequestType.LOG_OUT, session);
        ERSResponse eres = getResponse(ereq);

        if (isFailure(eres)){
            handleProblem(response, session, "eres type is: " + eres.getType(), "menu");
            return;
        }

        session.setAttribute("userID", -1);
        session.setAttribute("role", UserRole.LOGGED_OUT);
        handleSuccess(response, session, "Logged out.", "menu");
    }
}
