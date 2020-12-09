/**
 * This interface describes the public-facing methods needed for retrieving users from,
 * and storing them in, the database
 */
package com.revature.repository.DAO.interfaces;

import java.util.List;

import com.revature.model.UserProfile;
import com.revature.repository.DAO.exceptions.DAOException;

public interface UserProfileDAO {
    
    /**
     * Determines if the indicated user exists.
     * Throws exception if there is a database communication problem.
     * @param userID
     * @return
     * @throws DAOException
     */
    public boolean checkExists(int userID) throws DAOException;

    /**
     * Determines if the indicated user exists.
     * Throws exception if there is a database communication problem.
     */
    public boolean checkExists(String username) throws DAOException;

    /**
     * Determines if the indicated user exists.
     * Throws exception if there is a database communication problem.
     */
    public boolean checkExistsEmail(String emailAddress) throws DAOException;

    /**
     * Should return the safe, encrypted version of the password.
     * Throws exception if there is a database communication problem.
     * Throws exception if the user is not found.
     */
    public String getPassword(int userID) throws DAOException;
    
    /**
     * Should return the safe, encrypted version of the password.
     * Throws exception if there is a database communication problem.
     * Throws exception if the user is not found.
     */
    public String getPassword(String username) throws DAOException;

    /**
     * Returns a filled-out UserProfile object.
     * Throws exception if there is a database communication problem.
     * Throws exception if the user is not found.
     * @param userID
     * @return
     * @throws DAOException
     */
    public UserProfile getUserProfile(int userID) throws DAOException;

    /**
     * Returns a filled-out UserProfile object. 
     * Throws exception if there is a database communication problem. 
     * Throws exception if the user is not found.
     * 
     * @param username
     * @return
     * @throws DAOException
     */
    public UserProfile getUserProfile(String username) throws DAOException;

    /**
     * Returns a list of all employee profiles in the system.
     * Returns an empty list if there are no employees.
     * Throws exception if there is a database communication problem. 
     * Throws exception if the user is not found.
     * @return
     * @throws DAOException
     */
    public List<UserProfile> getAllEmployeeProfiles() throws DAOException;

    /**
     * Saves/writes the given UserProfile to the database.
     * If a matching user DOES exist, ID and Role will not be updated,
     * but all other properties will be.
     * If saving a new UserProfile, should use ID = -1. The system will automatically
     * generate a new ID and return it.
     * Throws exception if there is a database communication problem. 
     * 
     * @param up : profile to save
     * 
     * @return : The ID of the profile that was saved. If given a up with an ID, the given
     *      ID will be returned. If given a up with ID = -1, a new ID will be generated
     *      and returned.
     * 
     * @throws DAOException
     */
    public int saveUserProfile(UserProfile up) throws DAOException;
}
