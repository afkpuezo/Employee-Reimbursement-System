/**
 * Handles an employee updating their user profile information.
 */
package com.revature.servlets.employee;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revature.model.UserProfile;
import com.revature.model.UserProfile.UserRole;
import com.revature.service.comms.ERSRequest;
import com.revature.service.comms.ERSResponse;
import com.revature.service.comms.ERSRequest.ERSRequestType;
import com.revature.servlets.ERSServlet;

public class EmployeeUpdateSelfServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public EmployeeUpdateSelfServlet() {
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

        request.getRequestDispatcher("employee_update_self.html").forward(
                request, 
                response);
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

        // get the user's input - ignore fields that are blank
        boolean hasUsername = true;
        boolean hasFirstName = true;
        boolean hasLastName = true;
        boolean hasEmailAddress = true;
        
        String username = request.getParameter("username");
        if (isStringBlank(username)) hasUsername = false;
        String firstName = request.getParameter("firstName");
        if (isStringBlank(firstName)) hasFirstName = false;
        String lastName = request.getParameter("lastName");
        if (isStringBlank(lastName)) hasLastName = false;
        String emailAddress = request.getParameter("emailAddress");
        if (isStringBlank(emailAddress)) hasEmailAddress = false;

        // make sure the changed fields are valid
        if (hasUsername && !isUsernameValid(username)){
            handleProblem(response, request, "Invalid username.", "employee_update_self");
            return;
        }

        if (hasFirstName && !isPersonalNameValid(firstName)){
            handleProblem(
                    response, request, "Invalid first name.", "employee_update_self");
            return;
        }

        if (hasLastName && !isPersonalNameValid(lastName)){
            handleProblem(
                    response, request, "Invalid last name.", "employee_update_self");
            return;
        }
        
        if (hasEmailAddress && !isEmailAddressValid(emailAddress)){
            handleProblem(
                    response, request, "Invalid email address.", "employee_update_self");
            return;
        }

        // if a field is not being changed, get the old value
        ERSRequest upreq = makeERSRequest(ERSRequestType.EMPLOYEE_VIEW_SELF, request);
        ERSResponse upres = getResponse(upreq);

        if (isFailure(upres)){
            handleProblem(
                    response, 
                    request, 
                    "There was a problem making a connection to the database.", 
                    "menu");
            return;
        }

        UserProfile up = upres.getReturnedUserProfiles().get(0);
        if (!hasUsername) username = up.getUsername();
        if (!hasFirstName) firstName = up.getFirstName();
        if (!hasLastName) lastName = up.getLastName();
        if (!hasEmailAddress) emailAddress = up.getEmailAddress();

        // now we can build the real request
        ERSRequest ereq = makeERSRequest(ERSRequestType.EMPLOYEE_UPDATE_SELF, request);
        ereq.putParameter(ERSRequest.USERNAME_KEY, username);
        ereq.putParameter(ERSRequest.FIRST_NAME_KEY, firstName);
        ereq.putParameter(ERSRequest.LAST_NAME_KEY, lastName);
        ereq.putParameter(ERSRequest.EMAIL_ADDRESS_KEY, emailAddress);
        ERSResponse eres = getResponse(ereq);

        if (isFailure(eres)){
            handleProblem(response, request, eres.getMessage(), "employee_update_self");
            return;
        }

        handleSuccess(response, request, "User details successfully updated.", "menu");
    }
}
