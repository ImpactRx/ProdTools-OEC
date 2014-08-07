/*
 * CheckVerify.java
 *
 * Created on November 9, 2006, 7:42 AM
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
public class CheckVerify implements Serializable{
    
    private String count;
    private String programEventId;
    private String statusCode;
    private String tagTypeCode;
    
    /** Creates a new instance of CheckVerify */
    public CheckVerify() {
    }
    public String getTagTypeCode()
	{
		return tagTypeCode;
	}
	public void setTagTypeCode(String tagTypeCode)
	{
		this.tagTypeCode = tagTypeCode;
	}
	/**
     * @param String
     */
    public void setCount(String count)
    {
        this.count = count;
    }
    /** 
     * @return String
     */
    public String getCount()
    {
        return this.count;
    }
    /**
     * @param String
     */
    public void setProgramEventId(String programEventId)
    {
        this.programEventId = programEventId;
    }
    /**
     * @return String
     */
    public String getProgramEventId()
    {
        return this.programEventId;
    }
    /**
     * @param String
     */
    public void setStatusCode(String statusCode)
    {
        this.statusCode = statusCode;
    }
    /**
     * @return String
     */
    public String getStatusCode()
    {
        return this.statusCode;
    }
    
}
