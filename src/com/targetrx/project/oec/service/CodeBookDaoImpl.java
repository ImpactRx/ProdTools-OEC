package com.targetrx.project.oec.service;

import org.apache.log4j.Logger;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;

import com.targetrx.project.oec.bo.Change;
import com.targetrx.project.oec.bo.CodeBook;
import com.targetrx.project.oec.bo.CodeBookQuestions;
import com.targetrx.project.oec.bo.CodeBookView;
/**
 *
 * @author pkukk
 */
public class CodeBookDaoImpl extends JdbcDaoSupport implements CodeBookDao{
    private String SQL = "";
    private Logger log = Logger.getLogger(this.getClass());
    /**
     * @see com.targetrx.project.oec.service.CodeBookDao#updateCodeBookName(java.lang.String, java.lang.String)
     */
    public void updateCodeBookName(String pCodeBookId, String pCodeBookName)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "update codebooks set codebook_name = '"+pCodeBookName+"' where codebook_id = "+pCodeBookId;
        jt.execute(sql);
    }
    public Map getEveryCodeBook()
    {
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select codebook_id, codebook_name, description"+
                " from codebooks"+
                " order by 2";
        lst = jt.query(sql,new RowMapperResultReader(new CodeRowMapper()));
        for (int i=0; i<lst.size(); i++)
        {
            CodeBook cd = (CodeBook)lst.get(i);
            mp.put(cd.getCodeBookId(),cd.getCodeBookName());
        }
        return mp;
    }
    /**
     * @return
     */
    public Map<String, String> getCurrentCodeBooks()
    {       
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<CodeBook> list = null;
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        String query = 
        	"select distinct d.codebook_id, d.codebook_name, d.description 	" +
        	"from program_facts b, " +
        	"        codebook_groups_programs_xref a, " +
        	"        codebooks_groups_xref c, " +
        	"        codebooks d " +
        	"where a.program_event_id = b.program_event_id " +
        	"and c.codebook_group_id = a.codebook_group_id " +
        	"and c.codebook_id = d.codebook_id " +
        	"and b.oec_status_code = 'A' " +
        	"order by codebook_id";
        list =  jdbcTemplate.query(query, new CodeRowMapper());
        for (int i=0; i<list.size(); i++)
        {
            CodeBook cd = (CodeBook)list.get(i);
            map.put(cd.getCodeBookId(),cd.getCodeBookName());            
        } 
        return map; 
    }
    /**
     * Function returns all the CodeBooks that are in the CODEBOOKS table
     * @returns List
     **/
    public Map getAllCodeBooks()
    {       
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<CodeBook> list = null;
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        String query = 
        	"select distinct d.codebook_id, d.codebook_name, d.description 	" +
        	"from program_facts b, " +
        	"        codebook_groups_programs_xref a, " +
        	"        codebooks_groups_xref c, " +
        	"        codebooks d " +
        	"where a.program_event_id = b.program_event_id " +
        	"and c.codebook_group_id = a.codebook_group_id " +
        	"and c.codebook_id = d.codebook_id " +
        	"order by codebook_name";
        list =  jdbcTemplate.query(query, new CodeRowMapper());
        for (int i=0; i<list.size(); i++)
        {
            CodeBook cd = (CodeBook)list.get(i);
            map.put(cd.getCodeBookId(),cd.getCodeBookName());            
        } 
        return map; 
    }
    /**
     *
     */
    class CodeRowMapper implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        CodeBook cod = new CodeBook();
	        cod.setCodeBookId(rs.getString(1));
	        cod.setCodeBookName(rs.getString(2));
	        cod.setDescription(rs.getString(3));
	        return cod;
	      }      
	  }//class CodeRowMapper
    public Map getCodebookFromGroups(final String pCodebookGroupId)
    {
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        String sql = "select cb.codebook_id, cb.codebook_name, cb.description";
        sql = sql+" from codebooks cb, codebooks_groups_xref cx";
        sql = sql+" where cx.codebook_group_id = "+pCodebookGroupId;
        sql = sql+" and cb.codebook_id = cx.codebook_id";
        sql = sql+" order by cb.codebook_name";
        JdbcTemplate jt = getJdbcTemplate();
        lst =  jt.query(sql,new RowMapperResultReader(new CodeRowMapper()));
        for (int i=0; i<lst.size(); i++)
        {
            CodeBook cd = (CodeBook)lst.get(i);
            mp.put(cd.getCodeBookId(),cd.getCodeBookName());            
        } 
        return mp;        
    }
    /**
     * @param String
     * @param String
     * @param String
     */
    public void insertCodeBook(final String codeBookName, final String codeBookDesc, final String user)
    {
        JdbcTemplate jt = getJdbcTemplate();
        Object[] parameters = new Object[] { codeBookName, codeBookDesc, user };
        jt.update("insert into codebooks (codebook_name, description, created_user) values (?, ?, ?)",parameters);
    }
    
    public void deleteCodeBook(final String codeBookId)
    {
        
    }
    /**
     * @param String
     * @return List
     */
    public List getCodeBookName(final String codeBookId)
    {
        JdbcTemplate jt = getJdbcTemplate();
        return jt.query("select codebook_id, codebook_name, description from codebooks where codebook_id = "+codeBookId,new RowMapperResultReader(new CodeRowMapper()));
        
    }
    
     public int checkCodeBookName(final String pCodeBookName)
     {
         JdbcTemplate jt = getJdbcTemplate();
         String sql = "select codebook_id from codebooks where codebook_name = '"+pCodeBookName+"'";
         return jt.queryForInt(sql);
     }
     /**
      * @return Map
      * Returns all the Clonable Codebooks from the database. The template flag column must 
      * be set to 'Y' 
      */
     public Map getClonableCodeBooks()
     {
        Map mp = new LinkedHashMap();
        List lst = new ArrayList();
        
        JdbcTemplate jt = getJdbcTemplate();
        lst =  jt.query("select codebook_id, codebook_name, description from codebooks where template_flag = 'Y' order by codebook_name",new RowMapperResultReader(new CodeRowMapper()));
        for (int i=0; i<lst.size(); i++)
        {
            CodeBook cd = (CodeBook)lst.get(i);
            mp.put(cd.getCodeBookId(),cd.getCodeBookName());            
        } 
        return mp;
     }
     /**
      * @param String 
      * Method executes the PL/SQL function that will clone a codebook. The String
      * parameters that is passed in has to contain teh CodeBook Id and the CodeBook
      * Name delimited by a tilda.
      */
     public String  cloneCodeBook(String pCodeBookId, final String pCodeBookName)
     {
         JdbcTemplate jt = getJdbcTemplate();
         String result = null;
         String cid = "0";
         try {
            CloneBookCall call = new CloneBookCall(jt.getDataSource());
            Map res = call.execute(new Integer(cid), new Integer(pCodeBookId), pCodeBookName);
            for (Iterator it = res.entrySet().iterator(); it.hasNext(); ) {
                result = it.next().toString();
                result = result.substring(result.indexOf("=")+1,result.length());                
            }
         } catch (Exception e) {
             log.error(e.getMessage(), e);
         }
         return result;
     }
     private class CloneBookCall extends StoredProcedure
     {
        public CloneBookCall(DataSource ds)
        {
            super(ds, "codebook_util.clone_codebook");
            setFunction(false);
            declareParameter(new SqlOutParameter("cid", Types.INTEGER));
            declareParameter(new SqlParameter("codeBookId", Types.INTEGER));
            declareParameter(new SqlParameter("codeBookName", Types.VARCHAR));
            compile();
        }
        /**
         * @param cid
         * @param codeBookId
         * @param codeBookName
         */
        public Map execute(final Integer cid, final Integer codeBookId, final String codeBookName)
        {
            Map in = new HashMap(2);
            in.put("cid", cid);
            in.put("codeBookId", codeBookId);
            in.put("codeBookName", codeBookName);
            return execute(in);
        }
     }
     /**
      * @param String CodeBook Group Id
      * Method takes in the parameter and then uses that parameter to clone all the 
      * books in that CodeBook Group
      */
     public String cloneCodeBookGroup(final String pCodeBookGroupId)
     {
         JdbcTemplate jt = getJdbcTemplate();
         String result = null;
         String gid = "0";
         try {
            CloneGroupCall call = new CloneGroupCall(jt.getDataSource());
            Map res = call.execute(new Integer(gid), new Integer(pCodeBookGroupId));
            for (Iterator it = res.entrySet().iterator(); it.hasNext(); ) {
                result = it.next().toString();
                result = result.substring(result.indexOf("=")+1,result.length());                
            }
         } catch (Exception e) {
             log.error(e.getMessage(), e);
         }
         return result;
     }
     private class CloneGroupCall extends StoredProcedure
     {
        public CloneGroupCall(DataSource ds)
        {
            super(ds, "codebook_util.clone_codebook");
            setFunction(false);
            declareParameter(new SqlOutParameter("gid", Types.INTEGER));
            declareParameter(new SqlParameter("codeBookGroupId", Types.INTEGER));
            compile();
        }
        /**
         * @param cid
         * @param codeBookId
         * @param codeBookName
         */
        public Map execute(final Integer cid, final Integer codeBookGroupId)
        {
            Map in = new HashMap(1);
            in.put("gid", cid);
            in.put("codeBookGroupId", codeBookGroupId);
            return execute(in);
        }
     }
     
     /**
      * @param String CodebookId
      */
     public List viewCodeBook(final String pCodeBookId) throws Exception
     {
    	 try
    	 {
    	        List lst = null;
    	        String sql = "SELECT b.code_num,"+
    	                " b.code_label,"+
    	                " nvl((SELECT z.net1_id FROM codes_nets_xref z, nets y WHERE z.codebook_id = a.codebook_id AND z.code_id = a.code_id and y.net_id = z.net1_id and y.client_net_flag = 'N'),'') AS n1_id,"+
    	                " nvl((SELECT y.net_label FROM codes_nets_xref z, nets y WHERE z.codebook_id = a.codebook_id AND z.code_id = a.code_id AND y.net_id = z.net1_id and y.client_net_flag = 'N'),'') AS n1_label,"+
    	                " nvl((SELECT z.net2_id FROM codes_nets_xref z, nets y WHERE z.codebook_id = a.codebook_id AND z.code_id = a.code_id and y.net_id = z.net1_id and y.client_net_flag = 'N'),'') AS n2_id,"+
    	                " nvl((SELECT y.net_label FROM codes_nets_xref z, nets y WHERE z.codebook_id = a.codebook_id AND z.code_id = a.code_id AND y.net_id = z.net2_id and y.client_net_flag = 'N'),'') AS n2_label,"+
    	                " nvl((SELECT z.net3_id FROM codes_nets_xref z, nets y WHERE z.codebook_id = a.codebook_id AND z.code_id = a.code_id and y.net_id = z.net1_id and y.client_net_flag = 'N'),'') AS n3_id,"+
    	                " nvl((SELECT y.net_label FROM codes_nets_xref z, nets y WHERE z.codebook_id = a.codebook_id AND z.code_id = a.code_id AND y.net_id = z.net3_id and y.client_net_flag = 'N'),'') AS n3_label "+
    	                " FROM codebook_codes_xref a,"+
    	                " codes b"+	 
    	                " WHERE a.codebook_id = "+pCodeBookId+
    	                " AND b.code_id = a.code_id"+
    	                " AND b.client_code_flag = 'N'";
    	        log.debug("SQL #viewCodeBook "+sql);
    	        JdbcTemplate jt = getJdbcTemplate();
    	        lst = jt.query(sql, new RowMapperResultReader(new CBRowMapper()));
    	        return lst;
    	 } catch (Exception e)
    	 {
    		 log.error(e.getMessage(), e);
    		 throw e;
    	 }
     }
     class CBRowMapper implements RowMapper
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
            CodeBookView cb = new CodeBookView();
            cb.setCodeNum(rs.getString(1));
            cb.setCodeLabel(rs.getString(2));
            cb.setNet1Id(rs.getString(3));
            cb.setNet1Label(rs.getString(4));
            cb.setNet2Id(rs.getString(5));
            cb.setNet2Label(rs.getString(6));
            cb.setNet3Id(rs.getString(7));
            cb.setNet3Label(rs.getString(8));
            return cb;
	      }       
	  }
     /**
      * @param String CodebookId
      * @param String Month MM
      * @param String Year YYYY
      * @ return List
      * Method takes the three passed in parameters and queries the database for all the 
      * codes for a specific book for that month and year that have been changed.
      */
    public List viewChanges(final String pCodebookId, final String pMonth, final String pYear)
    {
        List lst = new ArrayList();
        String sql = "select (select codebook_name from codebooks where codebook_id = a.original_codebook_id) AS cbname,"+
        " b.code_num,"+ 
        " b.code_label,"+
        " a.history_action,"+
        " cody.concat_market_count(b.code_num,'"+pCodebookId+"','"+pMonth+"','"+pYear+"')"+
        " from cody.codes_history a,"+ 
        "    cody.codes b"+ 
        " where a.history_action_datetime > to_date('"+pMonth+"/01/"+pYear+"','mm/dd/yyyy')"+
        " and a.original_codebook_id = "+pCodebookId+ 
        " and a.history_action_datetime < to_date((SELECT to_char(LAST_DAY(to_date('"+pMonth+"/15/"+pYear+"','mm/dd/yyyy')),'mm/dd/yyyy') FROM dual),'mm/dd/yyyy')"+
        " and a.history_action_user != 'KMAHLENDORF'"+ 
        " and a.history_action in ('UPDATE','INSERT')"+ 
        " and b.code_id = a.code_id"+ 
        " and b.client_code_flag = 'N'"+
        " order by 1, b.code_label";
        log.debug("SQL #viewChanges "+sql);
        JdbcTemplate jt = getJdbcTemplate();
        lst = jt.query(sql, new RowMapperResultReader(new ChRowMapper()));
        return lst;        
    }
    class ChRowMapper implements RowMapper
    {
	     public Object mapRow(ResultSet rs, int index) throws SQLException {
            Change ch = new Change();
            ch.setCodebookLabel(rs.getString(1));
            ch.setCodeId(rs.getString(2));
            ch.setCodeLabel(rs.getString(3));
            ch.setHistoryAction(rs.getString(4));
            ch.setMarket(rs.getString(5));
            return ch;
	    } 
    }
    /**
     * @param String CodeBookId
     * @return List
     * Returns all the CodeBook Group Question Labels for that particular 
     * CodeBook.
     */
    public List getCodeBookQuestions(final String pCodebook)
    {
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        try
        {
            String sql = "select codebook_id, survey_question_label from codebook_questions where codebook_id = "+pCodebook;
            log.debug("#getCodebBookQuestions:: "+sql);
            lst =  jt.query(sql,new RowMapperResultReader(new GroupRowMapper()));            
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return lst;
    }
    class GroupRowMapper implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        CodeBookQuestions cbq = new CodeBookQuestions();
	        cbq.setCodebookId(rs.getString(1));
	        cbq.setSurveyQuestionLabel(rs.getString(2));
	        return cbq;
	      }      
	  }//class GroupRowMapper
    /**
     * @param String (CodeBook Id)
     * @param String (Group Quesiton Label)
     * @return String
     */
    public String saveCodebookQuestions(final String pCodebookId, final String pSurveyQuesLabel)
    {
       JdbcTemplate jt = getJdbcTemplate();
       String result = "Association has been saved";
       String sql = "insert into codebook_questions(codebook_id, survey_question_label) values ("+pCodebookId+",'"+pSurveyQuesLabel+"')";
       try
       {
           jt.execute(sql);
       } catch (Exception e)
       {
           result = "There has been a problem please contact your IT representative.";
           log.error(e.getMessage(), e);
       }
       return result;
    }
    /**
     * @param String (CodebookId)
     * @param String (Group Question Label)
     */
    public void deleteCodebookQuestions(final String pCodebookId, final String pSurveyQuesLabel)
    {
       JdbcTemplate jt = getJdbcTemplate();
       String sql = "delete from codebook_questions where codebook_id = "+pCodebookId+" and  survey_question_label = '"+pSurveyQuesLabel+"'";
       try
       {
           log.debug("# deleteCodebookQuestions:: "+sql);
           jt.execute(sql);
       } catch (Exception e)
       {
           log.error(e.getMessage(), e);
       }       
    }
}
