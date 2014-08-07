/*
 * JdbcQuery.java
 *
 * Created on February 21, 2006, 12:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.util;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;
import java.util.List;
import org.springframework.dao.DataAccessException;

/**
 *
 * @author pkukk
 */
public class JdbcQuery extends JdbcDaoSupport {
    
    private DataSource dataSourceIn;
    
    /** Creates a new instance of JdbcQuery */
    public JdbcQuery() {
    }
    /**
     * @params DataSource
     */
    public void setDataSourceIn(DataSource dataSourceIn)
    {
        this.dataSourceIn = dataSourceIn;
    }
    /**
     * @params String
     * @return JdbcTemplate
     */
    public JdbcTemplate getTemplate(final String pContextPath) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(pContextPath);
        dataSourceIn = (DataSource)ctx.getBean("dataSourceIn");        
        JdbcTemplate jt = new JdbcTemplate(dataSourceIn);        
        return jt;
    }
    public DataSource getDataSource(final String pContextPath)
    {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(pContextPath);
        dataSourceIn = (DataSource)ctx.getBean("dataSourceIn"); 
        return dataSourceIn;
    }
    /**
     * @params String
     * @return JdbcTemplate
     */
    public JdbcTemplate getTemplate(final String pContextPath, final String pDataSource) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(pContextPath);
        dataSourceIn = (DataSource)ctx.getBean(pDataSource);
        JdbcTemplate jt = new JdbcTemplate(dataSourceIn);
        return jt;
    }
}
