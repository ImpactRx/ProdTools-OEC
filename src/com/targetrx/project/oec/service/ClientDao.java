
package com.targetrx.project.oec.service;

import java.util.List;
import java.util.Map;

/**
 *
 * @author pkukk
 */
public interface ClientDao
{

	public Map getClients();
	public String getClientName(String pClientId);

}