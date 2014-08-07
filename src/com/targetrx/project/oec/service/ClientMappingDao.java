/*
 * ClientMappingDao.java
 *
 * Created on March 23, 2009, 3:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.service;

/**
 *
 * @author pkukk
 */
public interface ClientMappingDao {
    
    public int insertNewClientMapping(final String pClientId, final String pMappingLabel,final String pUser);
    public int getNextMappingId();
       
}
