
package com.targetrx.project.oec.bo;

import java.io.Serializable;
import java.util.*;


public class Client
{
	private String clientId = "";
  private String clientName = "";

	public Client()
	{
	}

	public Client(String clientId, String clientName)
	{
		this.clientId = clientId;
		this.clientName = clientName;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}
	public String getClientId()
	{
		return this.clientId;
	}
  public void setClientName(String clientName)
  {
      this.clientName = clientName;
  }
  public String getClientName()
  {
      return this.clientName;
  }

}
