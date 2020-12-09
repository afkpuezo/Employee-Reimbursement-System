/**
 * This class handles hashing and checking passwords.
 * 
 */

package com.revature.service;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     * Returns a hashed, secure version of the plaintext password.
     * 
     * @param password
     * @return
     */
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Retrurns true if the password matches.
     * 
     * @param plain
     * @param secure
     * @return
     */
    public static boolean checkPassword(String plain, String secure){
        return BCrypt.checkpw(plain, secure);
    }
}
