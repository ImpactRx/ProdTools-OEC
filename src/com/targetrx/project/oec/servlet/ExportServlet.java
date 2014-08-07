package com.targetrx.project.oec.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.targetrx.db.trxload.service.ISQLDictionaryService;
import com.targetrx.project.oec.bo.CodeBookView;
import com.targetrx.project.oec.bo.Response;
import com.targetrx.project.oec.service.CodeBookDao;
import com.targetrx.project.oec.service.CodeBookDaoImpl;
import com.targetrx.project.oec.service.ResponseDao;
import com.targetrx.project.oec.service.ResponseDaoImpl;

/**
 *
 * @author pkukk
 * @version
 */
public class ExportServlet extends HttpServlet
{
    private Logger log = Logger.getLogger(this.getClass());
    private CodeBookDao codeBookDao = null;
    private ResponseDao responseDao = null;
    private ISQLDictionaryService dictionaryService = null;
    public static final String REPORT_TYPE_909 = "909";
    public static final String REPORT_TYPE_RANDOM = "random";
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
        responseDao = (ResponseDaoImpl)webApplicationContext.getBean("responses");
        dictionaryService = (ISQLDictionaryService)webApplicationContext.getBean("dictionaryService");
    }
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String caller = (String) request.getParameter("caller");
        if (caller == null)
        {
        	String reportType = request.getParameter("reportType");
    		PrintWriter writer = null;
    		try
    		{
        		response.setContentType("application/vnd.ms-excel");
                writer = response.getWriter();
    			if (REPORT_TYPE_909.equals(reportType))
            	{
            		String start = request.getParameter("startPeriod");
            		String end = request.getParameter("endPeriod");
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    java.sql.Date startPeriod = new java.sql.Date(formatter.parse(start).getTime());
                    java.sql.Date endPeriod = new java.sql.Date(formatter.parse(end).getTime());
            		int[] parameterTypes = {
            				Types.DATE, 
            				Types.DATE, 
            				Types.DATE, 
            				Types.DATE, 
            				Types.DATE, 
            				Types.DATE, 
            				Types.DATE, 
            				Types.DATE, 
            				Types.DATE, 
            				Types.DATE, 
            				Types.DATE, 
            				Types.DATE};
            		Object[] parameters = {
            				startPeriod, 
            				endPeriod, 
            				startPeriod, 
            				endPeriod, 
            				startPeriod, 
            				endPeriod, 
            				startPeriod, 
            				endPeriod, 
            				startPeriod, 
            				endPeriod, 
            				startPeriod, 
            				endPeriod};
                    dictionaryService.execute("FrequencyReportQ909", parameters, parameterTypes, writer);
            	} else if (REPORT_TYPE_RANDOM.equals(reportType))
            	{
        			dictionaryService.execute("RandomResponseReport", null, null, writer);
            	}    			
    		} catch (Exception e)
    		{
    			writer.println(e.getMessage());
    			log.error(e.getMessage(), e);
    		}
        } else
        {
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
            if (caller.equalsIgnoreCase("response"))
            {
                String[] resp_array = {"Survey Question","Group Question","Response","Par1","Par2","Par3","Par4","Par5"};
                String peid = (String) request.getParameter("programid");
                OutputStream out = response.getOutputStream();
                try
                {
                    List rpv = responseDao.getResponseView(peid);
                    for (int i = 0; i < 8; i++) 
                    {
                        HSSFCell cell = rowHeader.createCell((short) (i));
                        cell.setCellStyle(styleHeader);
                        cell.setCellValue(resp_array[i]);
                    }
                    for (int i = 0; i < rpv.size(); i++)
                    {
                        HSSFRow row = sheet.createRow(i+1);
                        Response resp = (Response) rpv.get(i);
                        row.createCell((short)0).setCellValue(resp.getSurveyQuestionLabel());
                        row.createCell((short)1).setCellValue(resp.getGroupQuestionLabel());
                        row.createCell((short)2).setCellValue(resp.getResponseOrigStr());
                        row.createCell((short)3).setCellValue(resp.getP1Value());
                        row.createCell((short)4).setCellValue(resp.getP2Value());
                        row.createCell((short)5).setCellValue(resp.getP3Value());
                        row.createCell((short)6).setCellValue(resp.getP4Value());
                        row.createCell((short)7).setCellValue(resp.getP5Value());            
                    }
                    // Write the output 
                    wb.write(out);                	
                } catch (Exception e)
                {
                    out.write(e.getMessage().getBytes());
                	
                } finally
                {
                    out.close();
                }

            } else if (caller.equalsIgnoreCase("dynamic")) 
            {
                // NEED TO GET ALL THE PARAMETERS
                String peids = request.getParameter("peids");
                String mids = request.getParameter("mids");
                String pids = request.getParameter("pids");
                String qids = request.getParameter("qids");
                String cells = request.getParameter("cells");
                String sDate = request.getParameter("sDate");
                String[] dyn_array = {"Peid","Eid","TrxId","Cell","Product","Market","Response","Code1","Code1_Label","Code2","Code2_Label","Code3","Code3_Label","Code4","Code4_Label","Code5","Code5_Label","Code6","Code6_Label","Survey_Ques"};
                OutputStream out = response.getOutputStream();

                try
                {
                    List dynamic = responseDao.getDynamicReport(peids,mids,pids,qids,cells,sDate);
                    for (int i = 0; i < 20; i++) 
                    {
                        HSSFCell cell = rowHeader.createCell((short)(i));
                        cell.setCellStyle(styleHeader);
                        cell.setCellValue(dyn_array[i]);
                    }  
                    // IF DYNAMIC SIZE IS GREATER THE 65535 EXCEL WILL NOT BE ABLE TO HANDLE IT WE NEED 
                    // TO SEND A MESSAGE BACK TO THE USER.
                    HttpSession ses = request.getSession();
                    if (dynamic.size() > 65535)
                    {
                       HSSFRow row = sheet.createRow(1);
                       row.createCell((short)0).setCellValue("Too many rows returned. Excell can only return 65535 you have "+dynamic.size()+" rows being returned.");
                    } else
                    {
                        for (int i = 0; i < dynamic.size(); i++)
                        {
                            HSSFRow row = sheet.createRow(i+1);
                            Response resp = (Response) dynamic.get(i);
                            row.createCell((short)0).setCellValue(resp.getProgramEventId());
                            row.createCell((short)1).setCellValue(resp.getEventId());
                            row.createCell((short)2).setCellValue(resp.getTargetrxId());
                            row.createCell((short)3).setCellValue(resp.getCell());
                            row.createCell((short)4).setCellValue(resp.getP2Value());
                            row.createCell((short)5).setCellValue(resp.getP1Value());
                            row.createCell((short)6).setCellValue(resp.getResponseOrigStr());
                            row.createCell((short)7).setCellValue(resp.getCode1());
                            row.createCell((short)8).setCellValue(resp.getCode1Label());
                            row.createCell((short)9).setCellValue(resp.getCode2());
                            row.createCell((short)10).setCellValue(resp.getCode2Label());
                            row.createCell((short)11).setCellValue(resp.getCode3());
                            row.createCell((short)12).setCellValue(resp.getCode3Label());
                            row.createCell((short)13).setCellValue(resp.getCode4());
                            row.createCell((short)14).setCellValue(resp.getCode4Label());
                            row.createCell((short)15).setCellValue(resp.getCode5());
                            row.createCell((short)16).setCellValue(resp.getCode5Label());
                            row.createCell((short)17).setCellValue(resp.getCode6());
                            row.createCell((short)18).setCellValue(resp.getCode6Label());
                            row.createCell((short)19).setCellValue(resp.getSurveyQuestionLabel());            
                        }
                    }
                    // Write the output 
                    wb.write(out);                	
                } catch (Exception e)
                {
                    out.write(e.getMessage().getBytes());
                } finally
                {
                    out.close();
                }
            } else 
            {
                OutputStream out = response.getOutputStream();
                String[] hdr_array = {"Code Num","Code Label","Net1 Id","Net1 Label","Net2 Id","Net2 Label","Net3 Id","Net3 Label"};
                String cbid = (String) request.getParameter("codebookid");
                try
                {
                    List cbv = codeBookDao.viewCodeBook(cbid);
                    for (int i = 0; i < 8; i++) 
                    {
                        HSSFCell cell = rowHeader.createCell((short)(i));
                        cell.setCellStyle(styleHeader);
                        cell.setCellValue(hdr_array[i]);
                    }            
                    for (int i = 0; i < cbv.size(); i++)
                    {
                        HSSFRow row = sheet.createRow(i+1);
                        CodeBookView cv = (CodeBookView) cbv.get(i);
                        row.createCell((short)0).setCellValue(cv.getCodeNum());
                        row.createCell((short)1).setCellValue(cv.getCodeLabel());
                        row.createCell((short)2).setCellValue(cv.getNet1Id());
                        row.createCell((short)3).setCellValue(cv.getNet1Label());
                        row.createCell((short)4).setCellValue(cv.getNet2Id());
                        row.createCell((short)5).setCellValue(cv.getNet2Label());
                        row.createCell((short)6).setCellValue(cv.getNet3Id());
                        row.createCell((short)7).setCellValue(cv.getNet3Label());            
                    }
                    // WRITE THE OUTPUT 
                    wb.write(out);
                } catch (Exception e)
                {
                    out.write(e.getMessage().getBytes());
                } finally
                {
                    out.close();
                }
            } 
        }
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
