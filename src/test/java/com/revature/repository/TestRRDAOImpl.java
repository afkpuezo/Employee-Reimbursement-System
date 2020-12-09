/**
 * This class contains unit tests for the ReimbursementRequestDAOImpl class.
 */
package com.revature.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.revature.model.ReimbursementRequest;
import com.revature.model.UserProfile;
import com.revature.model.ReimbursementRequest.ReimbursementStatus;
import com.revature.model.ReimbursementRequest.ReimbursementType;
import com.revature.model.UserProfile.UserRole;
import com.revature.repository.DAO.exceptions.DAOException;
import com.revature.repository.DAO.impl.ReimbursementRequestDAOImpl;
import com.revature.repository.DAO.interfaces.ReimbursementRequestDAO;
import com.revature.repository.DAO.interfaces.ReimbursementRequestDAO.SearchType;
import com.revature.repository.Util.HibernateConnectionUtil;

public class TestRRDAOImpl {
    
    // class / static vars
    private ReimbursementRequestDAO rrdao;

    @Before
    public void setup(){
        HibernateConnectionUtil.enterTestMode();
        rrdao = new ReimbursementRequestDAOImpl();
    }

    @After
    public void cleanup(){
        HibernateConnectionUtil.exitTestMode();
        HibernateConnectionUtil.forceDropSessionFactory(); // unnecessary?
    }

