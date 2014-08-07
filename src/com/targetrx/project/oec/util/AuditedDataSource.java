package com.targetrx.project.oec.util;

import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import com.targetrx.project.oec.util.AuditedUser;

/**
 * @author Paul Kukk
 * @since Feb 19, 2007
 *
 * This class will wrap a regular DataSource and apply TargetRx specific auditing logic.
 * This includes setting the username for an internal user to their login name rather than
 * the user used for the connection pool.
 *
 */
public class AuditedDataSource implements javax.sql.DataSource {

	DataSource dataSource;


	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getLoginTimeout()
	 */
	public int getLoginTimeout() throws SQLException {
		return dataSource.getLoginTimeout();
	}
	/* (non-Javadoc)
	 * @see javax.sql.DataSource#setLoginTimeout(int)
	 */
	public void setLoginTimeout(int seconds) throws SQLException {
		dataSource.setLoginTimeout(seconds);
	}
	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws SQLException {
		return dataSource.getLogWriter();
	}
	/* (non-Javadoc)
	 * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter out) throws SQLException {
		dataSource.setLogWriter(out);
	}
	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection()
	 */
	public Connection getConnection() throws SQLException {
        String userName = AuditedUser.getName();
        Connection con = dataSource.getConnection();
        CallableStatement cstmt = con.prepareCall("{call trxsess.setusername(?)}");
        cstmt.setString(1, userName);
        cstmt.execute();
        cstmt.close();
		return con;
	}
	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	public Connection getConnection(String username, String password) throws SQLException {
		// TODO - Does not support username/password currently
		return this.getConnection();
	}
	@Override
	/**
	 * Not implemented
	 */
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	/**
	 * Not implemented
	 */
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
