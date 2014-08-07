package com.targetrx.project.oec.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import com.targetrx.project.oec.bo.Code;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

import com.targetrx.project.oec.bo.Change;
import com.targetrx.project.oec.bo.CheckVerify;
import com.targetrx.project.oec.service.MonthEndDaoImpl;
import com.targetrx.project.oec.service.NetDaoImpl;
import com.targetrx.project.oec.service.ProgramFactsDaoImpl;

/**
 *
 * @author pkukk
 */
public class CodeDaoImpl extends JdbcDaoSupport implements CodeDao{
	public final static String CHANGE_REPORT_ADHOC = "adhoc";
	public final static String CHANGE_REPORT_MONTHEND = "monthend";
	public final static String CODED_RESPONSE_STATUS_CV = "cv";
	public final static String CODED_RESPONSE_STATUS_CU = "cu";
	public final static String CODED_RESPONSE_STATUS_NEW = "new";
	private Logger log = Logger.getLogger(this.getClass());
    /**
     * @see com.targetrx.project.oec.service.CodeDao#getAllCodes()
     */
    public List getAllCodes()
    {
        JdbcTemplate jt = getJdbcTemplate();
        return jt.query("select code_id, code_label, report_label, code_num, hint_code from codes where active_flag = 'Y' and client_code_flag = 'N'",new RowMapperResultReader(new CodeRowMapper()));
    }
    class CodeRowMapper implements RowMapper {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        Code cod = new Code();
	        cod.setCodeId(rs.getString(1));
	        cod.setCodeLabel(rs.getString(2));
	        cod.setCodeReport(rs.getString(3));
          cod.setCodeNum(rs.getString(4));
          cod.setCodeHint(rs.getString(5));
	        return cod;
	      }
	  }//class CodeRowMapper
    /**
     * @param codebookId
     * @param subnetId
     * @return
     * @throws Exception
     */
    public TreeMap<String, String> getMappedCodesBySubnet(final int codebookId, final int subnetId) throws Exception
    {
    	List<Code> list = null;
    	TreeMap<String, String> map = new TreeMap<String, String>();;
    	String query = 
    		"select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code " +
            "from codes a, " +
            "        codes_nets_xref b " +
            "where a.client_code_flag = 'N' " +
            "and a.active_flag = 'Y' " +
            "and codebook_id = ? " +
            "and net2_id = ? " +
            "and a.code_id = b.code_id " +
            "order by code_label";
    	JdbcTemplate jdbcTemplate = getJdbcTemplate();
    	try
    	{
    		list = jdbcTemplate.query(query, new Object[] {new Integer(codebookId), new Integer(subnetId)}, new CodeRowMapper());
    	} catch (Exception e)	
    	{
    		log.error(e.getMessage(), e);
    		throw e;
    	}
        for (int i=0; i<list.size(); i++)
        {
            Code cd = list.get(i);
            map.put(cd.getCodeNum(),"CN: "+cd.getCodeNum()+" : "+cd.getCodeLabel());
        }
    	return map;
    }
    /**
     * @param codebookId
     * @param days
     * @return
     */
    public TreeMap<String, String> getMappedCodesByDate(final int codebookId, final int days) throws Exception
    {
    	List<Code> list = null;
    	TreeMap<String, String> map = new TreeMap<String, String>();;
    	String query = 
    		"select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code " +
            "from codes a, " +
            "        codebook_codes_xref b " +
            "where a.client_code_flag = 'N' " +
            "and a.active_flag = 'Y' " +
            "and codebook_id = ? " +
            "and a.added_datetime between (sysdate - ?) and sysdate " +
            "and a.code_id = b.code_id " +
            "order by code_label";
    	JdbcTemplate jdbcTemplate = getJdbcTemplate();
    	try
    	{
    		list = jdbcTemplate.query(query, new Object[] {new Integer(codebookId), new Integer(days)}, new CodeRowMapper());
    	} catch (Exception e)	
    	{
    		log.error(e.getMessage(), e);
    		throw e;
    	}
        for (int i=0; i<list.size(); i++)
        {
            Code cd = list.get(i);
            map.put(cd.getCodeNum(),"CN: "+cd.getCodeNum()+" : "+cd.getCodeLabel());
        }
    	return map;
    }
    /**
     * @return java.util.Map
     **/
    public TreeMap getMappedCodes(final String codeBookId)
    {
    	TreeMap mp = new TreeMap();
        List lst = new ArrayList();
        StringBuffer query = new StringBuffer();
        JdbcTemplate jt = getJdbcTemplate();
        query.append(" select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code");
        query.append(" from codes a, codebook_codes_xref b");
        query.append(" where a.active_flag = 'Y'");
        query.append(" and a.client_code_flag != 'Y'");
        query.append(" and a.code_id = b.code_id");
        query.append(" and b.codebook_id = ?");
        query.append(" order by a.code_label ");
        try {
            lst = jt.query(query.toString(), new Object[] {codeBookId}, new CodeRowMapper());
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        for (int i=0; i<lst.size(); i++)
        {
            Code cd = (Code)lst.get(i);
            mp.put(cd.getCodeNum(),"CN: "+cd.getCodeNum()+" : "+cd.getCodeLabel());
        }
        return mp;
    }
    /**
	 * @return java.util.Map
     */
    public TreeMap getClientCodes(final String pCodeBookId)
    {
    	TreeMap mp = new TreeMap();
        List lst = new ArrayList();
        StringBuffer query = new StringBuffer();
        JdbcTemplate jt = getJdbcTemplate();
        query.append(" select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code");
        query.append(" from codes a, codebook_codes_xref b");
        query.append(" where a.active_flag = 'Y'");
        query.append(" and a.client_code_flag = 'Y'");
        query.append(" and a.code_id = b.code_id");
        query.append(" and b.codebook_id = ?");
        query.append(" order by a.code_label ");
        try
        {
        	lst = jt.query(query.toString(), new Object[] {pCodeBookId}, new CodeRowMapper());
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        for (int i=0; i<lst.size(); i++)
        {
            Code cd = (Code)lst.get(i);
            mp.put(cd.getCodeNum(),"CN: "+cd.getCodeNum()+" : "+cd.getCodeLabel());
        }
        return mp;
	}
    /**
     * @return Map
     */
    public TreeMap getAllMappedCodes(final String pCodeBookId)
    {
    	TreeMap mp = new TreeMap();
        List lst = new ArrayList();
        StringBuffer query = new StringBuffer();
        JdbcTemplate jt = getJdbcTemplate();
        query.append(" select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code");
        query.append(" from codes a");
        query.append(" where a.active_flag = 'Y'");
        query.append(" and  a.client_code_flag = 'N'");
        query.append(" order by a.code_label ");
        try {
            lst = jt.query(query.toString(),new RowMapperResultReader(new CodeRowMapper()));
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        for (int i=0; i<lst.size(); i++)
        {
            Code cd = (Code)lst.get(i);
            mp.put(cd.getCodeId(),"CN: "+cd.getCodeNum()+" : "+cd.getCodeLabel());
        }
        return mp;
    }
    /**
     * @return List
     */
    public List getCodeIds()
    {
        List lst = null;
       try
        {
           JdbcTemplate jt = getJdbcTemplate();
            lst =  jt.queryForList("select code_label from codes where active_flag = 'Y' and client_flag = 'N'");
        } catch(Exception ex)
        {
            log.error("IMPL "+ex.getMessage(), ex);
        }
        return lst;
   }
    /**
     * 
     */
    public List getCodeId(final String cbId, final String pCodeNum)
    {
        String sql = "select c.code_id, c.code_label, c.report_label, c.code_num, c.hint_code";
        sql = sql+" from codes c, codebook_codes_xref x";
        sql = sql+" where c.code_num = ?";
        sql = sql+" and c.client_code_flag = 'N'";
        sql = sql+" and x.code_id = c.code_id";
        sql = sql+" and x.codebook_id = ?";
        JdbcTemplate jt = getJdbcTemplate();
        
        return jt.query(sql, new Object[] {pCodeNum,cbId}, new CodeRowMapper());
    }
   /**
    * @params String
    * @params String
    * @params String
    * @params String
    */
    public String addCode(final String pCodeBookId, final String pCodeNum, final String pCodeLabel, final String pReportLabel, final String pHintCode, final String pNet1, final String pNet2, final String pNet3,final String pUser)
    {
        String codeId = "";
        String success = "success";
        int rows = 0;
        JdbcTemplate jt = getJdbcTemplate();
        try {
            String sql = "insert into codes (code_id, code_num,code_label,report_label,hint_code,original_codebook_id,added_user) ";
            sql = sql+"values (?,?,?,?,?,?,?)";
            log.debug("inserting new code: "+sql);
            jt.update(sql, new Object[] {pCodeNum, pCodeNum, pCodeLabel, pReportLabel, pHintCode, pCodeBookId, pUser});
            log.debug(pUser+" added new code to CodeBook Id: "+pCodeBookId+" Code Number: "+pCodeNum);
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
            return "Error inserting codes into CODES table";
        }
        if (!pNet1.equalsIgnoreCase("-55555"))
        {
            NetDaoImpl net = new NetDaoImpl();
            net.insertCodesNetsXref(pCodeBookId,pCodeNum,pNet1,pNet2,pNet3,pUser, jt);
        }
        return success;
    }
    /**
     * @params String
     * @params String
     */
    public void inactivateCode(final String pCodeBookId, final String pCodeId, final String pUser)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String update_sql = "update codes set active_flag = 'N' where code_id = ?";
        String delete_sql = "delete from codes_net_xref where codebook_id = ? and code_id = ?";
        try {
            int rows_update = jt.update(update_sql, new Object[] {pCodeId});
            int rows_delete = jt.update(delete_sql, new Object[] {pCodeBookId,pCodeId});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
    /**
     * @param codebookId
     * @param subnetId
     * @param type
     * @param filterType
     * @param filter
     * @return
     * @throws Exception
     */
    public TreeMap<String, String> searchMappedCodesBySubnet(
    		final int codebookId, 
    		final int subnetId, 
    		final String type, 
    		final String filterType, 
    		final String filter) throws Exception
    {
    	TreeMap<String, String> map = new TreeMap<String, String>();
    	List<Code> list = null;
    	String filterClause = null;
    	String query = null;
    	String labelQuery = 
    		"select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code " +
            "from codes a, " +
            "        codes_nets_xref b " +
            "where a.client_code_flag = 'N' " +
            "and a.active_flag = 'Y' " +
            "and codebook_id = ? " +
            "and net2_id = ? " +
            "and a.code_id = b.code_id " +
            "and UPPER(a.code_label) like UPPER(?) " +
            "order by code_label";
    	String codeNumQuery = 
    		"select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code " +
            "from codes a, " +
            "        codes_nets_xref b " +
            "where a.client_code_flag = 'N' " +
            "and a.active_flag = 'Y' " +
            "and codebook_id = ? " +
            "and net2_id = ? " +
            "and a.code_id = b.code_id " +
            "and UPPER(a.code_num) like UPPER(?) " +
            "order by code_label";

    	if ("begins".equals(filterType))
    	{
    		filterClause = filter + "%";
    	} else
    	{
    		filterClause = "%" + filter + "%";
    	}
    	if ("label".equals(type))
    	{
    		query = labelQuery;
    	} else
    	{
    		query = codeNumQuery;
    	}
    	
    	JdbcTemplate jdbcTemplate = getJdbcTemplate();
    	try
    	{
    		list = jdbcTemplate.query(query, new Object[] {new Integer(codebookId), new Integer(subnetId), filterClause}, new CodeRowMapper());
            for (int i=0; i < list.size(); i++)
            {
                Code cd = (Code)list.get(i);
                map.put(cd.getCodeId(),"CN: "+cd.getCodeNum()+" : "+cd.getCodeLabel());
            }

    	} catch (Exception e)
    	{
    		log.error(e.getMessage(), e);
    		throw e;
    	}
    	return map;
    }
    public TreeMap<String, String> searchMappedCodesByDate(
    		final int codebookId,
    		final int days,
    		final String type, 
            final String filterType, 
            final String filter) throws Exception
    {
    	TreeMap<String, String> map = new TreeMap<String, String>();
    	List<Code> list = null;
    	String filterClause = null;
    	String query = null;
    	Object[] parameters = null;
    	String filterLabelQuery = 
    		"select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code " +
            "from codes a, " +
            "        codebook_codes_xref b " +
            "where a.client_code_flag = 'N' " +
            "and a.active_flag = 'Y' " +
            "and codebook_id = ? " +
            "and a.code_id = b.code_id " +
            "and a.added_datetime between (sysdate - ?) and sysdate " +
            "and UPPER(a.code_label) like UPPER(?) " +
            "order by code_label";
    	String filterNumQuery = 
    		"select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code " +
            "from codes a, " +
            "        codebook_codes_xref b " +
            "where a.client_code_flag = 'N' " +
            "and a.active_flag = 'Y' " +
            "and codebook_id = ? " +
            "and a.code_id = b.code_id " +
            "and a.added_datetime between (sysdate - ?) and sysdate " +
            "and UPPER(a.code_num) like UPPER(?) " +
            "order by code_label";
    	String noFilterLabelQuery = 
    		"select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code " +
            "from codes a, " +
            "        codebook_codes_xref b " +
            "where a.client_code_flag = 'N' " +
            "and a.active_flag = 'Y' " +
            "and codebook_id = ? " +
            "and a.code_id = b.code_id " +
            "and UPPER(a.code_label) like UPPER(?) " +
            "order by code_label";
    	String noFilterNumQuery = 
    		"select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code " +
            "from codes a, " +
            "        codebook_codes_xref b " +
            "where a.client_code_flag = 'N' " +
            "and a.active_flag = 'Y' " +
            "and codebook_id = ? " +
            "and a.code_id = b.code_id " +
            "and UPPER(a.code_num) like UPPER(?) " +
            "order by code_label";
    	if ("begins".equals(filterType))
    	{
    		filterClause = filter + "%";
    	} else
    	{
    		filterClause = "%" + filter + "%";
    	}
    	if ("label".equals(type))
    	{
    		if (days == -1)
    		{
    			query = noFilterLabelQuery;
    			parameters = new Object[] { new Integer(codebookId), filterClause };
    		} else
    		{
    			query = filterLabelQuery;
    			parameters = new Object[] { new Integer(codebookId), new Integer(days), filterClause };
    		}
    	} else
    	{
    		if (days == -1)
    		{
    			query = noFilterNumQuery;
    			parameters = new Object[] { new Integer(codebookId), filterClause };
    		} else
    		{
    			query = filterNumQuery;
    			parameters = new Object[] { new Integer(codebookId), new Integer(days), filterClause };
    		}
    	}
    	
    	JdbcTemplate jdbcTemplate = getJdbcTemplate();
    	try
    	{
    		log.debug("searchMappedCodesByDate: "+query);
    		list = jdbcTemplate.query(query, parameters, new CodeRowMapper());
    		log.debug("searchMappedCodesByDate found "+list.size()+" results");
            for (int i=0; i < list.size(); i++)
            {
                Code cd = (Code)list.get(i);
                map.put(cd.getCodeId(),"CN: "+cd.getCodeNum()+" : "+cd.getCodeLabel());
                //System.out.println(cd.getCodeId()+", CN: "+cd.getCodeNum()+" : "+cd.getCodeLabel());
            }

    	} catch (Exception e)
    	{
    		log.error(e.getMessage(), e);
    		throw e;
    	}
    	return map;
    }
    /**
     * @param type
     * @param filterType
     * @param filter
     * @return
     * @throws Exception
     */
    public TreeMap<String, String> searchMappedCodes(final String type, final String filterType, final String filter) throws Exception
    {
    	TreeMap<String, String> map = new TreeMap<String, String>();
    	List<Code> list = null;
    	String filterClause = null;
    	String query = null;
    	String labelQuery = 
	    	"select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code " +
	        "from codes a " +
	        "where a.active_flag = 'Y' " +
	        "and a.client_code_flag = 'N' " +
	        "and UPPER(a.code_label) LIKE UPPER(?) " +
	        "order by a.code_label";
    	String codeNumQuery = 
	    	"select a.code_id, a.code_label, a.report_label, a.code_num, a.hint_code " +
	        "from codes a " +
	        "where a.active_flag = 'Y' " +
	        "and a.client_code_flag = 'N' " +
	        "and UPPER(a.code_num) LIKE UPPER(?) " +
	        "order by a.code_label";
    	if ("begins".equals(filterType))
    	{
    		filterClause = filter + "%";
    	} else
    	{
    		filterClause = "%" + filter + "%";
    	}
    	if ("label".equals(type))
    	{
    		query = labelQuery;
    	} else
    	{
    		query = codeNumQuery;
    	}
    	
    	JdbcTemplate jdbcTemplate = getJdbcTemplate();
    	try
    	{
    		list = jdbcTemplate.query(query, new Object[] { filterClause }, new CodeRowMapper());
            for (int i=0; i < list.size(); i++)
            {
                Code cd = (Code)list.get(i);
                map.put(cd.getCodeId(),"CN: "+cd.getCodeNum()+" : "+cd.getCodeLabel());
            }

    	} catch (Exception e)
    	{
    		log.error(e.getMessage(), e);
    		throw e;
    	}
    	return map;
    }

    /**
     * @param String
     * @param String
     */
    public void deleteCode(final String pCodeBookId, final String pCodeId)
    {
        int row = 0;
        JdbcTemplate jt = getJdbcTemplate();
        String query = "updated codes set active_flag = 'N' where code_id = ?";
        String cb_code_xref = "delete from codebook_codes_xref where codebook_id = ? and code_id = ?";
        String code_net_xref = "delete from codes_nets_xref where codebook_id = ? and code_id = ?";
        try {
            row = jt.update(cb_code_xref, new Object[] {pCodeBookId, pCodeId});
            row = jt.update(query, new Object[] {pCodeId});
            row = jt.update(code_net_xref, new Object[] {pCodeBookId, pCodeId});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
    /**
     * @param String
     * @return List
     */
    public List getCode(final String pCodeId, final String cbId)
    {
        List lst = null;
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select c.code_id, c.code_label, c.report_label, c.code_num, c.hint_code";
        sql = sql+" from codes c, codebook_codes_xref x";
        sql = sql+" where x.codebook_id = ?";
        sql = sql+" and c.code_id = x.code_id";
        sql = sql+" and c.code_id = ?";
        try {
            lst =  jt.query(sql, new Object[] {cbId, pCodeId}, new CodeRowMapper());
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }

        return lst;
    }
    /**
     * @param String
     * @param String
     * @return List
     */
    public List getCodeMain(final String pCodeId, final String cbId)
    {
        List lst = null;
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select c.code_id, c.code_label, c.report_label, c.code_num, c.hint_code";
        sql = sql+" from codes c, codebook_codes_xref x";
        sql = sql+" where x.codebook_id = ?";
        sql = sql+" and c.client_code_flag = 'N'";
        sql = sql+" and c.code_id = x.code_id";
        sql = sql+" and c.code_id = ?";
        try {
            lst =  jt.query(sql, new Object[] {cbId, pCodeId}, new CodeRowMapper());
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }

        return lst;
    }
    /**
     * @param String
     * @param String
     * @param String
     * @return int
     */
    public int getCodeBasedId(String pCodeNum, String cbId, String bool)
    {
        int result = 0;
        String sql = "select c.code_id";
        sql = sql+" from codes c, codebook_codes_xref x";
        sql = sql+" where c.code_num = ?";
        sql = sql+" and c.client_code_flag = 'N'";
        sql = sql+" and x.code_id = c.code_id";
        sql = sql+" and x.codebook_id = ?";
        JdbcTemplate jt = getJdbcTemplate();
        try {
            result =  jt.queryForInt(sql, new Object[] {pCodeNum,cbId});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return result;
    }
    /**
     * @param String
     * @param String
     */
    public void updateCode(final String pCbId, final String pCodeId, final String pCodeNum, final String pCodeLabel, final String pReportLabel, final String pCodeHint, final String pUser)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "update codes set code_label = ?, report_label = ?,";
        sql = sql+" hint_code = ?, updated_user = ? where code_id = ?";
        try {
            int row = jt.update(sql, new Object[] {pCodeLabel, pReportLabel, pCodeHint, pUser, pCodeId});
            log.debug(pUser+" updated code to CodeBook Id: "+pCbId+" Code Number: "+pCodeNum+" Code Id:"+pCodeId);
        } catch (Exception e)
        {
            log.error("SQL: "+sql+" \n"+e.getMessage(), e);
        }
    }
    /**
     * @param String
     * @param String
     * @param String
     * @param String
     * @param String
     */
    public void saveCode(final String pCodeBookId, final String pCodeId, final String pNet1, final String pNet2, final String pNet3, final String pUser)
    {
        JdbcTemplate jt = getJdbcTemplate();
        log.debug("SAVING CODE: CodeBook: "+pCodeBookId+" Code:"+pCodeId+" Net1:"+pNet1);
        boolean proceed = this.codeNumExist(pCodeBookId, pCodeId);
        log.debug("========================> "+proceed);
        if (proceed)
        {
            String net2 = pNet2;
            String net3 = pNet3;
            String sql1 = "insert into codebook_codes_xref (codebook_id, code_id, added_user) values (?,?,?)";
            String sql = "";
            if (pNet1.equalsIgnoreCase("-55555"))
            {
                try {
                    log.debug("SaveCode: NET -55555 "+sql1);
                    //jt.execute(sql1);
                    jt.update(sql1, new Object[] {pCodeBookId, pCodeId, pUser});
                } catch (Exception e)
                {
                    log.error("SQL: "+sql+" \n"+e.getMessage(), e);
                }
            } else
            {
                try {
                    sql = "insert into codes_nets_xref (codebook_id, net1_id, code_id, net2_id, net3_id, added_user) values ";
                    if (net2.equalsIgnoreCase("0"))
                    {
                        sql = sql+"( ?, ?, ?, null, null, ?)";
                        log.debug("SaveCode SQL: "+sql);
                        jt.update(sql, new Object[] {pCodeBookId, pNet1, pCodeId, pUser});
                    } else if (net3.equalsIgnoreCase("0"))
                    {
                       sql = sql+"( ?, ?, ?, ?, null, ?)";
                       log.debug("SaveCode SQL: "+sql);
                       jt.update(sql, new Object[] {pCodeBookId, pNet1, pCodeId, pNet2, pUser});
                    } else
                    {
                        sql = sql+"( ?, ?, ?, ?, ?, ?)";
                        log.debug("SaveCode SQL: "+sql);
                        jt.update(sql, new Object[] {pCodeBookId, pNet1, pCodeId, pNet2, pNet3, pUser});
                    }
                } catch (Exception e)
                {
                    log.error("SQL: "+sql+" \n"+e.getMessage(), e);
                }
            }
        }

    }

    public boolean codeNumExist(final String pCodebookId, final String pCodeId)
    {
        boolean result = true;
        String sql = "select * from"+
             " (select distinct (autocoder.get_code_num(a.code_id)) tst"+
             " from codes_nets_xref a"+
             " where a.codebook_id = ?"+
             " union"+
             " select distinct (autocoder.get_code_num(b.code_id))"+
             " from codebook_codes_xref b"+
             " where b.codebook_id = ?) x"+
             " where autocoder.get_code_num(?) = x.tst";
        JdbcTemplate jt = getJdbcTemplate();
        List lst = jt.queryForList(sql, new Object[] {pCodebookId, pCodebookId, pCodeId});
        if (lst.size() > 0 )
        {
            result = false;
        }
        return result;
    }
    /**
     * @params String
     * @return List
     */
    public List checkCodeLabel(final String pCodeLabel)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select code_id, code_label, report_label from codes where UPPER(code_label) = UPPER(?)";
        return jt.queryForList(sql, new Object[] {pCodeLabel});
    }
    /**
     * @param String
     * @return List
     */
    public List checkCodeNum(final String pCodeNum)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select code_id, code_label, report_label from cody.codes where UPPER(code_num) = UPPER(?)";
        return jt.queryForList(sql, new Object[] {pCodeNum});
    }
    /**
     * @return int
     */
    public int getNextCodeId()
    {
        int result = 0;
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select cody.code_id_seq.nextval from dual";
        try {
            result = jt.queryForInt(sql);
        } catch (Exception e)
        {
           log.error("SQL: "+sql+" \n"+e.getMessage(), e);
        }
        return result;
    }
    /**
     * @param String
     * @return String
     */
    public String getCodeLabel(String pCodeNum)
    {
        String result = "";
        List lst = null;
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select code_label from cody.codes where code_num = ?";
        try 
        {
            lst = jt.queryForList(sql, new Object[] {pCodeNum});
        } catch (Exception e)
        {
            log.error(e.getMessage(),e);
        }
        for (int i=0; i<lst.size(); i++)
        {
            log.debug("--------------------------> "+lst.get(i));
            //result = (String) lst.get(i);
        }
        return result;
    }
    /**
     * @param String
     * @return String
     */
    public String getCodeLabelById(String pCodeNum, String pCodeBookId)
    {
        String result = "";
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select code_label from codes where code_id = autocoder.get_code_id(?,?)";
        log.debug("#getCodeLabelById:: "+sql);
        result = (String) jt.queryForObject(sql, new Object[] {pCodeBookId,pCodeNum}, String.class);
        log.debug("===> "+result);
        return result;
    }
    /**
     * @param type
     * @param date
     * @param user
     * @param filter
     * @param codebookId
     * @return
     */
    public List getNewCodeHistory(String type, String date, String user, String filter, int codebookId)
    {
    	List list = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String query = null;
        String prefixSQL =
        	"select d.codebook_name, " +
        	"         a.code_id, " +
        	"         b.code_label, " + 
        	"         a.net1_id, " +
        	"         c.net_label as net1_label, " + 
        	"         a.net2_id, " +
        	"         CodyCur.getNetLabel(a.net2_id) as net2_label, " +  
        	"         a.net3_id, " +
        	"         CodyCur.getNetLabel(a.net3_id) as net3_label, " +
        	"         a.history_action," +
        	"         a.history_action_datetime " +
        	"from codes_nets_xref_history a, " + 
        	"        codes b, " + 
        	"        nets c, " +
        	"        codebooks d " +
        	"where history_action_datetime between to_date(?,'mm/dd/yyyy') and last_day(to_date(?,'mm/dd/yyyy')) " +
        	"and history_action_user != 'KMAHLENDORF' " +
        	"and history_action in ('UPDATE','INSERT') " + 
        	"and b.code_id = a.code_id " +
        	"and c.net_id = a.net1_id " + 
        	"and a.codebook_id = d.codebook_id ";
        String codebookSQL = "and a.codebook_id = ? ";
        String suffixSQL = "order by 1, b.code_label";
        try
        {
        	if ("true".equals(filter))
        	{
        		query = new StringBuffer(prefixSQL).append(codebookSQL).append(suffixSQL).toString();
                list = jt.query(query, new Object[] {date, date, new Integer(codebookId)}, new RowMapperResultReader(new HistoryRowMapper()));
        	} else
        	{
        		query = new StringBuffer(prefixSQL).append(suffixSQL).toString();
                list = jt.query(query, new Object[] {date, date}, new RowMapperResultReader(new HistoryRowMapper()));
        	}
        	if (type.equals(CHANGE_REPORT_MONTHEND))
        	{
        		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        		Date fieldingPeriod = formatter.parse(date);
                MonthEndDaoImpl dao = new MonthEndDaoImpl();
                dao.setJdbcTemplate(jt);
                dao.setCheckStatus(fieldingPeriod, MonthEndDaoImpl.CHECK_CODE_CHANGE_REPORT, MonthEndDaoImpl.CHECK_STATUS_SUCCESS, user);
        	}
        } catch (Exception e)
        {
        	log.error(e.getMessage(), e);
        }
    	return list;
    }
    /**
     * @param String Date
     * @return List
     */
    public List getCodeHistory(String pChangeDate,String pEndDate, String pUser, final String pByCodeBook, final String pCodebookId)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select (select codebook_name from cody.codebooks where codebook_id = a.codebook_id), a.code_id, b.code_label, a.net1_id, c.net_label as net1_label, a.net2_id,"+
                " (select net_label from cody.nets where net_id = a.net2_id) as net2_label,  a.net3_id,"+
                " (select net_label from cody.nets where net_id = a.net3_id) as net3_label,  a.history_action"+
                " from codes_nets_xref_history a, codes b, nets c "+
                " where history_action_datetime > to_date('"+pChangeDate+"','mm/dd/yyyy')";
        if (pByCodeBook.equalsIgnoreCase("true"))
        {
            sql = sql +" and codebook_id = "+pCodebookId;
        }
        sql = sql + " and history_action_datetime < to_date('"+pEndDate+"','mm/dd/yyyy')"+
                " and   history_action_user != 'KMAHLENDORF' and history_action in ('UPDATE','INSERT') and b.code_id = a.code_id"+
                " and   c.net_id = a.net1_id order by 1, b.code_label";
        // THIS PROCEDURE WILL ADD A ROW TO THE FIELDING_PERIOD TABLE
        log.debug(sql);
        String sql_change = " begin month_end.trackFP('"+pChangeDate+"', 'change', 'executed', '"+pUser+"'); end;";
        try {
            jt.execute(sql_change);
        } catch (Exception e)
        {
            log.error("SQL: "+sql+" \n"+e.getMessage(), e);
        }
        log.debug(sql);
        return jt.query(sql,new RowMapperResultReader(new HistoryRowMapper()));
    }
    class HistoryRowMapper implements RowMapper {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        Change ch = new Change();
	        ch.setCodebookLabel(rs.getString(1));
          ch.setCodeId(rs.getString(2));
	        ch.setCodeLabel(rs.getString(3));
          ch.setNet1Id(rs.getString(4));
          ch.setNet1Label(rs.getString(5));
          ch.setNet2Id(rs.getString(6));
          ch.setNet2Label(rs.getString(7));
          ch.setNet3Id(rs.getString(8));
          ch.setNet3Label(rs.getString(9));
          ch.setHistoryAction(rs.getString(10));
          ch.setActionDatetime(rs.getDate(11));
	        return ch;
	      }
	  }//class HistoryRowMapper
    /**
     * 
     * @param programEventId
     * @param user
     * @return
     */
    public List getCheckVerifyForProgram(int programEventId, String user) throws Exception
    {
    	boolean passedCheck = true;
    	List list = new ArrayList();
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        String query = 
        	"select count(status_code) as total_count, program_event_id, status_code, Code_Verifier.getCheckLabel(tag_type_code) as tag_type_code "+
        	"from oec_response_staging a " +
        	"where program_event_id = ? " +
        	"and not exists " +
        	"( " +
        	"  select " +
        	"  1 " +
        	"  from do_not_post_questions b " +
        	"  where a.survey_question_label = b.survey_question_label " +
        	") "+
        	"group by program_event_id, status_code, tag_type_code " +
        	"order by status_code, tag_type_code nulls last";
        try
        {
            list =  jdbcTemplate.query(query, new Object[] {new Integer(programEventId)}, new CheckRowMapper());
            // check list for any rows that have a status of cu or new
            if (list != null)
            {
                for (int i = 0; i < list.size(); i++)
                {
                	CheckVerify checkRow = (CheckVerify)list.get(i);
                	if (CODED_RESPONSE_STATUS_CU.equals(checkRow.getStatusCode()) ||
                		CODED_RESPONSE_STATUS_NEW.equals(checkRow.getStatusCode()))
                	{
                		passedCheck = false;
                	}
                }
            } else
            {
            	passedCheck = false;
            }
            
            MonthEndDaoImpl dao = new MonthEndDaoImpl();
            dao.setJdbcTemplate(jdbcTemplate);
            if (passedCheck)
            {
            	dao.setCheckStatus(programEventId, MonthEndDaoImpl.CHECK_CODE_CHECK_VERIFY, MonthEndDaoImpl.CHECK_STATUS_SUCCESS, user);
            } else
            {
            	dao.setCheckStatus(programEventId, MonthEndDaoImpl.CHECK_CODE_CHECK_VERIFY, MonthEndDaoImpl.CHECK_STATUS_FAILED, user);
            }
        	
        } catch (Exception e) 
        {
        	log.error(e.getMessage(), e);
        	throw e;
        }
    	return list;
    }
    /**
     * @param String Date
     * @return List
     */
    public List getCheckVerify(String pChangeDate, String pAudit, String pPLabel, String pUser)
    {
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "";
        MonthEndDaoImpl me = new MonthEndDaoImpl();
        if (pAudit.equalsIgnoreCase("true"))
        {
            sql = "select count(status_code) as total_count, program_event_id, status_code from oec_response_staging"+
                    " where program_event_id in (  select program_event_id from program_facts"+
                    " where fielding_period = to_date(?, 'MM/DD/YYYY') and supplement_code in ('P', 'S') and study_type_code = 'AUDIT'"+
                    " ) and group_question_label <> '909' and status_code <> 'cv' group by program_event_id, status_code";
            lst =  jt.query(sql, new Object[] {pChangeDate}, new CheckRowMapper());
        } else
        {
            ProgramFactsDaoImpl pfd = new ProgramFactsDaoImpl();
            List peid = pfd.getProgramEvent(pPLabel, jt);

            sql = "select count(status_code) as total_count, program_event_id, status_code, tag_type_code"+
                    " from oec_response_staging where program_event_id = ? and group_question_label <> '909'"+
                    " and status_code <> 'cv' group by program_event_id, status_code, tag_type_code";
            lst =  jt.query(sql, new Object[] {(String)peid.get(0)}, new CheckRowMapper());
        }
        log.debug("#getCheckVerify ::"+sql);
        
        if (pAudit.equalsIgnoreCase("true"))
        {
            for (int i = 0; i < lst.size(); i++)
            {
                CheckVerify cv = (CheckVerify) lst.get(i);
                // QUERY WILL ADD ONE TO MANY ROWS TO THE FIELDING_PERIOD_PROGRAM_EVENT TABLE
                log.debug("#trackFPPE :: Date:: "+pChangeDate+" Check:: check' PEID:: "+cv.getProgramEventId()+" Execute:: executed User:: "+pUser);
                String check_sql = "begin month_end.trackFPPE('"+pChangeDate+"','check',"+cv.getProgramEventId()+",'executed','"+pUser+"'); end;";
                try {
                jt.execute(check_sql);
                } catch (Exception e)
                {
                    log.error("SQL: "+check_sql+" \n"+e.getMessage(), e);
                }
            }
        }
        if (lst.size() == 0)
        {
            if (pAudit.equalsIgnoreCase("true"))
            {
                 me.insertCheckList(pChangeDate, "Check/Verify", null, jt);
            } else
            {
                me.insertCheckList(null, "Check/Verify", pPLabel, jt);
            }
        }
        return lst;
    }
    class CheckRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int index) throws SQLException
        {
            CheckVerify cv = new CheckVerify();
            cv.setCount(rs.getString("total_count"));
            cv.setProgramEventId(rs.getString("program_event_id"));
            cv.setStatusCode(rs.getString("status_code"));
            cv.setTagTypeCode(rs.getString("tag_type_code"));
            return cv;
        }
    }
    /**
     * @param String ProgramEventId
     */
    public void checkInPeid(final String pProgramEventId)
    {
        JdbcTemplate jt = getJdbcTemplate();
        Connection conn = null;
        String sql = "begin code_verifier.verify("+pProgramEventId+"); end;";
        try {
            jt.execute(sql);
        } catch (Exception e)
        {
            log.error("SQL: "+sql+" \n"+e.getMessage(), e);
        }
    }
    
    /**
     * @param String
     * @param String
     * @param String
     */
    public void insertClientCode(final String pCodeId,final String pCodeLabel,final String pCodeBookId, final String pUser)
    {
        JdbcTemplate jt = getJdbcTemplate();
        try {
            
            String sql = "insert into cody.codes (code_id, code_num,code_label,client_code_flag, added_user) ";
            sql = sql+"values (?,?,?,'Y',?)";
            jt.update(sql, new Object[] {pCodeId,pCodeId,pCodeLabel,pUser});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);            
        }
    }
    /**
     * @param String
     * @param String
     * @param String
     */
     public void insertClientCodebookCodes(final String pCodeBookId, final String pCodeId, final String pUser)
     {
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "insert into cody.codebook_codes_xref (codebook_id, code_id, added_user) values (?,?,?)";
        try 
        {
            jt.update(sql, new Object[] {pCodeBookId,pCodeId,pUser});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
     }
     /**
     * @params String
     * @return String
     */
    public String checkCodesLabel(final String pCodeLabel)
    {
        String result = null;
        String sql = "select code_id from cody.codes where UPPER(code_label) = UPPER(?)";
        JdbcTemplate jt =  getJdbcTemplate();
        try
        {
            result = (String) jt.queryForObject(sql, new Object[] { pCodeLabel },String.class);
        } catch (Exception e)
        {
            log.info(e.getMessage());
        }
        return result;
    }
    /**
     * @param String Code Id
     * @param String Client Code Id
     * @param String Mapping Id
     */
    public void insertClientCodeXref(final String pCodebookId, final String pCodeId, final String pClientCodeId, final String pMappingId)
    {
        JdbcTemplate jt = getJdbcTemplate();
        try 
        {
            jt.update("insert into cody.client_codes_xref (codebook_id, code_id, client_code_id, mapping_id, active_flag, added_user, added_datetime) values (?,?,?,?,'Y','CODYLOADER',sysdate)", new Object[] {pCodebookId,pCodeId,pClientCodeId,pMappingId});
        } catch (Exception e)
        {
            log.info(e.getMessage(), e);
        }
    }
    public void insertClientCodesNets(String pCodeBookId, String pCodeId, String pNet1, String pNet2)
    {
        String sql = "insert into cody.codes_nets_xref (codebook_id, net1_id, code_id, net2_id, net3_id, added_user) values ";
        List result = null;
        if (pNet2.equalsIgnoreCase("0"))
        {
            sql = sql+"( ?, ?, ?, null, null,'CODYLOADER')";
            String selectSql = "select code_id from cody.nets_xref where codebook_id = ? and code_id = ? and net1_id = ?";
            try
            {
                JdbcTemplate jt = getJdbcTemplate();
                    jt.update(sql, new Object[] {pCodeBookId,pNet1,pCodeId});
            } catch (Exception e)
            {
                log.info(e.getMessage());
            }
        } else 
        {
            sql = sql+"( ?, ?, ?, ?,null,'CODYLOADER')";
            String selectSql = "select code_id from cody.nets_xref where codebook_id = ? and code_id = ? and net1_id = ? and net2_id = ?";
            try
            {
                JdbcTemplate jt = getJdbcTemplate();
                    jt.update(sql, new Object[] {pCodeBookId,pNet1,pCodeId,pNet2});
            } catch (Exception e)
            {
                log.info(e.getMessage());
            }
        }        
    }
}