/**
 * Handles the submit_request page and action.
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

public class SubmitRequestServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public SubmitRequestServlet() {
        super();
    }

    /**
     * Serves up the html page with the form.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (getCurrentUserRole(request) != UserRole.EMPLOYEE) {
            redirectToMenu(response);
            return;
        }

        request.getRequestDispatcher("submit_request.html").forward(request, response);
    }

    /**
     * Processess the form input, and if valid, adds the new reimb-req.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (getCurrentUserRole(request) != UserRole.EMPLOYEE) {
            redirectToMenu(response);
            return;
        }

        // get the user's input
        String moneyString = request.getParameter("moneyAmount");
        String typeString = request.getParameter("type");
        String description = request.getParameter("description");

        String bareMoney = null;

        // make sure inputs are valid
        if (isMoneyStringValid(moneyString)) 
            bareMoney = moneyStringToBareString(moneyString);
        else{
            handleProblem(response, request, "Invalid money format.", "submit_request");
            return;
        }

        // now we can build the request
        ERSRequest ereq = makeERSRequest(ERSRequestType.SUBMIT_REQUEST, request);
        ereq.putParameter(ERSRequest.REIMBURSEMENT_TYPE_KEY, typeString);
        ereq.putParameter(ERSRequest.MONEY_AMOUNT_KEY, "" + bareMoney);

        if (description != null && !description.equals(""))
            ereq.putParameter(ERSRequest.REIMBURSEMENT_DESCRIPTION_KEY, description);
        
        ERSResponse eres = getResponse(ereq);
        if (isFailure(eres)){
            handleProblem(response, request, eres.getMessage(), "submit_request");
            return;
        }

        // if successful, the response's message should include the ID of the new reimb
        handleSuccess(response, request, eres.getMessage(), "menu");
    }
}
