/**
 * Handles the task of an employee viewing all of their pending requests.
 */
package com.revature.servlets.employee;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revature.model.UserProfile.UserRole;
import com.revature.service.comms.ERSRequest;
import com.revature.service.comms.ERSResponse;
import com.revature.service.comms.ERSRequest.ERSRequestType;
import com.revature.servlets.ERSServlet;

public class EmployeeViewPendingServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public EmployeeViewPendingServlet() {
        super();
    }

    /**
     * Prompts the service layer for the reimb-reqs and displays the results.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // make sure only an employee can view this
        if (getCurrentUserRole(request) != UserRole.EMPLOYEE) {
            redirectToMenu(response);
            return;
        }

        ERSRequest ereq = makeERSRequest(ERSRequestType.EMPLOYEE_VIEW_PENDING, request.getSession());
        ERSResponse eres = getResponse(ereq);

        if (isFailure(eres)){
            handleProblem(response, request.getSession(), eres.getMessage(), "menu");
            return;
        }

        String table 
                = makeTableFromReimbursementRequests(eres.getReturnedReimbursementRequests());
        
        handleSuccess(response, request.getSession(), table, "menu");
    }

    
}
