package com.targetrx.project.oec.service;

import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.SqlOutParameter;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import com.targetrx.project.oec.bo.CustomMap;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 *
 * @author pkukk
 */
public class CustomMapDaoImpl extends JdbcDaoSupport implements CustomMapDao
{
    private Logger log = Logger.getLogger(this.getClass());

    public Map getCustomMaps(String pClientId)
    {
			Map mp = new LinkedHashMap();
		  List lst = new ArrayList();
		  String sql = "select mapping_id, client_id, mapping_label"+
						" from cody.custom_mapping where client_id = "+pClientId+
						" order by mapping_label";
		  System.out.println(sql);
			try
			{
				JdbcTemplate jt = getJdbcTemplate();
				lst = jt.query(sql,new RowMapperResultReader(new CustomRowMapper()));
			} catch (Exception e)
			{
				log.error(e.getMessage(), e);
			}
      for (int i=0; i<lst.size(); i++)
			{
       	CustomMap cm = (CustomMap)lst.get(i);
				mp.put(cm.getMappingId(),cm.getMappingLabel()+"("+cm.getMappingId()+")");
			}
		  return mp;
		}

		class CustomRowMapper implements RowMapper {
			public Object mapRow(ResultSet rs, int index) throws SQLException {
				CustomMap cm = new CustomMap();
				cm.setMappingId(rs.getString(1));
				cm.setClientId(rs.getString(2));
				cm.setMappingLabel(rs.getString(3));
			  return cm;
			}
		}//class CustomRowMapper

        class CustomRowMapper2 implements RowMapper {
            public Object mapRow(ResultSet rs, int index) throws SQLException {
                CustomMap cm = new CustomMap();
                cm.setMappingId(rs.getString(1));
                cm.setClientId(rs.getString(2));
                cm.setMappingLabel(rs.getString(3));
                cm.setClientName(rs.getString(4));
              return cm;
            }
        }
        
		/**
         * @see com.targetrx.project.oec.service.CustomMapDao#getAllCustomMappings()
         */
        public List getAllCustomMappings()
        {
            List list = null;
            String sql = "select mapping_id, " +
                              "         client_id, " +
                              "         mapping_label," +
                              "         parameter_value "+
                             "from custom_mapping a, " +
                             "        parameters b " +
                             "where a.client_id = b.parameter_id " +
                             "order by b.parameter_value, a.mapping_label";
                JdbcTemplate jt = getJdbcTemplate();
                list = jt.query(sql, new RowMapperResultReader(new CustomRowMapper2()));
            return list;
        }
        
        public String insertCustomMap(String pClientId, String pMappingLabel, String pUser)
		{
      String result = "New Client Mapping created.";  
			
      try
			{
        int mapId = this.getNextMappingId();
        String sql = "insert into cody.custom_mapping (mapping_id, client_id, mapping_label, added_user, added_datetime, active_flag)"+
					" values ("+mapId+","+pClientId+",'"+pMappingLabel+"',"+
					"'"+pUser+"', sysdate, 'Y')";
				JdbcTemplate jt = getJdbcTemplate();
				jt.execute(sql);
			} catch (Exception e)
			{
				log.error(e.getMessage());
        return "Failed to create new Client Mapping.";
			}
      return result;
		}
    /**
     * @return int
     */
    public int getNextMappingId()
    {
        int result = 0;
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select cody.mapping_id_seq.nextval from dual";
        try {
            result = jt.queryForInt(sql);
        } catch (Exception e)
        {
           log.error("SQL: "+sql+" \n"+e.getMessage(), e);
        }
        return result;
    }
    public String getCustomMapName(final String pMappingId)
    {
        String result = "";
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select mapping_label from cody.custom_mapping where mapping_id  = "+pMappingId;
        log.debug("#getCustomMapName:: "+sql);
        result = (String) jt.queryForObject(sql,String.class);
        log.debug("===> "+result);
        return result;
    }
    /**
     * @param String
     * @param String
     * @return boolean
     */
    public boolean cleanseCrap(final String pMappingId, final String pCodebookId)
     {
         boolean result = true;
         //List params = new ArrayList();
         try
         {
             JdbcTemplate jt = getJdbcTemplate();
             //String sql = "begin CODY.custom_util.cleanseCustomMap("+pMappingId+", "+pCodebookId+"); end;";
             //jt.execute(sql);
             List params = new ArrayList();
             params.add (new SqlOutParameter("pS", Types.VARCHAR));
             Map results = jt.call(new ProcCallableStatementCreator(Integer.parseInt(pMappingId), Integer.parseInt(pCodebookId)),params);
             String Str_result = (String)results.get("pS");
             if (!Str_result.equalsIgnoreCase("pass"))
             {
                 result = false;
                 log.error(Str_result);
             }
             
         } catch (Exception e)
         {
             log.error(e.getMessage());
             result = false;
             return result;
         }
         return result;
                 
     }
   private class ProcCallableStatementCreator implements CallableStatementCreator 
    {
        private int a;
        private int b;       
    
        public ProcCallableStatementCreator(int a, int b) 
        {
          this.a = a;
          this.b = b;        

        }

        public CallableStatement createCallableStatement(Connection conn) throws SQLException 
        {
          CallableStatement cs = conn.prepareCall("{? = call CODY.custom_util.cleanseCustomMap1(?,?)}");
          cs.registerOutParameter (1, Types.VARCHAR);
          cs.setInt(2, a);
          cs.setInt(3, b); 
          
          return cs;
        }
    } 
}