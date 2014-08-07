/**
 * 
 */
package com.targetrx.project.oec.bo;

/**
 * @author sstuart
 *
 */
public class CheckStatus
{
	private String checkCode;
	private String checkType;
	private String checkName;
	private String description;
	private String checkResult;
	/**
	 * @return the checkCode
	 */
	public String getCheckCode()
	{
		return checkCode;
	}
	/**
	 * @param checkCode the checkCode to set
	 */
	public void setCheckCode(String checkCode)
	{
		this.checkCode = checkCode;
	}
	/**
	 * @return the checkType
	 */
	public String getCheckType()
	{
		return checkType;
	}
	/**
	 * @param checkType the checkType to set
	 */
	public void setCheckType(String checkType)
	{
		this.checkType = checkType;
	}
	/**
	 * @return the checkName
	 */
	public String getCheckName()
	{
		return checkName;
	}
	/**
	 * @param checkName the checkName to set
	 */
	public void setCheckName(String checkName)
	{
		this.checkName = checkName;
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
	 * @return the checkResult
	 */
	public String getCheckResult()
	{
		return checkResult;
	}
	/**
	 * @param checkResult the checkResult to set
	 */
	public void setCheckResult(String checkResult)
	{
		this.checkResult = checkResult;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((checkCode == null) ? 0 : checkCode.hashCode());
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
		if (!(obj instanceof CheckStatus))
			return false;
		CheckStatus other = (CheckStatus) obj;
		if (checkCode == null)
		{
			if (other.checkCode != null)
				return false;
		} else if (!checkCode.equals(other.checkCode))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "CheckStatus [checkCode=" + checkCode + ", checkName="
				+ checkName + ", checkResult=" + checkResult + ", checkType="
				+ checkType + ", description=" + description + "]";
	}
}
