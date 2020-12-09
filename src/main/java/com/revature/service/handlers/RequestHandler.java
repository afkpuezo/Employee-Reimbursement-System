/**
 * An abstrast class extended by individual handlers, contains some shared helper/utility
 * methods that can be used by each of the actual request handlers.
 * 
 * @author Andrew Curry
 */
package com.revature.service.handlers;

import com.revature.service.comms.ERSResponse;
import com.revature.service.comms.ERSResponse.ERSResponseType;

public abstract class RequestHandler {

    /**
     * Returns a response indicating that a user was not found.
     * 
     * @param userID : included in the message
     * @return
     */
    protected ERSResponse getUserDoesNotExistResponse(int userID) {

        return new ERSResponse(ERSResponseType.INVALID_PARAMETER,
                String.format("No user profile with ID %d was found.", userID));
    }

    /**
     * Returns a response indicating that a user was not found.
     * 
     * @param username : included in the message
     * @return
     */
    protected ERSResponse getUserDoesNotExistResponse(String username) {

        return new ERSResponse(ERSResponseType.INVALID_PARAMETER,
                String.format("No user profile with username '%s' was found.", username));
    }

    /**
     * Returns a response indicating that there was a problem with the DAO
     * 
     * @return
     */
    protected ERSResponse getGenericDAOExceptionResponse() {

        return new ERSResponse(
                ERSResponseType.DATABASE_ERROR, 
                "There was a problem communicating with the database.");
    }

    /**
     * Returns a response indicating that the front end sent a malformed request to the
     * service layer.
     * 
     * NOTE: If I do this right, this should never happen, but it keeps mistakes from
     * NOTE: crashing the entire system during development. (hopefully)
     * 
     * @return
     */
    protected ERSResponse getMalformedRequestResponse() {
        
        return new ERSResponse(
                ERSResponseType.MALFORMED_REQUEST, 
                "Malformed request. Contant system admin.");
    }

    /**
     * Returns a response indicating that a reimbursement request was not found.
     * 
     * @param reimbID
     * @return
     */
    protected ERSResponse getReimbursementRequestDoesNotExistResponse(int reimbID) {
        return new ERSResponse(ERSResponseType.INVALID_PARAMETER,
                String.format(
                        "No reimbursement request with id %d was found.", reimbID));
    }
}
