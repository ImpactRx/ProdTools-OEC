/*
 * AudoCodeDao.java
 *
 * Created on June 19, 2006, 12:01 PM
 *
 * 
 */

package com.targetrx.project.oec.service;

import java.util.List;
import java.util.Map;
/**
 *
 * @author pkukk
 */
public interface AutoCodeDao {
    
    public List getDictAutoCode(final String pAutoCodeId);
    public List getAutoCodes(final String pStatusCode);
    public List getAutoCode(final String pAutoCodeId);
    public List getAutoCodeException(final String pAutoCodeId);
    public void updateStatusCode(final String pAutoCodeId, final String pStatusCode, final String pUpdater);
    public void exeAutoCodeDiscrepReport(String pUser);
    public List getAutoCodeDicreps(final String pCodeBook);
    public void updateDiscrepancy(final String pAutoCodeId, final String pResponseId, final String pCodebookId, final String pCode1, 
            final String pCode2, final String pCode3, final String pCode4, final String pCode5, final String pCode6, final String pUser, final String pOrig);
            //final String pAuto1,final String pAuto2,final String pAuto3,final String pAuto4,final String pAuto5,final String pAuto6);
    public List getAutoCodeDetailReport();
    public List getAutoCodesByCodeBook(final String pCodeBookId, final String pStatusCode);
    public String populateDictionary();
    public String autoCodeResponses(final String pProgramLabel);
    public List getProgramEventIds(final String pProgramLabel);
}
