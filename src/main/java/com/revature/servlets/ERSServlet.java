/**
 * This class contains utilty methods shared by servlets in this project.
 */
package com.revature.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.revature.model.ReimbursementRequest;
import com.revature.model.UserProfile;
import com.revature.model.UserProfile.UserRole;
import com.revature.service.BackEndUtil;
import com.revature.service.ServiceFront;
import com.revature.service.comms.ERSRequest;
import com.revature.service.comms.ERSResponse;
import com.revature.service.comms.ERSResponse.ERSResponseType;
import com.revature.service.comms.ERSRequest.ERSRequestType;

  
public abstract class ERSServlet extends HttpServlet{

    private static final long serialVersionUID = 0L;

    // constants
    protected static final String INVALID_USERNAME_MESSAGE
            = "Invalid Username: Must have at least 1 character and no spaces.";
    protected static final String INVALID_PASSWORD_MESSAGE
            = "Invalid Password: Must have at least 1 character and no spaces.";
    
    // methods

    /**
     * Returns a string containing the html text at the start of every web page (styling, 
     * etc)
     * Currently returns empty string.
     * 
     * @return
     */
    protected String getGenericHead(){
        return "<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"><title>Insert title here</title></head><body>";
    }

    /**
     * Returns a string containing the html text at the end of every web page
     * Currently returns empty string.
     * 
     * @return
     */
    protected String getGenericFoot(){
        return "</body></html>";
    }

    /**
     * Determines what kind of user is logged in to the request's session.
     * If the request's session does not have a user, or there is no session, defaults
     * to LOGGED_OUT.
     * 
     * @param request
     * @return
     */
    protected UserRole getCurrentUserRole(HttpServletRequest request){

        return getCurrentUserRole(request.getSession());
    }

    /**
     * Determines what kind of user is logged in to the current session.
     * If the request's session does not have a user, or there is no session, defaults
     * to LOGGED_OUT.
     * 
     * @param request
     * @return
     */
    protected UserRole getCurrentUserRole(HttpSession session){

        if (session != null && session.getAttribute("role") != null) {
			return (UserRole) session.getAttribute("role");
		} else {
            return UserRole.LOGGED_OUT;
        }
    }

    /**
     * Redirects the given response to the correct landing page when they try to access a
     * page they don't have permission to see (eg, an employee trying to view a manager
     * action)
     * 
     * @param response
     */
    protected void redirectToMenu(HttpServletResponse response) throws IOException{
        response.sendRedirect("menu");
    }

    /** 
     * Determines if the given username is in the valid format.
     * Currently, that just means no spaces and at least 1 letter.
     * 
     * @param username
     * @return
    */
    protected boolean isUsernameValid(String username){
        
        if (username.length() < 1) return false;

        for (char c : username.toCharArray()){
            if (c == ' ') return false;
        }
        return true;
    }

    /** 
     * Determines if the given password is in the valid format.
     * Currently, that just means no spaces and at least 1 letter.
     * 
     * @param username
     * @return
    */
    protected boolean isPasswordValid(String password){
        
        return isUsernameValid(password); // same requirements
    }

    /**
     * Determines if the given personal name is in the valid format.
     * Currently, that just means no spaces and at least 1 letter.
     * 
     * @param name
     * @return
     */
    protected boolean isPersonalNameValid(String name){

        return isUsernameValid(name); // same requirements
    }

