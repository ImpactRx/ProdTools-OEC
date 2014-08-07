package com.targetrx.project.oec.service;

import javax.swing.JTabbedPane;
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
import com.targetrx.project.oec.bo.CodeBookGroup;
/**
 *
 * @author pkukk
 */
public class CodebookGroupDaoImpl extends JdbcDaoSupport implements CodebookGroupDao {
    private Logger log = Logger.getLogger(this.getClass());
    /**
     * @return Map
     * Returns all the Codebook Groups from the database
     */
    public Map getAllCodebookGroup()
    {
       List lst = new ArrayList();
       Map mp = new LinkedHashMap();
       String sql = "select codebook_group_id, group_name, description from cody.codebook_groups order by group_name";
       JdbcTemplate jt = getJdbcTemplate();
       lst = jt.query(sql, new RowMapperResultReader(new CBRowMapper()));
       for (int i=0; i<lst.size(); i++)
       {
            CodeBookGroup cb = (CodeBookGroup)lst.get(i);
            mp.put(cb.getCodebookGroupId(),cb.getGroupName());            
       }  
       return mp;
    }
    class CBRowMapper implements RowMapper
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
            CodeBookGroup cb = new CodeBookGroup();
            cb.setCodebookGroupId(rs.getString(1));
            cb.setGroupName(rs.getString(2));
            cb.setDescription(rs.getString(3));
            return cb;
	      }       
	  }
    
    /**
     * @param String
     * @return List
     * Method returns a list of Codebook Groups based on the Codebook
     * group Id passed in
     */
    public List getCodebookGroup(final String pCodebookGroupId)
    {
        List lst = new ArrayList();
        //String sql = "select codebook_group_id, group_name, description from cody.codebook_groups";
        //sql = sql+" where codebook_group_id = "+pCodebookGroupId;
        String sql = "select codebook_group_id, group_name, description from cody.codebook_groups";
        sql = sql+" where codebook_group_id = ? order by group_name";
        JdbcTemplate jt = getJdbcTemplate();
        try 
        {
            //lst = jt.query(sql, new RowMapperResultReader(new CBRowMapper()));
            lst = jt.query(sql, new Object[] {pCodebookGroupId}, new CBRowMapper());
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return lst;
    }
    /**
     * @param String Codebooggroup Id
     * @param String Group Name
     * @param String Description
     * Adds a a new Codebook Group to the database     
     */
    public void addCodebookGroup(final String pCodebookGrpId, final String pGroupName, final String pDescription)
    {
        //String sql = "insert into codebook_groups (codebook_group_id, group_name, description) values ("+pCodebookGrpId+", '"+pGroupName+"', '"+pDescription+"')";
        String sql = "insert into codebook_groups (codebook_group_id, group_name, description) values (?, ?, ?)";
        JdbcTemplate jt = getJdbcTemplate();
        try
        {
            //jt.execute(sql);
            jt.update(sql, new Object[] {pCodebookGrpId, pGroupName, pDescription});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
    /**
     * @return int
     * Method gets the next Id for the Codebook Group 
     */
    public int getNextCodebookGroupId()
    {
        int result = 0;
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select codebook_group_id_seq.nextval from dual";
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
     * @param String Codebook Group Id
     * Method updates the codebook group from the codebook group id that is passed in
     */
    public void updateCodebookGroup(final String pCodebookGrpId, final String pGroupName, final String pDescription)
    {
        JdbcTemplate jt = getJdbcTemplate();
        //String sql = "update codebook_groups set group_name = '"+pGroupName+"', description = '"+pDescription+"' where codebook_group_id = "+pCodebookGrpId;
        String sql = "update codebook_groups set group_name = ?, description = ? where codebook_group_id = ?";
        try 
        {
            //jt.execute(sql);
            jt.update(sql, new Object[] {pGroupName, pDescription, pCodebookGrpId});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
    /**
     * @param String Codebook Group Id
     * Method removes all associations for this Codebook Group Id
     */
    public void deleteCodebookGroup(final String pCodebookGrpId)
    {
        JdbcTemplate jt = getJdbcTemplate();
        //String sql = "delete from codebooks_groups_xref where codebook_group_id = "+pCodebookGrpId;
        //String sql1 = "delete from codebook_groups_programs_xref where codebook_group_id = "+pCodebookGrpId;
        //String sql2 = "delete from codebook_groups where codebook_group_id = "+pCodebookGrpId;
        String sql = "delete from codebooks_groups_xref where codebook_group_id = ?";
        String sql1 = "delete from codebook_groups_programs_xref where codebook_group_id = ?";
        String sql2 = "delete from codebook_groups where codebook_group_id = ?";
        try
        {
            jt.update(sql, new Object[] {pCodebookGrpId});
            jt.update(sql1, new Object[] {pCodebookGrpId});
            jt.update(sql2, new Object[] {pCodebookGrpId});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
    /**
     * @param String Codebook Group Id
     * Method inserts a row into codebooks_groups_xref
     */
    public void assignCodeBook(final String pCodebookGrpId, final String pCodebookId)
    {
        JdbcTemplate jt = getJdbcTemplate();
        //String sql = "insert into codebooks_groups_xref (codebook_group_id, codebook_id) values ("+pCodebookGrpId+", "+pCodebookId+")";
        String sql = "insert into codebooks_groups_xref (codebook_group_id, codebook_id) values (?, ?)";
        try 
        {
            //jt.execute(sql);
            jt.update(sql, new Object[] {pCodebookGrpId,pCodebookId});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
    /**
     * @param String Program Event Id
     * @return Map
     * Return all Codebook Groups that does not have a Program Event Id associated to it
     */
    public Map getCodebookGroups(final String pProgramEventId)
    {
       JdbcTemplate jt = getJdbcTemplate();
       String sql = "select distinct cb.codebook_group_id, cb.group_name, nvl(b.cbid, -99)";
       sql = sql +" from cody.codebook_groups cb,";
       sql = sql +" (select nvl(cx.codebook_group_id,-99) as cbid";
       sql = sql +" from codebook_groups_programs_xref cx";
       sql = sql +" where cx.program_event_id = ?) b";
       sql = sql +" where cb.codebook_group_id = b.cbid(+) order by cb.group_name";
       List lst = new ArrayList();
       Map mp = new LinkedHashMap();
       //lst = jt.query(sql, new RowMapperResultReader(new CodeBookGrpRowMapper()));
       lst = jt.query(sql, new Object[] {pProgramEventId}, new CodeBookGrpRowMapper());
       
       for (int i = 0; i < lst.size(); i++)
       {
           CodeBookGroup cb = (CodeBookGroup)lst.get(i);
           if (cb.getProgramEvent().equalsIgnoreCase("-99"))
           {
            mp.put(cb.getCodebookGroupId(),cb.getGroupName()); 
           }
       }
       return mp;
    }
    class CodeBookGrpRowMapper implements RowMapper
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
            CodeBookGroup cb = new CodeBookGroup();
            cb.setCodebookGroupId(rs.getString(1));
            cb.setProgramEvent(rs.getString(3));
            cb.setGroupName(rs.getString(2));
            return cb;
	      }       
	  }
    /**
     * @param String Program Event Id
     * @param String Codebook Id
     * Method adds a row to the CODEBOOK_GROUPS_PROGRAMS_XREF table
     */
    public void addCodebookGroupsPrograms(final String pProgramEventId, final String pCodebookId)
    {
        JdbcTemplate jt = getJdbcTemplate();
        //String sql = "insert into codebook_groups_programs_xref (codebook_group_id, program_event_id)";
        //sql = sql + " values ("+pCodebookId+", "+pProgramEventId+")";
        String sql = "insert into codebook_groups_programs_xref (codebook_group_id, program_event_id)";
        sql = sql + " values (?, ?)";
        try 
        {
            //jt.execute(sql);
            jt.update(sql, new Object[] {pCodebookId,pProgramEventId});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        
    }
    
    public List getCodebookGroupAssoc(final String pProgramEventId)
    {
        List lst = new ArrayList();
        String sql = "select cgpx.codebook_group_id,"+
                 "   (select group_name from codebook_groups where codebook_group_id = cgpx.codebook_group_id), "+ 
                 "   (select oec_status_code from program_facts where program_event_id = ?) "+
                 " from codebook_groups_programs_xref cgpx"+
                 " where cgpx.program_event_id = ?";
        JdbcTemplate jt = getJdbcTemplate();
        //System.out.println(sql + " :: "+pProgramEventId);
        try 
        {
            //lst = jt.query(sql, new RowMapperResultReader(new CBRowMapper()));
            lst = jt.query(sql, new Object[] {pProgramEventId, pProgramEventId},new CBGRowMapper());
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
       
       return lst;
    }
    class CBGRowMapper implements RowMapper
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
            CodeBookGroup cb = new CodeBookGroup();
            cb.setCodebookGroupId(rs.getString(1));
            cb.setGroupName(rs.getString(2));
            cb.setActiveEvent(rs.getString(3));
            return cb;
	      }       
	  }
    
    public void deleteAssoc(String pProgramEventId)
    {
       String sql = "delete from codebook_groups_programs_xref where program_event_id = ?";
        JdbcTemplate jt = getJdbcTemplate();
        try 
        {
            //jt.execute(sql);
            jt.update(sql, new Object[] {pProgramEventId});
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        } 
    }
}
