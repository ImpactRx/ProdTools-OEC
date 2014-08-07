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

import com.targetrx.project.oec.bo.Client;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;


/**
 *
 * @author pkukk
 */
public class ClientDaoImpl extends JdbcDaoSupport implements ClientDao
{
    private Logger log = Logger.getLogger(this.getClass());

    public Map getClients()
    {
			Map mp = new LinkedHashMap();
		  List lst = new ArrayList();
		  String sql = "select client_id, client_name"+
						" from client"+
						" order by client_name";
			try
			{
				JdbcTemplate jt = getJdbcTemplate();
				lst = jt.query(sql,new RowMapperResultReader(new ClientRowMapper()));
			} catch (Exception e)
			{
				log.error(e.getMessage(), e);
			}
			for (int i=0; i<lst.size(); i++)
			{
				Client cl = (Client)lst.get(i);
				mp.put(cl.getClientId(),cl.getClientName());
			}
		  return mp;
		}

		class ClientRowMapper implements RowMapper {
			public Object mapRow(ResultSet rs, int index) throws SQLException {
				Client cl = new Client();
				cl.setClientId(rs.getString(1));
				cl.setClientName(rs.getString(2));
			  return cl;
			}
		}//class ClientRowMapper

		public String getClientName(String pClientId)
		{
			String result = "";
			String sql = "select client_name from client where client_id = "+pClientId;
      //System.out.println(sql);
			try
			{
				JdbcTemplate jt = getJdbcTemplate();
				result = (String) jt.queryForObject(sql, String.class);
        //System.out.println("++++ "+result);
			} catch (Exception e)
			{
				log.error(e.getMessage());
			}
			return result;
		}
}