    /**
     * Determines if the given email address is in the valid format.
     * Currently, very simple - just determines that there is some text, followed by an @,
     * followed by some text, followed by a dot, followed by some more text.
     * Also, only one @ allowed, but multiple dots.
     * Also, no spaces.
     * Also, dots must not be adjacted to another dot or the @.
     * 
     * (At some point, it's going to become worthwhile to learn regex)
     * 
     * @param email
     * @return
     */
    protected boolean isEmailAddressValid(String email){
        
        boolean foundText = false; // this one is re-used a few times
        boolean foundAtSymbol = false;
        boolean foundDotAfterAt = false;

        for (char c : email.toCharArray()){

            if (c == ' ') return false;
            if (c == '@'){
                if (foundAtSymbol) return false; // only one allowed
                if (!foundText) return false;
                // a permitted @
                foundText = false;
                foundAtSymbol = true;
            } // end if @
            else if (c == '.'){
                if (!foundText) return false;
                if (foundAtSymbol){
                    foundText = false;
                    foundDotAfterAt = true;
                }
                foundText = false;
            } // end if .
            else foundText = true;
        } // end for loop

        if (!foundAtSymbol || !foundDotAfterAt) return false;

        return true; // haven't found any problems yet
    }

    /**
     * Returns an ERSRequest with the given type and the info for the user currently
     * logged in to the given session.
     * 
     * @param type
     * @param session
     * @return
     */
    protected ERSRequest makeERSRequest(ERSRequestType type, HttpSession session){

        Integer userIDInteger = (Integer)session.getAttribute("userID");
        int userID;
        if (userIDInteger == null) userID = -1;
        else userID = userIDInteger;

        return new ERSRequest(type, userID, getCurrentUserRole(session));
    }

    /**
     * Returns an ERSRequest with the given type and the info for the user currently
     * logged in to the given session.
     * 
     * @param type
     * @param request
     * @return
     */
    protected ERSRequest makeERSRequest(ERSRequestType type, HttpServletRequest request){

        return makeERSRequest(type, request.getSession());
    }

    /**
     * Hands the given request to the service layer, and returns the response.
     * 
     * @param req
     * @returns
     */
    protected ERSResponse getResponse(ERSRequest req){
        ServiceFront sf = BackEndUtil.getBackEnd();
        return sf.handleERSRequest(req);
    }

    /**
     * Redirects the given response to the problem page, with the given message and
     * destination.
     * 
     * @param response
     * @param session
     * @param message
     * @param destination
     * @returns
     */
    protected void handleProblem (
            HttpServletResponse response,
            HttpSession session, 
            String message, 
            String destination) throws IOException {
        
        session.setAttribute("resultMessage", message);
        session.setAttribute("resultDestination", destination);
        response.sendRedirect("problem");
    }

    /**
     * Redirects the given response to the problem page, with the given message and
     * destination.
     * 
     * @param response
     * @param session
     * @param message
     * @param destination
     * @returns
     */
    protected void handleProblem (
            HttpServletResponse response,
            HttpServletRequest request, 
            String message, 
            String destination) throws IOException {
        
        handleProblem(response, request.getSession(), message, destination);
    }

    /**
     * Redirects the given response to the success page, with the given message and
     * destination.
     * 
     * @param response
     * @param session
     * @param message
     * @param destination
     * @throws IOException
     */
    protected void handleSuccess (
            HttpServletResponse response,
            HttpSession session, 
            String message, 
            String destination) throws IOException {
    
        session.setAttribute("resultMessage", message);
        session.setAttribute("resultDestination", destination);
        response.sendRedirect("success");
    }

    /**
     * Redirects the given response to the success page, with the given message and
     * destination.
     * 
     * @param response
     * @param session
     * @param message
     * @param destination
     * @throws IOException
     */
    protected void handleSuccess (
            HttpServletResponse response,
            HttpServletRequest request, 
            String message, 
            String destination) throws IOException {
    
        handleSuccess(response, request.getSession(), message, destination);
    }

    /**
     * Determines if the given ERSResponse indicates that the attempted action failed.
     * 
     * @param eres
     * @return
     */
    protected boolean isFailure(ERSResponse eres){
        return (eres.getType() != ERSResponseType.SUCCESS);
    }

