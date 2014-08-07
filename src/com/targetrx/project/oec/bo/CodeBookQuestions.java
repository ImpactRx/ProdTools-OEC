package com.targetrx.project.oec.bo;
/**
 *
 * @author pkukk
 */
public class CodeBookQuestions {
    private String codebookId;
    private String surveyQuestionLabel;
    /**
     * @param String
     */
    public void setCodebookId(String codebookId)
    {
        this.codebookId = codebookId;
    }
    /**
     * @return String
     */
    public String getCodebookId()
    {
        return this.codebookId;
    }
    /**
     * @param String
     */
    public void setSurveyQuestionLabel(String surveyQuestionLabel)
    {
        this.surveyQuestionLabel = surveyQuestionLabel;
    }
    /**
     * @return String
     */
    public String getSurveyQuestionLabel()
    {
        return this.surveyQuestionLabel;
    }
}
