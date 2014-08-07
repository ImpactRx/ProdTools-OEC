/*
 * ProgramFactsDao.java
 *
 * Created on March 30, 2006, 9:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.service;

import java.util.List;
import java.util.Map;
/**
 *
 * @author pkukk
 */
public interface ProgramFactsDao {
    
    public Map getProgramFacts();
    public List getStudyType(final String pProgramLabel);
    public Map getGroupName(final String pProgramLabel, final String pPeid, final String pTag);
    public Map getMarkets(final String pProgramLabel, final String pGroupName);
    public List getProgramEvent(final String pProgramLabel);
    public Map getProgramEvents(final String pProgramLabel);
    public List getGroupList(final String pProgramLabel, final String pParameter, final String pQuestionId, final String pTag);
    public List getActiveMonthAndYear();
    public int findProgramEventEventId(String pProgram);
    public List findEventIds(String pPeid);
    public List findProgramAndEvents(final String pDate);
    public List findEventsByProgram(final String pProgramEventId);
    public Map getAllProgramLabelsByType(String s, String pDate);
    public Map getAllProgramLabelsByType(String s, String pDate, String pEndDate);
    public Map getAllProgramLabelsByTypeAnDate(String pCells, String s, String pMarkets, String pProducts, String pBeginDate, String pEndDate);
}
