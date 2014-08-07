package com.targetrx.project.oec.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.targetrx.project.oec.bo.User;
import com.targetrx.project.oec.service.UserDao;
import com.targetrx.project.oec.service.UserDaoImpl;
import com.targetrx.project.oec.util.AuditedUser;

/**
 *
 * @author pkukk
 * @version
 */
public class OECFilter extends HttpServlet {
    private Logger log = Logger.getLogger(this.getClass());
    private UserDao userDao = null;
    
    /**
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        super.init();
        ServletContext servletContext = getServletContext();
        WebApplicationContext webApplicationContext = 
            WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        userDao = (UserDaoImpl)webApplicationContext.getBean("user");
    }

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        String dest = "/jsp/oec/index.jsp";
        HttpSession ses = request.getSession();
        User u = userDao.getUser(getCurrentUser(request));
        AuditedUser.setName(request.getUserPrincipal().getName());
        ses.setAttribute("user",u);
        RequestDispatcher rd = request.getRequestDispatcher(dest);
        log.debug("====>"+u.getRoleCode());
        rd.forward(request, response); 
    }
    
   	public static String getCurrentUser(HttpServletRequest request) 
    {
        String current_user = null;
        if (request.getUserPrincipal() != null) 
        {
            if(request.getUserPrincipal().getName() != null)
            {
                current_user = request.getUserPrincipal().getName();
            }
        }
        return current_user;
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
