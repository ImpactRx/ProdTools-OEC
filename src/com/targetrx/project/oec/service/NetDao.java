/*
 * NetDao.java
 *
 * Created on February 28, 2006, 2:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.service;

import java.util.List;
import java.util.Map;
import com.targetrx.project.oec.bo.Net;
/**
 *
 * @author pkukk
 */
public interface NetDao {
    
    public List getNetsByCode(final String codeBookId, final String codeId);
    
    public Map getNetsByCodeBook(final String codeBookId);
    
    public Map getMappedNets(final String codeBookId, final String codeId);
    
    public Net getNetLabel(final String netId);
    
    public Map getEveryNet(final String pCodeBookId);
    
    public Map getAllNets(final String pCodeBookId);
    
    public Map getSubNets(final String pCodeBookId, final String pNetId);
    
    public void updateCodesNetsXref(final String pCodeBookId, final String pCodeId, final String pNet1, final String pNet2, final String pNet3, final String pOldNet1, final String pUser);
    
    public void insertCodesNetsXref(final String pCodeBookId, final String pCodeId, final String pNet1, final String pNet2, final String pNet3, final String pUser);
    
    public void deleteCodesNetsXref(final String pCodeBookId, final String pCodeId, final String pNet1);
    
    public void addNet(final String netLabel, final String netDesc, final String pUser);
    
    public Map getRelSubNets(final String pNet1, final String pNet2);
    
    public void saveRelNets(final String pCodeBookId,final String pNet1, final String pNet2, final String pUser);
    
    public List checkNetLabel(final String pNetLabel);
    
    public Map getCodeBookNets(final String pCodeBookId);
    
     public String saveCodeBookNets(final String pNet, final String pCodebookId, final String pUser);
    
    public Map getCodeBookNet1(final String pCodeBookId);
    
    public void insertClientNet(final String pNetId, final String pNetLabel, final String pUser);
    
    public void insertClientCodebookNet(final String pNetId, final String pCodebookId, final String pUser);
    
    public void insertClientNetXref(final String pNetId, final String pClientNetId, final String pMappingId, final String pUser);
    
    public String getNetIdByLabel(final String pTrxNetLabel);
}
