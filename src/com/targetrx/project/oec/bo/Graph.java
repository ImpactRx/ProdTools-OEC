package com.targetrx.project.oec.bo;

import java.io.Serializable;
/**
 * group_question_label, os.program_event_id, count(*) as tQues, cv.tQues,
 *  cu.cuQues,  tag.tagQues
 * @author pkukk
 */
public class Graph implements Serializable
{
    private String surveyQuestionLabel;
    private String groupQuestionLabel;
    private String programEventId;
    private String totalCount;
    private String countVerify;
    private String countUnverify;
    private String countTag;
    private String countNew;
            
    public void setCountNew(String countNew)
    {
        this.countNew = countNew;
    }
    public String getCountNew()
    {
        return this.countNew;
    }
    public void setCountTag(String countTag)
    {
        this.countTag = countTag;
    }
    public String getCountTag()
    {
        return this.countTag;
    }
    public void setCountUnverify(String countUnverify)
    {
        this.countUnverify = countUnverify;
    }
    public String getCountUnverify()
    {
        return this.countUnverify;
    }
    public void setCountVerify(String countVerify)
    {
        this.countVerify = countVerify;
    }
    public String getCountVerify()
    {
        return this.countVerify;
    }
    public void setTotalCount(String totalCount)
    {
        this.totalCount = totalCount;
    }
    public String getTotalCount()
    {
        return this.totalCount;
    }
    public void setProgramEventId(String programEventId)
    {
        this.programEventId = programEventId;
    }
    public String getProgramEventId()
    {
        return this.programEventId;
    }
    public void setGroupQuestionLabel(String groupQuestionLabel)
    {
        this.groupQuestionLabel = groupQuestionLabel;
    }
    public String getGroupQuestionLabel()
    {
        return this.groupQuestionLabel;
    }
    /**
     * @return Returns the surveyQuestionLabel.
     */
    public String getSurveyQuestionLabel()
    {
        return surveyQuestionLabel;
    }
    /**
     * @param surveyQuestionLabel The surveyQuestionLabel to set.
     */
    public void setSurveyQuestionLabel(String surveyQuestionLabel)
    {
        this.surveyQuestionLabel = surveyQuestionLabel;
    }
}