    /**
     * Returns the entire contents of the given text file as a single, unedited String.
     * This is so that I can have tempalte html files that servlets modify based on data.
     * 
     * Based on this guide I found: 
     *      https://kodejava.org/how-do-i-read-text-file-in-servlet/
     * 
     * @param filename
     * @return
     */
    protected String readTextFile(String filename) throws IOException {
        
        ServletContext context = getServletContext();
        InputStream is = context.getResourceAsStream(filename);
        String text = "";
        if (is != null) {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            String line;

            // We read the file line by line and later will be displayed on the
            // browser page.
            while ((line = reader.readLine()) != null) {
                text = text + line;
            }
        }
        is.close();
        return text;
    }

    /**
     * Creates an html table based on the given list of reimb-reqs.
     * If the list is empty, returns a text string indicating there were no matching
     * results.
     * 
     * @param reimbs
     * @return
     */
    protected String makeTableFromReimbursementRequests(List<ReimbursementRequest> reimbs) {
        
        if (reimbs.isEmpty()) return "No matching reimbursement requests were found.";

        // start with opening the table and labeling the columns
        // ID, Author ID, Type, $Amount, Status, Desc., Time Sub., Resolver ID, Time R.
        String html = "<table><tr><th>ID</th><th>Author ID</th><th>Type</th>"
                + "<th>Money Amount</th><th>Status</th><th>Description</th>" 
                + "<th>Time Submitted</th><th>Resolver ID</th><th>Time Resolved</th>"
                + "</tr>";

        for (ReimbursementRequest rr : reimbs){
            String row = "<tr>";
            row = row + "<td>" + cleanUpID(rr.getID()) + "</td>";
            row = row + "<td>" + cleanUpID(rr.getAuthorID()) + "</td>";
            row = row + "<td>" + cleanUpEnum("" + rr.getType()) + "</td>";
            row = row + "<td>" + longToMoneyString(rr.getMoneyAmount()) + "</td>";
            row = row + "<td>" + cleanUpEnum("" + rr.getStatus()) + "</td>";
            row = row + "<td>" + cleanUpString(rr.getDescription()) + "</td>";
            row = row + "<td>" + cleanUpString(rr.getTimeSubmitted()) + "</td>";
            row = row + "<td>" + cleanUpID(rr.getResolverID()) + "</td>";
            row = row + "<td>" + cleanUpString(rr.getTimeResolved()) + "</td>";
            html = html + row + "</tr>";
        }
        html = html + "</table>";

        return html;
    }

    /**
     * Returns a more readable, user friendly version of an enum
     * EG, EXAMPLE_ENUM -> 'Example enum'
     * 
     * @return
     */
    private String cleanUpEnum(String enm){
        
        if (enm == null || enm.equals("")) return "";

        char[] output = new char[enm.length()];
        output[0] = enm.charAt(0);
        for (int i = 1; i < enm.length(); i++){

            char c = enm.charAt(i);
            char letter;
            if (c == '_') letter = ' ';
            else letter = Character.toLowerCase(c);

            output[i] = letter;
        } // end for loop

        return new String(output);
    }

    /**
     * If an ID is < 1 (that is, invalid), return "--". Otherwise, "#<ID>", eg "#1"
     * 
     * @param ID
     * @return
     */
    private String cleanUpID(int ID){

        if (ID < 1) return "--";
        else return "#" + ID;
    }

    /**
     * Used to 'clean up' / format data strings such as Time Resolved, Description, etc.
     * If null or empty, returns "--"
     * 
     * @param s
     * @return
     */
    private String cleanUpString(String s){

        if (s == null || s.equals("")) return "--";
        else return s;
    }

    /**
     * 123456L -> $1234.56
     * Assumes money is at least 0.
     * 
     * @param money
     * @return
     */
    private String longToMoneyString(long money){

        if (money == 0L) return "$0.00";
        if (money < 100) return "$0." + money;

        String bare = "" + money;
        int dotPos = bare.length() - 2;
        String result = "$" + bare.substring(0, dotPos) + "." + bare.substring(dotPos);
        return result;
    }

