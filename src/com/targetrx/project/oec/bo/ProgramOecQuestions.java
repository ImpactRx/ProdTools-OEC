package com.targetrx.project.oec.bo;

/**
 *
 * @author pkukk
 */
import java.io.Serializable;

public class ProgramOecQuestions implements Serializable
{
    private String programEventId;
    private String surveyQuestionLabel;
    private String groupName;
    public String getProgramEventId()
    {
        return this.programEventId;
    }
    public void setProgramEventId(String programEventId)
    {
        this.programEventId = programEventId;
    }
    public String getSurveyQuestionLabel()
    {
        return this.surveyQuestionLabel;
    }
    public void setSurveyQuestionLabel(String surveyQuestionLabel)
    {
        this.surveyQuestionLabel = surveyQuestionLabel;
    }
    public String getGroupName()
    {
        return this.groupName;
    }
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }
}
