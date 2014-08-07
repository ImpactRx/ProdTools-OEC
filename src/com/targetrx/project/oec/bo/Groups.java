/*
 * Groups.java
 *
 * Created on April 17, 2006, 10:19 AM
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
public class Groups implements Serializable {
    private String programEventId;
    private String label;
    private String questionId;
    private String parameterNo;
    private String parameterValue;
    private String parameterId;
    private String count;
    /** Creates a new instance of Groups */
    public Groups() {
    }
    public void setCount(String count)
    {
        this.count = count;
    }
    public String getCount()
    {
        return this.count;
    }
    public void setParameterId(String parameterId)
    {
        this.parameterId = parameterId;
    }
    public String getParameterId()
    {
        return this.parameterId;
    }
    public void setParameterValue(String parameterValue)
    {
        this.parameterValue = parameterValue;
    }
    public String getParameterValue()
    {
        return this.parameterValue;
    }
    public void setParameterNo(String parameterNo)
    {
        this.parameterNo = parameterNo;
    }
    public String getParameterNo()
    {
        return this.parameterNo;
    }
    public void setQuestionId(String questionId)
    {
        this.questionId = questionId;
    }
    public String getQuestionId()
    {
        return this.questionId;
    }
    public void setLabel(String label)
    {
        this.label = label;
    }
    public String getLabel()
    {
        return this.label;
    }
    public void setProgramEventId(String programEventId)
    {
        this.programEventId = programEventId;
    }
    public String getProgramEventId()
    {
        return this.programEventId;
    }
    
    
}
