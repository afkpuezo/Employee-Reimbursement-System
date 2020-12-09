/**
 * Handles the task of a manager viewing all of employee profiles.
 */
package com.revature.servlets.manager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revature.model.UserProfile.UserRole;
import com.revature.service.comms.ERSRequest;
import com.revature.service.comms.ERSResponse;
import com.revature.service.comms.ERSRequest.ERSRequestType;
import com.revature.servlets.ERSServlet;

public class ViewAllEmployeesServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public ViewAllEmployeesServlet() {
        super();
    }

    /**
     * Prompts the service layer for the user profiles and displays the results.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // make sure only an employee can view this
        if (getCurrentUserRole(request) != UserRole.MANAGER) {
            redirectToMenu(response);
            return;
        }

        ERSRequest ereq 
                = makeERSRequest(ERSRequestType.VIEW_ALL_EMPLOYEES, request.getSession());
        ERSResponse eres = getResponse(ereq);

        if (isFailure(eres)){
            handleProblem(response, request.getSession(), eres.getMessage(), "menu");
            return;
        }

        String table = makeTableFromUserProfiles(eres.getReturnedUserProfiles());
        
        handleSuccess(response, request.getSession(), table, "menu");
    }

    
}