    /**
     * Determines if the given money string is in a valid format.
     * May or may not have $ at the start.
     * May or may not have a decimal before exactly 2 numbers.
     * Must be positive (no negative sign -)
     * All other characters MUST be numbers.
     * 
     * (if I knew regex this would be easier)
     * 
     * @param moneyString
     * @return
     */
    protected boolean isMoneyStringValid(String moneyString){

        if (moneyString == null || moneyString.length() == 0) return false;

        int startIndex = 0;
        if (moneyString.charAt(0) == '$') startIndex = 1;
        boolean dotFound = false;
        int dotFoundAt = -1;

        for (int i = startIndex; i < moneyString.length(); i++){

            char c = moneyString.charAt(i);
            if (c == '.'){
                if (dotFound) return false; // only one dot allowed
                else{
                    dotFound = true;
                    dotFoundAt = i;
                }
            } // end if .
            else{
                if (!Character.isDigit(c)){
                    return false;
                }
                else{ // if a digit
                    if (dotFound && (i - dotFoundAt) > 2) 
                        return false; // too far after dot
                }
            }
        } // end for loop

        return true; // didn't find any problems
    }

    /**
     * Convers the given money String into a bare string that can later be converted to
     * a long. EG "$1234.56" -> "123456"
     * Assumes the string is properly formatted. (see isMoneyStringValid() )
     * 
     * Might have been better to do validation and parsing in the same method but this is
     * better for my workflow
     * 
     * @param moneyString
     * @return
     */
    protected String moneyStringToBareString(String moneyString){

        int startIndex = 0;
        if (moneyString.charAt(0) == '$') startIndex = 1;
        boolean dotFound = false;
        String bare = "";

        for (int i = startIndex; i < moneyString.length(); i++){

            char c = moneyString.charAt(i);

            if (c == '.') dotFound = true;
            else{ // assume number
                bare = bare + c;
            }
        } // end for loop

        if (!dotFound) bare = bare + "00"; // $5 is the same as $5.00

        return bare;
    }

    /**
     * Creates an html table based on the given list of user profiles.
     * If the list is empty, returns a text string indicating there were no matching
     * results.
     * 
     * @param users
     * @return
     */
    protected String makeTableFromUserProfiles(List<UserProfile> users) {
        
        if (users.isEmpty()) return "No matching user profiles were found.";

        // start with opening the table and labeling the columns
        // ID, username, first name, last name, email    
        String html = "<table><tr><th>ID</th><th>Username</th><th>First Name</th>"
                + "<th>Last Name</th><th>Email Address</th></tr>";

        for (UserProfile up : users){

            String row = "<tr>";
            row = row + "<td>" + cleanUpID(up.getID()) + "</td>";
            row = row + "<td>" + cleanUpString(up.getUsername()) + "</td>";
            row = row + "<td>" + cleanUpString(up.getFirstName()) + "</td>";
            row = row + "<td>" + cleanUpString(up.getLastName()) + "</td>";
            row = row + "<td>" + cleanUpString(up.getEmailAddress()) + "</td>";
            html = html + row + "</tr>";
        }
        html = html + "</table>";

        return html;
    }

    /**
     * Determines if the given string is 'blank' - null or ""
     * 
     * @param s
     * @return
     */
    protected boolean isStringBlank(String s){
        return (s == null || s.equals(""));
    }

    /**
     * Determines if the given ID string is in the valid format.
     * This must be a positive integer, optionally starting with #.
     * 
     * @param idString
     * @return
     */
    protected boolean isIDStringValid(String idString){

        int startIndex = 0;
        if (idString.charAt(0) == '#') startIndex = 1;

        for (int i = startIndex; i < idString.length(); i++){
            if (!Character.isDigit(idString.charAt(i))) return false;
        }

        return true; // no problems found
    }

    /**
     * If there is a # at the start of the string, remove it.
     * 
     * @param idString
     * @return
     */
    protected String cleanIDString(String idString){

        int startIndex = 0;
        if (idString.charAt(0) == '#') startIndex = 1;
        return idString.substring(startIndex);
    }
}
