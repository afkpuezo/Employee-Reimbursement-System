/**
 * This class is for handling view-requests, that is, requests to view something, rather
 * than a 'get a look at the request handler'.
 * 
 * It is used by the ServiceFront; it contains handle methods for those request types
 * whose (primary) purpose is to retrieve information from the database and display it to
 * the user.
 * 
 * Each handler method assumes that the ServiceFront has checked to make sure the user's
 * role is one that is permitted to take that action. Other permission/possibility checks
 * may still occur.
 */
package com.revature.service.handlers;

import com.revature.model.UserProfile;
import com.revature.repository.DAO.exceptions.DAOException;
import com.revature.repository.DAO.interfaces.ReimbursementRequestDAO;
import com.revature.repository.DAO.interfaces.UserProfileDAO;
import com.revature.repository.DAO.interfaces.ReimbursementRequestDAO.SearchType;
import com.revature.service.comms.ERSRequest;
import com.revature.service.comms.ERSResponse;
import com.revature.service.comms.ERSResponse.ERSResponseType;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestHandler extends RequestHandler {
    
    // enums -------------------------------

    // constants ---------------------------

    // static / class variables ------------

    // instance variables ------------------
    private UserProfileDAO updao;
    private ReimbursementRequestDAO rrdao;

    // constructor(s) ----------------------

    public ViewRequestHandler(UserProfileDAO updao, ReimbursementRequestDAO rrdao){

        this.updao = updao;
        this.rrdao = rrdao;
    }

    // handler methods ---------------------------------------------

    /**
     * Allows an employee to view all of their own pending reimb-reqs.
     * If there are no such requests, succeeds and returns a response with an empty list.
     * Fails if the employee doesnt exist.
     * Fails if there is a DAOException.
     * 
     * @param req
     * @return
     */
    public ERSResponse handleEmployeeViewPending(ERSRequest req){

        int userID = req.getUserID();

        try{
            if (!updao.checkExists(userID)) return getUserDoesNotExistResponse(userID);
            
            ERSResponse res = new ERSResponse(ERSResponseType.SUCCESS);
            res.setReturnedReimbursementRequests(
                    rrdao.getReimbursementRequests(userID, SearchType.PENDING));
            return res;
        }
        catch(DAOException e){
            return getGenericDAOExceptionResponse();
        }

    }

    /**
     * Allows an employee to view all of their own pending reimb-reqs.
     * If there are no such requests, succeeds and returns a response with an empty list.
     * Fails if the employee doesnt exist.
     * Fails if there is a DAOException.
     * 
     * @param req
     * @return
     */
    public ERSResponse handleEmployeeViewResolved(ERSRequest req) {
        
        int userID = req.getUserID();

        try{
            if (!updao.checkExists(userID)) return getUserDoesNotExistResponse(userID);
            
            ERSResponse res = new ERSResponse(ERSResponseType.SUCCESS);
            res.setReturnedReimbursementRequests(
                    rrdao.getReimbursementRequests(userID, SearchType.RESOLVED));
            return res;
        }
        catch(DAOException e){
            return getGenericDAOExceptionResponse();
        }
    }

    /**
     * Allows an employee to see their own personal/account info
     * Fails if the employee doesnt exist.
     * Fails if there is a DAOException.
     * 
     * @param req
     * @param return
     */
    public ERSResponse handleEmployeeViewSelf(ERSRequest req) {
        
        int userID = req.getUserID();

        try{
            if (!updao.checkExists(userID)) return getUserDoesNotExistResponse(userID);

            List<UserProfile> uplist = new ArrayList<>();
            uplist.add(updao.getUserProfile(userID));
            ERSResponse res = new ERSResponse(ERSResponseType.SUCCESS);
            res.setReturnedUserProfiles(uplist);

            return res;
        }
        catch(DAOException e){
            return getGenericDAOExceptionResponse();
        }
    }

    /**
     * Only called by managers, returns an response with every pending reimb-req.
     * The response's list is empty if there are no pending reqs.
     * Fails if there is a DAOException.
     * 
     * @param req
     * @return
     */
    public ERSResponse handleViewAllPending(ERSRequest req) {
        
        // ? no need to check if the manager actually exists

        try{
            ERSResponse res = new ERSResponse(ERSResponseType.SUCCESS);
            res.setReturnedReimbursementRequests(
                    rrdao.getReimbursementRequests(-1, SearchType.PENDING));
            return res;
        }
        catch (DAOException e){
            return getGenericDAOExceptionResponse();
        }
    }

    /**
     * Only called by managers, returns an response with every resolved reimb-req.
     * The response's list is empty if there are no pending reqs.
     * Fails if there is a DAOException.
     * 
     * @param req
     * @return
     */
    public ERSResponse handleViewAllResolved(ERSRequest req) {
        
        // ? no need to check if the manager actually exists

        try{
            ERSResponse res = new ERSResponse(ERSResponseType.SUCCESS);
            res.setReturnedReimbursementRequests(
                    rrdao.getReimbursementRequests(-1, SearchType.RESOLVED));
            return res;
        }
        catch (DAOException e){
            return getGenericDAOExceptionResponse();
        }
    }

    /**
     * Only called by managers, returns an response with every employee's UserProfile.
     * The response's list is empty if there are no employees.
     * Fails if there is a DAOException.
     * 
     * @param req
     * @return
     */
    public ERSResponse handleViewAllEmployees(ERSRequest req) {
        
        // ? no need to check if the manager actually exists

        try{
            ERSResponse res = new ERSResponse(ERSResponseType.SUCCESS);
            res.setReturnedUserProfiles(updao.getAllEmployeeProfiles());
            return res;
        }
        catch (DAOException e){
            return getGenericDAOExceptionResponse();
        }
    }

    /**
     * Only called by managers, returns an response with all of the reimb-reqs authored
     * by a single particular employee.
     * The response's list is empty if there are no such requests.
     * Fails if the employee does not exist.
     * Fails if there is a DAOException.
     * 
     * ? should it also return the targeted employee's UserProfile? stretch goal I suppose
     * 
     * @author Andrew Curry
     * 
     * @param req
     * @return
     */
    public ERSResponse handleManagerViewByEmployee(ERSRequest req) {
        
        try{
            // IMPORTANT - this is the targeted employee, NOT the manager triggering
            // this request.
            if (!req.hasParameter(ERSRequest.EMPLOYEE_ID_KEY))
                return getMalformedRequestResponse();

            int empID = Integer.parseInt(req.getParameter(ERSRequest.EMPLOYEE_ID_KEY));
            if (!updao.checkExists(empID)) return getUserDoesNotExistResponse(empID);

            ERSResponse res = new ERSResponse(ERSResponseType.SUCCESS);
            res.setReturnedReimbursementRequests(
                    rrdao.getReimbursementRequests(empID, SearchType.ALL));
            return res;
        }
        catch (DAOException e){
            return getGenericDAOExceptionResponse();
        }
    }
}
