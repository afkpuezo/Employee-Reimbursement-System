/**
 * This class manages the front end's reference to the back end through a
 * singleton pattern.
 * 
 * @author Andrew Curry
 */
package com.revature.service;

import com.revature.repository.DAO.impl.ReimbursementRequestDAOImpl;
import com.revature.repository.DAO.impl.UserProfileDAOImpl;
import com.revature.repository.DAO.interfaces.ReimbursementRequestDAO;
import com.revature.repository.DAO.interfaces.UserProfileDAO;
import com.revature.service.handlers.AuthRequestHandler;
import com.revature.service.handlers.ModifyRequestHandler;
import com.revature.service.handlers.ViewRequestHandler;

public class BackEndUtil {
    
    // class / static variables
    private static boolean instantiated;
    private static ServiceFront instance;

    public static ServiceFront getBackEnd(){
        
        if (!instantiated){
            // i'm pretty sure it's fine if all of these handlers use the same DAO objects
            UserProfileDAO updao = new UserProfileDAOImpl();
            ReimbursementRequestDAO rrdao = new ReimbursementRequestDAOImpl();
            AuthRequestHandler arh = new AuthRequestHandler(updao, rrdao);
            ViewRequestHandler vrh = new ViewRequestHandler(updao, rrdao);
            ModifyRequestHandler mrh = new ModifyRequestHandler(updao, rrdao);
            instance = new ServiceFront(arh, vrh, mrh);
            instantiated = true;
        }
        return instance;
    }
}
