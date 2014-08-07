/*
 * ProgramFacts.java
 *
 * Created on March 30, 2006, 9:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.bo;

/**
 * @author pkukk
 */
public class ProgramFacts
{
    private String programLabel;
    private String studyTypeCode;
    private String programEvent;
    private String eventId;
    private String supplementCode;
    private String statusCode;
    
    /** Creates a new instance of ProgramFacts */
    public ProgramFacts() {
    }
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }
    public String getEventId()
    {
        return this.eventId;
    }
    public void setProgramEvent(String programEvent)
    {
        this.programEvent = programEvent;
    }
    public String getProgramEvent()
    {
        return this.programEvent;
    }
    public void setProgramLabel(String programLabel)
    {
        this.programLabel = programLabel;
    }
    public String getProgramLabel()
    {
        return this.programLabel;
    }
    public void setStudyTypeCode(String studyTypeCode)
    {
        this.studyTypeCode = studyTypeCode;
    }
    public String getStudyTypeCode()
    {
        return this.studyTypeCode;
    }
	/**
	 * @return the supplementCode
	 */
	public String getSupplementCode()
	{
		return supplementCode;
	}
	/**
	 * @param supplementCode the supplementCode to set
	 */
	public void setSupplementCode(String supplementCode)
	{
		this.supplementCode = supplementCode;
	}
	/**
	 * @return the statusCode
	 */
	public String getStatusCode()
	{
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode)
	{
		this.statusCode = statusCode;
	}
    
}
