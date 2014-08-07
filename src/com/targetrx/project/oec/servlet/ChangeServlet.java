package com.targetrx.project.oec.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.targetrx.project.oec.bo.Change;
import com.targetrx.project.oec.service.CodeBookDao;
import com.targetrx.project.oec.service.CodeBookDaoImpl;

/**
 *
 * @author pkukk
 * @version
 */
public class ChangeServlet extends HttpServlet
{
    private CodeBookDao codeBookDao = null;
    /**
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        super.init();
        ServletContext servletContext = getServletContext();
        WebApplicationContext webApplicationContext = 
            WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        codeBookDao = (CodeBookDaoImpl)webApplicationContext.getBean("codebook");
    }

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String caller = "";
        caller = (String) request.getParameter("caller");
        response.setContentType("application/vnd.ms-excel");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();

        // Create a header style
        HSSFCellStyle styleHeader = wb.createCellStyle();
        HSSFFont fontHeader = wb.createFont();
        fontHeader.setBoldweight((short) 2);
        fontHeader.setFontHeightInPoints((short) 12);
        fontHeader.setFontName("Arial");
        styleHeader.setFont(fontHeader);

        HSSFRow rowHeader = sheet.createRow(0);
        String[] hdr_array = {"CodeBook Name","Code Num","Code Label","Action","Market"};
        String cbid = (String) request.getParameter("codebookid");
        String mValue = (String) request.getParameter("month");
        String yValue = (String) request.getParameter("year");
        
        
        List cbv = codeBookDao.viewChanges(cbid,mValue,yValue);
          
        for (int i = 0; i < 5; i++) 
        {
            HSSFCell cell = rowHeader.createCell((short)(i));
            cell.setCellStyle(styleHeader);
            cell.setCellValue(hdr_array[i]);
        }            
        for (int i = 0; i < cbv.size(); i++)
        {
            HSSFRow row = sheet.createRow(i+1);
            //CodeBookView cv = (CodeBookView) cbv.get(i);
            Change ch = (Change) cbv.get(i);
            row.createCell((short)0).setCellValue(ch.getCodebookLabel());
            row.createCell((short)1).setCellValue(ch.getCodeId());
            row.createCell((short)2).setCellValue(ch.getCodeLabel());
            row.createCell((short)3).setCellValue(ch.getHistoryAction());
            row.createCell((short)4).setCellValue(ch.getMarket());
            
        }
        // WRITE THE OUTPUT 
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();            
        
    }
    
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo()
    {
        return "Short description";
    }
}
