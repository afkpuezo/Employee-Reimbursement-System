/**
 * Handles the task of a manager viewing all of the requests authored by a single employee
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

public class ManagerViewByEmployeeServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public ManagerViewByEmployeeServlet() {
        super();
    }

    /**
     * Serves the html file with the form to input the authoring user id
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // make sure only a manager can view this
        if (getCurrentUserRole(request) != UserRole.MANAGER) {
            redirectToMenu(response);
            return;
        }

        request.getRequestDispatcher("manager_view_by_employee.html").forward(
                request, response);
    }

    /**
     * Validates and parses the input, then fetches the right reimb-reqs
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // make sure only a manager can view this
        if (getCurrentUserRole(request) != UserRole.MANAGER) {
            redirectToMenu(response);
            return;
        }

        String idString = request.getParameter("employeeID");
        
        if (isIDStringValid(idString)) idString = cleanIDString(idString);
        else{
            handleProblem(response, request, 
                    "Invalid ID# format.", "manager_view_by_employee");
            return;
        }

        // make the actual ERSreq
        ERSRequest ereq 
                = makeERSRequest(ERSRequestType.MANAGER_VIEW_BY_EMPLOYEE, request);
        ereq.putParameter(ERSRequest.EMPLOYEE_ID_KEY, idString);
        ERSResponse eres = getResponse(ereq);

        if (isFailure(eres)){
            handleProblem(response, request, 
                    eres.getMessage(), "manager_view_by_employee");
            return;
        }

        // display results
        String html = makeTableFromReimbursementRequests(
                    eres.getReturnedReimbursementRequests());
        
        handleSuccess(response, request, html, "menu");
    }
    
}
