/**
 * Handles a manager denying a request.
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

public class DenyRequestServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public DenyRequestServlet() {
        super();
    }

    /**
     * Serves the basic html page.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (getCurrentUserRole(request) != UserRole.MANAGER) {
            redirectToMenu(response);
            return;
        }

        request.getRequestDispatcher("deny_request.html").forward(request, response);
    }

    /**
     * Validate the ID number and carry out the actual action.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (getCurrentUserRole(request) != UserRole.MANAGER) {
            redirectToMenu(response);
            return;
        }

        String idString = request.getParameter("reimbID");

        if (isIDStringValid(idString)) idString = cleanIDString(idString);
        else{
            handleProblem(response, request, "invalid format for ID#", "deny_request");
            return;
        }

        // good to make the actual request
        ERSRequest ereq = makeERSRequest(ERSRequestType.DENY_REQUEST, request);
        ereq.putParameter(ERSRequest.REIMBURSEMENT_ID_KEY, idString);
        ERSResponse eres = getResponse(ereq);

        // handle results
        if (isFailure(eres)){
            handleProblem(response, request, eres.getMessage(), "deny_request");
            return;
        }

        String message = "Reimbursement Request #" + idString + " denied.";
        handleSuccess(response, request, message, "menu");
    }
}
