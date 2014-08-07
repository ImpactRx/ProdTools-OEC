/*
 * CodeDao.java
 *
 * Created on February 21, 2006, 10:12 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author pkukk
 */
public interface CodeDao  {

    public List getAllCodes();

    public TreeMap getMappedCodes(final String pCodeBookId);

    public TreeMap getAllMappedCodes(final String pCodeBookId);

    public List getCodeIds();

    public List getCodeId(final String cbId, final String pCodeNum);

    public int getCodeBasedId(String pCodeNum, String cbId, String bool);

    public String addCode(final String pCodeBookId,final String pCodeNum, final String pCodeLabel, final String pReportLabel, final String pHintCode, final String pNet1, final String pNet2, final String pNet3,final String pUser);

    public void inactivateCode(final String pCodeBookId, final String pCodeId, final String pUser);

    public TreeMap<String, String> searchMappedCodes(final String type, final String filterType, final String filter) throws Exception;

    public TreeMap<String, String> searchMappedCodesByDate(final int codebookId, final int days, final String type, final String filterType,final String filter) throws Exception;
    
    public void deleteCode(final String pCodeBookId, final String pCodeId);

    public List getCode(final String pCodeId, final String cbId);

    public void updateCode(final String pCbId, final String pCodeId, final String pCodeNum, final String pCodeLabel, final String pReportLabel, final String pCodeHint, final String pUser);

    public void saveCode(final String pCodeBookId, final String pCodeId, final String pNet1, final String pNet2, final String pNet3, final String pUser);

    public List checkCodeLabel(final String pCodeLabel);

    public List checkCodeNum(final String pCodeNum);

    public int getNextCodeId();

    public List getCodeHistory(String pChangeDate, String pEndDate, String pUser, final String byCodeBook, final String codebookId);

    public List getCheckVerify(String pChangeDate, String pAudit, String pPeid, String pUser);

    public void checkInPeid(final String pProgramEventId);

    public boolean codeNumExist(final String pCodebookId, final String pCodeId);

    public TreeMap getClientCodes(final String pCodeBookId);
    
    public void insertClientCode(final String pCodeId,final String pCodeLabel,final String pCodeBookId, final String pUser);
    
    public void insertClientCodebookCodes(final String pCodeBookId, final String pCodeId, final String pUser);
    
    public String checkCodesLabel(final String pCodeLabel);
    
    public void insertClientCodeXref(final String pCodebookId, final String pCodeId, final String pClientCodeId, final String pMappingId);

    public void insertClientCodesNets(String pCodeBookId, String pCodeId, String pNet1, String pNet2);
}
