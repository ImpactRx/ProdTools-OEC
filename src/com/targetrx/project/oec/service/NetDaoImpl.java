package com.targetrx.project.oec.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.targetrx.project.oec.bo.Code;
import com.targetrx.project.oec.bo.Net;
import com.targetrx.project.oec.util.JdbcQuery;
/**
 *
 * @author pkukk
 */
public class NetDaoImpl extends JdbcDaoSupport implements NetDao {
    private Logger log = Logger.getLogger(this.getClass());
    /**
     * 
     * @param codebookId
     * @return
     */
    public List<Net> getSubnetsByCodebookId(final int codebookId) throws Exception
    {
    	List<Net> list = null;
    	String query = 
    		"select distinct net2_id as subnet_id, b.net_label as subnet_label " +
    		"from codes_nets_xref a, " +
        	"        nets b " +
        	"where codebook_id = ? " +
            "and net2_id is not null " +
            "and a.net2_id = b.net_id " +
            "and b.CLIENT_NET_FLAG = 'N' " +
            "order by net_label";
    	try
    	{
            JdbcTemplate jdbcTemplate = getJdbcTemplate();
            list =  jdbcTemplate.query(query, new Object[] {new Integer(codebookId)}, new SubnetMapper());
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
    private class SubnetMapper implements RowMapper
    {
		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			Net net = new Net();
			net.setNet2Id(new Integer(rs.getInt("subnet_id")).toString());
			net.setNet2Label(rs.getString("subnet_label"));
			return net;
		}
    }
    /**
     * @see com.targetrx.project.oec.service.NetDao#getNetsByCode(java.lang.String, java.lang.String)
     */
    public List getNetsByCode(final String codeBookId, final String codeId)
    {
        List lst = null;
        StringBuffer sb = new StringBuffer();
        sb.append("select a.codebook_id, a.code_id,");
        sb.append(" a.net1_id,");
        sb.append(" b.net_label net1_label,");
        sb.append(" b.description net1_desc,");
        sb.append(" a.net2_id,");
        sb.append(" c.net_label net2_label,");
        sb.append(" c.description net2_desc,");
        sb.append(" a.net3_id,");
        sb.append(" d.net_label net3_label,");
        sb.append(" d.description net3_desc");
        sb.append(" FROM codes_nets_xref a, codes e,");
        sb.append(" cody.nets b,");
        sb.append(" cody.nets c,");
        sb.append(" cody.nets d");
        sb.append(" WHERE");
        sb.append(" a.codebook_id = "+codeBookId);
        sb.append(" AND   b.client_net_flag != 'Y'");
        sb.append(" AND e.code_id = a.code_id ");
        sb.append(" AND e.code_num = '"+codeId+"'");
        sb.append(" AND   a.net1_id = b.net_id(+)");
        sb.append(" AND   a.net2_id = c.net_id(+)");
        sb.append(" AND   a.net3_id = d.net_id(+)");
        
        JdbcTemplate jt = getJdbcTemplate();
        lst =  jt.query(sb.toString(),new RowMapperResultReader(new CodeRowMapper()));
        return lst;
    }
     class CodeRowMapper implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        Net net = new Net();
	        net.setCodeBookId(rs.getString(1));
          net.setCodeId(rs.getString(2));
	        net.setNet1Id(rs.getString(3));
          net.setNet1Label(rs.getString(4));
          net.setNet1Description(rs.getString(5));
          net.setNet2Id(rs.getString(6));
          net.setNet2Label(rs.getString(7));
          net.setNet2Description(rs.getString(8));
          net.setNet3Id(rs.getString(9));
          net.setNet3Label(rs.getString(10));
          net.setNet3Description(rs.getString(11));
	        return net;
	      }      
	  }//class CodeRowMapper
     /**
      * @see com.targetrx.project.oec.service.NetDao#getMappedNets(java.lang.String, java.lang.String)
      */
    public Map getMappedNets(final String codeBookId, final String codeId)
    {
        Map mp = new LinkedHashMap();
        List lst = null;
        StringBuffer sb = new StringBuffer();
        sb.append("select a.codebook_id, code_id,");
        sb.append(" a.net1_id,");
        sb.append(" b.net_label net1_label,");
        sb.append(" b.description net1_desc,");
        sb.append(" a.net2_id,");
        sb.append(" c.net_label net2_label,");
        sb.append(" c.description net2_desc,");
        sb.append(" a.net3_id,");
        sb.append(" d.net_label net3_label,");
        sb.append(" d.description net3_desc");
        sb.append(" FROM codes_nets_xref a, codes e,");
        sb.append(" nets b,");
        sb.append(" nets c,");
        sb.append(" nets d");
        sb.append(" WHERE");
        sb.append(" a.codebook_id = "+codeBookId);
        sb.append(" AND e.code_id = a.code_id ");
        sb.append(" AND   e.code_num = '"+codeId+"'");
        sb.append( "AND a.client_net_flag = 'N'");
        sb.append(" AND   a.net1_id = b.net_id(+)");
        sb.append(" AND   a.net2_id = c.net_id(+)");
        sb.append(" AND   a.net3_id = d.net_id(+)");
        JdbcTemplate jt = getJdbcTemplate();
        lst =  jt.query(sb.toString(),new RowMapperResultReader(new CodeRowMapper()));
        for (int i=0; i<lst.size(); i++)
        {
            Net cd = (Net)lst.get(i);
            mp.put(cd.getNet1Id(),cd.getNet1label());            
        }       
        return mp;
    }
    public Map getEveryNet(final String pCodeBookId)
    {
        Map mp = new LinkedHashMap();
        List lst = null;
        StringBuffer sb = new StringBuffer();
        sb.append("select n.net_id,  n.net_label, n.description from nets n where n.client_net_flag = 'N'");
        sb.append(" order by net_label");
        JdbcTemplate jt = getJdbcTemplate();
        lst =  jt.query(sb.toString(),new RowMapperResultReader(new CodeRowMapperNet()));
        for (int i=0; i<lst.size(); i++)
        {
          Net cd = (Net)lst.get(i);
          mp.put(cd.getNet1Id(),cd.getNet1label());            
        }
        return mp;
    }
    public Map getCodeBookNet1(final String pCodeBookId)
    {
        Map mp = new LinkedHashMap();
        List lst = null;
        StringBuffer sb = new StringBuffer();
        //sb.append("select DISTINCT x.net1_id, n.net_label, n.description");
        //sb.append(" from cody.codes_nets_xref x, cody.nets n where x.codebook_id = "+pCodeBookId+" and n.net_id = x.net1_id order by n.net_label");
        sb.append("SELECT c.net1_id, n.net_label, n.description");
        sb.append(" FROM cody.codes_nets_xref c,");
        sb.append(" nets n");
        sb.append(" WHERE c.codebook_id = "+pCodeBookId);
        sb.append(" AND  n.net_id = c.net1_id");
        sb.append(" AND  n.client_net_flag = 'N'");
        sb.append(" GROUP BY c.net1_id, n.net_label, n.description");
        sb.append(" UNION");
        sb.append(" SELECT  x.net_id, n.net_label, n.description");
        sb.append(" FROM codebook_nets_xref x,");
        sb.append(" nets n");
        sb.append(" WHERE x.codebook_id = "+pCodeBookId);
        sb.append(" AND   n.net_id = x.net_id");
        sb.append(" AND  n.client_net_flag = 'N'");
        sb.append(" GROUP BY  x.net_id, n.net_label, n.description");
        sb.append(" ORDER BY net_label");
        JdbcTemplate jt = getJdbcTemplate();
        lst =  jt.query(sb.toString(),new RowMapperResultReader(new CodeRowMapperNet()));
        log.debug("NET1 retrieved: "+lst.size());
        for (int i=0; i<lst.size(); i++)
        {
          Net cd = (Net)lst.get(i);
          mp.put(cd.getNet1Id(),cd.getNet1label());            
        }
        return mp;
    }
    public Map getAllNets(final String pCodeBookId)
    {
        Map mp = new LinkedHashMap();
        List lst = null;
        StringBuffer sb = new StringBuffer();
        sb.append("select DISTINCT n.net_id, n.net_label, n.description from nets n, nets_subnets_xref nsx");
        sb.append(" where nsx.codebook_id = "+pCodeBookId+" and n.net_id = nsx.net_id and n.client_net_flag = 'N' order by net_label");
        JdbcTemplate jt = getJdbcTemplate();
        lst =  jt.query(sb.toString(),new RowMapperResultReader(new CodeRowMapperNet()));
        for (int i=0; i<lst.size(); i++)
        {
          Net cd = (Net)lst.get(i);
          mp.put(cd.getNet1Id(),cd.getNet1label());            
        }
        return mp;
    }
    class CodeRowMapperNet implements RowMapper 
    {
	      public Object mapRow(ResultSet rs, int index) throws SQLException {
	        Net net = new Net();
	        net.setNet1Id(rs.getString(1));
          net.setNet1Label(rs.getString(2));
          net.setNet1Description(rs.getString(3));
          return net;
	      }      
	  }//class CodeRowMapper
    
    public Map getSubNets(final String pCodeBookId, final String pNetId)
    {
        Map mp = new LinkedHashMap();
        List lst = null;    
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select b.subnet_id, a.net_label, a.description from nets a, nets_subnets_xref b ";
        sql = sql + "where b.codebook_id = "+pCodeBookId+" and b.net_id = "+pNetId;
        sql = sql + " and a.net_id = b.subnet_id and a.client_net_flag = 'N' order by a.net_label";
        lst =  jt.query(sql,new RowMapperResultReader(new CodeRowMapperNet()));
        log.debug("#getSubNets: "+sql);
        for (int i=0; i<lst.size(); i++)
        {
          Net cd = (Net)lst.get(i);
          mp.put(cd.getNet1Id(),cd.getNet1label());            
        }
        
        return mp;
    }
    /**
     * @params String
     * @return Net
     */
    public Net getNetLabel(final String netId)
    {
        Net result = new Net();
        String query = "select net_label, description from nets where net_id = "+netId;
        JdbcTemplate jt = getJdbcTemplate();
        List lst = jt.queryForList(query);
        Iterator iter = lst.iterator();
        while (iter.hasNext()) {
            Map m = (Map) iter.next();
            result.setNet1Id(netId);
            result.setNet1Label((String)m.get("NET_LABEL"));
            result.setNet1Description((String)m.get("DESCRIPTION"));
        }
        return result;
    }
    /**
     * @params String
     * @params String
     * @params String
     * @params String
     */
    public void updateCodesNetsXref(final String pCodeBookId, final String codeNum, final String pNet1, final String pNet2, final String pNet3, final String pOldNet1, final String pUser)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String net2 = pNet2;
        String net3 = pNet3;
        CodeDaoImpl dao = new CodeDaoImpl();
        dao.setJdbcTemplate(jt);
        List<Code> codeIds = dao.getCodeId(pCodeBookId, codeNum);
        Code code = codeIds.iterator().next();
        String codeId = code.getCodeId();
        if (net2.equalsIgnoreCase("0"))
        {
            net2 = null;
        }
        if (net3.equalsIgnoreCase("0"))
        {
            net3 = null;
        }
        // IF NET1 IS COMING IN WITH A VALUE OF ZERO THEN THE USER WISHES TO REMOVE THE NET RELATIONSHIP
        if (pNet1.equalsIgnoreCase("0"))
        {
            jt.execute("DELETE FROM codes_nets_xref WHERE code_id = "+codeId+" AND net1_id = "+pOldNet1+"AND codebook_id = "+pCodeBookId);
        } else 
        {
            // IF NONE IS PASSED IN THIS MEANS WE NEED TO INSERT A NET ASSOC BECAUSE THERE WAS NONE BEFORE
            if (pOldNet1.equalsIgnoreCase("none"))
            {
                String sql = "INSERT INTO codes_nets_xref (codebook_id, net1_id, code_id, net2_id, net3_id) VALUES ("+pCodeBookId+","+pNet1+","+codeId+","+net2+","+net3+")";
                jt.execute(sql);
            } else 
            {
                jt.update("UPDATE codes_nets_xref SET net1_id = "+pNet1+", net2_id = "+net2+", net3_id = "+net3+", updated_user = '"+pUser+"' WHERE codebook_id = "+pCodeBookId+" AND code_id = "+codeId+" AND net1_id = "+pOldNet1);
            }
        }
    }
    /**
     * @params String
     * @params String
     * @params String
     * @params String
     */
    public void insertCodesNetsXref(final String pCodeBookId, final String pCodeId, final String pNet1, final String pNet2, final String pNet3, final String pUser)
    {
        insertCodesNetsXref(pCodeBookId, pCodeId, pNet1, pNet2, pNet3, pUser, null);
    }
    /**
     * @param pCodeBookId
     * @param pCodeId
     * @param pNet1
     * @param pNet2
     * @param pNet3
     * @param pUser
     * @param jdbcTemplate
     */
    public void insertCodesNetsXref(final String pCodeBookId, final String pCodeId, final String pNet1, final String pNet2, final String pNet3, final String pUser, final JdbcTemplate jdbcTemplate)
    {
        JdbcTemplate jt = (jdbcTemplate == null) ? getJdbcTemplate() : jdbcTemplate;
        // We need to check first if the NET the user is trying to insert already exists
        // in the table. If it does do not do anything if it does not go ahead and insert
        // the row into the database.
        String check_sql = "select count(*) from codes_nets_xref"+
                " where codebook_id = "+pCodeBookId+
                " and code_id = "+pCodeId+
                " and net1_id = "+pNet1;
        log.debug(check_sql);
        int rows = jt.queryForInt(check_sql);
        log.debug("ROWS -------------------- "+rows);
        if (rows == 0)
        {
            try {
                String sql = "insert into codes_nets_xref ( codebook_id, code_id, net1_id, added_user) values ";
                sql = sql+"("+pCodeBookId+","+pCodeId+","+pNet1+",'"+pUser+"')";
                jt.execute(sql);
                log.debug("#insertCodesNetsXref:: "+sql);
            } catch (Exception e)
            {
                log.error("ERROR #insertCodesNetsXref:: "+e.getMessage(), e);
            }
        }
    }
    /**
     * @params String
     * @params String
     * @params String
     */
    public void deleteCodesNetsXref(final String pCodeBookId, final String pCodeId, final String pNet1)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "delete from codes_nets_xref where codebook_id = "+pCodeBookId+" and code_id = "+pCodeId+" net1_id = "+pNet1;
        int rows = jt.update(sql);
    }
    /**
     * @params String
     * @params String
     */
    public void addNet(final String pNetLabel, final String pNetDesc, final String pUser)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "insert into nets (net_id, net_label, description, added_user) values ("+this.getNextNetId()+",'"+pNetLabel+"', '"+pNetDesc+"','"+pUser+"')";
        jt.execute(sql);
    }
    /**
     * @param String
     * @param String
     * @erturn Map
     */
    public Map getRelSubNets(final String pNet1, final String pNet2)
    {
        Map mp = new LinkedHashMap();
        List lst = null;
        StringBuffer sb = new StringBuffer();
        if (pNet2.equalsIgnoreCase("0"))
        {
            sb.append("select net_id, net_label, description from nets  where client_net_flag = 'N' and net_id != "+pNet1+"order by net_label");
        } else {
            sb.append("select net_id, net_label, description from nets  where client_net_flag = 'N' and net_id != "+pNet1+"and net_id != "+pNet2+"order by net_label");
        }
        JdbcTemplate jt = getJdbcTemplate();
        lst =  jt.query(sb.toString(),new RowMapperResultReader(new CodeRowMapperNet()));
        for (int i=0; i<lst.size(); i++)
        {
          Net cd = (Net)lst.get(i);
          mp.put(cd.getNet1Id(),cd.getNet1label());            
        }
        return mp;
    }
    /**
     * @param String
     * @param String
     * @param String
     */
    public void saveRelNets(final String pCodeBookId, final String pNet1, final String pNet2, final String pUser)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String val_net2 = pNet2;
        List result = null;
        if (val_net2.equalsIgnoreCase("null"))
        {
            val_net2 = null;
        }
        String sql = "insert into cody.nets_subnets_xref (codebook_id, net_id, subnet_id, added_user) values ";
        sql = sql+"( "+pCodeBookId+", "+pNet1+", "+pNet2+",'"+pUser+"')";
        String selectNet = "select added_user from cody.nets_subnets_xref where codebook_id = ? and net_id = ?";
        String selectSubNet = "select added_user from cody.nets_subnets_xref where codebook_id = ? and net_id = ? and subnet_id = ?";
        try 
        {
            if (val_net2.equalsIgnoreCase("null"))
            {
                result = jt.queryForList(selectNet, new Object[] {pCodeBookId,pNet1});
            } else 
            {
                result = jt.queryForList(selectSubNet, new Object[] {pCodeBookId,pNet1,pNet2});
            }
            log.debug("RESULT =============================> "+result+"  :::   "+result.size());
            if (result.size() == 0)
            {
                log.debug("SQL INSERT..........................");
                jt.execute(sql);
            }
        } catch (Exception e)
        {
            if (e.getMessage().indexOf("CODY.SYS_C00148051") > -1)
            {
                log.info("NetDaoImpl #saveRelNets:: Call calling from EXCEL LOADER");
            } else
            {
                log.error(e.getMessage(), e);
            }
        }
        
    }
    /**
     * @param String
     * @return List
     */ 
    public List checkNetLabel(final String pNetLabel)
    {
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select net_id, net_label, description from cody.nets where UPPER(net_label) = UPPER('"+pNetLabel+"')";
        return  jt.queryForList(sql);
        
    }
    /**
     * @return int
     */
    public int getNextNetId()
    {
        int result = 0;
        JdbcTemplate jt = getJdbcTemplate();
        //System.out.println("JDBCTEMPLATE:: "+jt);        
        String sql = "select cody.net_id_seq.nextval from dual";
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
     * @return map
     */
     public Map getCodeBookNets(final String pCodeBookId)
     {
        Map mp = new LinkedHashMap();
        List lst = null;
        JdbcTemplate jt = getJdbcTemplate();
        String sql = "select a.net_id, a.net_label, a.description";
        sql = sql + " from cody.nets a";
        sql = sql + " where a.client_net_flag = 'N' and a.net_id not in (select subnet_id from cody.nets_subnets_xref where codebook_id = "+pCodeBookId+")";
        sql = sql + " order by a.net_label";
        lst =  jt.query(sql,new RowMapperResultReader(new CodeRowMapperNet()));
        log.debug("#getCodeBookNets "+sql);
        for (int i=0; i<lst.size(); i++)
        {
          Net cd = (Net)lst.get(i);
          mp.put(cd.getNet1Id(),cd.getNet1label());            
        }
        return mp;
     }
     /**
      * @param String CodebookId
      * @return Map
      */
     public Map getNetsByCodeBook(final String pCodeBookId)
     {
        Map mp = new LinkedHashMap();
        List lst = null;
        String sql = " select distinct a.net_id, a.net_label, a.description"+
                " from cody.nets a,"+
                "    cody.codes_nets_xref b,"+
                "    (select net_id from cody.codebook_nets_xref where codebook_id  = "+pCodeBookId+") c"+
                " where b.codebook_id = "+pCodeBookId+
                " and a.client_net_flag = 'N' and a.net_id = b.net1_id or a.net_id = c.net_id"+
                " order by a.net_label";
        log.debug("#getNetsByCodeBook: "+sql);
        JdbcTemplate jt = getJdbcTemplate();
        try 
        {
            lst =  jt.query(sql,new RowMapperResultReader(new CodeRowMapperNet()));
            for (int i=0; i<lst.size(); i++)
            {
              Net cd = (Net)lst.get(i);
              mp.put(cd.getNet1Id(),cd.getNet1label());            
            }
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return mp;
     }
     /**
      * @param String NetId
      * @param String CodebookId
      * @param String User
      * @return String 
      */
     public String saveCodeBookNets(final String pNet, final String pCodebookId, final String pUser)
     {
         String result = "Net association has been made to Codebook.";
         try
         {
             JdbcTemplate jt = getJdbcTemplate();
             String sql = "insert into cody.codebook_nets_xref (codebook_id, net_id, added_user, added_datetime)"+
                     " values ("+pCodebookId+","+pNet+",'"+pUser+"',sysdate)";
             log.debug(sql);
             jt.execute(sql);
         } catch (Exception e)
         {
             log.error(e.getMessage(), e);
             result = "There was a problem trying to save the Net. Please contact an IT representative.";        
         }
         return result;
     }
     /**
      * @param String
      * @param String
      * @param String
      */
     public void insertClientNet(final String pNetId, final String pNetLabel, final String pUser)
     {
        JdbcTemplate jt = getJdbcTemplate();
        //String sql = "insert into cody.nets (net_id, net_label, description, added_user, client_net_flag) values ("+pNetId+",'"+pNetLabel+"', '"+pNetLabel+"','"+pUser+"','Y')";
        String sql = "insert into cody.nets (net_id, net_label, description, added_user, client_net_flag) values (?,?, ?,?,'Y')";
        try
        {
            //jt.execute(sql);
            jt.update(sql, new Object[] {pNetId,pNetLabel,pNetLabel,pUser});
        } catch (Exception e)
        {
            log.info(e.getMessage());
        }
     }
     /**
      * @param String
      * @param String
      * @param String
      */
     public void insertClientCodebookNet(final String pNetId, final String pCodebookId, final String pUser)
     {
         String result = "Net association has been made to Codebook.";
         try
         {
             JdbcTemplate jt = getJdbcTemplate();
             //String sql = "insert into cody.codebook_nets_xref (codebook_id, net_id, added_user, added_datetime)"+
             //        " values ("+pCodebookId+","+pNetId+",'"+pUser+"',sysdate)";
             //log.debug(sql);
             //jt.execute(sql);
             String sql = "insert into cody.codebook_nets_xref (codebook_id, net_id, added_user, added_datetime)"+
                     " values (?,?,?,sysdate)";
             jt.update(sql, new Object[] {pCodebookId,pNetId,pUser});
         } catch (Exception e)
         {
             log.error(e.getMessage(), e);
             result = "There was a problem trying to save the Net. Please contact an IT representative.";        
         }
     }
     /**
      * @param String
      * @param String
      * @param String
      * @param String
      */
     public void insertClientNetXref(final String pNetId, final String pClientNetId, final String pMappingId, final String pUser)
     {
         String result = "Net association has been made to Codebook.";
         try
         {
             JdbcTemplate jt = getJdbcTemplate();
             //String sql = "insert into cody.client_net_xref (client_net_id, added_user, mapping_id, active_flag)"+
             //        " values ("+pClientNetId+",'"+pUser+"',"+pMappingId+", 'Y')";
             //System.out.println(sql);
             //jt.execute(sql);
             String sql = "insert into cody.client_net_xref (client_net_id, added_user, mapping_id, active_flag)"+
                     " values (?,?,?, 'Y')";
             jt.update(sql, new Object[] {pClientNetId,pUser,pMappingId});
         } catch (Exception e)
         {
             log.error(e.getMessage(), e);
             result = "There was a problem trying to save the Net. Please contact an IT representative.";        
         }
     }
     /**
      * @param String
      * @return String
      */
     public String getNetIdByLabel(final String pTrxNetLabel)
     {
         String result = "";
         String sql = "select * from cody.nets where net_label = ? and client_net_flag = 'N'";
         try
         {
             JdbcTemplate jt = getJdbcTemplate();
             result = (String) jt.queryForObject(sql, new Object[] { pTrxNetLabel },String.class);
         } catch (Exception e)
         {
             log.info(e.getMessage());
         }
         return result;
     }
}
