/*
 * ExcelLoader.java
 *
 * Created on March 23, 2009, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.util;

// CBD PACKAGES
import com.targetrx.project.oec.bo.Code;
import com.targetrx.project.oec.service.CodeDao;        
import com.targetrx.project.oec.service.CodeDaoImpl;
import com.targetrx.project.oec.service.ClientMappingDaoImpl;
import com.targetrx.project.oec.service.CustomMapDaoImpl;
import com.targetrx.project.oec.service.NetDao;
import com.targetrx.project.oec.service.NetDaoImpl;

// JAVA
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

// THIRD PARTY
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import uk.ltd.getahead.dwr.WebContextFactory;

/**
 *
 * @author pkukk
 */
public class ExcelLoader 
{
    
    private Logger log = Logger.getLogger(this.getClass());
    // Create the static Strings for the SQL statement
    public Map keywords = new HashMap();
		//private final String[] colHeaders = {"trx_code_num","trx_code_label","client_net","client_subnet","new_code_label"};
    private final String[] colHeaders = {"code_num","code_label","net_label","subnet_label","client_code_label","client_net_label","client_subnet_label"};
    private String insertCode = "insert into cody.codes (code_id, code_num, code_label, report_label, active_flag, client_code_flag, added_user, added_datetime) values (";
    private String insertNet = "insert into cody.nets (net_id, net_label,client_net_flag,added_user,added_datetime) values (";
    private String insertCodebookCodesXref = "insert cody.codebooks_codes_xref (codebook_id, code_id,added_user,added_datetime) values (";
    private String insertClientCodeXref = "insert into cody.client_code_xref (code_id, client_code_id,mapping_id,active_flag,added_user,added_datetime) values (";
    private String insertClientNetXref = "insert into cody.client_net_xref (net_id,client_id,active_flag,added_user,added_datetime) values (";
    private String insertCustomMapping = "insert into cody.custom_mapping (mapping_id,client_id,mapping_label,added_user,added_datetime) values (";
    private String addedUser = "cody";
    private NetDaoImpl netDao = null;
    private CodeDaoImpl codeDao = null;
    private CustomMapDaoImpl customMap = null;
   
    
    /**
     *
     * @param columnHeaders
     * @param xlsPath
     * @param codeBookId
     * @param clientId
     */
    public String loadExcel(final String pXlsPath,final String pCodebookId,final String pClientId,final String pMappingId)
    {
        HttpServletRequest req = WebContextFactory.get().getHttpServletRequest();
        ServletContext servletContext =  WebContextFactory.get().getServletContext();
        WebApplicationContext wac = WebApplicationContextUtils.
        getRequiredWebApplicationContext(servletContext);
        CodeDaoImpl codeDao = (CodeDaoImpl) wac.getBean("codes");
        NetDaoImpl netDao = (NetDaoImpl) wac.getBean("net");
        CustomMapDaoImpl customMap = (CustomMapDaoImpl) wac.getBean("custom");
        InputStream inputStream = null;
        StringBuffer sb = new StringBuffer();
        // LIST FOR EXTRA COLUMNS
        List listHeaders = new ArrayList();
        // LIST FOR MAPPED COLUMN HEADERS
        List cHeaders = new ArrayList();
        //LIST FOR MAPPED COLUMN VALUES
        List columnNumber = new ArrayList();
        String mapping_id = "";
        ClientMappingDaoImpl cmdi = new ClientMappingDaoImpl();
        
        // We need to clear everything out of the database for this Map Id and Codebook Id
        boolean proceed = customMap.cleanseCrap(pMappingId,pCodebookId);
        System.out.println("CRAP CLEANSED");
        if (!proceed)
        {
            return "PROBLEM:: Data could not be cleared for Map Id "+pMappingId+" please contact an IT representative.";
        }
        //***********************************************************
        // LETS CHECK TO MAKE SURE IN COMING PARAMS ARE GOOD
        //***********************************************************
        if (!this.checkInParams(pClientId, pCodebookId,pMappingId))
        {
            return "Check of incoming params failed.";
        }
        //***********************************************************
        // GET THE COLUMN HEADERS FOR THE APPLICATION
        //***********************************************************
        cHeaders = this.getColumnsFromExcel(pXlsPath);
        if (cHeaders.size() <= 1)
        {
            return "Error with book. Please contact an IT representative.";
        }
        //***********************************************************
        //CREATE CLIENT MAPPING FOR THE SPREADSHEET
        //***********************************************************
        mapping_id = pMappingId;//Integer.toString(cmdi.insertNewClientMapping(pClientId,pMappingLabel,"CODY LOADER"));
        //IF NO MAPPING ID COMES BACK STOP PROCESS
        if (mapping_id == null)
        {
            return "No Mapping ID passed in.";
        }

        try
        {
            inputStream = new FileInputStream(pXlsPath);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found in the specified path. "+e.getMessage());
            e.printStackTrace ();
        }
        POIFSFileSystem fileSystem = null;
        
        //********************************************************
        // NOW IT IS TIME TO PROCESS THE SPREADSHEET
        //********************************************************
        try
        {
            fileSystem = new POIFSFileSystem(inputStream);

            HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
            HSSFSheet sheet    = workBook.getSheetAt(0);
            // WE NEED TO GET THE CELLS THAT MATCH THE PARAMETERS PASSED IN
            HSSFRow firstRow   = sheet.getRow(0);        // first row
            int tmp = firstRow.getPhysicalNumberOfCells();
            HSSFCell cellFirstRow;
            List numCell = new ArrayList();
            
            HashMap hm_codes = new HashMap();
            HashMap hm_nets = new HashMap();
            // WE NEED TO LOOP THRU THE REST OF THE ROWS AND COLLECT THE VALUES TO
            // INSERT THE ROWS INTO THE DATABASE.
            String[] listSql = new String[sheet.getLastRowNum()+1];
            int inner = 0;
            System.out.println("*****************************************************");
            System.out.println("*****************************************************");
            System.out.println("** Processing Codebook "+pCodebookId);
            System.out.println("** For Client "+pClientId);
            System.out.println("** XLS "+pXlsPath);
            System.out.println("** LAST ROW "+sheet.getLastRowNum()+1);
            System.out.println("** NUMBER OF COLUMNS "+cHeaders.size());
            System.out.println("*****************************************************");
            System.out.println("*****************************************************");
            CodeDaoImpl cdi = new CodeDaoImpl();
            hm_codes.put("CRAPLOADER","5555555");
            hm_nets.put("CRAPLOADER","5555555");
            String err_string = validateCrapLoaderSheet(sheet);
            System.out.println("ERR STRING:: "+err_string);
            if (err_string != "")
            {
                return err_string;
            }
            System.out.println("GOT PASS VALIDATION ================================================================================>");
            // ******************************************
            // REMOVE
            if (pClientId == "")
            {
                return "test";
            }
            // ******************************************
            outer:
            for (Iterator rows = sheet.rowIterator(); rows.hasNext();)
            {
                HSSFRow row = (HSSFRow) rows.next();
                
                if (row.getRowNum() > 0)
                {
                    //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    String codeIdExists = "";
                    String newCodeId = "";
                    String newNetId = "";
                    String newSubNetId = "";
                    HSSFCell cell1 = row.getCell(Short.parseShort("0"));
                    String code_num = getCellValue(cell1);
                    HSSFCell cell2 = row.getCell(Short.parseShort("1"));
                    String code_label = getCellValue(cell2);
                    HSSFCell cell5 = row.getCell(Short.parseShort("5"));
                    String net = getCellValue(cell5);
                    HSSFCell cell6 = row.getCell(Short.parseShort("6"));
                    String subnet = getCellValue(cell6);
                    HSSFCell cell7 = row.getCell(Short.parseShort("4"));
                    String new_code_label = getCellValue(cell7);
                    //System.out.println("COLUMN VALUES:: "+code_num+" - "+code_label+" - "+net+" - "+subnet+" - "+new_code_label);
                    if ((code_num.length() <= 1)&&(code_label.length() <= 1)&&(net.length() <= 1)&&(subnet.length() <= 1)&& (new_code_label.length() <= 1))
                    {
                        break outer;
                    }
                    // NEED TO CREATE A CODE MAPPING IF THIS IS TRUE
                    if ((new_code_label != null) && (!new_code_label.trim().equalsIgnoreCase("null") && ((new_code_label.length() > 1))))
                    {
                        // CHECK TO SEE IF THE CODE LABEL ALREADY EXISTS
                        // CHECK TO SEE IF THE CODE_LABEL IS IN THE HASHMAP
                        if ((hm_codes.size() != 0) && (!hm_codes.containsKey(new_code_label)))
                        {
                            newCodeId = this.processCode(new_code_label, pClientId, pCodebookId, false,"0", code_num,mapping_id);
                            hm_codes.put(new_code_label,newCodeId);
                        } else
                        {
                            newCodeId = this.processCode(new_code_label, pClientId, pCodebookId, true,(String)hm_codes.get(new_code_label), code_num,mapping_id);
                        }
                        
                        //IF THE CODEID THAT CAMEBACK IS EQUAL TO NULL THEN
                        //WE NEED TO STOP THE PROCESSING.
                        if (newCodeId == null)
                        {
                            return "ERROR OCCURRED PROCESSING CODE";
                        }
                    } else
                    {
                        //codeIdExists = codeDao.getCodeId(pCodebookId,code_num);
                        List cde = codeDao.getCodeId(pCodebookId,code_num);
                        for (int i = 0; i < cde.size(); i++)
                        {
                            Code cd = (Code) cde.get(i);
                            newCodeId = cd.getCodeId();
                        }
                    }
                    // CHECK TO SEE IF THERE IS RENETTING WHICH INCLUDES NETS
                    // AND SUBNETS
                    if ((net != null) && (!net.equalsIgnoreCase("null")) && (net.length() > 1))
                    {
                        //System.out.println("....Create a Renetting....");
                        //System.out.println(net);
                        // CREATE A NEW NET IF IT DOES NOT EXIST IN THE HASHMAP AS A KEY
                        if ((hm_nets.size() == 0) || (!hm_nets.containsKey(net)))
                        {
                            //System.out.println("NET NOT IN KEY");
                            newNetId = this.processNet(net,pClientId, pCodebookId,newCodeId, mapping_id,false,"0");
                            hm_nets.put(net,newNetId);                            
                        } else
                        {
                            //System.out.println("NET IN KEY");
                            newNetId = this.processNet(net,pClientId, pCodebookId,newCodeId, mapping_id,true,(String) hm_nets.get(net));
                        }
                        //IF NEW NET ID COMES BACK NULL THEN A PROBLEM OCCURRED.
                        if (newNetId == null)
                        {
                            System.out.println("ERROR OCCURRED PROCESSING NET"+newNetId);
                            return "ERROR OCCURRED PROCESSING NET";
                        }
                        
                        if ((subnet != null) && (!subnet.equalsIgnoreCase("null")) && (subnet.length() > 1 ))
                        {
                            //System.out.println("......Create a Subnet.....");
                            if ((hm_nets.size() == 0) || (!hm_nets.containsKey(subnet)))
                            {
                                //System.out.println("***** "+subnet);
                                newSubNetId = this.processNet(subnet,pClientId, pCodebookId,newCodeId,mapping_id,false,"0");
                                hm_nets.put(subnet,newSubNetId);
                                //IF NEW NET ID COMES BACK NULL THEN A PROBLEM OCCURRED.
                            } else
                            {
                                //System.out.println("++++++ "+subnet);
                                newSubNetId = this.processNet(subnet,pClientId, pCodebookId,newCodeId,mapping_id,true,(String) hm_nets.get(subnet));
                            }
                            if (newSubNetId == null)
                            {
                                return "ERROR OCCURRED PROCESSING SUB NET";
                            }
                        } else
                        {
                            newSubNetId = "0";
                        }
                        //hm_nets.put(subnet,newSubNetId);
                        // WHERE THERE SUBNETS TO THIS ROW IF SO WE NEED TO ADD ROWS
                        // TO NETS_SUBNET_XREF, CODES_NETS_XREF
                        this.processSubNet(pCodebookId,newNetId,newSubNetId,newCodeId);
                        //processCodeNetRel(pCodebookId,newCodeId,newNetId,newSubNetId);
                    }
                    this.processCodeNetRel(pCodebookId,newCodeId,newNetId,newSubNetId);
                    inner++;
                    //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                }
            }

         } catch (Exception e)
         {
             System.out.println("ERROR: "+e.getMessage());
         } finally
         {
             try {
                inputStream.close();
             } catch (Exception ex)
             {
                 System.out.println(ex.getMessage());
             }
         }
         return "Excel file has been processed. Clients Codes and/or Nets have been mapped.";
    }
    /**
     * @param HSSFSheet
     * @return String
     */
    private String validateCrapLoaderSheet(HSSFSheet sheet)
    {
        List lst = new ArrayList();
        HashMap hm = new HashMap();
        String result = "";
        for (Iterator rows = sheet.rowIterator(); rows.hasNext();)
        {
            HSSFRow row = (HSSFRow) rows.next();
            if (row.getRowNum() > 0)
            {   
                HSSFCell cell2 = row.getCell(Short.parseShort("1"));
                String code_label = getCellValue(cell2);
                HSSFCell cell5 = row.getCell(Short.parseShort("5"));
                String net = getCellValue(cell5);
                HSSFCell cell6 = row.getCell(Short.parseShort("6"));
                String subnet = getCellValue(cell6);
                HSSFCell cell7 = row.getCell(Short.parseShort("4"));
                String new_code_label = getCellValue(cell7);
                
                if ((net == null) || (net.length() <= 0))
                {
                    result = result+" Code Label:: "+code_label+" does not have a net assigned to it.<br />";
                }
                try
                {
                    hm.put(code_label,net);
                    if ((new_code_label != null) || (new_code_label.length() > 0))
                    {
                        hm.put(new_code_label,net);
                    }
                } catch (Exception e)
                {
                    result = result+" New Code Label:: "+new_code_label+" is assigned to more then one net.<br />";
                }
            }
        }
        return result;
    }
    /**
     * @param String
     * @param String
     * @param String
     * @param String
     */
    private void processCodeNetRel(String pCodeBookId, String pCodeId, String pNet1, String pNet2)
    {
        HttpServletRequest req = WebContextFactory.get().getHttpServletRequest();
        ServletContext servletContext =  WebContextFactory.get().getServletContext();
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        CodeDaoImpl codeDao = (CodeDaoImpl) wac.getBean("codes");
        try
        {
            codeDao.insertClientCodesNets(pCodeBookId, pCodeId, pNet1, pNet2);
            
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    /**
     * @param String 
     * @param String
     * @param String
     * @param String
     *
     * @return String
     */
    private String processSubNet(final String pCodebookId,final String newNetId,final String newSubNetId,final String newCodeId)
    {
        String result = null;
        HttpServletRequest req = WebContextFactory.get().getHttpServletRequest();
        ServletContext servletContext =  WebContextFactory.get().getServletContext();
        WebApplicationContext wac = WebApplicationContextUtils.
        getRequiredWebApplicationContext(servletContext);
        netDao = (NetDaoImpl) wac.getBean("net");
        try
        {
            netDao.saveRelNets(pCodebookId, newNetId, newSubNetId, "CODYLOADER");            
            
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return result;
    }
    /**
     * @param String
     * @param String
     * @param String
     * @param String
     * @param String 
     * @param String
     * @param boolean
     * @param String
     *
     * @param String
     */
    private String processNet(final String pNetLabel, final String pClientId, final String pCodebookId,final String pCodeId,final String pMappingId, boolean pExists, final String pENetId)
    {
        String result = null;
        String trxNetId = "";
        HttpServletRequest req = WebContextFactory.get().getHttpServletRequest();
        ServletContext servletContext =  WebContextFactory.get().getServletContext();
        WebApplicationContext wac = WebApplicationContextUtils.
        getRequiredWebApplicationContext(servletContext);
        netDao = (NetDaoImpl) wac.getBean("net");
        try
        {
            if (!pExists)
            {
                //GET THE NEXT NET ID
                result =  Integer.toString(netDao.getNextNetId());
                // INSERT A ROW INTO NETS TABLE SET CLIETN_NET_FLAG = 'Y'
                netDao.insertClientNet(result, pNetLabel, "CODYLOADER");
                //If net label does not exist insert a new row into codebook_nets_xref table
                netDao.insertClientCodebookNet(result,pCodebookId,"CODYLOADER");
                //INSERT ONE ROW INTO client_nets_xref table.
                netDao.insertClientNetXref(trxNetId,result,pMappingId,"CODYLOADER");
                
            } else
            {
                result = pENetId;
            }           
            
            
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
        return result;
    }
    /**
     *
     * @param  String CodeLabel
     * @param  String
     * @param  String
     * @param boolean
     * @param  String
     * @param  String
     * @param  String
     *
     * @return String
     */
    private String processCode(final String pCodeLabel, final String pClientId, final String pCodebookId, boolean pExists, String pClientCodeId, final String pCodeNum, final String pMappingId)
    {
        String result = null;
        List code = new ArrayList();
        Code cde = null;
        HttpServletRequest req = WebContextFactory.get().getHttpServletRequest();
        ServletContext servletContext =  WebContextFactory.get().getServletContext();
        WebApplicationContext wac = WebApplicationContextUtils.
        getRequiredWebApplicationContext(servletContext);
        codeDao = (CodeDaoImpl) wac.getBean("codes");
        try
        {
            
            if (!pExists)
            {
                // If codeLabel does not exist for theis spreadsheet 
                // insert a new row into tables codes,
                // codebook_codes_xref table get the codeId first
                result = Integer.toString(codeDao.getNextCodeId());
                codeDao.insertClientCode(result, pCodeLabel, pCodebookId, "CODYLOADER");
                codeDao.insertClientCodebookCodes(pCodebookId, result, "CODYLOADER");    
                
                if (pClientCodeId == "0")
                {    
                    pClientCodeId = result;
                }
                code = codeDao.getCodeId(pCodebookId,pCodeNum);
                for (int i = 0; i < code.size(); i++)
                {
                    cde = (Code) code.get(i);
                }
                codeDao.insertClientCodeXref(pCodebookId, cde.getCodeId(), result, pMappingId);   
                
            } else
            {
                code = codeDao.getCodeId(pCodebookId,pCodeNum);
                for (int i = 0; i < code.size(); i++)
                {
                    cde = (Code) code.get(i);
                }
                codeDao.insertClientCodeXref(pCodebookId, cde.getCodeId(), pClientCodeId, pMappingId);
                result = pClientCodeId;
            }
            
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return result;
    }
    /**
     *
     * @param String ClientId
     * @param String CodebookId
     *
     * @return boolean
     */
    private boolean checkInParams(final String pClientId, final String pCodebookId, final String pMappingLabel)
    {
        boolean result = true;
        // CHECK TO SEE IF THERE IS A CLIENT ID PASSED IN
        if ((pClientId == null) || pClientId.equalsIgnoreCase(""))
        {
            result = false;
        }
        // CHECK TO SEE IF THERE IS A CODEBOOK ID PASSED IN
        if ((pCodebookId == null) || pCodebookId.equalsIgnoreCase(""))
        {
            result = false;
        }
        // CHECK TO SEE IF THERE IS A MAPPING LABEL PASSED IN
        if ((pMappingLabel == null) || pMappingLabel.equalsIgnoreCase(""))
        {
            result = false;
        }
        return result;
    }
    /**
     *
     * @param xlsPath
     *
     * @return List
     */
    public List getColumnsFromExcel(String xlsPath)
    {
        List arrList = new ArrayList();
        InputStream inputStream = null;
        try
        {
            System.out.println("===============> "+xlsPath);
            inputStream = new FileInputStream(xlsPath);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found in the specified path. "+e.getMessage());
            e.printStackTrace ();
        }
        POIFSFileSystem fileSystem = null;
        try
        {
            fileSystem = new POIFSFileSystem(inputStream);

            HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
            HSSFSheet sheet    = workBook.getSheetAt(0);
            HSSFRow row     = sheet.getRow(0);        // first row
            int tmp = row.getPhysicalNumberOfCells();
            HSSFCell cell;
            for (int i=0; i < tmp; i++)
            {
                cell = row.getCell((short)i);
                //System.out.println("CELL COLUMN ==> "+cell.getRichStringCellValue().toString().toUpperCase()+" ARRAY COMPARE VALUE "+colHeaders[i].toUpperCase());
                if (!cell.getRichStringCellValue().toString().toUpperCase().equalsIgnoreCase(colHeaders[i].toUpperCase()))
                {
                    String err = "ALERT MESSAGE: There was a problem in your column headers. Column header <i><u>"+cell.getRichStringCellValue().toString()+"</u></i> is a keyword and cannot be used";
                    arrList.clear();
                    arrList.add(err);
                    return arrList;
                }
                arrList.add(cell.getRichStringCellValue().toString());
            }

         } catch (Exception e)
         {
             System.out.println(e.getMessage());
         } finally
         {
             try {
                inputStream.close();
             } catch (Exception ex)
             {
                 System.out.println(ex.getMessage());
             }
         }
        return arrList;
    }
    /**
     *
     * @param cell
     * @return
     */
    private String getCellValue (HSSFCell cell)
    {
        if (cell == null) return null;

        String result = null;

        int cellType = cell.getCellType();
        switch (cellType) {
          case HSSFCell.CELL_TYPE_BLANK:
            result = " ";
            break;
          case HSSFCell.CELL_TYPE_BOOLEAN:
            result = cell.getBooleanCellValue() ?
              "true" : "false";
            break;
          case HSSFCell.CELL_TYPE_ERROR:
            result = "ERROR: " + cell.getErrorCellValue();
            break;
          case HSSFCell.CELL_TYPE_FORMULA:
            result = cell.getCellFormula();
            break;
          case HSSFCell.CELL_TYPE_NUMERIC:
            HSSFCellStyle cellStyle = cell.getCellStyle();
            short dataFormat = cellStyle.getDataFormat();

            // assumption is made that dataFormat = 15,
            // when cellType is HSSFCell.CELL_TYPE_NUMERIC
            // is equal to a DATE format.
            if (dataFormat == 15) {
              result = cell.getDateCellValue().toString();
            } else {
              //result = String.valueOf(cell.getNumericCellValue());
              NumberFormat numberFormat = NumberFormat.getIntegerInstance();
              numberFormat.setGroupingUsed(false);
              result = numberFormat.format(cell.getNumericCellValue());
            }

            break;
          case HSSFCell.CELL_TYPE_STRING:
            result = cell.getStringCellValue();
            break;
          default: break;
        }

        return result;
      }

}

