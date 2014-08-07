/*
 * User.java
 *
 * Created on March 22, 2006, 12:54 PM
 *
 * @author Paul Kukk
 */

package com.targetrx.project.oec.bo;

import java.io.Serializable;
/**
 *
 * @author pkukk
 */
public class User implements Serializable {
    private String staffId;
    private String firstName;
    private String lastName;
    private String eMail;
    private String roleCode;
    private String userName;
    private String lockedResponses;
    
    /** Creates a new instance of User */
    public User() {
    }
    public void setLockedResponses(String lockedResponses)
    {
        this.lockedResponses = lockedResponses;
    }
    public String getLockedResponses()
    {
        return this.lockedResponses;
    }
    public void setStaffId(String staffId)
    {
        this.staffId = staffId;
    }
    public String getStaffId()
    {
        return this.staffId;
    }
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    public String getFirstName()
    {
        return this.firstName;
    }
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    public String getLastName()
    {
        return this.lastName;
    }
    public void setEMail(String eMail)
    {
        this.eMail = eMail;
    }
    public String getEMail()
    {
        return this.eMail;
    }
    public void setRoleCode(String roleCode)
    {
        this.roleCode = roleCode;
    }
    public String getRoleCode()
    {
        return this.roleCode;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    public String getUserName()
    {
        return this.userName;
    }
}
