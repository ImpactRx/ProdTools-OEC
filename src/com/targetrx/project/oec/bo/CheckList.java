/*
 * CheckList.java
 *
 * Created on August 14, 2007, 8:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.bo;


/**
 *
 * @author pkukk
 */
public class CheckList {
    String fieldingPeriod;
    String programLabel;
    String checkName;
    String checkValue;
    /** Creates a new instance of CheckList */
    public CheckList() {
    }
    /**
     * Sets the fielding period
     * @param String Fielding Period
     */
    public void setFieldingPeriod(String fieldingPeriod)
    {
        this.fieldingPeriod = fieldingPeriod;
    }
    /**
     * Returns fielding period
     * String 
     */
    public String getFieldingPeriod()
    {
        return this.fieldingPeriod;
    }
    public void setProgramLabel(String programLabel)
    {
        this.programLabel = programLabel;
    }
    public String getProgramLabel()
    {
        return this.programLabel;
    }
    public void setCheckName(String checkName)
    {
        this.checkName = checkName;
    }
    public String getCheckName()
    {
        return this.checkName;
    }
    public void setCheckValue(String checkValue)
    {
        this.checkValue = checkValue;
    }
    public String getCheckValue()
    {
        return this.checkValue;
    }
}
