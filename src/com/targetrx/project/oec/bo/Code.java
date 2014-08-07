/*
 * Code.java
 *
 * Created on February 21, 2006, 10:14 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.bo;

import java.io.Serializable;
import java.util.*;


public class Code
{
	private String codeId = "";
  private String codeNum = "";
	private String codeLabel = "";
	private String codeReport = "";
  private String codeHint = "";
  
	public Code()
	{
	}

	public Code(String codeLabel, String codeDescription)
	{
		this.codeLabel = codeLabel;
		this.codeReport = codeDescription;
	}

	public void setCodeId(String codeId)
	{
		this.codeId = codeId;
	}
	public String getCodeId()
	{
		return this.codeId;
	}
  public void setCodeNum(String codeNum)
  {
      this.codeNum = codeNum;
  }
  public String getCodeNum()
  {
      return this.codeNum;
  }
	public void setCodeLabel(String codeLabel)
	{
		this.codeLabel = codeLabel;
	}
	public String getCodeLabel()
	{
		return codeLabel;
	}

	public void setCodeReport(String codeReport)
	{
		this.codeReport = codeReport;
	}
	public String getCodeReport()
	{
		return this.codeReport;
	}
  public void setCodeHint(String codeHint)
  {
      this.codeHint = codeHint;
  }
  public String getCodeHint()
  {
      return this.codeHint;
  }

}
