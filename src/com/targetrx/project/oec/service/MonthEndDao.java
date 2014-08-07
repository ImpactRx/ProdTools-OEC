/*
 * MonthEndDao.java
 *
 * Created on November 28, 2006, 8:12 AM
 *
 */

package com.targetrx.project.oec.service;

import org.springframework.dao.DataAccessException;

import java.util.*;
import java.util.List;
import java.util.Map;
/**
 *
 * @author pkukk
 */
public interface MonthEndDao {
    
    public String getEndMonthStatus(final String pDate, final String pProgramEventId, final String pUser);    
    public String checkDisconnect(final String pDate, final String pType, final String pProgram);
    public Collection getCheckLists(String pDate, String pProgram) throws DataAccessException;
}
