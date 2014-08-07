package com.targetrx.project.oec.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.targetrx.project.oec.bo.Cell;
import com.targetrx.project.oec.bo.OecTagTypes;
import com.targetrx.project.oec.bo.Groups;
import com.targetrx.project.oec.bo.MonthYear;
import com.targetrx.project.oec.bo.Parameters;
import com.targetrx.project.oec.bo.ProgramFacts;
import com.targetrx.project.oec.bo.Response;
import com.targetrx.project.oec.bo.Market;
import com.targetrx.project.oec.bo.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author pkukk
 */
public class ProgramFactsDaoImpl extends JdbcDaoSupport implements ProgramFactsDao
{
    private Logger log = Logger.getLogger(this.getClass());
    /**
     * @param programEventId
     * @return
     */
    public String getProgramLabel(int programEventId)
    {
        List list = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select program_label from program_facts where program_event_id = ?";
        try 
        {
            list = jt.query(sql, new Object[] {new Integer(programEventId)}, new RowMapperResultReader(new ProgramLabelMapper2()));
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return (String)list.get(0);
    }
    /**
     * @author sstuart
     */
    class ProgramLabelMapper2 implements RowMapper
    {
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			return rs.getString("program_label");
		}
    }
    /**
     * @return List
     * Method returns distinct Study Type Codes
     */
    public List getAllStudyTypes()
    {
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select distinct study_type_code from program_facts order by study_type_code asc";
        try 
        {
            lst =  jt.query(sql,new RowMapperResultReader(new StudyTypeMapper()));
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return lst;
    }
    class StudyTypeMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException {
	        ProgramFacts pf = new ProgramFacts();
	        pf.setProgramLabel("");
	        pf.setStudyTypeCode(rs.getString(1));
	        return pf;
	      }          
    }
    /**
     * @param String Study Type Code
     * @return Map
     * Method gets all the program labels and their events 
     */
    public Map getAllProgramLabelsByType(String s, String pDate)
    {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Object obj = new ArrayList();
        JdbcTemplate jdbctemplate = getJdbcTemplate();
        String s1 = "select distinct program_label, program_event_id, fielding_period from inventory.program_facts where study_type_code = '" + s + "' "+
                " and fielding_period >= to_date('"+pDate+"','mm/dd/yyyy') order by program_label, fielding_period desc";
        //System.out.println(s1);
        try
        {
            obj = jdbctemplate.query(s1, new RowMapperResultReader(new ProgramLabelMapper()));
        }
        catch(Exception exception)
        {
            log.error("SQL:: " + s1 + "\n" + exception.getMessage(), exception);
        }
        for(int i = 0; i < ((List) (obj)).size(); i++)
        {
            ProgramFacts programfacts = (ProgramFacts)((List) (obj)).get(i);
            linkedhashmap.put(programfacts.getProgramEvent(), programfacts.getProgramLabel());
        }

        return linkedhashmap;
    }
    public Map getAllProgramLabelsByType(String s, String pDate, String pEndDate)
    {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Object obj = new ArrayList();
        JdbcTemplate jdbctemplate = getJdbcTemplate();
        String s1 = "select distinct program_label, program_event_id, fielding_period from inventory.program_facts where study_type_code = '" + s + "' "+
                " and fielding_period >= to_date('"+pDate+"','mm/dd/yyyy') and fielding_period <= to_date('"+pEndDate+"','mm/dd/yyyy') order by program_label, fielding_period desc";
        System.out.println(s1);
        try
        {
            obj = jdbctemplate.query(s1, new RowMapperResultReader(new ProgramLabelMapper()));
        }
        catch(Exception exception)
        {
            log.error("SQL:: " + s1 + "\n" + exception.getMessage(), exception);
        }
        for(int i = 0; i < ((List) (obj)).size(); i++)
        {
            ProgramFacts programfacts = (ProgramFacts)((List) (obj)).get(i);
            linkedhashmap.put(programfacts.getProgramEvent(), programfacts.getProgramLabel());
        }

        return linkedhashmap;
    }
    public Map getAllProgramLabelsByTypeAnDate(String pCells, String s, String pMarkets, String pProducts, String pBeginDate, String pEndDate)
    {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Object obj = new ArrayList();
        String cells = "";
        if (pCells.indexOf(",") > -1)
        {
            StringTokenizer st = new StringTokenizer(pCells, ",");
            while (st.hasMoreTokens())
            {
                cells = cells+"'"+(String) st.nextToken()+"',";
            }
            cells = cells.substring(0,cells.length()-1);
        } else
        {
            cells = "'"+pCells+"'";
        }
        JdbcTemplate jdbctemplate = getJdbcTemplate();
        String endSql = ") order by 1";
        /**String historySql = " union"+
                " select distinct vpf.program_label, vpf.program_event_id"+
                " from cody.oec_response_staging_history orsh,"+
                "    program_facts vpf,"+
                "    person_facts perf"+
                " where orsh.added_datetime >= to_date('"+pBeginDate+"','mm/dd/yyyy')"+
                " and orsh.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
                " and orsh.par1_parameter_id in ("+pMarkets+")"+
                " and orsh.par2_parameter_id in ("+pProducts+")"+
                " and vpf.study_type_code = '"+s+"'"+
                " and vpf.program_event_id = orsh.program_event_id"+
                " and vpf.oec_status_code = 'I'"+
                " and perf.program_event_id = orsh.program_event_id"+
                " and perf.event_id = orsh.event_id"+
                " and perf.targetrx_id = orsh.targetrx_id"+
                " and perf.cell_code1 in ("+cells+")";
         */
        String historySql = " union"+ 
                " select  program_label, program_event_id"+
                " from program_facts pf"+
                " where pf.study_type_code = '"+s+"'"+
                " and pf.oec_status_code = 'I'"+
                " and exists (select /*+ index(perf PK_PERSON_FACTS) */ 1"+
                "               from oec_response_staging orsh, person_facts perf"+
                "              where pf.program_event_id = orsh.program_event_id"+
                "                and orsh.par1_parameter_id in ("+pMarkets+")"+
                "                and orsh.par2_parameter_id in ("+pProducts+")"+
                "                and orsh.added_datetime >= to_date('"+pBeginDate+"','mm/dd/yyyy')"+
                "                and orsh.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
                "                and orsh.program_event_id = perf.program_event_id"+
                "                and orsh.event_id = perf.event_id"+
                "                and orsh.targetrx_id = perf.targetrx_id"+
                "                and perf.cell_code1 in ("+cells+"))";
        /*String sq1 = "select program_label, program_event_id from ( select distinct pf.program_label, vpf.program_event_id"+
                " from cody.oec_response_staging ors,"+
                "    program_facts vpf,"+
                "    inventory.program_facts pf, person_facts perf"+
                " where ors.added_datetime >= to_date('"+pBeginDate+"','mm/dd/yyyy')"+
                " and ors.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
                " and ors.par1_parameter_id in ("+pMarkets+")"+
                " and ors.par2_parameter_id in ("+pProducts+")"+
                " and vpf.study_type_code = '"+s+"'"+
                " and vpf.program_event_id = ors.program_event_id"+
                " and vpf.oec_status_code = 'I'"+
                " and pf.program_event_id = ors.program_event_id "+
                " and perf.program_event_id = ors.program_event_id"+
                " and perf.event_id = ors.event_id"+
                " and perf.targetrx_id = ors.targetrx_id"+
                " and perf.cell_code1 in ("+cells+")";
         **/
        String sq1 = "select program_label, program_event_id from (select  program_label, program_event_id"+
                " from program_facts pf"+
                " where pf.study_type_code = '"+s+"'"+
                " and pf.oec_status_code = 'I'"+
                " and exists (select /*+ index(perf PK_PERSON_FACTS) */ 1"+
                "               from oec_response_staging ors, person_facts perf"+
                "              where pf.program_event_id = ors.program_event_id"+
                "                and ors.par1_parameter_id in ("+pMarkets+")"+
                "                and ors.par2_parameter_id in ("+pProducts+")"+
                "                and ors.added_datetime >= to_date('"+pBeginDate+"','mm/dd/yyyy')"+
                "                and ors.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
                "                and ors.program_event_id = perf.program_event_id"+
                "                and ors.event_id = perf.event_id"+
                "                and ors.targetrx_id = perf.targetrx_id"+
                "                and perf.cell_code1 in ("+cells+"))";
                
        boolean data = isThereData(pBeginDate);
        if (data)
        {
            sq1 = sq1+historySql+endSql;
        } else
        {
            sq1 = sq1+endSql;
        }
        try
        {
            //System.out.println(sq1);
            obj = jdbctemplate.query(sq1, new RowMapperResultReader(new ProgramLabelMapper()));
        }
        catch(Exception exception)
        {
            log.error("SQL:: " + sq1 + "\n" + exception.getMessage(), exception);
        }
        for(int i = 0; i < ((List) (obj)).size(); i++)
        {
            ProgramFacts programfacts = (ProgramFacts)((List) (obj)).get(i);
            linkedhashmap.put(programfacts.getProgramEvent(), programfacts.getProgramLabel());
        }

        return linkedhashmap;
    }
    /**
     * @param String Study Type Code
     * @return Map
     * Method gets all the program labels and their events 
     */
    public Map getAllProgramLabels(String s)
    {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Object obj = new ArrayList();
        JdbcTemplate jdbctemplate = getJdbcTemplate();
        String s1 = "select distinct program_label, program_event_id from program_facts where study_type_code = '" + s + "' order by program_label";
        try
        {
            obj = jdbctemplate.query(s1, new RowMapperResultReader(new ProgramLabelMapper()));
        }
        catch(Exception exception)
        {
            log.error("SQL:: " + s1 + "\n" + exception.getMessage(), exception);
        }
        for(int i = 0; i < ((List) (obj)).size(); i++)
        {
            ProgramFacts programfacts = (ProgramFacts)((List) (obj)).get(i);
            linkedhashmap.put(programfacts.getProgramEvent(), programfacts.getProgramLabel());
        }

        return linkedhashmap;
    }
    class ProgramLabelMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException {
	        ProgramFacts pf = new ProgramFacts();
	        pf.setProgramLabel(rs.getString(1));
	        pf.setProgramEvent(rs.getString(2));
	        return pf;
	      }          
    }
    
    /**
     * @return Map
     */
    public Map getProgramFacts()
    {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Object obj = new ArrayList();
        JdbcTemplate jdbctemplate = getJdbcTemplate();
        String s = "select distinct oec_response_util.get_oec_file_prefix(program_label,";
        s = s + " fielding_period, supplement_code, study_type_code), study_type_code from program_facts";
        s = s + " where oec_status_code = 'A'";
        try
        {
            obj = jdbctemplate.query(s, new RowMapperResultReader(new ProgramRowMapper()));
        }
        catch(Exception exception)
        {
            log.error("SQL:: " + s + "\n" + exception.getMessage(), exception);
        }
        for(int i = 0; i < ((List) (obj)).size(); i++)
        {
            ProgramFacts programfacts = (ProgramFacts)((List) (obj)).get(i);
            linkedhashmap.put(programfacts.getProgramLabel(), programfacts.getProgramLabel());
        }

        return linkedhashmap;
    }
    class ProgramRowMapper implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        ProgramFacts pf = new ProgramFacts();
	        pf.setProgramLabel(rs.getString(1));
	        pf.setStudyTypeCode(rs.getString(2));
	        return pf;
	      }      
	  }//class CodeRowMapper
    
    // Returns a list of StudyType Codes
    public List getStudyType(final String pProgramLabel)
    {
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = " select program_label, study_type_code from program_facts where ";
        sql = sql+" program_label = '"+pProgramLabel+"'";
        lst =  jt.query(sql, new RowMapperResultReader(new ProgramRowMapper()));
        return lst;
    }
    /**
     * @param String
     * @return Map
     */
    public Map getGroupName(String s, String s1, String s2)
    {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Object obj = new ArrayList();
        JdbcTemplate jdbctemplate = getJdbcTemplate();
        String s3 = "";
        s3 = "select a.question_id,";
        s3 = s3 + " CASE ";
        s3 = s3 + "   WHEN GROUP_QUESTION_LABEL IS NOT NULL THEN";
        s3 = s3 + " 'W'||group_question_label || ' / Q' ||survey_question_label";
        s3 = s3 + " ELSE";
        s3 = s3 + "      'Q'||survey_question_label";
        s3 = s3 + " END || ' ('||count(*)||')' as label";
        s3 = s3 + " from   oec_response_staging  a";
        s3 = s3 + " where  a.program_event_id in (select program_event_id  ";
        s3 = s3 + "     from   program_facts  ";
        s3 = s3 + "     where  (study_type_code <> 'AUDIT'";
        s3 = s3 + "     AND";
        s3 = s3 + "     program_label = '" + s + "')";
        s3 = s3 + "     OR  ";
        s3 = s3 + "     (study_type_code = 'AUDIT'";
        s3 = s3 + "     AND  ";
        s3 = s3 + "     supplement_code = 'C'";
        s3 = s3 + "     AND  ";
        s3 = s3 + "     program_label = '" + s + "')  ";
        s3 = s3 + "     OR   ";
        s3 = s3 + "     (study_type_code = 'AUDIT'";
        s3 = s3 + "     AND  ";
        s3 = s3 + "     supplement_code <> 'C'";
        s3 = s3 + "     AND  ";
        s3 = s3 + "     fielding_period =    get_fielding_period('" + s + "')))";
        s3 = s3 + " AND lock_flag = 'N'";
        s3 = s3 + " AND status_code = 'new'";
        if(s2.equalsIgnoreCase("false"))
            s3 = s3 + " AND tag_type_code IS NOT NULL";
        else
            s3 = s3 + " AND tag_type_code IS NULL";
        s3 = s3 + " group by";
        s3 = s3 + " a.question_id,";
        s3 = s3 + " CASE ";
        s3 = s3 + "     WHEN GROUP_QUESTION_LABEL IS NOT NULL THEN";
        s3 = s3 + "     'W'||group_question_label || ' / Q' ||survey_question_label";
        s3 = s3 + " ELSE";
        s3 = s3 + "      'Q'||survey_question_label";
        s3 = s3 + " END";
        s3 = s3 + " order by label";
        log.debug("SQL #getGroupName: "+s3);
        try
        {
            obj = jdbctemplate.query(s3, new RowMapperResultReader(new ReponseRowMapper()));
        }
        catch(Exception exception)
        {
            log.error("SQL:: " + s3 + "\n" + exception.getMessage(), exception);
        }
        for(int i = 0; i < ((List) (obj)).size(); i++)
        {
            Response response = (Response)((List) (obj)).get(i);
            linkedhashmap.put(response.getQuestionId(), response.getSurveyQuestionLabel());
        }

        return linkedhashmap;
    }
    class ReponseRowMapper implements RowMapper
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        Response resp = new Response();
	        resp.setSurveyQuestionLabel(rs.getString(2));
          resp.setQuestionId(rs.getString(1));
	        return resp;
	      }       
	  }
    /**
     * @param String
     * @param String
     * @return Map
     */
   public Map getMarkets(String s, String s1)
    {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Object obj = new ArrayList();
        JdbcTemplate jdbctemplate = getJdbcTemplate();
        String s2 = "";
        s2 = "select distinct a.question_id,";
        s2 = s2 + " b.parameter_no,";
        s2 = s2 + " initcap(b.category_code)";
        s2 = s2 + " from  oec_response_staging  a,";
        s2 = s2 + " question_parameters b";
        s2 = s2 + " where  a.question_id = " + s1;
        s2 = s2 + " and    a.program_event_id in (select program_event_id ";
        s2 = s2 + "     from   program_facts ";
        s2 = s2 + "     where  (study_type_code <> 'AUDIT'";
        s2 = s2 + "     AND";
        s2 = s2 + "     program_label = '" + s + "')";
        s2 = s2 + "     OR";
        s2 = s2 + "     (study_type_code = 'AUDIT'";
        s2 = s2 + "     AND";
        s2 = s2 + "     supplement_code = 'C'";
        s2 = s2 + "     AND";
        s2 = s2 + "     program_label = '" + s + "')";
        s2 = s2 + "     OR";
        s2 = s2 + "     (study_type_code = 'AUDIT'";
        s2 = s2 + "     AND";
        s2 = s2 + "     supplement_code <> 'C'";
        s2 = s2 + "     AND";
        s2 = s2 + "     fielding_period =  get_fielding_period('" + s + "')))";
        s2 = s2 + " and    a.question_id = b.question_id ";
        s2 = s2 + " order by ";
        s2 = s2 + " b.parameter_no asc";
        try
        {
            obj = jdbctemplate.query(s2, new RowMapperResultReader(new ParameterRowMapper()));
        }
        catch(Exception exception)
        {
            log.error("SQL:: " + s2 + "\n" + exception.getMessage(), exception);
        }
        for(int i = 0; i < ((List) (obj)).size(); i++)
        {
            Parameters parameters = (Parameters)((List) (obj)).get(i);
            linkedhashmap.put(parameters.getQuestionId() + "~" + parameters.getParameterNo(), parameters.getInitCap());
        }

        return linkedhashmap;
    }
    class ParameterRowMapper implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        //String temp = rs.getString(1);
          Parameters pars = new Parameters();
          pars.setQuestionId(rs.getString(1));
          //pars.setParameterId(rs.getString(4));
          pars.setParameterNo(rs.getString(2));
          pars.setInitCap(rs.getString(3));
	        return pars;
	      }      
	  }
    /**
     * @param s
     * @param jdbcTemplate
     * @return
     */
    public List getProgramEvent(final String s, final JdbcTemplate jdbcTemplate)
    {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Object obj = new ArrayList();
        JdbcTemplate jt = (jdbcTemplate == null) ? getJdbcTemplate() : jdbcTemplate;
        String s1 = "select DISTINCT q.program_event_id";
        s1 = s1 + " from program_facts p, program_oec_questions q";
        s1 = s1 + " where  p.program_event_id = q.program_event_id";
        s1 = s1 + " and oec_response_util.get_oec_file_prefix(program_label, fielding_period, supplement_code, study_type_code) = '" + s + "'";
        s1 = s1 + " and p.oec_status_code = 'A'";
        try
        {
            obj = jt.query(s1, new RowMapperResultReader(new StringRowMapper()));
        }
        catch(Exception exception)
        {
            log.error("SQL:: " + s1 + "\n" + exception.getMessage(), exception);
        }
        return ((List) (obj));
    }
    /**
     * @see com.targetrx.project.oec.service.ProgramFactsDao#getProgramEvent(java.lang.String)
     */
    public List getProgramEvent(String s)
    {
        return getProgramEvent(s, null);
    }
    /**
     * @param String
     * @return Map
     */
    public Map getProgramEvents(final String pProgramLabel)
    {
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select DISTINCT q.program_event_id";
        sql = sql +" from program_facts p, program_oec_questions q";
        sql = sql +" where  p.program_event_id = q.program_event_id";
        sql = sql +" and oec_response_util.get_oec_file_prefix(program_label, fielding_period, supplement_code, study_type_code) = '"+pProgramLabel+"'";
        sql = sql +" and p.oec_status_code = 'A'";
        try 
        {
            lst =  jt.query(sql, new RowMapperResultReader(new StringRowMapper()));
            for (int i=0; i<lst.size(); i++)
            {
                mp.put(lst.get(i),lst.get(i));            
            }
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
         
        return mp;
    }
    class StringRowMapper implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        String temp = rs.getString(1);
          return temp;
	      }      
	  }
    /**
     * @param String
     * @param String
     * @return List
     */
    public List getGroupList(final String pProgramLabel, final String pParameter, final String pQuestionId, final String pTag)
    {
        List lst = new ArrayList();
        String sql = "";
        JdbcTemplate jt = getJdbcTemplate();
        if (pParameter.equalsIgnoreCase("Please select a Parameter."))
        {
            sql = "select a.program_event_id,";
            sql = sql+" CASE";
            sql = sql+" WHEN GROUP_QUESTION_LABEL IS NOT NULL THEN";
            sql = sql+"      'W'||group_question_label || ' / Q' ||survey_question_label";
            sql = sql+" ELSE";
            sql = sql+"     'Q'||survey_question_label";
            sql = sql+" END as label,";
            sql = sql+" a.question_id,";
            sql = sql+" count(*)";
            sql = sql+"  from   oec_response_staging a";
            sql = sql+"  where  a.question_id = "+pQuestionId;
            sql = sql+"  and    a.program_event_id in (select program_event_id";
            sql = sql+"        from   program_facts";
            sql = sql+"        where  (study_type_code <> 'AUDIT'";
            sql = sql+"       AND";
            sql = sql+"        program_label = '"+pProgramLabel+"')";
            sql = sql+"        OR";
            sql = sql+"       (study_type_code = 'AUDIT'";
            sql = sql+"        AND";
            sql = sql+"        supplement_code = 'C'";
            sql = sql+"        AND";
            sql = sql+"       program_label = '"+pProgramLabel+"')";
            sql = sql+"        OR";
            sql = sql+"       (study_type_code = 'AUDIT'";
            sql = sql+"        AND";
            sql = sql+"        supplement_code <> 'C'";
            sql = sql+"        AND";
            sql = sql+"        fielding_period = get_fielding_period('"+pProgramLabel+"')))";
            sql = sql+"  AND a.lock_flag != 'Y'";
            sql = sql+"  AND a.status_code = 'new'";
            if (pTag.equalsIgnoreCase("false"))
            {
                sql = sql+" AND a.tag_type_code IS NOT NULL";
            } else 
            {
                sql = sql+" AND a.tag_type_code IS NULL";
            }
            sql = sql+"   group by";
            sql = sql+"  a.program_event_id,";
            sql = sql+"  CASE";
            sql = sql+"    WHEN GROUP_QUESTION_LABEL IS NOT NULL THEN";
            sql = sql+" 'W'||group_question_label || ' / Q' ||survey_question_label";
            sql = sql+"  ELSE";
            sql = sql+"       'Q'||survey_question_label";
            sql = sql+"  END,";
            sql = sql+"  a.question_id";
            sql = sql + " order by label";
            log.debug("SQL #getGroupList: "+sql);
            try 
            {
                lst =  jt.query(sql, new RowMapperResultReader(new GroupRowMapperNoParams()));
            } catch (Exception e)
            {
                log.error(e.getMessage(), e);
            }
            
        } else 
        {
            String quesId = pParameter.substring(0,pParameter.indexOf("~"));
            String paraNo = pParameter.substring(pParameter.indexOf("~")+1, pParameter.length());
            sql = "select a.program_event_id,";
            sql = sql+" CASE";
            sql = sql+" WHEN GROUP_QUESTION_LABEL IS NOT NULL THEN";
            sql = sql+" 'W'||group_question_label || ' / Q' ||survey_question_label";
            sql = sql+" ELSE";
            sql = sql+"      'Q'||survey_question_label";
            sql = sql+" END as label,";
            sql = sql+" a.question_id, ";
            sql = sql+paraNo+" as parameter_no,";
            sql = sql+" b.parameter_value,";
            sql = sql+" a.par"+paraNo+"_parameter_id,";
            sql = sql+" count(*)";
            sql = sql+" from   oec_response_staging a,";
            sql = sql+" parameters b";
            sql = sql+" where  a.question_id ="+quesId;
            sql = sql+" and    a.par"+paraNo+"_parameter_id = b.parameter_id";
            sql = sql+" and    a.program_event_id in (select program_event_id";
            sql = sql+"       from   program_facts";
            sql = sql+"       where  (study_type_code <> 'AUDIT'";
            sql = sql+"       AND";
            sql = sql+"       program_label = '"+pProgramLabel+"')";
            sql = sql+"       OR";
            sql = sql+"      (study_type_code = 'AUDIT'";
            sql = sql+"       AND";
            sql = sql+"       supplement_code = 'C'";
            sql = sql+"       AND";
            sql = sql+"       program_label = '"+pProgramLabel+"')";
            sql = sql+"       OR";
            sql = sql+"      (study_type_code = 'AUDIT'";
            sql = sql+"       AND";
            sql = sql+"       supplement_code <> 'C'";
            sql = sql+"       AND";
            sql = sql+"       fielding_period = get_fielding_period('"+pProgramLabel+"')))";
            sql = sql+" AND a.lock_flag != 'Y'";
            sql = sql+" AND a.status_code = 'new'";
            if (pTag.equalsIgnoreCase("false"))
            {
                sql = sql+" AND a.tag_type_code IS NOT NULL";
            } else 
            {
                sql = sql+" AND a.tag_type_code IS NULL";
            }
            sql = sql+" group by";
            sql = sql+" a.program_event_id,";
            sql = sql+" CASE";
            sql = sql+"   WHEN GROUP_QUESTION_LABEL IS NOT NULL THEN";
            sql = sql+" 'W'||group_question_label || ' / Q' ||survey_question_label";
            sql = sql+" ELSE";
            sql = sql+"      'Q'||survey_question_label";
            sql = sql+" END,";
            sql = sql+" a.question_id, ";
            sql = sql+paraNo+" ,";
            sql = sql+" b.parameter_value,";
            sql = sql+" a.par"+paraNo+"_parameter_id";
            sql = sql + " order by label";
            log.debug("SQL #getGroupList: "+sql);
            try
            {
                lst =  jt.query(sql, new RowMapperResultReader(new GroupRowMapper()));
            } catch (Exception e)
            {
                log.error(e.getMessage(), e);
            }
        }
        return lst;
    }
    class GroupRowMapper implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        Groups grp = new Groups();
          grp.setProgramEventId(rs.getString(1));
          grp.setLabel(rs.getString(2));
          grp.setQuestionId(rs.getString(3));
          grp.setParameterValue(rs.getString(5));
          grp.setParameterNo(rs.getString("parameter_no"));
          grp.setParameterId(rs.getString(6));
          grp.setCount(rs.getString(7));
          return grp;
	      }      
	  }
    class GroupRowMapperNoParams implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        Groups grp = new Groups();
          grp.setProgramEventId(rs.getString(1));
          grp.setLabel(rs.getString(2));
          grp.setQuestionId(rs.getString(3));
          grp.setParameterValue("");
          grp.setParameterNo("");
          grp.setParameterId("");
          grp.setCount(rs.getString(4));
          return grp;
	      }      
	  }
    /**
     * @return List
     */
    public Map getOecTypeCodes()
    {
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select tag_type_code, description from oec_tag_types";
        try 
        {
            lst = jt.query(sql, new RowMapperResultReader(new TagTypeMapper()));
            for (int i=0; i<lst.size(); i++)
            {
                OecTagTypes ot = (OecTagTypes)lst.get(i);
                mp.put(ot.getTagTypeCode(),ot.getDescription());            
            } 
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return mp;
    }
    class TagTypeMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException
        {
            OecTagTypes ot = new OecTagTypes();
            ot.setTagTypeCode(rs.getString(1));
            ot.setDescription(rs.getString(2));
            return ot;
        }
    }
    /**
     * 
     * @param afterDate
     * @return
     * @throws Exception
     */
    public List<MonthYear> getDistinctFieldingPeriods(String afterDate) throws Exception
    {
    	List<MonthYear> list = null;
    	String sql = 
    		"select distinct fielding_period, " +
    		"         to_char(fielding_period, 'MM/DD/YYYY') as fielding_period_string, " +
    		"         to_char(fielding_period, 'MM/DD/YYYY') as display_period " +
    		"from program_facts " +
    		"where fielding_period is not null " +
    		"and fielding_period >= ? " +
    		"order by fielding_period";
    	try
    	{
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    		Date date = formatter.parse(afterDate);

    		JdbcTemplate jt = getJdbcTemplate();
            list = new LinkedList(jt.query(sql, new Object[] {date}, new MonthYearMapper()));            
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw e;
        }
        return list;
    }
    /**
     * @return List
     * Method returns a list of months and years to the calling object.
     * The month and year are separated by a "/"
     */
    public List getActiveMonthAndYear()
    {
        List list = new ArrayList();
        String sql = 
        	"select fielding_period, " +
        	"         to_char(fielding_period, 'MM/DD/YYYY') as fielding_period_string, " +
        	"         to_char(fielding_period, 'MM/DD/YYYY') || ' (' || count(fielding_period) || ')' as display_period "+
            "from program_facts "+
            "where oec_status_code = Month_End.OEC_STATUS_ACTIVE "+
            "and fielding_period is not null "+
            "group by fielding_period " +
            "order by fielding_period desc";
        
        try
        {
            JdbcTemplate jt = getJdbcTemplate();
            list = jt.query(sql, new MonthYearMapper());            
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return list;
    }
    class MonthYearMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException
        {
            MonthYear my = new MonthYear();
            my.setFullDate(rs.getString("fielding_period_string"));
            my.setDisplayDate(rs.getString("display_period"));
            return my;
        }
    }
    /**
     * 
     * @param fieldingPeriod
     * @return
     */
    public List getStudyTypesFromPeriod(String fieldingPeriod)
    {
        List lst = new ArrayList();
        String[] parameters = {fieldingPeriod};
        String sql = 
        	"select study_type_code, " +
        	"         study_type_code || ' (' || count(program_event_id) || ')' as study_type_display " +
            "from program_facts "+
            "where oec_status_code = Month_End.OEC_STATUS_ACTIVE "+
            "and fielding_period = to_date(?, 'MM/DD/YYYY') "+
            "group by study_type_code " +
            "order by study_type_code";
        try
        {
            JdbcTemplate jt = getJdbcTemplate();
            log.debug("#getStudyTypesFromPeriod :"+sql);
            lst = jt.query(sql, parameters, new RowMapperResultReader(new StudyTypeMapper2()));
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return lst;
    }
    /**
     * Yeah, I know its lazy ... maybe change later
     * @author sstuart
     *
     */
    class StudyTypeMapper2 implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException {
	        ProgramFacts pf = new ProgramFacts();
	        pf.setProgramLabel(rs.getString("study_type_display"));
	        pf.setStudyTypeCode(rs.getString("study_type_code"));
	        return pf;
	      }          
    }
    /**
     * @param fieldingPeriod
     * @param studyTypeCode
     * @return
     */
    public List getProgramsFromPeriodAndStudyType(String fieldingPeriod, 
    		                                                                 String studyTypeCode)
    {
        List lst = new ArrayList();
        String[] parameters = {fieldingPeriod, studyTypeCode};
        String sql = 
        	"select program_event_id, program_label || ' (' || program_event_id || ')' as program_label " +
            "from program_facts "+
            "where oec_status_code = Month_End.OEC_STATUS_ACTIVE "+
            "and fielding_period = to_date(?, 'MM/DD/YYYY') " +
            "and study_type_code = ? "+
            "order by program_label";
        try
        {
            JdbcTemplate jt = getJdbcTemplate();
            log.debug("#getProgramsFromPeriodAndStudyType :"+sql);
            lst = jt.query(sql, parameters, new RowMapperResultReader(new ProgramFactsMapper()));
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return lst;
    }
    /**
     * @author sstuart
     */
    class ProgramFactsMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException {
	        ProgramFacts pf = new ProgramFacts();
	        pf.setProgramLabel(rs.getString("program_label"));
	        pf.setEventId(rs.getString("program_event_id"));
	        return pf;
	      }          
    }
    /**
     * @param String
     * @return int
     * Returns a Event Id for a Program Event Id passed in.
     */
    public int findProgramEventEventId(String pProgram)
    {
        return findProgramEventEventId(pProgram, null);
    }
    /**
     * @param pProgram
     * @param jdbcTemplate
     * @return
     */
    public int findProgramEventEventId(String pProgram, JdbcTemplate jdbcTemplate)
    {
        JdbcTemplate jt = (jdbcTemplate == null) ? getJdbcTemplate() : jdbcTemplate;
        String sql = " select program_event_id from program_facts where program_label = '"+pProgram+"'";
        log.debug("#findEventid :: "+sql);
        return jt.queryForInt(sql);
    }
    /**
     * @param String Program Event Id
     * @return List
     */
    public List findEventIds(String pPeid)
    {
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select distinct event_id from response_facts where program_event_id = "+pPeid;
        return jt.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException 
            {
               ProgramFacts pf = new ProgramFacts();
               pf.setEventId(rs.getString(1));
               return pf;                    
            }
        });
    }
    /**
     * @param String Fielding Date
     * @return List
     * Method returns a list of ProgramFacts objects that have values in
     * their Program Event id and Event id attributes.
     */
    public List findProgramAndEvents(final String pDate)
    {
        return findProgramAndEvents(pDate, null);
    }
    /**
     * @param pDate
     * @param jdbcTemplate
     * @return
     */
    public List findProgramAndEvents(final String pDate, final JdbcTemplate jdbcTemplate)
    {
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = (jdbcTemplate == null) ? getJdbcTemplate() : jdbcTemplate;
        String sql = "select distinct a.program_event_id,"+
                " b.event_id from program_facts a, response_facts b"+
                "   where a.study_type_code = 'AUDIT'"+
                " and a.fielding_period = to_date('"+pDate+"','mm/dd/yyyy')"+
                "   and b.program_event_id = a.program_event_id"+
                "   order by a.program_event_id";
        return jt.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException 
            {
               ProgramFacts pf = new ProgramFacts();
               pf.setProgramEvent(rs.getString(1));
               pf.setEventId(rs.getString(2));
               return pf;                    
            }
        });        
    }
    /**
     * @param String ProgramEventId
     * @return List
     */
    public List findEventsByProgram(final String pProgramEventId)
    {
        return findEventsByProgram(pProgramEventId, null);
    }
    /**
     * @param pProgramEventId
     * @param jdbcTemplate
     * @return
     */
    public List findEventsByProgram(final String pProgramEventId, final JdbcTemplate jdbcTemplate)
    {
        String sql = "select program_event_id, event_id from program_events where program_event_id = "+pProgramEventId;
        JdbcTemplate jt = (jdbcTemplate == null) ? getJdbcTemplate() : jdbcTemplate;
        return jt.query(sql,new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                ProgramFacts pf = new ProgramFacts();
                pf.setProgramEvent(rs.getString(1));
                pf.setEventId(rs.getString(2));
                return pf;
            }
        });        
    }
    public Map getMarketsByPeid(String pType, String pPeids)
    {
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "";
        if ((pType.equalsIgnoreCase("LA")) || (pType.equalsIgnoreCase("PSD")))
        {
            sql = "select distinct focus_market_par_id,"+
                " (select parameter_value from parameters where parameter_id = focus_market_par_id) as m_value"+
                " from v_program_facts"+
                " where study_type_code = '"+pType+"'"+
                " and program_event_id in ("+pPeids+")"+
                " order by m_value";
        } else
        {
            sql = "select distinct market_par_id,"+
                " (select parameter_value from parameters where parameter_id = market_par_id) as m_value"+
                " from v_program_facts"+
                " where study_type_code = '"+pType+"'"+
                " and program_event_id in ("+pPeids+")"+
                " order by m_value	";
        }
        try 
        {
            lst = jt.query(sql, new RowMapperResultReader(new MarketMapper()));
            System.out.println(lst+" ==> "+lst.size());
            for (int i=0; i<lst.size(); i++)
            {
                Market mkt = (Market)lst.get(i);
                if ((mkt.getMarketValue() != null) || (mkt.getMarketValue() != "null"))
                {
                    mp.put(mkt.getMarketId(),mkt.getMarketValue());            
                }
            } 
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return mp;
    }
    class MarketMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException
        {
            Market mkt = new Market();
            mkt.setMarketId(rs.getString(1));
            mkt.setMarketValue(rs.getString(2));
            return mkt;
        }
    }
    public Map getMarketsByDateAndType(String pType, String pBeginDate, String pEndDate)
    {
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        boolean data = isThereData(pBeginDate);
        
        String historySql = "union select distinct par1_parameter_id,"+
                "       p.parameter_value"+
                " from cody.oec_response_staging_history orsh,"+
                "     parameters p,"+
                "     program_facts pf"+
                " where orsh.par1_parameter_id is not null"+
                " and pf.study_type_code = '"+pType+"'"+
                " and orsh.added_datetime >= to_date('"+pBeginDate+"','mm/dd/yyyy')"+
                " and orsh.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
                " and pf.program_event_id = orsh.program_event_id"+
                " and p.parameter_id = orsh.par1_parameter_id"+
                " and p.category_code = 'MARKET'";
        String endSql = ") order by 2";
        String sql = "select par1_parameter_id, parameter_value from (select distinct par1_parameter_id,"+
              "       p.parameter_value"+
              "  from cody.oec_response_staging ors,"+
              "     parameters p,"+
              "     program_facts pf"+
              "  where ors.par1_parameter_id is not null"+
              "  and pf.study_type_code = '"+pType+"'"+
              "  and ors.added_datetime >= to_date('"+pBeginDate+"','mm/dd/yyyy')"+
              "  and ors.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
              "  and pf.program_event_id = ors.program_event_id"+
              "  and p.parameter_id = ors.par1_parameter_id"+
              "  and p.category_code = 'MARKET'";              
        if (data)
        {
            sql = sql+historySql+endSql;
        } else
        {
            sql = sql+endSql;
        }
        try 
        {
            lst = jt.query(sql, new RowMapperResultReader(new MarketMapper()));
            for (int i=0; i<lst.size(); i++)
            {
                Market mkt = (Market)lst.get(i);
                if ((mkt.getMarketValue() != null) || (mkt.getMarketValue() != "null"))
                {
                    mp.put(mkt.getMarketId(),mkt.getMarketValue());            
                }
            } 
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return mp;
    }
    public Map getProductsByMarketAndDate(String pMarkets, String pBeginDate, String pEndDate, String pType)
    {
        String endSql = ") order by 2";
        String historySql = " union "+
                " select distinct par2_parameter_id, p.parameter_value"+
                " from cody.oec_response_staging_history orsh,"+
                "      parameters p,"+
                "      program_facts pf"+
                " where orsh.par1_parameter_id in ("+pMarkets+")"+
                " and pf.study_type_code = '"+pType+"'"+
                " and pf.program_event_id = orsh.program_event_id"+
                " and orsh.added_datetime >= to_date('"+pBeginDate+"','mm/dd/yyyy')"+
                " and orsh.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
                " and p.parameter_id = orsh.par2_parameter_id"+
                " and p.category_code = 'PRODNAME'";
        String sql = "select par2_parameter_id, parameter_value from (select distinct par2_parameter_id, p.parameter_value"+
                " from cody.oec_response_staging ors,"+
                "     parameters p,"+
                "     program_facts pf"+
                " where ors.par1_parameter_id in ("+pMarkets+")"+
                " and ors.added_datetime >= to_date('"+pBeginDate+"','mm/dd/yyyy')"+
                " and ors.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
                " and p.parameter_id = ors.par2_parameter_id"+
                " and pf.study_type_code = '"+pType+"'"+
                " and pf.program_event_id = ors.program_event_id"+
                " and p.category_code = 'PRODNAME'";
        boolean data = isThereData(pBeginDate);
        if (data)
        {
            sql = sql+historySql+endSql;
        } else
        {
            sql = sql+endSql;
        }        
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        try 
        {
            lst = jt.query(sql, new RowMapperResultReader(new ProductMapper()));
            for (int i=0; i<lst.size(); i++)
            {
                Product pdt = (Product)lst.get(i);
                if ((pdt.getProductValue() != null) || (pdt.getProductValue() != "null"))
                {
                    mp.put(pdt.getProductId(),pdt.getProductValue());            
                }
            } 
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return mp;
    }
    public Map getProductsByPeidMarket(String pPeids, String pMarkets)
    {
        String sql = "select distinct par2_parameter_id,"+
                " (select parameter_value from parameters where parameter_id = par2_parameter_id) as pValue"+
                " from cody.oec_response_staging"+
                " where par1_parameter_id in ("+pMarkets+")"+
                " and par2_parameter_id is not null"+
                " and program_event_id in ("+pPeids+") "+
                " order by pValue";
        
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        try 
        {
            lst = jt.query(sql, new RowMapperResultReader(new ProductMapper()));
            for (int i=0; i<lst.size(); i++)
            {
                Product pdt = (Product)lst.get(i);
                if ((pdt.getProductValue() != null) || (pdt.getProductValue() != "null"))
                {
                    mp.put(pdt.getProductId(),pdt.getProductValue());            
                }
            } 
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return mp;
    }
    class ProductMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException
        {
            Product pdt = new Product();
            pdt.setProductId(rs.getString(1));
            pdt.setProductValue(rs.getString(2));
            return pdt;
        }
    }
    public Map getCellByProductMarket(String pStudy, String pProducts, String pMarkets, String pStartDate, String pEndDate)
    {
        String endSql = ") order by 1";
        String historySql = " union"+
                " select distinct pfa.cell_code1, pfa.cell_code1"+
                " from cody.oec_response_staging_history orst,"+
                "     person_facts pfa"+
                " where orst.par1_parameter_id in ("+pProducts+")"+
                " and orst.par2_parameter_id in ("+pMarkets+")"+
                " and orst.added_datetime >= to_date('"+pStartDate+"','mm/dd/yyyy')"+
                " and orst.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
                " and pfa.program_event_id = orst.program_event_id"+
                " and pfa.event_id = orst.event_id"+
                " and pfa.targetrx_id = orst.targetrx_id";
        String sql = "select cc1, cc1 from (select distinct pf.cell_code1 as cc1, pf.cell_code1"+
                " from cody.oec_response_staging ors,"+
                "    person_facts pf"+
                " where ors.par1_parameter_id in ("+pProducts+")"+
                " and ors.par2_parameter_id in ("+pMarkets+")"+
                " and ors.added_datetime >= to_date('"+pStartDate+"','mm/dd/yyyy')"+
                " and ors.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
                " and pf.program_event_id = ors.program_event_id"+
                " and pf.event_id = ors.event_id"+
                " and pf.targetrx_id = ors.targetrx_id";
        boolean data = isThereData(pStartDate);
        if (data)
        {
            sql = sql+historySql+endSql;
        } else
        {
            sql = sql+endSql;
        }           
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        try 
        {
            lst = jt.query(sql, new RowMapperResultReader(new CellMapper()));
            for (int i=0; i<lst.size(); i++)
            {
                Cell cell = (Cell)lst.get(i);
                if ((cell.getCellCode() != null) || (cell.getCellCode() != "null"))
                {
                    mp.put(cell.getCellCode(),cell.getCellCode());            
                }
            } 
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return mp;
    }
    class CellMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException
        {
            Cell cell = new Cell();
            cell.setCellCode(rs.getString(1));
            cell.setCellDesc(rs.getString(2));
            return cell;
        }
    }
    
    public boolean isThereData(String pDate)
    {
        boolean result = false;
        JdbcTemplate jt = getJdbcTemplate();
        try 
        {
            String sql = "select to_char(max(added_datetime), 'mm/dd/yyyy') from oec_response_staging_history";
            String dbDate = (String) jt.queryForObject(sql, String.class);;
            Date passedIn = new Date(pDate);
            Date dbDateIn = new Date(dbDate);
            if (passedIn.compareTo(dbDateIn) <= 0)
            {
                result = true;
            } else
            {
                result = false;
            }
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return result;
    }
}