    /**
     * Should test different combinations of parameters
     * 
     * @throws DAOException
     */
    @Test
    public void testGetReimbursementRequests() throws DAOException{

        // should get an empty list if there are no reimb-reqs
        List<ReimbursementRequest> reimbList 
                = rrdao.getReimbursementRequests(-1, SearchType.ALL);
        assertNotNull(reimbList);
        assertTrue(reimbList.isEmpty());

        // put some reimbs in there...which also requires people
        UserProfile up0 = new UserProfile();
        up0.setUsername("up0");
        up0.setRole(UserRole.EMPLOYEE);
        UserProfile up1 = new UserProfile();
        up1.setUsername("up1");
        up1.setRole(UserRole.EMPLOYEE);

        Session session = HibernateConnectionUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.save(up0);
        session.save(up1);
        tx.commit();

        ReimbursementRequest rr0 = new ReimbursementRequest();
        rr0.setAuthor(up0);
        rr0.setStatus(ReimbursementStatus.PENDING);
        rr0.setType(ReimbursementType.FOOD); 

        ReimbursementRequest rr1 = new ReimbursementRequest();
        rr1.setAuthor(up1);
        rr1.setStatus(ReimbursementStatus.APPROVED);
        rr1.setType(ReimbursementType.LODGING);

        tx = session.beginTransaction();
        session.save(rr0);
        session.save(rr1);
        tx.commit();
        session.close();

        // now look for the new reqs
        reimbList = rrdao.getReimbursementRequests(-1, SearchType.ALL);
        assertNotNull(reimbList);
        assertEquals(2, reimbList.size());

        // clumsy way of making sure that the right reimb-reqs are returned
        ReimbursementRequest found0 = null;
        ReimbursementRequest found1 = null;

        for (ReimbursementRequest reimb : reimbList){
            if (reimb.getID() == rr0.getID()) found0 = reimb;
            else if (reimb.getID() == rr1.getID()) found1 = reimb;
        }

        assertNotNull(found0);
        assertNotNull(found1);

        assertEquals(rr0.getStatus(), found0.getStatus());
        assertEquals(rr0.getType(), found0.getType());
        assertEquals(rr0.getAuthorID(), found0.getAuthorID());

        assertEquals(rr1.getStatus(), found1.getStatus());
        assertEquals(rr1.getType(), found1.getType());
        assertEquals(rr1.getAuthorID(), found1.getAuthorID());

        // now test the discriminator parameters

        // by author
        reimbList = rrdao.getReimbursementRequests(12345, SearchType.ALL);
        assertNotNull(reimbList);
        assertTrue(reimbList.isEmpty());

        reimbList = rrdao.getReimbursementRequests(rr0.getAuthorID(), SearchType.ALL);
        assertNotNull(reimbList);
        assertEquals(1, reimbList.size());
        found0 = reimbList.get(0);

        assertEquals(rr0.getStatus(), found0.getStatus());
        assertEquals(rr0.getType(), found0.getType());
        assertEquals(rr0.getAuthorID(), found0.getAuthorID());

        reimbList = rrdao.getReimbursementRequests(rr1.getAuthorID(), SearchType.ALL);
        assertNotNull(reimbList);
        assertEquals(1, reimbList.size());
        found1 = reimbList.get(0);

        assertEquals(rr1.getStatus(), found1.getStatus());
        assertEquals(rr1.getType(), found1.getType());
        assertEquals(rr1.getAuthorID(), found1.getAuthorID());

        // by status
        reimbList = rrdao.getReimbursementRequests(-1, SearchType.PENDING);
        assertNotNull(reimbList);
        assertEquals(1, reimbList.size());
        found0 = reimbList.get(0);

        assertEquals(rr0.getStatus(), found0.getStatus());
        assertEquals(rr0.getType(), found0.getType());
        assertEquals(rr0.getAuthorID(), found0.getAuthorID());

        reimbList = rrdao.getReimbursementRequests(-1, SearchType.RESOLVED);
        assertNotNull(reimbList);
        assertEquals(1, reimbList.size());
        found1 = reimbList.get(0);

        assertEquals(rr1.getStatus(), found1.getStatus());
        assertEquals(rr1.getType(), found1.getType());
        assertEquals(rr1.getAuthorID(), found1.getAuthorID());

        // by both
        reimbList 
                = rrdao.getReimbursementRequests(rr0.getAuthorID(), SearchType.RESOLVED);
        assertNotNull(reimbList);
        assertTrue(reimbList.isEmpty());

        reimbList 
                = rrdao.getReimbursementRequests(rr0.getAuthorID(), SearchType.PENDING);
        assertNotNull(reimbList);
        assertEquals(1, reimbList.size());
        found0 = reimbList.get(0);

        assertEquals(rr0.getStatus(), found0.getStatus());
        assertEquals(rr0.getType(), found0.getType());
        assertEquals(rr0.getAuthorID(), found0.getAuthorID());

        reimbList 
                = rrdao.getReimbursementRequests(rr1.getAuthorID(), SearchType.RESOLVED);
        assertNotNull(reimbList);
        assertEquals(1, reimbList.size());
        found1 = reimbList.get(0);

        assertEquals(rr1.getStatus(), found1.getStatus());
        assertEquals(rr1.getType(), found1.getType());
        assertEquals(rr1.getAuthorID(), found1.getAuthorID());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSaveReimbursementRequest() throws DAOException{

        // make a user to be the author
        UserProfile up = new UserProfile();
        up.setUsername("up");
        up.setRole(UserRole.EMPLOYEE);

        Session session = HibernateConnectionUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.save(up);
        tx.commit();
        session.evict(up);
        session.close();

        // now make a request
        ReimbursementRequest reimb = new ReimbursementRequest();
        reimb.setAuthor(up);
        reimb.setStatus(ReimbursementStatus.PENDING);
        reimb.setType(ReimbursementType.FOOD);

        int reimbID = rrdao.saveReimbursementRequest(reimb);
        reimb.setID(reimbID);

        // see if it was added
        session = HibernateConnectionUtil.getSession();
        ReimbursementRequest found 
                = (ReimbursementRequest) session.get(ReimbursementRequest.class, reimbID);
        session.evict(found);
        session.close();

        assertNotNull(found);
        assertEquals(reimb.getID(), found.getID());
        assertEquals(reimb.getStatus(), found.getStatus());
        assertEquals(reimb.getType(), found.getType());

        // test over-writing
        reimb.setStatus(ReimbursementStatus.APPROVED);
        rrdao.saveReimbursementRequest(reimb);

        session = HibernateConnectionUtil.getSession();
        found = (ReimbursementRequest) session.get(ReimbursementRequest.class, reimbID);
        session.evict(found);
        session.close();

        assertNotNull(found);
        assertEquals(reimb.getID(), found.getID());
        assertEquals(reimb.getStatus(), found.getStatus());
        assertEquals(reimb.getType(), found.getType());

        // test adding a 2nd one // ? necessary?
        ReimbursementRequest secondReimb = new ReimbursementRequest();
        secondReimb.setAuthor(up);
        secondReimb.setStatus(ReimbursementStatus.DENIED);
        secondReimb.setType(ReimbursementType.TRAVEL);

        reimbID = rrdao.saveReimbursementRequest(secondReimb);

        session = HibernateConnectionUtil.getSession();
        found = (ReimbursementRequest) session.get(ReimbursementRequest.class, reimbID);
        session.evict(found);
        session.close();

        assertNotNull(found);
        assertEquals(secondReimb.getID(), found.getID());
        assertEquals(secondReimb.getStatus(), found.getStatus());
        assertEquals(secondReimb.getType(), found.getType());

        session = HibernateConnectionUtil.getSession();
        Criteria crit = session.createCriteria(ReimbursementRequest.class);
        List<ReimbursementRequest> reimbList = crit.list();
        session.close();
        assertEquals(2, reimbList.size());
    }

    /**
     * Tests both positive and negative cases
     * 
     * @throws DAOException
     */
    @Test
    public void testCheckExists() throws DAOException {

        // look for a reimb that doesn't exist
        assertFalse(rrdao.checkExists(12345));

        // add a reimb and look for it
        // make a user to be the author
        UserProfile up = new UserProfile();
        up.setUsername("up");
        up.setRole(UserRole.EMPLOYEE);

        Session session = HibernateConnectionUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.save(up);
        tx.commit();
        session.evict(up);
        session.close();

        // now make a request
        ReimbursementRequest reimb = new ReimbursementRequest();
        reimb.setAuthor(up);
        reimb.setStatus(ReimbursementStatus.PENDING);
        reimb.setType(ReimbursementType.FOOD);

        session = HibernateConnectionUtil.getSession();
        tx = session.beginTransaction();
        session.save(reimb);
        tx.commit();
        session.evict(reimb);
        session.close();

        assertTrue(rrdao.checkExists(reimb.getID()));
    }

    @Test
    public void testGetReimbursementRequest() throws DAOException {

        // get one that's not there
        ReimbursementRequest reimb = rrdao.getReimbursementRequest(12345);
        assertNull(reimb);

        // put one in there so we can get it
        // make a user to be the author
        UserProfile up = new UserProfile();
        up.setUsername("up");
        up.setRole(UserRole.EMPLOYEE);

        Session session = HibernateConnectionUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.save(up);
        tx.commit();
        session.evict(up);
        session.close();

        // now make a request
        reimb = new ReimbursementRequest();
        reimb.setAuthor(up);
        reimb.setStatus(ReimbursementStatus.PENDING);
        reimb.setType(ReimbursementType.FOOD);

        session = HibernateConnectionUtil.getSession();
        tx = session.beginTransaction();
        session.save(reimb);
        tx.commit();
        session.evict(reimb);
        session.close();

        // get it
        ReimbursementRequest found = rrdao.getReimbursementRequest(reimb.getID());
        assertNotNull(found);
        assertEquals(reimb.getID(), found.getID());
        assertEquals(reimb.getAuthorID(), found.getAuthorID());
        assertEquals(reimb.getStatus(), found.getStatus());
        assertEquals(reimb.getType(), found.getType());
    }
}
