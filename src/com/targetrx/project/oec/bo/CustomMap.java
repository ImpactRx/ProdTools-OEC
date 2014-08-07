
package com.targetrx.project.oec.bo;

public class CustomMap
{
	private String mappingId = "";
	private String clientId = "";
  private String mappingLabel = "";
  private String clientName;

	public CustomMap()
	{
	}
	public CustomMap(String mappingId, String clientId, String mappingLabel)
	{
		this.mappingId = mappingId;
		this.clientId = clientId;
		this.mappingLabel = mappingLabel;
	}

	public void setMappingId(String mappingId)
	{
		this.mappingId = mappingId;
	}
	public String getMappingId()
	{
		return this.mappingId;
	}
	public void setClientId(String clientId)
		{
			this.clientId = clientId;
		}
		public String getClientId()
		{
			return this.clientId;
	}
  public void setMappingLabel(String mappingLabel)
  {
      this.mappingLabel = mappingLabel;
  }
  public String getMappingLabel()
  {
      return this.mappingLabel;
  }
/**
 * @return Returns the clientName.
 */
public String getClientName()
{
    return clientName;
}
/**
 * @param clientName The clientName to set.
 */
public void setClientName(String clientName)
{
    this.clientName = clientName;
}

}
