/**
 * This servlet displays result messages (problem or success) to the user and then directs
 * them to the appropriate page.
 * 
 * I didn't call it an error page to distinguish between Http errors and things like
 * invalid user IDs.
 * 
 * @author Andrew Curry
 */
package com.revature.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ResultServlet extends ERSServlet {

    private static final long serialVersionUID = 0L;

    public ResultServlet() {
        super();
    }

    /**
     * Display the message and the okay button.
     * 
     * @param request
     * @param response
     * @throws ServletException, IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // get the parameters/attributes from the page that sent the user here
        HttpSession session = request.getSession();
        String message = (String)session.getAttribute("resultMessage");
        if (message == null) message = "";
        String destination = (String)session.getAttribute("resultDestination");
        if (destination == null) destination = "menu";

        String html = readTextFile("result.html");

        html = html.replace("%MESSAGE%", message);
        html = html.replace("%DEST%", destination);

        response.getWriter().write(html);
    }
}
