package com.targetrx.project.oec.service;

import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.targetrx.project.oec.bo.CheckList;
import com.targetrx.project.oec.bo.CheckStatus;
import com.targetrx.project.oec.bo.CheckType;
import com.targetrx.project.oec.bo.ProgramFacts;
import com.targetrx.project.oec.service.ProgramFactsDaoImpl;
/**
 *
 * @author pkukk
 */
public class MonthEndDaoImpl extends JdbcDaoSupport implements MonthEndDao {
	public static final String CHECK_STATUS_SUCCESS = "Success";
	public static final String CHECK_STATUS_FAILED = "Failed";
	public static final String CHECK_CODE_CHECK_VERIFY = "Check/Verify";
	public static final String CHECK_CODE_CHECK_COUNT = "Check Count";
	public static final String CHECK_CODE_END_EVENT = "EndEvent";
	public static final String CHECK_CODE_DISCONNECTED = "Disconnect";
	public static final String CHECK_CODE_CHANGE_REPORT = "change";
	public static final String CHECK_CODE_DISCREPANCY_REPORT = "discrep";
	private Logger log = Logger.getLogger(this.getClass());
	/**
	 * @param studyType
	 * @param fieldingPeriod
	 * @return
	 */
	public List<ProgramFacts> getMonthStatus(String studyType, String fieldingPeriod) throws Exception
	{
		List<ProgramFacts> list = null;
		String query =
			"select program_event_id, " +
			"         program_label, " +
			"         b.description as supplement_code, " +
			"         decode(oec_status_code, 'A', 'Not Posted', 'I', 'Posted') as post_status " +
            "from program_facts a, " +
            "        supplement_codes b " +
            "where fielding_period = ? " +
            "and study_type_code = ? " +
            "and a.supplement_code = b.supplement_code " +
            "order by decode(a.supplement_code, 'P', 1, 'S', 2, 'Y', 3, 'C', 4), program_label";
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    		Date date = formatter.parse(fieldingPeriod);
            list = jdbcTemplate.query(query, new Object[] {date, studyType}, new MonthStatusRowMapper());
        } catch (Exception e)
        {
        	log.error(e.getMessage(), e);
        	throw e;
        }
		return list;
	}
	/**
	 * @author sstuart
	 */
	private class MonthStatusRowMapper implements RowMapper
	{
		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			ProgramFacts programFact = new ProgramFacts();
			programFact.setEventId(new Integer(rs.getInt("program_event_id")).toString());
			programFact.setProgramLabel(rs.getString("program_label"));
			programFact.setSupplementCode(rs.getString("supplement_code"));
			programFact.setStatusCode(rs.getString("post_status"));
			return programFact;
		}
	}
	/**
	 * @param programEventId
	 * @return
	 */
	public String postProgram(int programEventId)
	{
		String result;
		JdbcTemplate jdbcTemplate = getJdbcTemplate();
		try
		{
			jdbcTemplate.call(new PostProgramCallable(programEventId), new ArrayList());
			result = "<span style=\"color: white;\">Program has been queued to post. Check the Job Monitor for status.</span>";
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
			result = "<span style=\"color: red;\">Error posting program: " + e.getMessage() + "</span>";
		}
		return result;
	}
	/**
	 * @author sstuart
	 */
	private class PostProgramCallable implements CallableStatementCreator
	{
		private int programEventId;
		/**
		 * @param programEventId
		 */
		public PostProgramCallable(int programEventId)
		{
			this.programEventId = programEventId;
		}
		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.CallableStatementCreator#createCallableStatement(java.sql.Connection)
		 */
		@Override
		public CallableStatement createCallableStatement(Connection connection)
				throws SQLException
		{
	          CallableStatement callable = connection.prepareCall("{call month_end.postProgram(?)}");
	          callable.setInt(1, programEventId);
	          return callable;
		}
	}
	/**
	 * @param programEventId
	 * @return List of CheckType objects
	 */
	public List<CheckType> getCheckStatus(int programEventId)
	{
		String checkTypesSQL =
			"select check_type_code, description from cody_check_types order by check_type_code";
		String checkResultSQL =
			"select a.check_code, " +
			"         a.check_type_code, " +
			"         a.check_name, " +
			"         a.description, " +
			"         CodyChecklist.getCheckStatus(?, a.check_code) as check_result " +
            "from cody_check_codes a, " +
            "        cody_check_types b " +
            "where a.check_type_code = b.check_type_code " +
            "and a.check_type_code = ? " +
            "order by a.check_code";
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        List<CheckType> checkTypes = jdbcTemplate.query(checkTypesSQL, new GetCheckStatusRowMapper());
        for (int i = 0; i < checkTypes.size(); i++)
        {
        	CheckType checkType = (CheckType)checkTypes.get(i);
        	List checkResults = jdbcTemplate.query(
        			checkResultSQL, 
        			new Object[] {new Integer(programEventId), checkType.getCode()}, 
        			new GetCheckResultRowMapper());
        	log.debug(checkResults.toString());
        	checkType.setCheckStatusList(checkResults);
        }
        log.debug(checkTypes.toString());
		return checkTypes;
	}
	/**
	 * @author sstuart
	 */
	private class GetCheckStatusRowMapper implements RowMapper
	{
		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			CheckType checkType = new CheckType();
			checkType.setCode(rs.getString("check_type_code"));
			checkType.setDescription(rs.getString("description"));
			return checkType;
		}
	}
	/**
	 * @author sstuart
	 */
	private class GetCheckResultRowMapper implements RowMapper
	{
		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			CheckStatus checkStatus = new CheckStatus();
			checkStatus.setCheckCode(rs.getString("check_code"));
			checkStatus.setCheckType(rs.getString("check_type_code"));
			checkStatus.setCheckName(rs.getString("check_name"));
			checkStatus.setDescription(rs.getString("description"));
			checkStatus.setCheckResult(rs.getString("check_result"));
			return checkStatus;
		}
	}
    /**
     * @param String Fielding Period
     * @param String ProgramEventId
     * @param String Username
     * @return String
     */
    public String getEndMonthStatus(final String pDate, final String pProgramEventId, final String pUser)
    {
       String result = "Posting of codes have been queued.";
       List lst = new ArrayList();
       JdbcTemplate jt = getJdbcTemplate();
       if (pDate.length() <= 2)
       {
           try {
               List params = new ArrayList();
               params.add (new SqlOutParameter("pS", Types.VARCHAR));
               // WE NEED TO GET THE PROGRAM EVENT ID BASED ON THE PROGRAM LABEL PASSED IN
               ProgramFactsDaoImpl pfd = new ProgramFactsDaoImpl();
               log.debug("Checking to see if posting "+pProgramEventId+" can happen.");
               List peid = pfd.getProgramEvent(pProgramEventId, jt);
               if (peid.size() != 0)
               {
                    log.debug("Checking to see if we can post this program: "+pDate+" "+peid.get(0));
                   
                    // We need to check to see if the user has did the Disconnect and 
                    // EndEvent checks.
                    List pst = new ArrayList(this.getCheckLists(null,pProgramEventId));
                    if (pst.size() >= 4)
                    {
                        // POST THE CODES SO RESPONSES CAN BE RENEXRACTED ON A PROGRAM LEVEL
                        log.debug("Posting codes for "+peid.get(0));
                        jt.execute("begin month_end.postProgram("+(String)peid.get(0)+"); end;");
                        result = "Posting of codes have been queued.";
                    } else
                    {
                       result = "Please conduct all checks before posting codes. Click on \"Open CheckList\" to assist you.";
                    }
               } else 
               {
                   result = "Please conduct all checks before posting codes. Click on \"Open CheckList\" to assist you.";
               }
           } catch (Exception e)
           {
               log.error(e.getMessage(), e);
           }
       } else 
       {
           try {
               List params = new ArrayList();
               params.add (new SqlOutParameter("pS", Types.VARCHAR));
               Map results = jt.call(new AuditCallableStatementCreator(pDate),params);
               result = (String)results.get("pS");
               // IF RESULTS ARE NO RECORD THEN PASS THAT BACK TO THE CALLER. USER WILL NOT BE 
               // ABLE TO POST RECORDS
               if (results ==  null)
               {
                   result = "All checks are not complete cannot post at this time.";
                   return result;
               } else
               {
                    // We need to check to see if the user has did the Disconenct and 
                    // EndEvent checks.
                    List pst = new ArrayList(this.getCheckLists(pDate,null));
                    if (pst.size() >= 4)
                    {
                        // POST THE CODES SO RESPONSES CAN BE RENEXRACTED ON A AUDIT FIELDING PERIOD LEVEL
                        log.debug("Posting codes for Audit in "+pDate);
                        jt.execute("begin cody.month_end.postAudit('"+pDate+"'); end;");  
                        result = "Posting of codes have been queued.";
                    } else
                    {
                      result = "Please conduct all checks before posting codes. Click on \"Open CheckList\" to assist you.";
                    }
                }
           } catch (Exception e)
           {
               log.error(e.getMessage(), e);
           }
       }
       return result;
    }
    /**
     * 
     */
    private class ProcCallableStatementCreator implements CallableStatementCreator 
    {
        private String a;
        private int b;       
        /**
         * @param a
         * @param b
         */
        public ProcCallableStatementCreator(String a, int b) 
        {
          this.a = a;
          this.b = b;        
        }
        /**
         * 
         */
        public CallableStatement createCallableStatement(Connection conn) throws SQLException 
        {
          CallableStatement cs = conn.prepareCall("{? = call month_end.allowPostProgram(?,?)}");
          cs.registerOutParameter (1, Types.VARCHAR);
          cs.setString(2, a);
          cs.setInt(3, b); 
          return cs;
        }
    } 
    /**
     * 
     */
    private class AuditCallableStatementCreator implements CallableStatementCreator 
    {
        private String a;
        /**
         * @param a
         */
        public AuditCallableStatementCreator(String a) 
        {
          this.a = a;         
        }
        /**
         * 
         */
        public CallableStatement createCallableStatement(Connection conn) throws SQLException 
        {
          CallableStatement cs = conn.prepareCall("{? = call month_end.allowPostAudit(?)}");
          cs.registerOutParameter (1, Types.VARCHAR);
          cs.setString(2, a);
          return cs;
        }
    }
    /**
     * @param programEventId
     * @return
     */
    public List findOpenEvents(int programEventId, String user)
    {
    	List list = new ArrayList();
    	String query = 
    		"select b.event_id, b.event_label " +
    		"from program_elements a, " +
    		"        events b " +
    		"where a.program_event_id = ? " +
    		"and a.element_event_id = b.event_id " +
    		"and status_code = ? " +
    		"order by b.event_id";
    	try
    	{
	        JdbcTemplate jt = getJdbcTemplate();
            list =  jt.query(query, new Object[] {new Integer(programEventId), "O"}, new ProgramFactsMapper());
	        
	        if (list.size() > 0)
	        {  // set check as failed in check list
	        	setCheckStatus(programEventId, CHECK_CODE_DISCONNECTED, CHECK_STATUS_FAILED, user);
	        } else
	        { // set check as succeeded
	        	setCheckStatus(programEventId, CHECK_CODE_DISCONNECTED, CHECK_STATUS_SUCCESS, user);
	        }
	    } catch (Exception e)
	    {
	        log.error(e.getMessage(), e);
	    }                
	    return list;
    }
    /**
     * 
     * @param programEventId
     * @return
     */
    public List findDataDisconnectedDate(int programEventId, String user)
    {
    	List list = new ArrayList();
    	String query = 
    		"select b.event_id, b.event_label " +
    		"from program_elements a, " +
    		"        events b " +
    		"where a.program_event_id = ? " +
    		"and a.element_event_id = b.event_id " +
    		"and data_cutoff_datetime is null " +
    		"order by b.event_id";
    	try
    	{
	        JdbcTemplate jt = getJdbcTemplate();
            list =  jt.query(query, new Object[] {new Integer(programEventId)}, new ProgramFactsMapper());
	        
	        if (list.size() > 0)
	        {  // set check as failed in check list
	        	setCheckStatus(programEventId, CHECK_CODE_END_EVENT, CHECK_STATUS_FAILED, user);
	        } else
	        { // set check as succeeded
	        	setCheckStatus(programEventId, CHECK_CODE_END_EVENT, CHECK_STATUS_SUCCESS, user);
	        }
	    } catch (Exception e)
	    {
	        log.error(e.getMessage(), e);
	    }                
	    return list;
    }
    /**
     * @author sstuart
     */
    private class ProgramFactsMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException {
	        ProgramFacts pf = new ProgramFacts();
	        pf.setProgramLabel(rs.getString("event_label"));
	        pf.setEventId(rs.getString("event_id"));
	        return pf;
	      }          
    }
    /**
     * @param programEventId
     * @param checkCode
     * @param statusCode
     * @param user
     */
    public void setCheckStatus(int programEventId, String checkCode, String statusCode, String user)
    {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        jdbcTemplate.call(new SetCheckStatusCallable(programEventId, checkCode, statusCode, user), new ArrayList());
    }
    /**
     * @param fieldingPeriod
     * @param checkCode
     * @param statusCode
     * @param user
     */
    public void setCheckStatus(Date fieldingPeriod, String checkCode, String statusCode, String user)
    {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        jdbcTemplate.call(new SetCheckStatusCallable(fieldingPeriod, checkCode, statusCode, user), new ArrayList());
    }
    /**
     * @author sstuart
     */
    private class SetCheckStatusCallable implements CallableStatementCreator
    {
    	private int programEventId;
    	private Date fieldingPeriod;
    	private String checkCode;
    	private String checkStatus;
    	private String user;
    	/**
    	 * @param programEventId
    	 * @param checkCode
    	 * @param checkStatus
    	 * @param user
    	 */
		public SetCheckStatusCallable(int programEventId, String checkCode,
				String checkStatus, String user)
		{
			this.programEventId = programEventId;
			this.checkCode = checkCode;
			this.checkStatus = checkStatus;
			this.user = user;
		}
		/**
		 * @param fieldingPeriod
		 * @param checkCode
		 * @param checkStatus
		 * @param user
		 */
		public SetCheckStatusCallable(Date fieldingPeriod, String checkCode,
				String checkStatus, String user)
		{
			this.fieldingPeriod = fieldingPeriod;
			this.checkCode = checkCode;
			this.checkStatus = checkStatus;
			this.user = user;
		}
		@Override
		public CallableStatement createCallableStatement(Connection connection)
				throws SQLException
		{
	          CallableStatement callable = connection.prepareCall("{call CodyChecklist.setCheckStatus(?, ?, ?, ?)}");
	          if (fieldingPeriod != null)
	          {
		          callable.setDate(1, new java.sql.Date(fieldingPeriod.getTime()));
	          } else
	          {
		          callable.setInt(1, programEventId);
	          }
	          callable.setString(2, checkCode);
	          callable.setString(3, checkStatus);
	          callable.setString(4, user);
	          return callable;
		}
    }
    /**
     * @param programEventId
     * @return
     */
    public String checkDisconnectByProgram(int programEventId)
    {
    	String result = null;
    	String sql = 
    		"select event_id "+
    		"from events " +
    		"where event_id in (select program_event_id  from program_facts where program_event_id = ?) " +
    		"and status_code = 'O' " +
    		"and event_type = 'S'";
    	try
    	{
	        JdbcTemplate jt = getJdbcTemplate();
	        log.debug("#checkDisconnect :: "+sql);
            List list =  jt.queryForList(sql, new Object[] {new Integer(programEventId)});
	        
	        // If the list size is greater than zero that means not all the programs
	        // have been disconnected. A recruiter has to be notified to close the
	        // program.
	        if (list.size() > 0)
	        {
	            result = "Please contact one of the recruiters to disconnect the program.";
	        } else
	        {
	        	ProgramFactsDaoImpl dao = new ProgramFactsDaoImpl();
	        	dao.setJdbcTemplate(jt);
	        	String label = dao.getProgramLabel(programEventId);
	            result = "All programs have been disconnected.";
	            this.insertCheckList(null,"Disconnect",label);
	        }
	    } catch (Exception e)
	    {
	        log.error("#checkDisconnect "+e.getMessage(), e);
	    }                
	    return result;
    }
    /**
     * @param String Date
     * @param String Study Type
     * @param String Program Label
     * @return String
     */
    public String checkDisconnect(final String pDate, final String pType, final String pProgram)
    {
        String result = "";
        String sql = "";
        // If what we are checking is an Audit then we need to do this by the fielding period
        // otherwise we need to do this by program label.
        log.debug("Incoming type to checkDisconnect "+pType);
        if (pType.equalsIgnoreCase("AUDIT"))
        {
            sql = "select event_id"+
            " from events where event_id in "+
            " (select program_event_id "+
            " from program_facts where fielding_period = to_date('"+pDate+"','mm/dd/yyyy')"+
            " and study_type_code = '"+pType+"')"+
            " and status_code = 'O' and event_type = 'S'";
        } else
        {
            sql = "select event_id"+
            " from events where event_id in "+
            " (select program_event_id  from program_facts where"+
            " program_label = '"+pProgram+"')"+
            " and status_code = 'O' and event_type = 'S'";            
        }
        try
        {
            JdbcTemplate jt = getJdbcTemplate();
            log.debug("#checkDisconnect :: "+sql);
            List lst = jt.queryForList(sql);
            
            log.debug("LIST COUNT "+lst.size());
            // If the list size is greater than zero that means not all the programs
            // have been disconnected. A recruiter has to be notified to close the
            // program.
            if (lst.size() > 0)
            {
                result = "Please contact one of the recruiters to disconnect the program.";
            } else
            {
                result = "All programs have been disconnected.";
                if (pType.equalsIgnoreCase("AUDIT"))
                {
                    this.insertCheckList(pDate,"Disconnect",null);
                } else
                {
                    this.insertCheckList(null,"Disconnect",pProgram);
                }
            }
        } catch (Exception e)
        {
            log.error("#checkDisconnect "+e.getMessage(), e);
        }                
        return result;
    }
    /**
     * @param String Date
     * @param String Study Type
     * @param String Program Label
     * @return String
     */
    public String checkEndEvent(final String pDate, final String pType, final String pProgram)
    {
        String result = "";
        String sql = "";
        log.debug("=====> "+pType);
        if (pType.equalsIgnoreCase("AUDIT"))
        {
            sql = "select event_id from events"+
                " where event_id in ("+
                " select program_event_id from program_facts"+
                " where fielding_period = to_date('"+pDate+"','mm/dd/yyyy') and study_type_code = 'AUDIT')"+
                " and end_datetime is null";
        } else
        {
            sql = "select event_id from events"+
                " where event_id in ("+
                " select program_event_id from program_facts"+
                " where program_label = '"+pProgram+"')"+
                " and end_datetime is null";
        }
        try
        {
            JdbcTemplate jt = getJdbcTemplate();
            log.debug("#checkEndEvent :: "+sql);
            List lst = jt.queryForList(sql);
            // If the list size is greater than zero that means not all the programs
            // have been disconnected. A recruiter has to be notified to close the
            // program.
            if (lst.size() > 0)
            {
                result = "Please contact one of the recruiters to disconnect the program.";
            } else
            {
                result = "All programs have an End Event.";
                if (pType.equalsIgnoreCase("AUDIT"))
                {
                    this.insertCheckList(pDate,"EndEvent",null);
                } else
                {
                    this.insertCheckList(null,"EndEvent",pProgram);
                }
            }
        } catch (Exception e)
        {
            log.error("#checkEndEvent "+e.getMessage(), e);
        }            
        return result;
    }
    /**
     * @param String Fielding Date
     * @param String Check Code
     * @param String Program Label
     * Insert a row CODY_CHECKLIST to make sure that the coders can post the codes
     */
    public void insertCheckList(String pDate, String pCheckCode, String pProgram)
    {
        insertCheckList(pDate, pCheckCode, pProgram, null);
    }
    /**
     * @param pDate
     * @param pCheckCode
     * @param pProgram
     * @param jdbcTemplate
     */
    public void insertCheckList(final String pDate, final String pCheckCode, final String pProgram, final JdbcTemplate jdbcTemplate)
    {
        String sql = "";
        JdbcTemplate jt = (jdbcTemplate == null) ? getJdbcTemplate() : jdbcTemplate;
        // If pDate is not equal to null that means we are inserting for a Audit
        if (pDate != null)
        {
            sql = "insert into CODY_CHECKLIST (fielding_period, check_code, check_value, added_datetime) values ("+
                    " to_date('"+pDate+"','mm/dd/yyyy'), '"+pCheckCode+"','true',sysdate)";
        } else
        {
            sql = "insert into CODY_CHECKLIST (program_label, check_code, check_value, added_datetime) values ("+
                    " '"+pProgram+"', '"+pCheckCode+"','true',sysdate)";
        }
        try
        {
            log.debug("Inserting into CheckList :: "+sql);
            jt.execute(sql);
        } catch (Exception e)
        {
            log.error("#insertCheckList :: "+e.getMessage(), e);
        }
    }
    /**
     * @param String Date
     * @param String Program
     * @return Collection
     */    
    public Collection getCheckLists(String pDate, String pProgram)
    {
        String sql = "";
        JdbcTemplate jt = getJdbcTemplate();
        // Id pDate is not null that means the check is gonig to Audit related. 
        if (pDate != null)
        {
            sql = "select distinct check_code from CODY_CHECKLIST where fielding_period = to_date('"+pDate+"','mm/dd/yyyy') and (check_code = 'Disconnect' or check_code = 'EndEvent' or check_code = 'Check Count' or check_code = 'Check/Verify')";
        } else
        {
            sql = "select distinct check_code from CODY_CHECKLIST where program_label = '"+pProgram+"' and (check_code = 'Disconnect' or check_code = 'EndEvent' or check_code = 'Check Count' or check_code = 'Check/Verify')";
        }
        log.debug("#getCheckList :: "+sql);
        return jt.query(sql,new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                CheckList ck = new CheckList();
                ck.setCheckValue(rs.getString("check_code"));
                return ck;
            }
        });        
    }  
    /**
     * Returns a list of CheckList objects
     * @return List CheckList Objects
     */
    public List getCompleteCheckList()
    {
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select to_char(fielding_period,'mm/dd/yyyy'), program_label, check_code, check_value "+
                " from cody_checklist order by fielding_period, program_label, check_code";
        log.debug("#getCompleteCheckList :: "+sql);
        return jt.query(sql,new RowMapper() {
           public Object mapRow(ResultSet rs, int rowNum) throws SQLException
           {
               CheckList ck = new CheckList();
               ck.setFieldingPeriod(rs.getString(1));
               ck.setProgramLabel(rs.getString(2));
               ck.setCheckName(rs.getString(3));
               ck.setCheckValue(rs.getString(4));               
               return ck;
           }
        });
    }
    /**
     * @param programEventId
     * @return
     */
    public String checkCodyCount(int programEventId, String user) throws Exception
    {
    	String result = null;
    	Integer checkId = null;
    	Integer checkCount = null;
    	List<SqlOutParameter> checkParameters = new ArrayList();
    	List<SqlOutParameter> countParameters = new ArrayList();
    	Map<String, Object> checkMap = null;
    	Map<String, Object> countMap = null;
    	JdbcTemplate jdbcTemplate = getJdbcTemplate();
    	try
    	{
    		checkParameters.add(null);
    		checkParameters.add(new SqlOutParameter("checkId", Types.INTEGER));
    		checkMap = jdbcTemplate.call(new CodyResponseCheckerCreator(programEventId), checkParameters);
    		checkId = (Integer)checkMap.get("checkId");
    		countParameters.add(new SqlOutParameter("checkCount", Types.INTEGER));
    		countMap = jdbcTemplate.call(new GetCheckCountCreator(checkId), countParameters);
    		checkCount = (Integer)countMap.get("checkCount");
    		if (checkCount.intValue() == 0)
    		{
    			result = "<span style=\"color: green;\">Check returned successfully</span>";
    	    	setCheckStatus(programEventId, CHECK_CODE_CHECK_COUNT, CHECK_STATUS_SUCCESS, user);
    		} else
    		{
    			result = "<span style=\"color: red;\">Check found " + checkCount + " records not exported to Cody</span>";
    	    	setCheckStatus(programEventId, CHECK_CODE_CHECK_COUNT, CHECK_STATUS_FAILED, user);
    		}
    	} catch (Exception e)
    	{
    		log.error(e.getMessage(), e);
    		throw e;
    	}
    	return result;
    }
    /**
     * @author sstuart
     */
    private class CodyResponseCheckerCreator implements CallableStatementCreator 
    {
    	private int programEventId;
    	/**
    	 * @param programEventId
    	 */
        public CodyResponseCheckerCreator(int programEventId)
		{
			this.programEventId = programEventId;
		}
		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.CallableStatementCreator#createCallableStatement(java.sql.Connection)
		 */
		@Override
		public CallableStatement createCallableStatement(Connection conn) 
				throws SQLException 
        {
          CallableStatement callable = conn.prepareCall("{ call CodyResponseChecker.executeProgramChecks(?, ?) }");
          callable.setInt(1, programEventId);
          callable.registerOutParameter (2, Types.INTEGER);
          return callable;
        }
    }
    /**
     * @author sstuart
     */
    private class GetCheckCountCreator implements CallableStatementCreator
    {
    	private int checkId;
    	/**
    	 * @param checkId
    	 */
		public GetCheckCountCreator(int checkId)
		{
			this.checkId = checkId;
		}
		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.CallableStatementCreator#createCallableStatement(java.sql.Connection)
		 */
		@Override
		public CallableStatement createCallableStatement(Connection connection)
				throws SQLException
		{
			CallableStatement callable = connection.prepareCall("{ ? = call CodyResponseChecker.getCheckCount(?) }");
			callable.registerOutParameter(1, Types.INTEGER);
			callable.setInt(2, checkId);
			return callable;
		}
    }
    /**
     * Return String result in whether you Response Facts and Oec Response
     * Staging match as far as number of records.
     * @param String pDate
     * @param String pProgram
     * @return String
     */
    public String matchFactsToStaging(String pDate, String pProgram)
    {
        String result = "";
        JdbcTemplate jt = getJdbcTemplate();
        // If pProgram is not null then lets get the event id
        ProgramFactsDaoImpl pf = new ProgramFactsDaoImpl();
        List params = new ArrayList();
        int eventId = 0;
        //log.debug("===============> "+pProgram);
        // This is how we handle individual programs count
        if (pProgram != null)
        {
            eventId = pf.findProgramEventEventId(pProgram, jt);
            List pne = pf.findEventsByProgram(Integer.toString(eventId), jt);
            log.debug("PEID ==== "+eventId);
            //params.add (new SqlOutParameter("pS", Types.VARCHAR));
            //Map results = jt.call(new CheckCallableStatementCreator(Integer.parseInt(pProgram),eventId),params);
            //result = (String)results.get("pS");
            for (int i=0; i < pne.size(); i++)
            {
                ProgramFacts pfs = (ProgramFacts) pne.get(i);
                params.add (new SqlOutParameter("pS", Types.VARCHAR));
                //log.debug("? = call oec_response_util.oec_get_count("+eventId+","+pfs.getEventId()+")");
                log.debug("? = call oec_response_util.oec_get_count("+pfs.getProgramEvent()+","+pfs.getEventId()+")");
                //Map results = jt.call(new CheckCallableStatementCreator(eventId,Integer.parseInt(pfs.getEventId())),params);
                Map results = jt.call(new CheckCallableStatementCreator(Integer.parseInt(pfs.getProgramEvent()),Integer.parseInt(pfs.getEventId())),params);
                result = (String)results.get("pS");
                //log.debug("RESULT FROM CALL :: "+result);
                if (result.equalsIgnoreCase("0")) { return result; }
                params.clear();
            }
        } else
        // This is how we handle the Audit count.
        {
            List pne = pf.findProgramAndEvents(pDate, jt);
            for (int i=0; i < pne.size(); i++)
            {
                ProgramFacts pfs = (ProgramFacts) pne.get(i);
                params.add (new SqlOutParameter("pS", Types.VARCHAR));
                log.debug("? = call oec_response_util.oec_get_count("+pfs.getProgramEvent()+","+pfs.getEventId()+")");
                Map results = jt.call(new CheckCallableStatementCreator(Integer.parseInt(pfs.getProgramEvent()),Integer.parseInt(pfs.getEventId())),params);
                result = (String)results.get("pS");
                //log.debug("RESULT FROM CALL :: "+result);
                if (result.equalsIgnoreCase("0")) { return result; }
                params.clear();
            }
        }
        params = null;
        pf = null;
        if (result.equalsIgnoreCase("1"))
        {
            this.insertCheckList(pDate,"Check Count",pProgram);
        }
        return result;
    }
    private class CheckCallableStatementCreator implements CallableStatementCreator 
    {
        private int a;
        private int b;       
    
        public CheckCallableStatementCreator(int a, int b) 
        {
          this.a = a;
          this.b = b;        

        }

        public CallableStatement createCallableStatement(Connection conn) throws SQLException 
        {
          CallableStatement cs = conn.prepareCall("{? = call oec_response_util.oec_get_count(?,?)}");
          cs.registerOutParameter (1, Types.VARCHAR);
          cs.setInt(2, a);
          cs.setInt(3, b); 
          
          return cs;
        }
    }
}
