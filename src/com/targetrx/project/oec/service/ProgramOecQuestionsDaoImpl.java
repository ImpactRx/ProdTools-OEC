package com.targetrx.project.oec.service;
/**
 *
 * @author pkukk
 */
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import com.targetrx.project.oec.bo.ProgramOecQuestions;

public class ProgramOecQuestionsDaoImpl extends JdbcDaoSupport implements ProgramOecQuestionsDao
{
    private Logger log = Logger.getLogger(this.getClass());
    public List getProgramOecQuestions(final String pProgramLabel)
    {
        List lst = new ArrayList();
        String sql = "select distinct program_event_id, survey_question_label";
        sql = sql +" from program_oec_questions";
        sql = sql +" where program_event_id in (";
        sql = sql +" select program_event_id";
        sql = sql +" from   program_facts";
        sql = sql +" where  (study_type_code <> 'AUDIT' AND program_label = '"+pProgramLabel+"'";
        sql = sql +" OR";
        sql = sql +" (study_type_code = 'AUDIT'";
        sql = sql +" AND";
        sql = sql +" supplement_code = 'C'";
        sql = sql +" AND";
        sql = sql +" program_label = '"+pProgramLabel+"')";
        sql = sql +" OR";
        sql = sql +" (study_type_code = 'AUDIT'";
        sql = sql +" AND";
        sql = sql +" supplement_code <> 'C'";
        sql = sql +" AND";
        sql = sql +" fielding_period = get_fielding_period('"+pProgramLabel+"')))) order by program_event_id, survey_question_label";
        JdbcTemplate jt = getJdbcTemplate();
        lst = jt.query(sql,new RowMapperResultReader(new GroupProgramOec()));
        return lst;
    }
    class  GroupProgramOec implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        ProgramOecQuestions grp = new ProgramOecQuestions();
          grp.setProgramEventId(rs.getString(1));
          grp.setSurveyQuestionLabel(rs.getString(2));
          return grp;
	      }      
	  }
    /**
     * Function inserts rows into two seperate tables PROGRAM_OEC_PROGRAMS, STUDY_OEC_QUESTIONS
     * @param String
     * @param String
     * @param String
     */
    public void saveProgramOecQuestions(final String pProgramEventIds, final String pSurveyQuestion, final String pStudyTypeCode)
    {
        JdbcTemplate jt = getJdbcTemplate();
        if (pProgramEventIds.indexOf("~") > 1)
        {
            StringTokenizer st = new StringTokenizer(pProgramEventIds,"~");
            while (st.hasMoreTokens())
            {
                jt.execute("insert into program_oec_questions (program_event_id, survey_question_label) values ( "+st.nextToken()+", '"+pSurveyQuestion+"')");
            } 
        }else
        {
            jt.execute("insert into program_oec_questions (program_event_id, survey_question_label) values ( "+pProgramEventIds+", '"+pSurveyQuestion+"')");
        }
        try
        {
            jt.execute("insert into study_oec_questions (study_type_code, survey_question_label) values ( '"+pStudyTypeCode+"', '"+pSurveyQuestion+"')");
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
    public void deleteProgramOecQuestion(final String pProgramEventId, final String pSurveyQuestionLabel)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "delete from program_oec_questions where program_event_id = "+pProgramEventId+" and survey_question_label = "+pSurveyQuestionLabel;
        log.debug("#deleteProgramOecQuestion: "+sql);
        try 
        {
            jt.execute(sql);
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
    public List getCustomOecQuestions(final String pProgramLabel)
    {
        List lst = new ArrayList();
        String sql = "select distinct program_event_id, survey_question_label, group_name";
        sql = sql +" from program_oec_custom_questions";
        sql = sql +" where program_event_id in (";
        sql = sql +" select program_event_id";
        sql = sql +" from   program_facts";
        sql = sql +" where  (study_type_code <> 'AUDIT' AND program_label = '"+pProgramLabel+"'";
        sql = sql +" OR";
        sql = sql +" (study_type_code = 'AUDIT'";
        sql = sql +" AND";
        sql = sql +" supplement_code = 'C'";
        sql = sql +" AND";
        sql = sql +" program_label = '"+pProgramLabel+"')";
        sql = sql +" OR";
        sql = sql +" (study_type_code = 'AUDIT'";
        sql = sql +" AND";
        sql = sql +" supplement_code <> 'C'";
        sql = sql +" AND";
        sql = sql +" fielding_period = get_fielding_period('"+pProgramLabel+"')))) order by program_event_id, survey_question_label";
        JdbcTemplate jt = getJdbcTemplate();
        lst = jt.query(sql,new RowMapperResultReader(new CustomProgramOec()));
        return lst;
    }
    class  CustomProgramOec implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        ProgramOecQuestions grp = new ProgramOecQuestions();
          grp.setProgramEventId(rs.getString(1));
          grp.setSurveyQuestionLabel(rs.getString(2));
          grp.setGroupName(rs.getString(3));
          return grp;
	      }      
	  }
     public void deleteCustomOecQuestion(String pProgramEventId, final String pSurveyQuestionLabel, final String pStudyTypeCode, final String pProgramLabel)
     {
        JdbcTemplate jt = getJdbcTemplate();
        if (pProgramEventId.equalsIgnoreCase(""))
        {
            pProgramEventId = this.getProgramEventId(pProgramLabel);
        }
        String sql = "delete from program_oec_custom_questions where program_event_id = "+pProgramEventId+" and survey_question_label = "+pSurveyQuestionLabel;
        jt.execute(sql);        
     }
    public void saveCustomOecQuestions(String pProgramEventIds, final String pSurveyQuestionLabel, final String pGroupName,final String pProgramLabel)
    {
        JdbcTemplate jt = getJdbcTemplate();
        if (pProgramEventIds.indexOf("~") > 1)
        {
            StringTokenizer st = new StringTokenizer(pProgramEventIds,"~");
            while (st.hasMoreTokens())
            {
                jt.execute("insert into program_oec_custom_questions (program_event_id, survey_question_label, group_name) values ( "+st.nextToken()+", '"+pSurveyQuestionLabel+"','"+pGroupName+"')");
            } 
        }else
        {
            pProgramEventIds = this.getProgramEventId(pProgramLabel);
            jt.execute("insert into program_oec_custom_questions (program_event_id, survey_question_label, group_name) values ( "+pProgramEventIds+", '"+pSurveyQuestionLabel+"','"+pGroupName+"')");
        }
    }
    private String getProgramEventId(final String pProgramLabel)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String result = "";
        String sql = "select program_event_id from program_facts  where program_label = ?";
        try {
            result =  (String) jt.queryForObject(sql, new Object[] { pProgramLabel },String.class);
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return result;
    }
    public Map getOecQuestionsBYMarketProduct(String pMarkets, String pProducts, String pBeginDate, String pEndDate)
    {
        String sql = "select distinct ors.survey_question_label"+
                " from cody.oec_response_staging ors"+
                " where ors.par1_parameter_id in ("+pMarkets+")"+
                " and ors.par2_parameter_id in ("+pProducts+")"+
                " and ors.added_datetime >= to_date('"+pBeginDate+"','mm/dd/yyyy')"+
                " and ors.added_datetime <= to_date('"+pEndDate+"','mm/dd/yyyy')"+
                " order by ors.survey_question_label";
        
        System.out.println(sql);
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        try 
        {
            lst = jt.query(sql, new RowMapperResultReader(new QuestionMapper()));
            for (int i=0; i<lst.size(); i++)
            {
                ProgramOecQuestions pcq = (ProgramOecQuestions)lst.get(i);
                if ((pcq.getSurveyQuestionLabel() != null) || (pcq.getSurveyQuestionLabel() != "null"))
                {
                    mp.put(pcq.getSurveyQuestionLabel(),pcq.getSurveyQuestionLabel());            
                }
            } 
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return mp;
    }
    /**
     * @param String Program Event Id
     * @return Map
     */
    public Map getOecQuestions(String pPeids)
    {
        String sql = "select distinct survey_question_label from program_oec_questions"+
            " where program_event_id in ("+pPeids+") "+
            " union"+
            " select distinct survey_question_label from  program_oec_custom_questions"+
            " where program_event_id in ("+pPeids+") "+" order by survey_question_label";
        
        System.out.println(sql);
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        try 
        {
            lst = jt.query(sql, new RowMapperResultReader(new QuestionMapper()));
            for (int i=0; i<lst.size(); i++)
            {
                ProgramOecQuestions pcq = (ProgramOecQuestions)lst.get(i);
                if ((pcq.getSurveyQuestionLabel() != null) || (pcq.getSurveyQuestionLabel() != "null"))
                {
                    mp.put(pcq.getSurveyQuestionLabel(),pcq.getSurveyQuestionLabel());            
                }
            } 
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return mp;
    }
    class QuestionMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int index) throws SQLException
        {
            ProgramOecQuestions poq = new ProgramOecQuestions();
            poq.setSurveyQuestionLabel(rs.getString(1));            
            return poq;
        }
    }
}
