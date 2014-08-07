/*
 * CodebookGroupDao.java
 *
 * Created on June 8, 2006, 9:00 AM
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
public interface CodebookGroupDao {
    
    public Map getAllCodebookGroup();
    public List getCodebookGroup(final String pCodebookGroupId);
    public void addCodebookGroup(final String pCodebookGrpId, final String pGroupName, final String pDescription);
    public int getNextCodebookGroupId();
    public void updateCodebookGroup(final String pCodebookGrpId, final String pGroupName, final String pDescription);
    public void deleteCodebookGroup(final String pCodebookGrpId);
    public void assignCodeBook(final String pCodebookGrpId, final String pCodebookId);  
    public Map getCodebookGroups(final String pProgramEventId);
    public void addCodebookGroupsPrograms(final String pProgramEventId, final String pCodebookId);
    public List getCodebookGroupAssoc(final String pProgramEventId);
}
