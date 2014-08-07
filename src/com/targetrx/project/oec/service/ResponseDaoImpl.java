package com.targetrx.project.oec.service;

import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Types;
import java.sql.CallableStatement;

import com.targetrx.project.oec.bo.ProgramFacts;
import com.targetrx.project.oec.bo.Response;
import com.targetrx.project.oec.bo.User;
import com.targetrx.project.oec.bo.TaggedResponse;
import com.targetrx.project.oec.bo.Graph;
import com.targetrx.project.oec.service.ProgramFactsDaoImpl;
/**
 *
 * @author pkukk
 */
public class ResponseDaoImpl extends JdbcDaoSupport implements ResponseDao {
    private Logger log = Logger.getLogger(this.getClass());
    /**
     * @param String
     * @return List
     */
    public List getReponses(final ArrayList pParams, final String pUser,final String pTag)
    {
        List lst =  new ArrayList();
        List params = new ArrayList();
        String sql = "";
        // If user already has responses checked out for coding do not allow them to check anymore out
        // If they do not have any checked out then go ahead and check some out.
        
        if (this.getNumberofCheckedResponses(pUser) != 0)
        {
            return lst;
        }
        if (pParams != null)
        {
            JdbcTemplate jt = getJdbcTemplate();
            for (int i = 0; i<pParams.size(); i++)
            {
                //user~programEventId~questionId~parameterNo~parameterId
                if (pParams.get(i) != null)
                {
                    StringTokenizer strTok = new StringTokenizer((String)pParams.get(i),"~");

                    while (strTok.hasMoreTokens())
                    {
                        params.add(strTok.nextToken());
                    }
                    sql = "select group_question_label, response_orig_str, par1_parameter_id, par3_parameter_id,";
                    sql = sql + " code1, code2, code3, code4, code5, code6,";
                    sql = sql + " par2_parameter_id, targetrx_id, question_seq_no, thread_no, program_event_id, event_id,";
                    sql = sql + " survey_question_label, response_extract_id, par4_parameter_id, question_id,status_code,lock_user,";
                    sql = sql + " lock_flag,tag_type_code,tag_user";
                    sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par1_parameter_id) as p1_value";
                    sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par2_parameter_id) as p2_value";
                    sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par3_parameter_id) as p3_value";
                    sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par4_parameter_id) as p4_value";
                    sql = sql + " from oec_response_staging";
                    sql = sql + " where program_event_id = "+params.get(1);
                    sql = sql + " and question_id = "+params.get(2);
                    
                    if (params.size() > 3) 
                    {
                     sql = sql + " and par"+params.get(3)+"_parameter_id = "+params.get(4);
                    }
                    sql = sql + " and lock_flag = 'N'";
                    sql = sql + " and tag_type_code IS NULL";
                    sql = sql + " and status_code = 'new' ";
                    lst.add(jt.query(sql, new RowMapperResultReader(new ReponseRowMapper())));
                    
                    if (params.size() > 3) 
                    {
                        this.checkOut((String)params.get(1),(String)params.get(2),(String)params.get(3),(String)params.get(4),pUser,pTag);
                    } else
                    {
                        this.checkOut((String)params.get(1),(String)params.get(2),"0","0",pUser,pTag);
                    }
                    
