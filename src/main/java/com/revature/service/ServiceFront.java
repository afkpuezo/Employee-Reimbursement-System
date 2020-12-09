/**
 * This class is the interaction point between the front end and the back end.
 * Requests are passed into handleRequest, and the ServiceFront will pass them to the
 * correct RequestHandler field.
 * 
 * The front end will not create instances of this class; they will use the BackEndUtil
 * class which will implement a singleton pattern.
 * 
 * NOTE: I'm very unsure about the quality of this organization style
 * 
 * @author Andrew Curry
 */
package com.revature.service;

import com.revature.model.UserProfile.UserRole;
import com.revature.service.comms.ERSRequest;
import com.revature.service.comms.ERSResponse;
import com.revature.service.comms.ERSRequest.ERSRequestType;
import com.revature.service.comms.ERSResponse.ERSResponseType;
import com.revature.service.handlers.AuthRequestHandler;
import com.revature.service.handlers.ModifyRequestHandler;
import com.revature.service.handlers.ViewRequestHandler;

public class ServiceFront {
    
    // constants

    // these arrays control what actions each user role can make
    private static final ERSRequestType[] NONE_TYPES = {};
    private static final ERSRequestType[] LOGGED_OUT_TYPES = {
        ERSRequestType.LOG_IN,
    };
    private static final ERSRequestType[] EMPLOYEE_TYPES = {
        ERSRequestType.SUBMIT_REQUEST,
        ERSRequestType.EMPLOYEE_VIEW_PENDING,
        ERSRequestType.EMPLOYEE_VIEW_RESOLVED,
        ERSRequestType.EMPLOYEE_VIEW_SELF,
        ERSRequestType.EMPLOYEE_UPDATE_SELF,
        ERSRequestType.LOG_OUT,
    };
    private static final ERSRequestType[] MANAGER_TYPES = {
        ERSRequestType.APPROVE_REQUEST,
        ERSRequestType.DENY_REQUEST,
        ERSRequestType.VIEW_ALL_PENDING,
        ERSRequestType.VIEW_ALL_RESOLVED,
        ERSRequestType.VIEW_ALL_EMPLOYEES,
        ERSRequestType.MANAGER_VIEW_BY_EMPLOYEE,
        ERSRequestType.LOG_OUT,
    };

    // class/static variables

    // instance variables
    private AuthRequestHandler arh;
    private ViewRequestHandler vrh;
    private ModifyRequestHandler mrh;

    // constructor(s)

    /**
     * Standard dependency-injection constructor
     */
    public ServiceFront(
            AuthRequestHandler arh,
            ViewRequestHandler vrh, 
            ModifyRequestHandler mrh){

        this.arh = arh;
        this.vrh = vrh;
        this.mrh = mrh;
    }

    // method(s)

    /**
     * Returns an array containing the permitted request types for the given role.
     * EG, if passed MANAGER, returns all of the actions a manager can take.
     * 
     * @param role
     * @return
     */
    public ERSRequestType[] getPossibleRequestTypes(UserRole role){
        
        switch (role){
            case LOGGED_OUT:
                return LOGGED_OUT_TYPES;
            case EMPLOYEE:
                return EMPLOYEE_TYPES;
            case MANAGER:
                return MANAGER_TYPES;
            default:
                return NONE_TYPES;
        }
    }

    /**
     * Passes the given req to the appropriate RequestHandler and returns the resulting
     * response.
     * Assumes the req is not null.
     * 
     * @param req
     * @return
     */
    public ERSResponse handleERSRequest(ERSRequest req){
        
        ERSRequestType type = req.getType();

        // can the current user do this action?
        ERSRequestType[] permitted = getPossibleRequestTypes(req.getUserRole());
        boolean found = false;
        for (ERSRequestType pType : permitted){
            if (type == pType){
                found = true;
                break;
            }
        }
        if (!found) return new ERSResponse(
                ERSResponseType.FORBIDDEN,
                "You do not have permission to take that action.");

        switch(type){

            case LOG_IN:
                return arh.handleLogIn(req);
            case LOG_OUT:
                return arh.handleLogOut(req);
            case SUBMIT_REQUEST:
                return mrh.handleSubmitRequest(req);
            case EMPLOYEE_VIEW_PENDING:
                return vrh.handleEmployeeViewPending(req);
            case EMPLOYEE_VIEW_RESOLVED:
                return vrh.handleEmployeeViewResolved(req);
            case EMPLOYEE_VIEW_SELF:
                return vrh.handleEmployeeViewSelf(req);
            case EMPLOYEE_UPDATE_SELF:
                return mrh.handleEmployeeUpdateSelf(req);
            case APPROVE_REQUEST:
                return mrh.handleApproveRequest(req);
            case DENY_REQUEST:
                return mrh.handleDenyRequest(req);
            case VIEW_ALL_PENDING:
                return vrh.handleViewAllPending(req);
            case VIEW_ALL_RESOLVED:
                return vrh.handleViewAllResolved(req);
            case VIEW_ALL_EMPLOYEES:
                return vrh.handleViewAllEmployees(req);
            case MANAGER_VIEW_BY_EMPLOYEE:
                return vrh.handleManagerViewByEmployee(req);
            default: // should never happen?
                return new ERSResponse(
                        ERSResponseType.MALFORMED_REQUEST,
                        String.format("Request Type '%s' not recognized", type.name()));
        }
    }
}
