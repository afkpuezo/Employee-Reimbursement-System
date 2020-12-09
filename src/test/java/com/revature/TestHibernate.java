/**
 * This class tests to make sure that I've set up hibernate properly.
 */
package com.revature;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.revature.repository.Util.HibernateConnectionUtil;

import org.hibernate.Session;
import org.junit.Test;

public class TestHibernate {
    
    @Test
    public void testConnection(){
        Session session = HibernateConnectionUtil.getSession();
        assertTrue(session.isConnected());
        HibernateConnectionUtil.enterTestMode();
        Session testSession = HibernateConnectionUtil.getSession();
        assertTrue(testSession.isConnected());
        assertNotEquals(session.getSessionFactory(), testSession.getSessionFactory());

        HibernateConnectionUtil.forceDropSessionFactory();
        HibernateConnectionUtil.exitTestMode();
        Session thirdSession = HibernateConnectionUtil.getSession();
        assertTrue(thirdSession.isConnected());

        session.close();
        testSession.close();
    }
}
