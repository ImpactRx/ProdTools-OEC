package com.targetrx.project.oec.service;

import java.util.List;
import java.util.Map;
/**
 *
 * @author pkukk
 */
public interface CodeBookDao {
    
    public void updateCodeBookName(String pCodeBookId, String pCodeBookName);
    
    public Map getEveryCodeBook();
    
    public Map getAllCodeBooks();
    
    public void insertCodeBook(final String pCodeBookName, final String pCodeBookDesc, final String pUser);
    
    public void deleteCodeBook(final String pCodeBookId);
    
    public List getCodeBookName(final String pCodeBookId);
    
    public int checkCodeBookName(final String pCodeBookName);
    
    public Map getClonableCodeBooks();
    
    public String cloneCodeBook(final String pValue, final String pCodeBookName);
    
    public String cloneCodeBookGroup(final String pCodeBookGroupId);
    
    List viewCodeBook(final String pCodeBookId) throws Exception;
    
    public String saveCodebookQuestions(final String pCodebookId, final String pSurveyQuesLabel);
    
    public void deleteCodebookQuestions(final String pCodebookId, final String pSurveyQuesLabel);
    
    public List viewChanges(final String pCodebookId, final String pMonth, final String pYear);
}
