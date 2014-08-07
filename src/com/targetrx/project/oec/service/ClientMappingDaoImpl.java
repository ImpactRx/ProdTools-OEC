/*
 * ClientMappingDaoImpl.java
 *
 * Created on March 23, 2009, 3:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.service;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import com.targetrx.project.oec.bo.Code;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import com.targetrx.project.oec.bo.Change;
import com.targetrx.project.oec.bo.CheckVerify;
import com.targetrx.project.oec.bo.CustomMap;
import com.targetrx.project.oec.service.MonthEndDaoImpl;
import com.targetrx.project.oec.service.NetDaoImpl;
import com.targetrx.project.oec.service.ProgramFactsDaoImpl;
/**
 *
 * @author pkukk
 */
public class ClientMappingDaoImpl extends JdbcDaoSupport implements ClientMappingDao {
     private Logger log = Logger.getLogger(this.getClass());
    /** Creates a new instance of ClientMappingDaoImpl */
    public ClientMappingDaoImpl() {
    }
    /**
     * @param String
     * @param String
     * @param String
     */
    public int insertNewClientMapping(final String pClientId, final String pMappingLabel,final String pUser)
    {
        JdbcTemplate jt = getJdbcTemplate();
        int mappingId = 0;
        try
        {
            mappingId = this.getNextMappingId();
            String insertCustomMapping = "insert into cody.custom_mapping (mapping_id,client_id,mapping_label,added_user,added_datetime) values ("+
                mappingId+", "+pClientId+", '"+pMappingLabel+"', '"+pUser+"')";
            System.out.println(insertCustomMapping);
            jt.execute(insertCustomMapping);
        } catch (Exception e)
        {
            log.error(e.getMessage());
        }
        return mappingId;
    }
    /**
     * @return int
     */
    public int getNextMappingId()
    {
        int result = 0;
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select mapping_id_seq.nextval from dual";
        try {
            result = jt.queryForInt(sql);
        } catch (Exception e)
        {
           log.error("SQL: "+sql+" \n"+e.getMessage(), e);
        }
        return result;
    }
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
     
}
