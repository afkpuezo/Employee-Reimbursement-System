/**
 * This class is a (very clumsy) solution for filling the database with a starting set of 
 * data for the purpose of demonstration.
 * 
 * Also, by the specification, there is no way to register user accounts through the 
 * website, so I suppose this is the only way of creating new users...
 * 
 * This will never be called in ordinary operation, only manually triggered by me.
 * Hibernate has a built-in way to accomplish this sort of thing, but I find this easier
 * for now.
 * 
 * It has a failsafe where it will not add anything if it detects that the databse is
 * already populated.
 */
package com.revature;

import com.revature.model.ReimbursementRequest;
import com.revature.model.UserPassword;
import com.revature.model.UserProfile;
import com.revature.model.ReimbursementRequest.ReimbursementStatus;
import com.revature.model.ReimbursementRequest.ReimbursementType;
import com.revature.model.UserProfile.UserRole;
import com.revature.repository.DAO.exceptions.DAOException;
import com.revature.repository.DAO.impl.UserProfileDAOImpl;
import com.revature.repository.DAO.interfaces.UserProfileDAO;
import com.revature.repository.Util.HibernateConnectionUtil;
import com.revature.service.PasswordUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class DataSetup {
   
    public static void main (String[] args) throws DAOException{

        // make the DAOs - skip the service layer
        UserProfileDAO updao = new UserProfileDAOImpl();
        //ReimbursementRequestDAO rrdao = new ReimbursementRequestDAOImpl();

        // check to make sure it's okay to proceed
        if (updao.checkExists(1)) return;

        // prepare hibernate vars/references
        Session session = HibernateConnectionUtil.getSession();
        Transaction tx;

        // create and save the actual objects/data

        // the first manager account
        UserProfile man0 = new UserProfile();
        man0.setID(-1);
        man0.setRole(UserRole.MANAGER);
        man0.setUsername("admin");
        man0.setFirstName("Adam");
        man0.setLastName("Strator");
        man0.setEmailAddress("admin@ers.com");
        tx = session.beginTransaction();
        session.save(man0);
        tx.commit();
        session.evict(man0);

        UserPassword man0Pass = new UserPassword();
        man0Pass.setUser(man0);
        man0Pass.setPass(PasswordUtil.hash("admin"));
        tx = session.beginTransaction();
        session.save(man0Pass);
        tx.commit();
        session.evict(man0Pass);

        // the second manager account
        UserProfile man1 = new UserProfile();
        man1.setID(-1);
        man1.setRole(UserRole.MANAGER);
        man1.setUsername("manager");
        man1.setFirstName("Mandy");
        man1.setLastName("Baus");
        man1.setEmailAddress("mandy@ers.com");
        tx = session.beginTransaction();
        session.save(man1);
        tx.commit();
        session.evict(man1);

        UserPassword man1Pass = new UserPassword();
        man1Pass.setUser(man1);
        man1Pass.setPass(PasswordUtil.hash("manager"));
        tx = session.beginTransaction();
        session.save(man1Pass);
        tx.commit();
        session.evict(man1Pass);

        // the first employee acount
        UserProfile emp0 = new UserProfile();
        emp0.setID(-1);
        emp0.setRole(UserRole.EMPLOYEE);
        emp0.setUsername("user");
        emp0.setFirstName("Eustace");
        emp0.setLastName("Guy");
        emp0.setEmailAddress("eustace@ers.com");
        tx = session.beginTransaction();
        session.save(emp0);
        tx.commit();
        session.evict(emp0);

        UserPassword emp0Pass = new UserPassword();
        emp0Pass.setUser(emp0);
        emp0Pass.setPass(PasswordUtil.hash("password"));
        tx = session.beginTransaction();
        session.save(emp0Pass);
        tx.commit();
        session.evict(emp0Pass);

        // the second employee acount
        UserProfile emp1 = new UserProfile();
        emp1.setID(-1);
        emp1.setRole(UserRole.EMPLOYEE);
        emp1.setUsername("employee");
        emp1.setFirstName("Edward");
        emp1.setLastName("Mann");
        emp1.setEmailAddress("edward@ers.com");
        tx = session.beginTransaction();
        session.save(emp1);
        tx.commit();
        session.evict(emp1);

        UserPassword emp1Pass = new UserPassword();
        emp1Pass.setUser(emp1);
        emp1Pass.setPass(PasswordUtil.hash("password"));
        tx = session.beginTransaction();
        session.save(emp1Pass);
        tx.commit();
        session.evict(emp1Pass);

        // now some reimb-reqs

        // for the first employee
        ReimbursementRequest rr0 = new ReimbursementRequest();
        rr0.setID(-1);
        rr0.setAuthor(emp0);
        rr0.setMoneyAmount(123456L);
        rr0.setType(ReimbursementType.FOOD);
        rr0.setStatus(ReimbursementStatus.PENDING);
        rr0.setDescription("A delicious cheese burger");
        rr0.setTimeSubmitted(java.time.LocalDateTime.now().toString());
        tx = session.beginTransaction();
        session.save(rr0);
        tx.commit();
        session.evict(rr0);

        ReimbursementRequest rr1 = new ReimbursementRequest();
        rr1.setID(-1);
        rr1.setAuthor(emp0);
        rr1.setMoneyAmount(654321L);
        rr1.setType(ReimbursementType.LODGING);
        rr1.setStatus(ReimbursementStatus.APPROVED);
        rr1.setDescription("Hotel while on-site for client");
        rr1.setTimeSubmitted(java.time.LocalDateTime.now().toString());
        rr1.setResolverID(man1.getID());
        rr1.setTimeResolved(java.time.LocalDateTime.now().toString());
        tx = session.beginTransaction();
        session.save(rr1);
        tx.commit();
        session.evict(rr1);

        // for the second employee
        ReimbursementRequest rr2 = new ReimbursementRequest();
        rr2.setID(-1);
        rr2.setAuthor(emp1);
        rr2.setMoneyAmount(987654L);
        rr2.setType(ReimbursementType.TRAVEL);
        rr2.setStatus(ReimbursementStatus.PENDING);
        rr2.setDescription("Plane tickets");
        rr2.setTimeSubmitted(java.time.LocalDateTime.now().toString());
        tx = session.beginTransaction();
        session.save(rr2);
        tx.commit();
        session.evict(rr2);

        ReimbursementRequest rr3 = new ReimbursementRequest();
        rr3.setID(-1);
        rr3.setAuthor(emp1);
        rr3.setMoneyAmount(1500L);
        rr3.setType(ReimbursementType.OTHER);
        rr3.setStatus(ReimbursementStatus.DENIED);
        rr3.setDescription("Subcribed to WOW");
        rr3.setTimeSubmitted(java.time.LocalDateTime.now().toString());
        rr3.setResolverID(man0.getID());
        rr3.setTimeResolved(java.time.LocalDateTime.now().toString());
        tx = session.beginTransaction();
        session.save(rr3);
        tx.commit();
        session.evict(rr3);

        // finally, end
        session.close();
    }
}
