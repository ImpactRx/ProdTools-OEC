package com.targetrx.project.oec.service;

import java.util.List;
/**
 *
 * @author pkukk
 */
public interface ProgramOecQuestionsDao {
    
    public List getProgramOecQuestions(final String pProgramLabel);
    public void deleteProgramOecQuestion(final String pProgramEventId, final String pSurveyQuestionLabel);
    public void saveProgramOecQuestions(final String pProgramEventIds, final String pSurveyQuestion, final String pStudyTypeCode);
    public List getCustomOecQuestions(final String pProgramLabel);
    public void deleteCustomOecQuestion(final String pProgramEventId, final String pSurveyQuestionLabel, final String pStudyTypeCode,final String pProgramLabel);
    public void saveCustomOecQuestions(String pProgramEventIds, final String pSurveyQuestion, final String pGroupName, final String pProgramLabel);
}
