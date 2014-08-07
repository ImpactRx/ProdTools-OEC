/*
 * ResponseDao.java
 *
 * Created on April 12, 2006, 7:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
/**
 *
 * @author pkukk
 */
public interface ResponseDao {
    public List getReponses(final ArrayList pParams,final String pUser,final String pTag);   
    public int getNumberofCheckedResponses(final String pUser);
    public void checkOut(final String pPeid, final String pQid, final String pParamNo, final String pParamId, final String pUser, final String ptag);
    public List getCheckedOutResponses(final String pUser);
    public void setCodedUnverified(final String pResponseExtractId, String pCode1, String pCode2, String pCode3, String pCode4, String pCode5, String pCode6, final String pUser);
    public void setCodedVerified(final String pResponseExtractId, final String pCode1, final String pCode2, final String pCode3, final String pCode4, final String pCode5, final String pCode6, final String pUser);
    public void setCodedPosted(final String pResponseExtractId, final String pCode1, final String pCode2, final String pCode3, final String pCode4, final String pCode5, final String pCode6, final String pUser);
    public void checkIn(final String pUser);
    public void setTagTypeCode(final String pTag,final String pUser, final String pResponseExtractId,final String pCode1, final String pCode2, final String pCode3, final String pCode4, final String pCode5, final String pCode6);
    public int checkCodebookQues(final String pCodeBookId, final String pQuestionLabel);
    public String getPrevResponse(final String pPeid, final String pEid, final String pTid, final String pQid, final String pQsn);
    public List getResponseView(String pProgramEventId) throws Exception;
    public String resetResponses(final String pCodePosition, final String pCodeNum, final String pProgramLabel, final String pUser);
    public int getResetCount(final String pCodePosition, final String pCodeNum, final String pProgramLabel);
    public String loadOecResponse(final String pProgramLabel);
    public List getDynamicReport(String pPeids, String pMarket, String pProduct, String pQuestions, String pCells, String pStartDate) throws Exception;
}
