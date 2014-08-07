package com.targetrx.project.oec.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.targetrx.project.oec.bo.User;
/**
 *
 * @author pkukk
 */
public class UserDaoImpl extends JdbcDaoSupport implements UserDao
{
    private Logger log = Logger.getLogger(this.getClass());
    /**
     * @param String LoginName
     * @return User
     * Method returns a User object from the Login name that
     * is passed in
     */
    public User getUser(final String pLoginName)
    {
        User result = new User();
        String query = "select s.staff_id, s.last_name, s.first_name,";
        query = query+" s.email, sr.role_id, r.role_code, windows_login_name";
        query = query+" from v_staff s, v_staff_roles_xref  sr, v_roles r";
        query = query+" where s.windows_login_name = lower('"+pLoginName+"')";
        query = query+" and sr.staff_id = s.staff_id";
        query = query+" and sr.status_code = 'A'";
        query = query+" and r.role_id = sr.role_id";
        query = query+" and r.role_id in (22, 20, 21,19)";
        
        JdbcTemplate jt = getJdbcTemplate();
        List lst = jt.queryForList(query);
        Iterator iter = lst.iterator();
        log.debug("SQL #getUser: "+query);
        while (iter.hasNext()) {
            Map m = (Map) iter.next();
            result.setStaffId(m.get("STAFF_ID").toString());
            result.setEMail((String)m.get("EMAIL"));
            result.setFirstName((String)m.get("FIRST_NAME"));
            result.setLastName((String)m.get("LAST_NAME"));
            result.setRoleCode((String)m.get("ROLE_CODE"));
            result.setUserName((String)m.get("WINDOWS_LOGIN_NAME"));  
            log.debug((String)m.get("WINDOWS_LOGIN_NAME")+" just logged in.");
            System.out.println("Loggin in: "+pLoginName+", role code is: "+result.getRoleCode());
        }
        return result;
    }
}
