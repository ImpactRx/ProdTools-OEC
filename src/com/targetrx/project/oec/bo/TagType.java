/*
 * TagType.java
 *
 * Created on January 17, 2007, 1:14 PM
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
public class TagType implements Serializable {
    String programEventId;
    String programLabel;
    String tagTypeCode;
    String description;
    String discrepNumber;
    /** Creates a new instance of TagType */
    public TagType() {
    }
    public void setProgramEventId(String programEventId)
    {
        this.programEventId = programEventId;
    }
    public String getProgramEventId()
    {
        return this.programEventId;
    }
    public void setProgramLabel(String programLabel)
    {
        this.programLabel = programLabel;
    }
    public String getProgramLabel()
    {
        return this.programLabel;
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
    public void setDiscrepNumber(String discrepNumber)
    {
        this.discrepNumber = discrepNumber;        
    }
    public String getDiscrepNumber()
    {
        return this.discrepNumber;
    }
}
