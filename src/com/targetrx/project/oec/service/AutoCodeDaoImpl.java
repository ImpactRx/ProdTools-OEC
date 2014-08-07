package com.targetrx.project.oec.service;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.apache.log4j.Logger;

import trx.util.trxJob;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.targetrx.project.oec.bo.AutoCode;
import com.targetrx.project.oec.bo.AutoDiscrep;
import com.targetrx.project.oec.bo.TagType;

/**
 *
 * @author pkukk
 */
public class AutoCodeDaoImpl extends JdbcDaoSupport implements AutoCodeDao {
    private Logger log = Logger.getLogger(this.getClass());
    /**
     * @param String AutoCode Id
     * @return List
     */
     public List getDictAutoCode(final String pAutoCodeId)
     {
         List lst = new ArrayList();
         JdbcTemplate jt = getJdbcTemplate();
         String sql = "select a.autocode_id, a.codebook_id, a.code1_id,"+
                " (select code_label from cody.codes  where code_id = a.code1_id),"+
                " a.code2_id, (select code_label from cody.codes where code_id = a.code2_id),"+
                " a.code3_id, (select code_label from cody.codes where code_id = a.code3_id),"+
                " a.code4_id, (select code_label from cody.codes where code_id = a.code4_id),"+ 
                " a.code5_id, (select code_label from cody.codes where code_id = a.code5_id),"+
                " a.code6_id, (select code_label from cody.codes where code_id = a.code6_id), "+
                " a.verbatim_str, a.status_code,"+
                " a.orig_response_extract_id, c.codebook_name"+
                " from autocode_dictionary a, codebooks c"+
                " where a.autocode_id = "+pAutoCodeId+
                " and	  c.codebook_id = a.codebook_id";
         
         try {
            lst = jt.query(sql, new RowMapperResultReader(new DictAutoCodeMapper()));
         } catch (Exception e)
         {
            log.error("SQL:: "+sql+"\n"+e.getMessage(), e);
         }
         return lst;
     }
     class DictAutoCodeMapper implements RowMapper
     {
         public Object mapRow(ResultSet rs, int index) throws SQLException {
            AutoCode ac = new AutoCode();
            ac.setAutoCodeId(rs.getString(1));
            ac.setCodeBookId(rs.getString(2));
            ac.setCode1Id(rs.getString(3));
            ac.setCode1Label(rs.getString(4));
            ac.setCode2Id(rs.getString(5));
            ac.setCode2Label(rs.getString(6));
            ac.setCode3Id(rs.getString(7));
            ac.setCode3Label(rs.getString(8));
            ac.setCode4Id(rs.getString(9));
            ac.setCode4Label(rs.getString(10));
            ac.setCode5Id(rs.getString(11));
            ac.setCode5Label(rs.getString(12));
            ac.setCode6Id(rs.getString(13));
            ac.setCode6Label(rs.getString(14));
            ac.setVerbatimStr(rs.getString(15));
            ac.setStatusCode(rs.getString(16));
            ac.setOrigResponseId(rs.getString(17));
            ac.setCodeBookName(rs.getString(18));
            return ac;
        }
     }
    /**
     * @param String STATUS CODE
     * @return List
     */
    public List getAutoCodes(final String pStatusCode)
    {
        List lst = new ArrayList();
        String sql = "select *";
        sql = sql + " from";
        sql = sql + " (select autocode_id, codebook_id, code1_id, code2_id, code3_id,";
        sql = sql + " code4_id, code5_id, code6_id, verbatim_str, status_code,";
        sql = sql + " orig_response_extract_id, occurrences_used, added_datetime";
        sql = sql + " from autocode_dictionary";
        sql = sql + " where status_code = '"+pStatusCode+"'";
        sql = sql + " and occurrences_used > 0";
        sql = sql + " order by occurrences_used desc)";
        sql = sql + " where rownum < 20";
        JdbcTemplate jt = getJdbcTemplate();
        try {
            lst = jt.query(sql, new RowMapperResultReader(new AutoCodeRowMapper()));
        } catch (Exception e)
        {
            log.error("SQL:: "+sql+"\n"+e.getMessage(), e);
        }
        return lst;
    }
     class AutoCodeRowMapper implements RowMapper
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
            AutoCode ac = new AutoCode();
            ac.setAutoCodeId(rs.getString(1));
            ac.setCodeBookId(rs.getString(2));
            ac.setCode1Id(rs.getString(3));
            ac.setCode2Id(rs.getString(4));
            ac.setCode3Id(rs.getString(5));
            ac.setCode4Id(rs.getString(6));
            ac.setCode5Id(rs.getString(7));
            ac.setCode6Id(rs.getString(8));
            ac.setVerbatimStr(rs.getString(9));
            ac.setStatusCode(rs.getString(10));
            ac.setOrigResponseId(rs.getString(11));
            return ac;
        }
     }        
      public List getAutoCodesByCodeBook(final String pCodeBookId, final String pStatusCode)
      {
          List lst = new ArrayList();
          String sql = "select *";
          sql = sql + " from";
          sql = sql + " (select autocode_id, codebook_id, code1_id, code2_id, code3_id,";
          sql = sql + " code4_id, code5_id, code6_id, verbatim_str, status_code,";
          sql = sql + " orig_response_extract_id, occurrences_used, added_datetime";
          sql = sql + " from autocode_dictionary";
          sql = sql + " where status_code = '"+pStatusCode+"'";
          sql = sql + " and codebook_id = "+pCodeBookId;
          sql = sql + " order by occurrences_used desc)";
          sql = sql + " where rownum < 20";
          JdbcTemplate jt = getJdbcTemplate();
          try {
            log.debug(sql);
            lst = jt.query(sql, new RowMapperResultReader(new AutoCodeRowMapper()));
          } catch (Exception e)
          {
            log.error("SQL:: "+sql+"\n"+e.getMessage(), e);
          }
        
          return lst;
      }
     /**
      * @param String AUTO CODE ID
      * @return List
      */
     public List getAutoCode(final String pAutoCodeId)
     {
        List lst = new ArrayList();
        String sql = "select a.autocode_id, a.codebook_id, a.code1_id, a.code2_id, a.code3_id,";
        sql = sql + " a.code4_id, a.code5_id, a.code6_id, a.verbatim_str, a.status_code,";
        sql = sql + " a.orig_response_extract_id, c.codebook_name,";
        sql = sql + " os.par1_parameter_id, os.par2_parameter_id, os.par3_parameter_id, os.par4_parameter_id, os.group_question_label,";
        sql = sql + " m.code_label,n.code_label,o.code_label,p.code_label,q.code_label,r.code_label";
        sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par1_parameter_id) as p1_value";
        sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par2_parameter_id) as p2_value";
        sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par3_parameter_id) as p3_value";
        sql = sql + " ,(select parameter_value from partributes.parameters where parameter_id = par4_parameter_id) as p4_value";
        sql = sql + " from autocode_dictionary a, codebooks c, oec_response_staging os,";
        sql = sql + " (select code_label, code_id from cody.codes co) m,";
        sql = sql + " (select code_label, code_id from cody.codes co) n,";
        sql = sql + " (select code_label, code_id from cody.codes co) o,";
        sql = sql + " (select code_label, code_id from cody.codes co) p,";
        sql = sql + " (select code_label, code_id from cody.codes co) q,";
        sql = sql + " (select code_label, code_id from cody.codes co) r";
        sql = sql + " where a.autocode_id = "+pAutoCodeId+"";
        sql = sql + " and c.codebook_id = a.codebook_id";
        sql = sql + " and os.response_extract_id = a.orig_response_extract_id";
        sql = sql + " and m.code_id (+)= a.code1_id";
        sql = sql + " and n.code_id (+)= a.code2_id";
        sql = sql + " and o.code_id (+)= a.code3_id";
        sql = sql + " and p.code_id (+)= a.code4_id";
        sql = sql + " and q.code_id (+)= a.code5_id";
        sql = sql + " and r.code_id (+)= a.code6_id";        
        
        log.debug("#getAutoCode ::"+sql);
        JdbcTemplate jt = getJdbcTemplate();
        try {
            lst = jt.query(sql, new RowMapperResultReader(new AutoCodeMapper()));
        } catch (Exception e)
        {
            log.error("SQL:: "+sql+"\n"+e.getMessage(), e);
        }
        if (lst.size() == 0)
        {
            sql = "";
            sql = "select a.autocode_id, a.codebook_id, a.code1_id, a.code2_id, a.code3_id,";
            sql = sql + " a.code4_id, a.code5_id, a.code6_id, a.verbatim_str, a.status_code,";
            sql = sql + " a.orig_response_extract_id, c.codebook_name";
            sql = sql + " from autocode_dictionary a, codebooks c";
            sql = sql + " where a.autocode_id = "+pAutoCodeId;
            sql = sql + " and c.codebook_id = a.codebook_id";
            try {
                lst = jt.query(sql, new RowMapperResultReader(new AutoCode1Mapper()));
            } catch (Exception e)
            {
                log.error("SQL:: "+sql+"\n"+e.getMessage(), e);
            }
        }
        return lst;
     }
     class AutoCodeMapper implements RowMapper
     {
         public Object mapRow(ResultSet rs, int index) throws SQLException {
            AutoCode ac = new AutoCode();
            ac.setAutoCodeId(rs.getString(1));
            ac.setCodeBookId(rs.getString(2));
            ac.setCode1Id(rs.getString(3));
            ac.setCode2Id(rs.getString(4));
            ac.setCode3Id(rs.getString(5));
            ac.setCode4Id(rs.getString(6));
            ac.setCode5Id(rs.getString(7));
            ac.setCode6Id(rs.getString(8));
            ac.setVerbatimStr(rs.getString(9));
            ac.setStatusCode(rs.getString(10));
            ac.setOrigResponseId(rs.getString(11));
            ac.setCodeBookName(rs.getString(12));
            ac.setPar1(rs.getString(13));
            ac.setPar2(rs.getString(14));
            ac.setPar3(rs.getString(15));
            ac.setPar4(rs.getString(16));
            ac.setGroupQuestionLabel(rs.getString(17));
            ac.setCode1Label(rs.getString(18));
            ac.setCode2Label(rs.getString(19));
            ac.setCode3Label(rs.getString(20));
            ac.setCode4Label(rs.getString(21));
            ac.setCode5Label(rs.getString(22));
            ac.setCode6Label(rs.getString(23));
            ac.setPar1Value(rs.getString(24));
            ac.setPar2Value(rs.getString(25));
            ac.setPar3Value(rs.getString(26));
            ac.setPar4Value(rs.getString(27));
            return ac;
        }
     }
     class AutoCode1Mapper implements RowMapper
     {
         public Object mapRow(ResultSet rs, int index) throws SQLException {
            AutoCode ac = new AutoCode();
            ac.setAutoCodeId(rs.getString(1));
            ac.setCodeBookId(rs.getString(2));
            ac.setCode1Id(rs.getString(3));
            ac.setCode2Id(rs.getString(4));
            ac.setCode3Id(rs.getString(5));
            ac.setCode4Id(rs.getString(6));
            ac.setCode5Id(rs.getString(7));
            ac.setCode6Id(rs.getString(8));
            ac.setVerbatimStr(rs.getString(9));
            ac.setStatusCode(rs.getString(10));
            ac.setOrigResponseId(rs.getString(11));
            ac.setCodeBookName(rs.getString(12));
            return ac;
        }
     }
     public List getAutoCodeException(final String pAutoCodeId)
     {
         List lst = new ArrayList();
         JdbcTemplate jt = getJdbcTemplate();
         String sql = "select a.autocode_id, c.codebook_id,";
         sql = sql +"        cody.autocoder.get_code_id(c.codebook_id, b.code1) as code1_id, cody.autocoder.get_code_id(c.codebook_id, b.code2) as code2_id,";
         sql = sql +"        cody.autocoder.get_code_id(c.codebook_id, b.code3) as code3_id, cody.autocoder.get_code_id(c.codebook_id, b.code4) as code4_id,";
         sql = sql +"        cody.autocoder.get_code_id(c.codebook_id, b.code5) as code5_id, cody.autocoder.get_code_id(c.codebook_id, b.code6) as code6_id,";
         sql = sql +"        b.response_orig_str,b.status_code, a.response_extract_id, d.codebook_name,";
         sql = sql +"        b.par1_parameter_id, b.par2_parameter_id, b.par3_parameter_id, b.par4_parameter_id, b.group_question_label,";
         sql = sql +"        m.code_label, n.code_label, o.code_label, p.code_label, q.code_label, r.code_label,";
         sql = sql +"        (select parameter_value from partributes.parameters where parameter_id = b.par1_parameter_id) as p1_value,";
         sql = sql +"        (select parameter_value from partributes.parameters where parameter_id = b.par2_parameter_id) as p2_value,";
         sql = sql +"        (select parameter_value from partributes.parameters where parameter_id = b.par3_parameter_id) as p3_value,";
         sql = sql +"        (select parameter_value from partributes.parameters where parameter_id = b.par4_parameter_id) as p4_value";
         sql = sql +" from autocode_dictionary_exceptions a, oec_response_staging b, autocode_dictionary c, codebooks d,";
         sql = sql +"      (select code_label, code_id from cody.codes co) m,";
         sql = sql +"      (select code_label, code_id from cody.codes co) n,";
         sql = sql +"      (select code_label, code_id from cody.codes co) o,";
         sql = sql +"      (select code_label, code_id from cody.codes co) p,";
         sql = sql +"      (select code_label, code_id from cody.codes co) q,";
         sql = sql +"      (select code_label, code_id from cody.codes co) r";
         sql = sql +" where a.autocode_id = "+pAutoCodeId;
         sql = sql +" and   c.autocode_id = a.autocode_id";
         sql = sql +" and   d.codebook_id = c.codebook_id";
         sql = sql +" and   b.response_extract_id = a.response_extract_id";
         sql = sql +" and   m.code_id (+)= code1_id";
         sql = sql +" and   n.code_id (+)= code2_id";
         sql = sql +" and   o.code_id (+)= code3_id";
         sql = sql +" and   p.code_id (+)= code4_id";
         sql = sql +" and   q.code_id (+)= code5_id";
         sql = sql +" and   r.code_id (+)= code6_id";
         try {
            lst =jt.query(sql, new RowMapperResultReader(new AutoCodeMapper()));
         } catch (Exception e)
         {
            log.error("SQL:: "+sql+"\n"+e.getMessage(), e);
         }
         return lst;
     }
     /**
      * @param String Auto Code Id
      * Method updates the AUTOCODE_DICTIONARY table by setting the status code
      * to verified.
      */
     public void updateStatusCode(final String pAutoCodeId,
                                                   final String pStatusCode, 
                                                   final String pUpdater)
     {
         JdbcTemplate jt = getJdbcTemplate();
        log.debug("auto code id="+pAutoCodeId+"; status code="+pStatusCode+"; user="+pUpdater);
        String sql = 
            "update autocode_dictionary set status_code = '"+pStatusCode+
            "', updated_user = '"+pUpdater+
            "' where autocode_id = "+pAutoCodeId;
        try {
            jt.execute(sql);
        } catch (Exception e)
        {
            log.error("SQL:: "+sql+"\n"+e.getMessage(), e);
        }
     }
     /**
      * Queues up a AutoCode Discrepancy Job
      */
     public void exeAutoCodeDiscrepReport(String pUser)
     {
        JdbcTemplate jt = getJdbcTemplate();
        Calendar to = Calendar.getInstance();
        int to_month = to.get(Calendar.MONTH)+1;
        if (to_month == 13) {
            to_month = 1;
        }
        String in_date = to_month+"/01/"+to.get(Calendar.YEAR);
        try
        {
        	jt.execute(new TrxJobCallback(in_date, pUser));
        } catch (Exception e)
        {
           log.error("Error occurred while trying to execte PL/SQL procedure. "+e.getMessage(), e);
        }
     }
     /**
      * @return job sequence
      * @author sstuart
      *
      */
     private class TrxJobCallback implements ConnectionCallback
     {
    	private String date = null;
    	private String user = null;
    	/**
    	 * @param date
    	 * @param user
    	 */
		public TrxJobCallback(String date, String user)
		{
			this.date = date;
			this.user = user;
		}
		@Override
		public Object doInConnection(Connection con) throws SQLException,
				DataAccessException
		{
			int jobSeq = trxJob.add(con, "POP_AUTOCODE_DICT_MONTH_END", null);
			trxJob.setJobParam(con, jobSeq, "JOB_SEQ", jobSeq);
			trxJob.setJobParam(con, jobSeq, "DATE", date);
			trxJob.setJobParam(con, jobSeq, "USER", user);
			trxJob.queue(con, jobSeq);
			return new Integer(jobSeq);
		}
     }
     /**
      * 
      */
     public List getAutoCodeDicreps(final String pCodeBook)
     {
         List lst = new ArrayList();
         JdbcTemplate jt = getJdbcTemplate();
         
         String sql = "select a.autocode_id, c.response_extract_id,"+
                      "  a.codebook_id,"+ 
                      "  autocoder.get_code_num(a.code1_id) as acode1,"+
                      "  autocoder.get_code_num(a.code2_id) as acode2,"+ 
                      "  autocoder.get_code_num(a.code3_id) as acode3,"+
                      "  autocoder.get_code_num(a.code4_id) as acode4,"+
                      "  autocoder.get_code_num(a.code5_id) as acode5,  autocoder.get_code_num(a.code6_id) as code6,"+ 
                      "  c.code1 as ocode1, c.code2 as ocode2, c.code3 as ocode3, c.code4 as ocode4, c.code5 as ocode5, c.code6 as ocode6, c.response_orig_str,"+
                      "  a.verbatim_str, a.status_code"+	
                      "  from autocode_dictionary a, oec_response_staging c, autocode_dictionary_exceptions d "+
                      "  where a.autocode_id = d.autocode_id"+
                      "  and c.response_extract_id = d.response_extract_id"+
                      "  and a.codebook_id = "+pCodeBook +
                      "  and rownum <= 1 ";
         try {
             lst = jt.query(sql, new RowMapperResultReader(new AutoDiscrepMapper()));
         } catch (Exception e)
         {
             log.error("Error occurred in AutoCodeDaoImpl#getAutoCodeDiscreps "+e.getMessage(), e);
         }
         return lst;
     }
     class AutoDiscrepMapper implements RowMapper
     {
         public Object mapRow(ResultSet rs, int index) throws SQLException {
            AutoDiscrep ac = new AutoDiscrep();
            ac.setAutocodeId(rs.getString(1));
            ac.setReponseId(rs.getString(2));
            ac.setCodebookId(rs.getString(3));
            ac.setAcode1(rs.getString(4));
            ac.setAcode2(rs.getString(5));
            ac.setAcode3(rs.getString(6));
            ac.setAcode4(rs.getString(7));
            ac.setAcode5(rs.getString(8));
            ac.setAcode6(rs.getString(9));
            ac.setOcode1(rs.getString(10));
            ac.setOcode2(rs.getString(11));
            ac.setOcode3(rs.getString(12));
            ac.setOcode4(rs.getString(13));
            ac.setOcode5(rs.getString(14));
            ac.setOcode6(rs.getString(15));
            ac.setReponseOrigStr(rs.getString(16));
            ac.setVerbatimStr(rs.getString(17));
            ac.setStatusCode(rs.getString(18));
            return ac;
        }
     }
    public void updateDiscrepancy(final String pAutoCodeId, final String pResponseId, final String pCodebookId, final String pCode1, 
            final String pCode2, final String pCode3, final String pCode4, final String pCode5, final String pCode6, final String pUser, final String pOrig)
    {
        JdbcTemplate jt = getJdbcTemplate();
        log.debug("Updating the discrepancy AutoCode Id "+pAutoCodeId+" Reponse Id "+pResponseId+" CodeBookId = "+pCodebookId);
        String sql = "begin month_end.fixDiscrepancy("+pAutoCodeId+","+pResponseId+","+pCodebookId+","+pCode1+","+
                pCode2+","+pCode3+","+pCode4+","+pCode5+","+pCode6+",'"+pUser+"','"+pOrig+"'); end;";
        try {
            jt.execute(sql);
        } catch (Exception e)
        {
            log.error("Error occurred while trying to execte PL/SQL procedure. "+e.getMessage(), e);
        }
    }
    public List getAutoCodeDetailReport()
    {
        List lst = new ArrayList();
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select distinct(o.program_event_id)"+
            " , prog.program_label"+
            " , tags.tag_type_code"+
            " , tags.description"+
            " , (select count(*) "+
            " from oec_response_staging"+
            " where tag_type_code = tags.tag_type_code"+
            " and program_event_id = o.program_event_id) as DISCREP"+
            " from oec_response_staging o"+
            " , inventory.program_facts prog"+
            " , (select tag_type_code, description from oec_tag_types) tags"+
            " where prog.program_event_id = o.program_event_id"+
            " and prog.oec_status_code = 'A'"+
            " order by o.program_event_id desc";
        try {
            lst = jt.query(sql, new RowMapperResultReader(new TagTypeMapper()));
        } catch (Exception e)
        {
           log.error(e.getMessage(), e);
        }
        return lst;
    }
    class TagTypeMapper implements RowMapper
     {
         public Object mapRow(ResultSet rs, int index) throws SQLException {
            TagType tt = new TagType();
            tt.setProgramEventId(rs.getString(1));
            tt.setProgramLabel(rs.getString(2));
            tt.setTagTypeCode(rs.getString(3));
            tt.setDescription(rs.getString(4));
            tt.setDiscrepNumber(rs.getString(5));
            return tt;
        }
     }
    /**
     * @erturn String
     */
    public String populateDictionary()
    {
        String result = "Process has been started.";
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "begin exeAutoCodeDiscrepancy; end;";
        try
        {
            jt.execute(sql);
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
            result = "There has been a problem please notify an IT representative.";
        }
        return result;
    }
    /**
     * @param String ProgramLabel
     * @return String
     */
    public String autoCodeResponses(final String pProgramLabel)
    {
        String result = "Autocoding has been started...";
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "";
        List lst = this.getProgramEventIds(pProgramLabel);
        try
        {
            for (int i = 0; i < lst.size(); i++)
            {
               String item = lst.get(i).toString();
               log.debug(item.substring(item.indexOf("=")+1, item.length()-1));
               sql = "begin EXEAUTOCODE('"+item.substring(item.indexOf("=")+1, item.length()-1)+"'); end;";
               jt.execute(sql);
            }            
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
            result = "There has been a problem please notify an IT representative.";
        }
        return result;
                
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
        String sql = "select program_event_id"+
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
        try
        {
            lst = jt.queryForList(sql);             
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);            
        }
        return lst;
    }
}
