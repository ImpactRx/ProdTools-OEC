package com.targetrx.project.oec.bo;

import java.util.List;

public class CheckType
{
	private String code;
	private String description;
	private List<CheckStatus> checkStatusList;
	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code)
	{
		this.code = code;
	}
	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	/**
	 * @param checkStatusList the checkStatusList to set
	 */
	public void setCheckStatusList(List<CheckStatus> checkStatusList)
	{
		this.checkStatusList = checkStatusList;
	}
	/**
	 * @return the checkStatusList
	 */
	public List<CheckStatus> getCheckStatusList()
	{
		return checkStatusList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CheckType))
			return false;
		CheckType other = (CheckType) obj;
		if (code == null)
		{
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "CheckType [checkStatusList=" + checkStatusList + ", code="
				+ code + ", description=" + description + "]";
	}
}
