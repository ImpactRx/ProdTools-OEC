package com.targetrx.project.oec.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author pkukk
 */
public interface CustomMapDao
{

	public Map getCustomMaps(String pClientId);
    public List getAllCustomMappings();
    public String insertCustomMap(String pClientId, String pMappingLabel, String pUser);
    public String getCustomMapName(final String pMappingId);
    public boolean cleanseCrap(final String pMappingId, final String pCodebookId); 
}