                    params.clear();
                }
            }
            return lst;
        }
        return lst;
    }
    class ReponseRowMapper implements RowMapper
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        Response resp = new Response();
	        resp.setGroupQuestionLabel(rs.getString(1));
          resp.setResponseOrigStr(rs.getString(2));
          resp.setPar1ParameterId(rs.getString(3));
          resp.setPar3ParameterId(rs.getString(4));
          resp.setCode1(rs.getString(5));
          resp.setCode2(rs.getString(6));
          resp.setCode3(rs.getString(7));
          resp.setCode4(rs.getString(8));
          resp.setCode5(rs.getString(9));
          resp.setCode6(rs.getString(10));
          resp.setPar2ParameterId(rs.getString(11));
          resp.setTargetrxId(rs.getString(12));
          resp.setQuestionSeqNo(rs.getString(13));
          resp.setThreadNo(rs.getString(14));
          resp.setProgramEventId(rs.getString(15));
          resp.setEventId(rs.getString(16));
          resp.setSurveyQuestionLabel(rs.getString(17));
          resp.setResponseExtractId(rs.getString(18));
          resp.setPar4ParameterId(rs.getString(19));
          resp.setQuestionId(rs.getString(20));
          resp.setStatusCode(rs.getString(21));
          resp.setLockedUser(rs.getString(22));
          resp.setLockedFlag(rs.getString(23));
          resp.setTagTypeCode(rs.getString(24));
          resp.setTagUser(rs.getString(25));
          resp.setP1Value(rs.getString(26));
          resp.setP2Value(rs.getString(27));
          resp.setP3Value(rs.getString(28));
          resp.setP4Value(rs.getString(29));
	        return resp;
	      }       
	  }
    public int getNumberofCheckedResponses(final String pUser)
    {
        int result = 0;
        String sql = " select count(*) from oec_response_staging";
        sql = sql + " where lock_user = '"+pUser+"'";
        sql = sql + " and lock_flag = 'Y'";
        JdbcTemplate jt = getJdbcTemplate();
        try {
            result = jt.queryForInt(sql);
        } catch (Exception e)
        {
            log.error(e.getMessage());
        }
        return result;
    }
    public void checkOut(final String pPeid, final String pQid, final String pParamNo, final String pParamId, final String pUser, final String pTag)
    {
        String sql = " update oec_response_staging set lock_flag = 'Y', lock_user = '"+pUser+"' , lock_datetime = sysdate";
        sql = sql + " where program_event_id = "+pPeid+" and question_id = "+pQid+" and status_code = 'new' ";
        if (!pParamNo.equalsIgnoreCase("0"))
        {
            sql = sql + " and par"+pParamNo+"_parameter_id = "+pParamId;
        }
        if (pTag.equalsIgnoreCase("false"))
        {
            sql = sql+" AND tag_type_code IS NOT NULL";
        } else 
        {
            sql = sql+" AND tag_type_code IS NULL";
        }
        JdbcTemplate jt = getJdbcTemplate();
        try {
            jt.execute(sql);
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
    public List getCheckedOutResponses(final String pUser)
    {
        List lst =  new ArrayList();
        String sql = "select group_question_label, response_orig_str, par1_parameter_id, par3_parameter_id,";
        sql = sql + " code1, code2, code3, code4, code5, code6,";
        sql = sql + " par2_parameter_id, targetrx_id, question_seq_no, thread_no, program_event_id, event_id,";
        sql = sql + " survey_question_label, response_extract_id, par4_parameter_id, question_id,status_code,lock_user,";
        sql = sql + " lock_flag,tag_type_code,tag_user";
        sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par1_parameter_id) as p1_value";
        sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par2_parameter_id) as p2_value";
        sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par3_parameter_id) as p3_value";
        sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par4_parameter_id) as p4_value";
        sql = sql + " from oec_response_staging";
        sql = sql + " where lock_user = '"+pUser+"'";
        sql = sql + " and lock_flag = 'Y'";
        sql = sql + " and status_code = 'new'";
        JdbcTemplate jt = getJdbcTemplate();
        try {
            lst = jt.query(sql, new RowMapperResultReader(new ReponseRowMapper()));
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return lst;
    }    
   public void setCodedUnverified(final String pResponseExtractId, String pCode1, String pCode2, String pCode3, String pCode4, String pCode5, String pCode6, final String pUser)
   {
       String[] code_arr = {pCode1, pCode2, pCode3, pCode4, pCode5, pCode6};
       String code_result = this.checkCodeValue(code_arr);
       String sql = "update oec_response_staging set status_code = 'cu', tag_type_code = '', tag_user = '', tag_datetime = '', updated_user = '"+pUser+"', updated_datetime = sysdate,";
       sql = sql+code_result;
       sql = sql + " where response_extract_id = "+pResponseExtractId;
       JdbcTemplate jt = getJdbcTemplate();
       try {
        jt.execute(sql);
       } catch (Exception e)
       {
           log.error(e.getMessage(), e);
       }
   }
   
   public void setCodedVerified(final String pResponseExtractId, final String pCode1, final String pCode2, final String pCode3, final String pCode4, final String pCode5, final String pCode6, final String pUser)
   {
       String[] code_arr = {pCode1, pCode2, pCode3, pCode4, pCode5, pCode6};
       String code_result = this.checkCodeValue(code_arr);
       String sql = "update oec_response_staging set status_code = 'cv', tag_type_code = '', tag_user = '', tag_datetime = '', updated_user = '"+pUser+"', updated_datetime = sysdate,";
       sql = sql+code_result;
       sql = sql + "' where response_extract_id = "+pResponseExtractId;
       JdbcTemplate jt = getJdbcTemplate();
       try {
        jt.execute(sql);
       } catch (Exception e)
       {
           log.error(e.getMessage(), e);
       }
   }
   public void setCodedPosted(final String pResponseExtractId, final String pCode1, final String pCode2, final String pCode3, final String pCode4, final String pCode5, final String pCode6, final String pUser)
   {
       String[] code_arr = {pCode1, pCode2, pCode3, pCode4, pCode5, pCode6};
       String code_result = this.checkCodeValue(code_arr);
       String sql = "update oec_response_staging set status_code = 'post', tag_type_code = '', tag_user = '', tag_datetime = '', updated_user = '"+pUser+"', updated_datetime = sysdate,";
       sql = sql+code_result;
       sql = sql + " where response_extract_id = "+pResponseExtractId;
       JdbcTemplate jt = getJdbcTemplate();
       try {
        jt.execute(sql);
       } catch (Exception e)
       {
           log.error(e.getMessage());
       }
   }
   public String checkCodeValue(String[] pValue)
   {
       String sql = "";
       for (int i=0; i<pValue.length; i++)
       {
           if (i != 5)
           {
               if (pValue[i].length() == 0)
               {
                sql = sql+" code"+(i+1)+" = null, ";
               } else {
                sql = sql+" code"+(i+1)+" = '"+pValue[i]+"', ";   
               }
           } else 
           {
               if (pValue[i].length() == 0)
               {
                sql = sql+" code"+(i+1)+" = null ";
               } else {
                sql = sql+" code"+(i+1)+" = '"+pValue[i]+"' ";   
               }
           }
       }
       return sql;
   }
   /**
    * @param String pUser
    * @return String
    */
   public String checkInStatus(final String pUser)
   {
       String result = null;
       // Build query to see if there is a row in the checkin_processing table
       // for this user.
       String sql = "select process_datetime from cody.checkin_processing where process_user = ?";
       String sqlCount = "select count(*) from cody.checkin_processing where process_user = ?";
       try
       {
           JdbcTemplate jt = getJdbcTemplate();
           int num_row = jt.queryForInt(sqlCount, new Object[] {pUser});
           if (num_row == 0)
           {
               result = "NO DATA";
           } else
           {
               result = (String) jt.queryForObject(sql, new Object[] {pUser}, String.class);
           }           
       } catch (Exception e)
       {
           log.error(e.getMessage());           
       }
       return result;
   }
   public void checkIn(final String pUser)
   {
       String sql = "call code_verifier.verify_and_checkins('"+pUser+"')";
       JdbcTemplate jt = getJdbcTemplate();
       jt.execute(sql);
   }
   public void setTagTypeCode(final String pTag,final String pUser, final String pResponseExtractId, final String pCode1, final String pCode2, final String pCode3, final String pCode4, final String pCode5, final String pCode6)
   {
       String[] code_arr = {pCode1, pCode2, pCode3, pCode4, pCode5, pCode6};
       String code_result = this.checkCodeValue(code_arr);
       String sql = " update oec_response_staging set tag_type_code = '"+pTag+"', tag_user = '"+pUser+"', updated_user = '"+pUser+"', updated_datetime = sysdate,";
       sql = sql + " tag_datetime = sysdate, status_code = 'new', ";
       sql = sql+code_result;
       sql = sql + " where response_extract_id = "+pResponseExtractId;
       JdbcTemplate jt = getJdbcTemplate();
       try {
        jt.execute(sql);
       } catch (Exception e)
       {
           log.error(e.getMessage(), e);
       }
   }
   
   public Map getLockedResponses()
   {
       List lst = new ArrayList();
       Map mp = new LinkedHashMap();
       String sql = "select o.lock_user, count(*) ";
       sql = sql+" from v_staff s, v_staff_roles_xref  sr, v_roles r, oec_response_staging o";
       sql = sql + " where o.lock_flag = 'Y'  and o.lock_user = s.windows_login_name  and s.staff_id = sr.staff_id";
       sql = sql + " and r.role_id in (22, 20, 21) and r.role_id = sr.role_id group by o.lock_user order by o.lock_user";
       JdbcTemplate jt = getJdbcTemplate();
       try {
        lst = jt.query(sql, new RowMapperResultReader(new LockedRowMapper()));
       } catch (Exception e)
       {
           log.error(e.getMessage(), e);
       }
       for (int i=0; i<lst.size(); i++)
       {
            User u = (User)lst.get(i);
            mp.put(u.getUserName(),"Coder "+u.getUserName()+ " has "+u.getLockedResponses()+" locked.");            
       }  
       return mp;
   }
   class LockedRowMapper implements RowMapper
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
            User user = new User();
            user.setUserName(rs.getString(1));
            user.setLockedResponses(rs.getString(2));
            return user;
	      }       
	  }
   public Map getTaggedResponsesCount()
   {
       Map mp = new LinkedHashMap();
       List lst = new ArrayList();
       String sql = "select tag_type_code, count(*)";
       sql = sql +" from oec_response_staging";
       sql = sql +" where tag_type_code is not NULL  group by tag_type_code";
       JdbcTemplate jt = getJdbcTemplate();
       try {
        lst = jt.query(sql, new RowMapperResultReader(new TaggedRowMapper()));
       } catch (Exception e)
       {
           log.error(e.getMessage(), e);
       }
       for (int i=0; i<lst.size(); i++)
       {
            TaggedResponse r = (TaggedResponse)lst.get(i);
            mp.put(r.getTagTypeCode(),r.getCount()+" responses tagged with "+r.getTagTypeCode());            
       }  
       return mp;
   }
   class TaggedRowMapper implements RowMapper
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
            TaggedResponse r= new TaggedResponse();
            r.setTagTypeCode(rs.getString(1));
            r.setCount(rs.getString(2));
            return r;
	      }       
	  }
   public String getStudyType(final String pProgramLabel)
    {
        String sType = "";
        JdbcTemplate jt = getJdbcTemplate();
        String sql = " select study_type_code from program_facts where ";
        sql = sql+" program_label = ?";
        try
        {
            sType = (String) jt.queryForObject(sql, new Object[] {pProgramLabel}, String.class);
        } catch (Exception e)
        {
            return null;
        }
        return sType;
    }
   
   /**
    * @param String
    * @return List
    */
   public List getProgramQuesGraph(final String pProgramLabel)
   {
       List lst = new ArrayList();
       String result = "";
       String studyType = this.getStudyType(pProgramLabel);
       System.out.println("HERE");
       String sql = "select distinct s.group_question_label as lbl, prog.program_event_id as preid,";
	     sql = sql+" (select count(*) ";
       sql = sql+" from oec_response_staging ";
	     sql = sql+" where survey_question_label = s.survey_question_label";
	     sql = sql+" and program_event_id = prog.program_event_id";
	     sql = sql+" ) as tques,";
	     sql = sql+" (select count(*) ";
	     sql = sql+" from oec_response_staging ";
	     sql = sql+" where survey_question_label = s.survey_question_label";
	     sql = sql+" and program_event_id = prog.program_event_id";
	     sql = sql+" and status_code = 'cu') as cu,";
	     sql = sql+" (select count(*) ";
	     sql = sql+" from oec_response_staging ";
	     sql = sql+" where survey_question_label = s.survey_question_label";
	     sql = sql+" and program_event_id = prog.program_event_id";
	     sql = sql+" and status_code = 'new'";
	     sql = sql+" and tag_type_code is not null) as tagged,";
	     sql = sql+" (select count(*) ";
	     sql = sql+" from oec_response_staging ";
	     sql = sql+" where survey_question_label = s.survey_question_label";
	     sql = sql+" and program_event_id = prog.program_event_id";
	     sql = sql+" and status_code = 'cv') as verified,";
	     sql = sql+" (select count(*) ";
	     sql = sql+" from oec_response_staging ";
	     sql = sql+" where survey_question_label = s.survey_question_label";
	     sql = sql+" and program_event_id = prog.program_event_id";
	     sql = sql+" and status_code = 'new'";
	     sql = sql+" and tag_type_code is null) as newcode, s.survey_question_label ";
       sql = sql+" from oec_response_staging s,";
 	     sql = sql+" (select program_event_id";
	     sql = sql+" from   inventory.program_facts";
       if ((studyType == null) || (studyType == ""))
       {
           sql = sql+" where  (study_type_code = 'AUDIT'";
           sql = sql+" AND supplement_code <> 'C'";
           sql = sql+" AND fielding_period =    get_fielding_period('"+pProgramLabel+"'))";
           sql = sql+" OR (study_type_code = 'AUDIT'";
           sql = sql+" AND supplement_code = 'C'";
           sql = sql+" AND program_label = '"+pProgramLabel+"')";
           sql = sql+" OR (study_type_code = 'AUDIT'";
           sql = sql+" AND supplement_code <> 'C'";
           sql = sql+" AND fielding_period = get_fielding_period('"+pProgramLabel+"'))) prog";
           sql = sql+" where  prog.program_event_id = s.program_event_id";
           sql = sql+" order by prog.program_event_id, s.survey_question_label";
       } else 
       {
           sql = sql+" where  (study_type_code = '"+studyType+"'";
           //sql = sql+" AND supplement_code <> 'C'";
           sql = sql+" AND fielding_period =    get_fielding_period('"+pProgramLabel+"'))";
           sql = sql+" OR (study_type_code = '"+studyType+"'";
           //sql = sql+" AND supplement_code = 'C'";
           sql = sql+" AND program_label = '"+pProgramLabel+"')";
           sql = sql+" OR (study_type_code = '"+studyType+"'";
           //sql = sql+" AND supplement_code <> 'C'";
           sql = sql+" AND fielding_period = get_fielding_period('"+pProgramLabel+"'))) prog";
           sql = sql+" where  prog.program_event_id = s.program_event_id";
           sql = sql+" order by prog.program_event_id, s.survey_question_label";
       }
       JdbcTemplate jt = getJdbcTemplate();
       try {
        lst = jt.query(sql, new RowMapperResultReader(new GraphRowMapper()));
       } catch (Exception e)
       {
           log.error(e.getMessage(), e);
       }
       return lst;
   }
   class GraphRowMapper implements RowMapper
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
            Graph g= new Graph();
            g.setGroupQuestionLabel(rs.getString(1));
            g.setProgramEventId(rs.getString(2));
            g.setCountVerify(rs.getString(6));
            g.setCountUnverify(rs.getString(4));
            g.setCountTag(rs.getString(5));
            g.setCountNew(rs.getString(7));
            g.setTotalCount(rs.getString(3));
            g.setSurveyQuestionLabel(rs.getString(8));
            return g;
	      }       
	  }
   /**
    * @param String
    * @return List
    */
   public List getTaggedResponses(final String pTageTypeCode)
   {
       List lst = new ArrayList();
       String sql = "select group_question_label, response_orig_str, par1_parameter_id, par3_parameter_id,";
       sql = sql + " code1, code2, code3, code4, code5, code6,";
       sql = sql + " par2_parameter_id, targetrx_id, question_seq_no, thread_no, program_event_id,event_id,";
       sql = sql + " survey_question_label, response_extract_id, par4_parameter_id, question_id,status_code,lock_user,";
       sql = sql + " lock_flag,tag_type_code,tag_user";
       sql = sql + " from oec_response_staging";
       sql = sql + " where tag_type_code = 'review'";
       JdbcTemplate jt = getJdbcTemplate();
       try {
        lst = jt.query(sql, new RowMapperResultReader(new ReponseRowMapper()));
       } catch (Exception e)
       {
           log.error(e.getMessage(), e);
       }
       return lst;
   }
   /**
    * @param String
    * @param String
    * @return int
    */
   public int checkCodebookQues(final String pCodeBookId, final String pQuestionLabel)
   {
        int result = 0;
        log.debug("#checkCodebookQues QuestionLabel:: "+pQuestionLabel);
        if (pQuestionLabel.equalsIgnoreCase("null"))
        {
            return 1;
        } else
        {
            String sql = "select count(*) from codebook_questions";
            sql = sql + " where codebook_id = "+pCodeBookId;
            sql = sql + " and survey_question_label = '"+pQuestionLabel+"'";
            JdbcTemplate jt = getJdbcTemplate();
            try {
                result = jt.queryForInt(sql);
            } catch (Exception e)
            {
                log.error(e.getMessage(), e);
            }
        }
        return result;
   }
   /**
    * @param String
    * @return String
    */
   public String getPrevResponse(final String pPeid, final String pEid, final String pTid, final String pQid, final String pQsn)
   {
       String result = "";
       Connection conn = null;
       List lst = new ArrayList();
       JdbcTemplate jt = getJdbcTemplate();
       List params = new ArrayList();
       params.add (new SqlOutParameter("pS", Types.VARCHAR));
       Map results = jt.call(new ProcCallableStatementCreator(Integer.parseInt(pPeid), Integer.parseInt(pEid),Integer.parseInt(pTid),Integer.parseInt(pQid),Integer.parseInt(pQsn)), params);
       try {
        result = (String)results.get("pS");
       } catch (Exception e)
       {
           log.error(e.getMessage(), e);
       }
       return result;
   }
   private class ProcCallableStatementCreator implements CallableStatementCreator {
    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    
    public ProcCallableStatementCreator(int a, int b, int c, int d, int e) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
      this.e = e;
    }

    public CallableStatement createCallableStatement(Connection conn) throws SQLException {
      CallableStatement cs = conn.prepareCall("{? = call get_previous_response(?,?,?,?,?)}");
      cs.registerOutParameter (1, Types.VARCHAR);
      cs.setInt (2, a);
      cs.setInt (3, b);
      cs.setInt (4, c);
      cs.setInt (5, d);
      cs.setInt (6, e);
      return cs;
    }
  }  
   /**
    * @param String
    * @return List
    */
  public List getResponseView(String pProgramEventLabel) throws Exception
  {
      List lst = new ArrayList();
      String sql = "SELECT survey_question_label, group_question_label, response_orig_str,"+
                 " (SELECT parameter_value FROM parameters WHERE parameter_id = par1_parameter_id) AS par1,"+
                 " (SELECT parameter_value FROM parameters WHERE parameter_id = par2_parameter_id) AS par2,"+ 
                 " (SELECT parameter_value FROM parameters WHERE parameter_id = par3_parameter_id) AS par3,"+ 
                 " (SELECT parameter_value FROM parameters WHERE parameter_id = par4_parameter_id) AS par4,"+
                 " (SELECT parameter_value FROM parameters WHERE parameter_id = par5_parameter_id) AS par5 "+
                 " FROM oec_response_staging"+
                 " WHERE program_event_id in (select program_event_id  "+
                    "     from   inventory.program_facts  "+
                    "     where  (study_type_code <> 'AUDIT'"+
                    "     AND"+
                    "     program_label = '"+pProgramEventLabel+"')"+
                    "     OR  "+
                    "     (study_type_code = 'AUDIT'"+
                    "     AND  "+
                    "     supplement_code = 'C'"+
                    "     AND  "+
                    "     program_label = '"+pProgramEventLabel+"')  "+
                    "     OR   "+
                    "     (study_type_code = 'AUDIT'"+
                    "     AND  "+
                    "     supplement_code <> 'C'"+
                    "     AND  "+
                    "     fielding_period =    get_fielding_period('"+pProgramEventLabel+"')))"+
                 " ORDER by response_orig_str";
      JdbcTemplate jt = getJdbcTemplate();
      try
      {
          lst = jt.query(sql, new RowMapperResultReader(new ResponseViewRowMapper()));
      } catch (Exception e)
      {
          log.error(e.getMessage());
          throw e;
      }
      return lst;
  }
  class ResponseViewRowMapper implements RowMapper
  {
	      public Object mapRow(ResultSet rs, int index) throws SQLException 
        {
              Response resp = new Response();
              resp.setSurveyQuestionLabel(rs.getString(1));
              resp.setGroupQuestionLabel(rs.getString(2));
              resp.setResponseOrigStr(rs.getString(3));
              resp.setP1Value(rs.getString(4));
              resp.setP2Value(rs.getString(5));
              resp.setP3Value(rs.getString(6));
              resp.setP4Value(rs.getString(7));
              resp.setP5Value(rs.getString(8));
              return resp;
	      }
  }
  
  /**
   * @param String 
   * @param String
   * @return String 
   */
  public String resetResponses(final String pCodePosition, final String pCodeNum, final String pProgramLabel, final String pUser)
  {
     String result = "Responses have been reset.";
     String sql = "update oec_response_staging set status_code = 'new', updated_user = '"+pUser+"', updated_datetime = sysdate, lock_flag = 'N' where "+
             " code"+pCodePosition+" = '"+pCodeNum+"' AND status_code != 'new' "+
             " and program_event_id in (select program_event_id  "+
             "     from   program_facts  "+
             "     where  (study_type_code <> 'AUDIT'"+
             "     AND"+
             "     program_label = '"+pProgramLabel+"')"+
             "     OR  "+
             "     (study_type_code = 'AUDIT'"+
             "     AND  "+
             "     supplement_code = 'C'"+
             "     AND  "+
             "     program_label = '"+pProgramLabel+"')  "+
             "     OR   "+
             "     (study_type_code = 'AUDIT'"+
             "     AND  "+
             "     supplement_code <> 'C'"+
             "     AND  "+
             "     fielding_period =    get_fielding_period('"+pProgramLabel+"')))";
     JdbcTemplate jt = getJdbcTemplate();
     
     try
     {
        jt.execute(sql);
     } catch (Exception e)
     {
         log.error(e.getMessage(), e);
         result = "An error has occurred and the the responses have not been reset.";
     }
     return result;
  }
  
  /**
   * @param String CodePosition
   * @param String CodeNumber
   * @param String ProgramLabel
   * @return int
   */
  public int getResetCount(final String pCodePosition, final String pCodeNum, final String pProgramLabel)
  {
      int result = 0;
      String sql = "SELECT COUNT(*)"+
        " FROM oec_response_staging a"+
        " WHERE a.program_event_id in (select program_event_id "+
        "      from   program_facts  "+
        "      where  (study_type_code <> 'AUDIT'"+
        "      AND"+
        "      program_label = '"+pProgramLabel+"')"+
        "      OR  "+
        "      (study_type_code = 'AUDIT'"+
        "      AND  "+
        "      supplement_code = 'C'"+
        "      AND  "+
        "      program_label = '"+pProgramLabel+"')  "+
        "      OR   "+
        "      (study_type_code = 'AUDIT'"+
        "      AND  "+
        "      supplement_code <> 'C'"+
        "      AND "+
        "      fielding_period =    get_fielding_period('"+pProgramLabel+"')))"+
        " AND a.code"+pCodePosition+" = "+pCodeNum+" AND status_code != 'new'";
      JdbcTemplate jt = getJdbcTemplate();
      try
      {
          result = jt.queryForInt(sql);
      } catch (Exception e)
      {
          log.error(e.getMessage(), e);
      }
      return result;
  }
  /**
   * @return String
   * Method will put a job in the queue to load any OEC responses that is 
   * not in CODY at this time. User can access the Job Monitor to view
   * the status of the job.
   */
   public String loadOecResponse(final String pProgramLabel)
   {
       List lst = this.getProgramEventIds(pProgramLabel);
       JdbcTemplate jt = getJdbcTemplate();
       jt.execute("queueLoadOECResponses(inProgramEventID IN NUMBER, inEventID IN NUMBER, inContactArray IN TRX_ARRAY DEFAULT NULL, inRunDatetime IN DATE DEFAULT SYSDATE");
       return "A job has been added to the queue.";
   }
   /**
     * @param String
     * @return List
     * Method returns a list of program event ids for a patricular 
     * program label.
     */
    public List getProgramEventIds(final String pProgramLabel)
    {
        List lst = new ArrayList();
        String sql = "select program_event_id, event_id"+
            " from   program_facts"+  
            " where  (study_type_code <> 'AUDIT'"+
            " AND"+
            " program_label = '"+pProgramLabel+"')"+
            " OR"+  
            " (study_type_code = 'AUDIT'"+
            " AND  "+
            "  supplement_code = 'C'"+
            " AND "+
            " program_label = '"+pProgramLabel+"')"+
            " OR   "+
            " (study_type_code = 'AUDIT'"+
            " AND  "+
            " supplement_code <> 'C'"+
            " AND  "+
            " fielding_period =    get_fielding_period('"+pProgramLabel+"'))";
        JdbcTemplate jt = getJdbcTemplate();
        log.debug("#getProgramEventIds :: "+sql);
        try
        {
            lst = jt.queryForList(sql);             
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);            
        }
        return lst;
    }
    /**
     * @param String Date
     * @paranm String Program Label
     * This method allows the users to load Oec responses from responses facts
     * by queuing up jobs by program event id / event id
     */
    public String loadOec(final String pDate, final String pProgramLabel)
    {
        log.debug("pDate ================> "+pDate);
        log.debug("pProgramLabel ========> "+pProgramLabel);
        String result = "Loading";
        ProgramFactsDaoImpl pf = new ProgramFactsDaoImpl();
        List params = new ArrayList();
        int eventId = 0;
        JdbcTemplate jt = getJdbcTemplate();
        try {
            if (!pDate.equalsIgnoreCase("null"))
            {
                List pne = pf.findProgramAndEvents(pDate, jt);
                for (int i=0; i < pne.size(); i++)
                {
                    ProgramFacts pfs = (ProgramFacts) pne.get(i);
                    params.add (new SqlOutParameter("pS", Types.VARCHAR));
                    jt.call(new LoadCallableStatementCreator(Integer.parseInt(pfs.getProgramEvent()),Integer.parseInt(pfs.getEventId())),params);
                    params.clear();
                }
            } else
            {
                eventId = pf.findProgramEventEventId(pProgramLabel, jt);
                List pne = pf.findEventsByProgram(Integer.toString(eventId), jt);
                for (int i=0; i < pne.size(); i++)
                {
                   ProgramFacts pfs = (ProgramFacts) pne.get(i);
                   params.add (new SqlOutParameter("pS", Types.VARCHAR));
                   jt.call(new LoadCallableStatementCreator(Integer.parseInt(pfs.getProgramEvent()),Integer.parseInt(pfs.getEventId())),params);
                   params.clear();
                }
            }
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
            result = e.getMessage();
        }
        return result;
    }
    private class LoadCallableStatementCreator implements CallableStatementCreator 
    {
        private int a;
        private int b;       
    
        public LoadCallableStatementCreator(int a, int b) 
        {
          this.a = a;
          this.b = b;        

        }

        public CallableStatement createCallableStatement(Connection conn) throws SQLException 
        {
          CallableStatement cs = conn.prepareCall("{call NxOEC.queueLoadOECResponses(?,?,TRX_ARRAY('codesloaded@targetrx.com'),SYSDATE)}");
          cs.setInt(1, a);
          cs.setInt(2, b); 
          return cs;
        }
    }
    public List getDynamicReport(String pPeids, String pMarket, String pProduct, String pQuestions, String pCells, String pStartDate) throws Exception
    {
        List lst = null;
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
        String historySql = " union select distinct a.response_extract_id, a.program_event_id, a.targetrx_id, pf.cell_code1,"+
             " a.event_id,"+
             " (select parameter_value from parameters where parameter_id = a.par2_parameter_id) as prd,"+
             " (select parameter_value from parameters where parameter_id = a.par1_parameter_id) as mkt,"+
             " a.response_orig_str,"+
             " a.code1,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code1 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c1_label,"+
             " a.code2,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code2 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c2_label,"+
             " a.code3,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code3 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c3_label,"+
             " a.code4,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code4 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c4_label,"+
             " a.code5,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code5 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c5_label,"+
             " a.code6,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code6 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c6_label,"+
             " a.survey_question_label,"+
             " (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "  where cgpx.program_event_id = a.program_event_id"+
             "  and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "  and cq.survey_question_label = a.survey_question_label) as cbid"+
        " from cody.oec_response_staging_history a, person_facts pf"+
        " where a.program_event_id in ("+pPeids+")"+
        " and a.par1_parameter_id in ("+pMarket+")"+
        " and a.par2_parameter_id in ("+pProduct+")"+
        " and a.survey_question_label in ("+pQuestions+")"+
        " and pf.cell_code1 in ("+cells+")"+
        " and a.targetrx_id = pf.targetrx_id and pf.program_event_id = a.program_event_id and pf.event_id = a.event_id";
        String endSql = ") order by 2, 5, 3";
        String sql = "select * from (select distinct a.response_extract_id, a.program_event_id, a.targetrx_id, pf.cell_code1,"+
             " a.event_id,"+
             " (select parameter_value from parameters where parameter_id = a.par2_parameter_id) as prd,"+
             " (select parameter_value from parameters where parameter_id = a.par1_parameter_id) as mkt,"+
             " a.response_orig_str,"+
             " a.code1,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code1 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c1_label,"+
             " a.code2,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code2 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c2_label,"+
             " a.code3,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code3 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c3_label,"+
             " a.code4,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code4 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c4_label,"+
             " a.code5,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code5 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c5_label,"+
             " a.code6,"+
             " (select code_label from cody.codes c, cody.codebook_codes_xref ccx where c.code_num = a.code6 and ccx.code_id = c.code_id "+
             "       and ccx.codebook_id = (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "     where cgpx.program_event_id = a.program_event_id"+
             "     and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "     and cq.survey_question_label = a.survey_question_label)) as c6_label,"+
             " a.survey_question_label,"+
             " (select distinct cq.codebook_id from cody.codebook_groups_programs_xref cgpx, cody.codebook_questions cq, cody.codebooks_groups_xref cgx "+
             "  where cgpx.program_event_id = a.program_event_id"+
             "  and cgx.codebook_group_id = cgpx.codebook_group_id and cq.codebook_id = cgx.codebook_id"+
             "  and cq.survey_question_label = a.survey_question_label) as cbid"+
        " from cody.oec_response_staging a, person_facts pf"+
        " where a.program_event_id in ("+pPeids+")"+
        " and a.par1_parameter_id in ("+pMarket+")"+
        " and a.par2_parameter_id in ("+pProduct+")"+
        " and a.survey_question_label in ("+pQuestions+")"+
        " and pf.cell_code1 in ("+cells+")"+    
        " and a.targetrx_id = pf.targetrx_id and pf.program_event_id = a.program_event_id and pf.event_id = a.event_id";
        boolean data = isThereData(pStartDate);
        if (data)
        {
            sql = sql+historySql+endSql;
        } else
        {
            sql = sql+endSql;
        }  
        JdbcTemplate jt = getJdbcTemplate();        
        log.debug(pPeids+" "+pMarket+" "+pProduct+" "+pQuestions+" "+pCells);
      try
      {
          lst = jt.query(sql, new RowMapperResultReader(new ResponseDynamicRowMapper()));          
      } catch (Exception e)
      {
          log.error(e.getMessage());
          throw e;
      }
      
      return lst;
  }
  class ResponseDynamicRowMapper implements RowMapper
  {
	      public Object mapRow(ResultSet rs, int index) throws SQLException 
        {
              Response resp = new Response();
              resp.setProgramEventId(rs.getString(2));
              resp.setTargetrxId(rs.getString(3));
              resp.setCell(rs.getString(4));
              resp.setEventId(rs.getString(5));
              resp.setP2Value(rs.getString(6));
              resp.setP1Value(rs.getString(7));
              resp.setResponseOrigStr(rs.getString(8));
              resp.setCode1(rs.getString(9));
              resp.setCode1Label(rs.getString(10));
              resp.setCode2(rs.getString(11));
              resp.setCode2Label(rs.getString(12));
              resp.setCode3(rs.getString(13));
              resp.setCode3Label(rs.getString(14));
              resp.setCode4(rs.getString(15));
              resp.setCode4Label(rs.getString(16));
              resp.setCode5(rs.getString(17));
              resp.setCode5Label(rs.getString(18));
              resp.setCode6(rs.getString(19));
              resp.setCode6Label(rs.getString(20));
              resp.setSurveyQuestionLabel(rs.getString(21));
              
              return resp;
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
            log.error(e.getMessage());
        }
        return result;
    }
}
