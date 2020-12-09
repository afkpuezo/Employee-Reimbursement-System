/**
 * Thrown by a DAO object/method when there is a problem communicating
 * with the database.
 */
package com.revature.repository.DAO.exceptions;

public class DAOException extends Exception {
    
    static final long serialVersionUID = 0L;

    public DAOException(String message){
        super(message);
    }
}
