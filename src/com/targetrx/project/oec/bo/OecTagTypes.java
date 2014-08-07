/*
 * OecTagTypes.java
 *
 * Created on May 3, 2006, 7:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.bo;

/**
 *
 * @author pkukk
 */
public class OecTagTypes {
    private String tagTypeCode;
    private String description;
    /** Creates a new instance of OecTagTypes */
    public OecTagTypes() {
    }
    public void setTagTypeCode(String tagTypeCode)
    {
        this.tagTypeCode = tagTypeCode;
    }
    public String getTagTypeCode()
    {
        return this.tagTypeCode;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getDescription()
    {
        return this.description;
    }
}
