/**
 * This class contains unit tests for the UserProfileDAOImpl class.
 */
package com.revature.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hibernate.Transaction;

import com.revature.model.UserPassword;
import com.revature.model.UserProfile;
import com.revature.model.UserProfile.UserRole;
import com.revature.repository.DAO.exceptions.DAOException;
import com.revature.repository.DAO.impl.UserProfileDAOImpl;
import com.revature.repository.DAO.interfaces.UserProfileDAO;
import com.revature.repository.Util.HibernateConnectionUtil;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestUPDAOImpl {
    
    // class / static vars
    private UserProfileDAO updao;

    @Before
    public void setup(){
        HibernateConnectionUtil.enterTestMode();
        updao = new UserProfileDAOImpl();
    }

    @After
    public void cleanup(){
        HibernateConnectionUtil.exitTestMode();
        HibernateConnectionUtil.forceDropSessionFactory(); // unnecessary?
    }

    @Test
    public void testCheckExistsByID() throws DAOException{

        int userID = 999;
        assertFalse(updao.checkExists(userID));
        // now put one in there
        Session session = HibernateConnectionUtil.getSession();
        UserProfile up = new UserProfile(userID, UserRole.EMPLOYEE);
        Transaction tx = session.beginTransaction();
        session.save(up);
        tx.commit();
        userID = up.getID();
        session.evict(up);

        session.close();
        assertTrue(updao.checkExists(userID));
    }

    @Test
    public void testCheckExistsByUsername() throws DAOException{

        String username = "not_found";
        assertFalse(updao.checkExists(username));

        Session session = HibernateConnectionUtil.getSession();
        UserProfile up = new UserProfile(-1, UserRole.EMPLOYEE);
        username = "found";
        up.setUsername(username);
        Transaction tx = session.beginTransaction();
        session.save(up);
        tx.commit();
        session.evict(up);

        session.close();
        assertTrue(updao.checkExists(username));
    }

    @Test
    public void testCheckExistsByEmail() throws DAOException{

        String emailAddress = "not_found";
        assertFalse(updao.checkExistsEmail(emailAddress));

        Session session = HibernateConnectionUtil.getSession();
        UserProfile up = new UserProfile(-1, UserRole.EMPLOYEE);
        emailAddress = "found";
        up.setEmailAddress(emailAddress);
        Transaction tx = session.beginTransaction();
        session.save(up);
        tx.commit();
        session.evict(up);

        session.close();
        assertTrue(updao.checkExistsEmail(emailAddress));
    }

    /**
     * Tests both by ID and by username
     */
    @Test
    public void testGetUserProfile() throws DAOException{

        // should be null if no user found
        UserProfile up;
        Session session;
        String username = "username";
        session = HibernateConnectionUtil.getSession();

        up = (UserProfile) updao.getUserProfile(-1);
        assertNull(up);

        boolean caught = false;
        try{
            up = (UserProfile) updao.getUserProfile(username);
        }
        catch(DAOException e){
            caught = true;
        }
        assertTrue(caught);

        // now insert a user
        UserProfile inserted = new UserProfile(-1, UserRole.EMPLOYEE);
        inserted.setUsername(username);
        Transaction tx = session.beginTransaction();
        session.save(inserted);
        tx.commit();
        int userID = inserted.getID();
        session.evict(inserted);
        session.close();

        // see if we can find it
        up = updao.getUserProfile(userID);
        assertNotNull(up);
        assertEquals(userID, up.getID());
        assertEquals(username, up.getUsername());

        up = updao.getUserProfile(username);
        assertNotNull(up);
        assertEquals(userID, up.getID());
        assertEquals(username, up.getUsername());
    }

    /**
     * This should test both by ID and username
     * 
     * @throws DAOException
     * @throws HibernateException
     */
    @Test
    public void testGetPassword() throws DAOException, HibernateException{

        // create a user with a password
        Session session = HibernateConnectionUtil.getSession();
        UserProfile up = new UserProfile(-1, UserRole.EMPLOYEE);
        String username = "username";
        up.setUsername(username);
        Transaction tx = session.beginTransaction();
        session.save(up);
        tx.commit();
        session.evict(up);
        int userID = up.getID();
        String password = "password";
        UserPassword uPass = new UserPassword();
        uPass.setUser(up);
        uPass.setPass(password);
        tx = session.beginTransaction();
        session.save(uPass);
        tx.commit();
        session.evict(uPass);
        session.close();

        // now see if we can get it
        String foundPass = updao.getPassword(userID);
        assertEquals(password, foundPass);

        foundPass = updao.getPassword(username);
        assertEquals(password, foundPass);
    }

    @Test
    public void testGetAllEmployeeProfiles() throws DAOException, HibernateException{

        // should return an empty list if there are no employees
        List<UserProfile> users = updao.getAllEmployeeProfiles();
        assertNotNull(users);
        assertTrue(users.isEmpty());

        // put some users in there
        UserProfile user0 = new UserProfile();
        user0.setUsername("user0");
        user0.setRole(UserRole.EMPLOYEE);
        UserProfile user1 = new UserProfile();
        user1.setUsername("user1");
        user1.setRole(UserRole.EMPLOYEE);

        Session session = HibernateConnectionUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.save(user0);
        session.save(user1);
        tx.commit();
        session.close();

        // should be able to find them now
        users = updao.getAllEmployeeProfiles();
        assertNotNull(users);
        assertEquals(2, users.size());
        
        // try to validate that each user is correctly stored
        boolean found0 = false;
        boolean found1 = false;
        boolean foundWrong = false;
        for (UserProfile up : users){
            if (up.getID() == user0.getID()){
                found0 = true;
            }
            else if (up.getID() == user1.getID()){
                found1 = true;
            }
            else foundWrong = true;
        }
        assertTrue(found0);
        assertTrue(found1);
        assertFalse(foundWrong);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSaveUserProfile() throws DAOException, HibernateException{

        // make a new profile
        String username = "newguy";
        UserRole role = UserRole.EMPLOYEE;
        UserProfile up = new UserProfile();
        up.setUsername(username);
        up.setRole(role);

        // insert it
        updao.saveUserProfile(up);

        // can we find it?
        Session session = HibernateConnectionUtil.getSession();
        UserProfile found = (UserProfile) session.get(UserProfile.class, up.getID());
        session.evict(found);
        session.close();

        assertNotNull(found);
        assertEquals(up.getID(), found.getID());
        assertEquals(username, found.getUsername());
        assertEquals(role, found.getRole());

        // now try to update it - should change the old entry rather than adding a new one
        String secondUsername = "oldguy";
        up.setUsername(secondUsername);
        updao.saveUserProfile(up);

        session = HibernateConnectionUtil.getSession();
        found = (UserProfile) session.get(UserProfile.class, up.getID());
        session.evict(found);
        session.close();

        assertNotNull(found);
        assertEquals(up.getID(), found.getID());
        assertEquals(secondUsername, found.getUsername());
        assertEquals(role, found.getRole());

        // make sure oinly one entry
        session = HibernateConnectionUtil.getSession();
        Criteria crit = session.createCriteria(UserProfile.class);
        List<UserProfile> userList = crit.list();
        session.close();
        assertEquals(1, userList.size());
    }
}
