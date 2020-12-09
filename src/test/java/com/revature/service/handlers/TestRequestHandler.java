/**
 * Test classes meant for specific RequestHandler classes should extend this.
 * It contains some common util/helper methods for tests.
 * 
 * @author Andrew Curry
 */
package com.revature.service.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.revature.service.comms.ERSResponse;
import com.revature.service.comms.ERSResponse.ERSResponseType;

public abstract class TestRequestHandler {
    
    /**
     * Ensures that the given response is in the correct format to indicate that
     * there was an invalid parameter in the original request (EG, a user that did
     * not exist). 
     * Currently, does not check the message.
     * 
     * @param res
     */
    protected void ensureInvalidParameterResponse(ERSResponse res){

        assertNotNull(res);
        assertEquals(ERSResponseType.INVALID_PARAMETER, res.getType());
        ensureResponseListsAreEmpty(res);
    }

    /**
     * Ensures that the given response is in the correct format to indicate that
     * there was a DAOException thrown while handling the original request (EG, could not
     * make a connection with the database).
     * Currently, does not check the message.
     * 
     * @param res
     */
    protected void ensureDatabaseErrorResponse(ERSResponse res) {

        assertNotNull(res);
        assertEquals(ERSResponseType.DATABASE_ERROR, res.getType());
        ensureResponseListsAreEmpty(res);
    }

    /**
     * Ensures that both returned-lists are empty.
     * 
     * @param res
     */
    protected void ensureResponseListsAreEmpty(ERSResponse res){

        assertNotNull(res);
        assertTrue(res.getReturnedUserProfiles().isEmpty());
        assertTrue(res.getReturnedReimbursementRequests().isEmpty());
    }

    /**
     * Ensures that the given res is of type SUCCESS
     * 
     * @param res
     */
    protected void ensureSuccessfulResponse(ERSResponse res){

        assertNotNull(res);
        assertEquals(ERSResponseType.SUCCESS, res.getType());
    }

    /**
     * Ensures that the given response is in the correct format to indicate that
     * the original request was malformed (EG, outright missing an expected parameter)
     * Currently, does not check the message.
     * 
     * @param res
     */
    protected void ensureMalformedRequestResponse(ERSResponse res){

        assertNotNull(res);
        assertEquals(ERSResponseType.MALFORMED_REQUEST, res.getType());
        ensureResponseListsAreEmpty(res);
    }
}
