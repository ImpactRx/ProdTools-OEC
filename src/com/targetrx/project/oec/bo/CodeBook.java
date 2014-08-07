/*
 * CodeBook.java
 *
 * Created on February 21, 2006, 10:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.bo;

import java.io.Serializable;
/**
 *
 * @author pkukk
 */
public class CodeBook implements Serializable{
    
    private String codeBookId = null;
    private String codeBookName = null;
    private String description = null;
    private String createdUser = null;
    
    /** Creates a new instance of CodeBook */
    public CodeBook() {
    }
    public void setCodeBookId(String codeBookId)
    {
        this.codeBookId = codeBookId;
    }
    public String getCodeBookId()
    {
        return this.codeBookId;
    }
    public void setCodeBookName(String codeBookName)
    {
        this.codeBookName = codeBookName;
    }
    public String getCodeBookName()
    {
        return this.codeBookName;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getDescription()
    {
        return this.description;
    }
    public void setCreatedUser(String createdUser)
    {
        this.createdUser = createdUser;
    }
    public String getCreatedUser()
    {
        return this.createdUser;
    }
}
