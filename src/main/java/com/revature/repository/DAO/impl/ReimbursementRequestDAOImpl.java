/**
 * This class provides an implementation of DAO functionality for reimb-reqs.
 * 
 * @author Andrew Curry
 */
package com.revature.repository.DAO.impl;

import java.util.List;

import com.revature.model.ReimbursementRequest;
import com.revature.model.ReimbursementRequest.ReimbursementStatus;
import com.revature.repository.DAO.exceptions.DAOException;
import com.revature.repository.DAO.interfaces.ReimbursementRequestDAO;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.revature.repository.Util.HibernateConnectionUtil;

public class ReimbursementRequestDAOImpl implements ReimbursementRequestDAO {

    // constants

    // class/static variables

    // instance variables

    // constructor(s)

    // ---------------------------
    // methods from ReimbursementRequestDAO
    // ---------------------------

    /**
     * Returns a list of reimb-reqs matching the given constraints.
     * If no matching reimb-reqs are found, returns an empty list.
     * 
     * @param authorID : if authorID != -1, search is limited to reqs submitted by the 
     *      employee with the matching authorID. If authorID == -1, reqs from any employee
     *      will be considered.
     * @param searchBy : results are limited to reqs whose Status correpsonds to searchBy
     * @return
     * @throws DAOException
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ReimbursementRequest> getReimbursementRequests(
            int authorID,
            SearchType searchBy) throws DAOException {

        try{
            Session session = HibernateConnectionUtil.getSession();
            Criteria crit = session.createCriteria(ReimbursementRequest.class);
            if (authorID != -1)
                    crit.add(Restrictions.eq("author.ID", authorID));
            if (searchBy == SearchType.PENDING)
                    crit.add(Restrictions.eq("status", ReimbursementStatus.PENDING));
            else if (searchBy == SearchType.RESOLVED)
                    crit.add(Restrictions.or(
                            Restrictions.eq("status", ReimbursementStatus.APPROVED),
                            Restrictions.eq("status", ReimbursementStatus.DENIED)));
    
            List<ReimbursementRequest> reimbList = crit.list();
            for (ReimbursementRequest reimb : reimbList){
                session.evict(reimb); // ? is this necessary?
            }
            session.close();
            return reimbList;
        }
        catch(HibernateException e){
            throw new DAOException("HibernateException: " + e.getMessage());
        }
        
    }

    /**
     * Saves/writes the given reimb-req to the database.
     * Returns the ID of the reimb-req.
     * If given a new reimb-req (ID = -1), the system will determine a new, unique ID, and
     * return that.
     * 
     * @param reimb
     * @return ID of the reimb-req
     * @throws DAOException
     */
    @Override
    public int saveReimbursementRequest(ReimbursementRequest reimb) throws DAOException{

        try{
            Session session = HibernateConnectionUtil.getSession();
            Transaction tx = session.beginTransaction();
            if (reimb.getID() == -1) session.save(reimb);
            else session.saveOrUpdate(reimb);
            tx.commit();
            session.evict(reimb);
            session.close();
    
            return reimb.getID();
        }
        catch(HibernateException e){
            throw new DAOException("HibernateException: " + e.getMessage());
        }
    }

    /**
     * Returns true if there is a reimb-req in the database with the given id, false
     * otherwise.
     * 
     * @param reimbID
     * @return
     * @throws DAOException
     */
    @Override
    public boolean checkExists(int reimbID) throws DAOException{
        
        try{
            Session session = HibernateConnectionUtil.getSession();
            ReimbursementRequest reimb 
                    = (ReimbursementRequest)session.get(ReimbursementRequest.class, reimbID);
            session.close();
            return reimb != null;
        }
        catch(HibernateException e){
            throw new DAOException("HibernateException: " + e.getMessage());
        }
    }

    /**
     * Retrieves the reimb-req corresponding to the given ID.
     * Returns null if no matching reimb found // ? is this a good idea/consistent?
     * Throws an exception if there is a problem with the database.
     * 
     * @param reimbID
     * @return
     * @throws DAOException
     */
    @Override
    public ReimbursementRequest getReimbursementRequest(int reimbID) throws DAOException{

        try{
            Session session = HibernateConnectionUtil.getSession();
            ReimbursementRequest reimb 
                    = (ReimbursementRequest)session.get(ReimbursementRequest.class, reimbID);
            session.evict(reimb);
            session.close();
            return reimb;
        }
        catch(HibernateException e){
            throw new DAOException("HibernateException: " + e.getMessage());
        }
    }
}
