/**
 * This class contains tests for the AuthRequestHandler class.
 */
package com.revature.service.handlers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.List;

import com.revature.model.UserProfile;
import com.revature.model.UserProfile.UserRole;
import com.revature.repository.DAO.exceptions.DAOException;
import com.revature.repository.DAO.interfaces.ReimbursementRequestDAO;
import com.revature.repository.DAO.interfaces.UserProfileDAO;
import com.revature.service.PasswordUtil;
import com.revature.service.comms.ERSRequest;
import com.revature.service.comms.ERSResponse;
import com.revature.service.comms.ERSRequest.ERSRequestType;
import com.revature.service.comms.ERSResponse.ERSResponseType;

public class TestARH extends TestRequestHandler{
    
    // rules
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    // static/class variables
    private static AuthRequestHandler arh;
    private static UserProfileDAO updao;
    private static ReimbursementRequestDAO rrdao;

    @Before
    public void setUpVRH(){
        
        updao = mock(UserProfileDAO.class); // more than one way to do this
        rrdao = mock(ReimbursementRequestDAO.class);
        arh = new AuthRequestHandler(updao, rrdao);
    }

    // --------------------------------------------------------------------------
    // --------------------------------------------------------------------------
    // Tests
    // --------------------------------------------------------------------------
    // --------------------------------------------------------------------------

    // --------------------------------------------------------------------------
    // handleLogIn
    // --------------------------------------------------------------------------

    @Test
    public void testHandleLogIn() throws DAOException{

        int loID = -1;
        UserRole lorole = UserRole.LOGGED_OUT; 

        int userID = 1; // the account that is being logged in to
        UserRole userRole = UserRole.EMPLOYEE;
        String username = "testuser";
        String password = "testpass";
        String firstName = "testfirst";
        String lastName = "testlast";
        String email = "email@test.com";
        UserProfile up 
                = new UserProfile(userID, userRole, username, firstName, lastName, email);
        ERSRequest req = new ERSRequest(ERSRequestType.LOG_IN, loID, lorole);
        req.putParameter(ERSRequest.USERNAME_KEY, username);
        req.putParameter(ERSRequest.PASSWORD_KEY, password);

        when(updao.checkExists(username)).thenReturn(true);
        when(updao.getPassword(username)).thenReturn(encryptPassword(password));
        when(updao.getUserProfile(username)).thenReturn(up);

        ERSResponse res = arh.handleLogIn(req);
        ensureSuccessfulResponse(res);
        assertTrue(res.getReturnedReimbursementRequests().isEmpty());
        List<UserProfile> returnedUsers = res.getReturnedUserProfiles();
        assertEquals(1, returnedUsers.size());
        assertEquals(up, returnedUsers.get(0));
    }

    @Test
    public void testHandleLogInUserNotFound() throws DAOException{

        int loID = -1;
        UserRole lorole = UserRole.LOGGED_OUT; 

        String username = "testuser";
        String password = "testpass";
        ERSRequest req = new ERSRequest(ERSRequestType.LOG_IN, loID, lorole);
        req.putParameter(ERSRequest.USERNAME_KEY, username);
        req.putParameter(ERSRequest.PASSWORD_KEY, password);

        when(updao.checkExists(username)).thenReturn(false);
        ERSResponse res = arh.handleLogIn(req);
        ensureInvalidParameterResponse(res);
    }

    @Test
    public void testHandleLogInBadPass() throws DAOException{

        int loID = -1;
        UserRole lorole = UserRole.LOGGED_OUT; 

        String username = "testuser";
        String password = "testpass";
        ERSRequest req = new ERSRequest(ERSRequestType.LOG_IN, loID, lorole);
        req.putParameter(ERSRequest.USERNAME_KEY, username);
        req.putParameter(ERSRequest.PASSWORD_KEY, password);

        when(updao.checkExists(username)).thenReturn(true);
        when(updao.getPassword(username)).thenReturn(encryptPassword("different"));

        ERSResponse res = arh.handleLogIn(req);
        ensureInvalidParameterResponse(res);
    }

    @Test
    public void testHandleLogInDAOException() throws DAOException{

        int loID = -1;
        UserRole lorole = UserRole.LOGGED_OUT; 

        String username = "testuser";
        String password = "testpass";
        ERSRequest req = new ERSRequest(ERSRequestType.LOG_IN, loID, lorole);
        req.putParameter(ERSRequest.USERNAME_KEY, username);
        req.putParameter(ERSRequest.PASSWORD_KEY, password);

        when(updao.checkExists(username)).thenThrow(new DAOException(""));

        ERSResponse res = arh.handleLogIn(req);
        ensureDatabaseErrorResponse(res);
    }

    @Test
    public void testHandleLogInAlreadyLoggedIn() throws DAOException{

        int userID = 3;
        UserRole role = UserRole.EMPLOYEE; 

        String username = "testuser";
        String password = "testpass";
        ERSRequest req = new ERSRequest(ERSRequestType.LOG_IN, userID, role);
        req.putParameter(ERSRequest.USERNAME_KEY, username);
        req.putParameter(ERSRequest.PASSWORD_KEY, password);

        ERSResponse res = arh.handleLogIn(req);
        assertNotNull(res);
        assertEquals(ERSResponseType.FORBIDDEN, res.getType());
        ensureResponseListsAreEmpty(res);
    }

    @Test
    public void testHandleLogInMalformed() throws DAOException{

        int loID = -1;
        UserRole lorole = UserRole.LOGGED_OUT; 

        ERSRequest req = new ERSRequest(ERSRequestType.LOG_IN, loID, lorole);
        ERSResponse res = arh.handleLogIn(req);
        ensureMalformedRequestResponse(res);
    }

    /**
     * A helper for test methods related to passwords.
     * Eventually, this will be setup to mimic whatever encryption method I use.
     * TODO: actual encryption
     */
    private String encryptPassword(String password){
        return PasswordUtil.hash(password);
    }

    // --------------------------------------------------------------------------
    // handleLogOut
    // --------------------------------------------------------------------------

    @Test
    public void testHandleLogOut() throws DAOException{

        int userID = 3;
        UserRole role = UserRole.EMPLOYEE; 

        ERSRequest req = new ERSRequest(ERSRequestType.LOG_OUT, userID, role);
        ERSResponse res = arh.handleLogOut(req);
        ensureSuccessfulResponse(res);
        ensureResponseListsAreEmpty(res);
    }

    @Test
    public void testHandleLogOutAlreadyLoggedOut() throws DAOException{

        int loID = -1;
        UserRole lorole = UserRole.LOGGED_OUT; 

        ERSRequest req = new ERSRequest(ERSRequestType.LOG_OUT, loID, lorole);
        ERSResponse res = arh.handleLogOut(req);
        assertNotNull(res);
        assertEquals(ERSResponseType.FORBIDDEN, res.getType());
        ensureResponseListsAreEmpty(res);
    }
}
