/*
 * Response.java
 *
 * Created on April 4, 2006, 3:40 PM
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
public class Response implements Serializable {
    private String groupQuestionLabel;
    private String responseOrigStr;
    private String reviewed;
    private String par1ParameterId;
    private String par2ParameterId;
    private String par3ParameterId;
    private String par4ParameterId;
    private String code1;
    private String code2;
    private String code3;
    private String code4;
    private String code5;
    private String code6;
    private String targetrxId;
    private String questionSeqNo;
    private String threadNo;
    private String programEventId;
    private String eventId;
    private String surveyQuestionLabel;
    private String responseExtractId;
    private String statusCode;
    private String lockedUser;
    private String questionId;
    private String lockedFlag;
    private String tagTypeCode;
    private String tagUser;
    private String p1Value;
    private String p2Value;
    private String p3Value;
    private String p4Value;
    private String p5Value;
    private String code1Label;
    private String code2Label;
    private String code3Label;
    private String code4Label;
    private String code5Label;
    private String code6Label;
    private String cell;
    
    /** Creates a new instance of Response */
    public Response() {
    }
    public void setCell(String cell)
    {
        this.cell = cell;
    }
    public String getCell()
    {
        return this.cell;
    }
    public void setCode6Label(String code6Label)
    {
        this.code6Label = code6Label;
    }
    public String getCode6Label()
    {
        return this.code6Label;
    }
    public void setCode5Label(String code5Label)
    {
        this.code5Label = code5Label;
    }
    public String getCode5Label()
    {
        return this.code5Label;
    }
    public void setCode4Label(String code4Label)
    {
        this.code4Label = code4Label;
    }
    public String getCode4Label()
    {
        return this.code4Label;
    }
    public void setCode3Label(String code3Label)
    {
        this.code3Label = code3Label;
    }
    public String getCode3Label()
    {
        return this.code3Label;
    }
    public void setCode2Label(String code2Label)
    {
        this.code2Label = code2Label;
    }
    public String getCode2Label()
    {
        return this.code2Label;
    }
    public void setCode1Label(String code1Label)
    {
        this.code1Label = code1Label;
    }
    public String getCode1Label()
    {
        return this.code1Label;
    }
    public void setP5Value(String p5Value)
    {
        this.p5Value = p5Value;
    }
    public String getP5Value()
    {
        return this.p5Value;
    }
    public void setP4Value(String p4Value)
    {
        this.p4Value = p4Value;
    }
    public String getP4Value()
    {
        return this.p4Value;
    }
    public void setP3Value(String p3Value)
    {
        this.p3Value = p3Value;
    }
    public String getP3Value()
    {
        return this.p3Value;
    }
    public void setP2Value(String p2Value)
    {
        this.p2Value = p2Value;
    }
    public String getP2Value()
    {
        return this.p2Value;
    }
    public void setP1Value(String p1Value)
    {
        this.p1Value = p1Value;
    }
    public String getP1Value()
    {
        return this.p1Value;
    }
    public void setTagUser(String tagUser)
    {
        this.tagUser = tagUser;
    }
    public String getTagUser()
    {
        return this.tagUser;
    }
    public void setTagTypeCode(String tagTypeCode)
    {
        this.tagTypeCode = tagTypeCode;
    }
    public String getTagTypeCode()
    {
        return this.tagTypeCode;
    }
    public void setLockedFlag(String lockedFlag)
    {
        this.lockedFlag = lockedFlag;
    }
    public String getLockedFlag()
    {
        return this.lockedFlag;
    }
    public void setQuestionId(String questionId)
    {
        this.questionId = questionId;
    }
    public String getQuestionId()
    {
        return this.questionId;
    }
    
    public void setLockedUser(String lockedUser)
    {
        this.lockedUser = lockedUser;
    }
    public String getLockedUser()
    {
        return this.lockedUser;
    }
    public void setStatusCode(String statusCode)
    {
        this.statusCode = statusCode;
    }
    public String getStatusCode()
    {
        return this.statusCode;
    }
    public void setResponseExtractId(String responseExtractId)
    {
        this.responseExtractId = responseExtractId;
    }
    public String getResponseExtractId()
    {
        return this.responseExtractId;
    }
    public void setSurveyQuestionLabel(String surveyQuestionLabel)
    {
        this.surveyQuestionLabel = surveyQuestionLabel;
    }
    public String getSurveyQuestionLabel()
    {
        return this.surveyQuestionLabel;
    }
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }
    public String getEventId()
    {
        return this.eventId;
    }
    public void setProgramEventId(String programEventId)
    {
        this.programEventId = programEventId;
    }
    public String getProgramEventId()
    {
        return this.programEventId;
    }
    public void setThreadNo(String threadNo)
    {
        this.threadNo = threadNo;
    }
    public String getThreadNo()
    {
        return this.threadNo;
    }
    public void setQuestionSeqNo(String questionSeqNo)
    {
        this.questionSeqNo = questionSeqNo;
    }
    public String getQuestionSeqNo()
    {
        return this.questionSeqNo;
    }
    public void setTargetrxId(String targetrxId)
    {
        this.targetrxId = targetrxId;
    }
    public String getTargetrxId()
    {
        return this.targetrxId;
    }
    public void setCode6(String code6)
    {
        this.code6 = code6;
    }
    public String getCode6()
    {
        return this.code6;
    }
    public void setCode5(String code5)
    {
        this.code5 = code5;
    }
    public String getCode5()
    {
        return this.code5;
    }
    public void setCode4(String code4)
    {
        this.code4 = code4;
    }
    public String getCode4()
    {
        return this.code4;
    }
    public void setCode3(String code3)
    {
        this.code3 = code3;
    }
    public String getCode3()
    {
        return this.code3;
    }
    public void setCode2(String code2)
    {
        this.code2 = code2;
    }
    public String getCode2()
    {
        return this.code2;
    }
    public void setCode1(String code1)
    {
        this.code1 = code1;
    }
    public String getCode1()
    {
        return this.code1;
    }
    public void setPar4ParameterId(String par4ParameterId)
    {
        this.par4ParameterId = par4ParameterId;
    }
    public String getPar4ParameterId()
    {
        return this.par4ParameterId;
    }
    public void setPar3ParameterId(String par3ParameterId)
    {
        this.par3ParameterId = par3ParameterId;
    }
    public String getPar3ParameterId()
    {
        return this.par3ParameterId;
    }
    public void setPar2ParameterId(String par2ParameterId)
    {
        this.par2ParameterId = par2ParameterId;
    }
    public String getPar2ParameterId()
    {
        return this.par2ParameterId;
    }
    public void setPar1ParameterId(String par1ParameterId)
    {
        this.par1ParameterId = par1ParameterId;
    }
    public String getPar1ParameterId()
    {
        return this.par1ParameterId;
    }
    public void setGroupQuestionLabel(String groupQuestionLabel)
    {
        this.groupQuestionLabel = groupQuestionLabel;
    }
    public String getGroupQuestionLabel()
    {
        return this.groupQuestionLabel;
    }
    public void setResponseOrigStr(String responseOrigStr)
    {
        this.responseOrigStr = responseOrigStr;
    }
    public String getResponseOrigStr()
    {
        return this.responseOrigStr;
    }
    public void setReviewed(String reviewed)
    {
        this.reviewed = reviewed;
    }
    public String getReviewed()
    {
        return this.reviewed;
    }    
}
