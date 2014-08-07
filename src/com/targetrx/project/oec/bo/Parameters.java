/*
 * Parameter.java
 *
 * Created on April 17, 2006, 7:56 AM
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
public class Parameters implements Serializable {
    private String questionId;
    private String parameterId;
    private String initCap;
    private String parameterNo;
    /** Creates a new instance of Parameter */
    public Parameters() {
    }
    public void setParameterNo(String parameterNo)
    {
        this.parameterNo = parameterNo;
    }
    public String getParameterNo()
    {
        return this.parameterNo;
    }
    public void setInitCap(String initCap)
    {
        this.initCap = initCap;
    }
    public String getInitCap()
    {
        return this.initCap;
    }
    public void setParameterId(String parameterId)
    {
        this.parameterId = parameterId;
    }
    public String getParameterId()
    {
        return this.parameterId;
    }
    public void setQuestionId(String questionId)
    {
        this.questionId = questionId;
    }
    public String getQuestionId()
    {
        return this.questionId;
    }
}
