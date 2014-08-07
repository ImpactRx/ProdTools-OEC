/*
 * CodeBookGroup.java
 *
 * Created on June 8, 2006, 8:33 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.bo;

/**
 *
 * @author pkukk
 */
public class CodeBookGroup {
    private String codebookGroupId;
    private String groupName;
    private String description;
    private String programEvent;
    private String activeEvent;
    
    /** Creates a new instance of CodeBookGroup */
    public CodeBookGroup() {
    }
    public void setActiveEvent(String activeEvent)
    {
        this.activeEvent = activeEvent;
    }
    public String getActiveEvent()
    {
        return this.activeEvent;
    }
    public void setProgramEvent(String programEvent)
    {
        this.programEvent = programEvent;
    }
    public String getProgramEvent()
    {
        return this.programEvent;
    }
    public void setCodebookGroupId(String codebookGroupId)
    {
        this.codebookGroupId = codebookGroupId;
    }
    public String getCodebookGroupId()
    {
        return this.codebookGroupId;
    }
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }
    public String getGroupName()
    {
        return this.groupName;
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
