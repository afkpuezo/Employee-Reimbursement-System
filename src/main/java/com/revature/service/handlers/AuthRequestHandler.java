/**
 * This handler class handles requests related to user authorization (currently, just
 * logging in and logging out)
 * 
 * @author Andrew Curry
 */
package com.revature.service.handlers;

import java.util.ArrayList;
import java.util.List;

import com.revature.model.UserProfile;
import com.revature.model.UserProfile.UserRole;
import com.revature.repository.DAO.exceptions.DAOException;
import com.revature.repository.DAO.interfaces.ReimbursementRequestDAO;
import com.revature.repository.DAO.interfaces.UserProfileDAO;
import com.revature.service.PasswordUtil;
import com.revature.service.comms.ERSRequest;
import com.revature.service.comms.ERSResponse;
import com.revature.service.comms.ERSResponse.ERSResponseType;

public class AuthRequestHandler extends RequestHandler {
    
    // instance variables ------------------
    private UserProfileDAO updao;
    //private ReimbursementRequestDAO rrdao; // never needs this

    // constructor(s) ----------------------

    public AuthRequestHandler(UserProfileDAO updao, ReimbursementRequestDAO rrdao){

        this.updao = updao;
        //this.rrdao = rrdao;
    }

    // handler methods

    /**
     * Attempts to log in to the user profile indicated by the parameters in the request.
     * Succeeds if the username matches an existing account, and the given password
     * matches the password for the given account.
     * Fails if the user is not found, or if user is already logged in.
     * 
     * @param req
     * @return
     */
    public ERSResponse handleLogIn(ERSRequest req){

        try{
            if (!req.hasParameter(ERSRequest.USERNAME_KEY) 
                    || !req.hasParameter(ERSRequest.PASSWORD_KEY))
                return getMalformedRequestResponse();

            if (req.getUserRole() != UserRole.LOGGED_OUT)
                    return new ERSResponse(
                            ERSResponseType.FORBIDDEN,
                            String.format(
                                "Unable to log in: there is already a user logged in."));

            String username = req.getParameter(ERSRequest.USERNAME_KEY);
            if (!updao.checkExists(username)) 
            return getUserDoesNotExistResponse(username);
            
            String password = req.getParameter(ERSRequest.PASSWORD_KEY);
            if (!checkPassword(username, password)) // helper handles encryption
                return getIncorrectPasswordResponse(username);

            // return the UP object to let the front end know information like ID
            ERSResponse res = new ERSResponse(ERSResponseType.SUCCESS);
            List<UserProfile> returnedUsers = new ArrayList<>();
            returnedUsers.add(updao.getUserProfile(username));
            res.setReturnedUserProfiles(returnedUsers);

            return res;
        }
        catch(DAOException e){
            return getGenericDAOExceptionResponse();
        }
    }

    /**
     * Currently, only checks to make sure that someone is logged in. Succeeds unless the
     * user is trying to logout while not logged in.
     * 
     * NOTE: currently checks only the req's userRole, not the userID
     * 
     * @param req
     * @return
     */
    public ERSResponse handleLogOut(ERSRequest req){
        
        if (req.getUserRole() == UserRole.LOGGED_OUT)
            return new ERSResponse(
                    ERSResponseType.FORBIDDEN,
                    "Unable to log out: No one is currently logged in.");
        else return new ERSResponse(ERSResponseType.SUCCESS);
    }

    // helpers

    /**
     * Checks to see if the given password is valid for the account indicated by the
     * given username.
     * 
     * NOTE: currently, encryption is not implemented.
     * TODO: encryption
     * 
     * @param username
     * @param password
     * @return
     */
    private boolean checkPassword(String username, String password) throws DAOException{
        
        String truePassword = updao.getPassword(username);
        return PasswordUtil.checkPassword(password, truePassword);
    }

    /**
     * Returns a standardized response indicating the user was trying to log in with an
     * incorrect password.
     * 
     * This is (or should be) the only handler class that needs to deal with passwords,
     * so this method will not be in the abstract RequestHandler.
     * 
     * @param username
     * @return
     */
    private ERSResponse getIncorrectPasswordResponse(String username) {

        return new ERSResponse(
                ERSResponseType.INVALID_PARAMETER,
                String.format("Invalid password for account '%s'", username));
    }
}
