/*
 * Change.java
 *
 * Created on November 6, 2006, 3:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.bo;

import java.io.Serializable;
import java.util.Date;
/**
 *
 * @author pkukk
 */
public class Change implements Serializable {
    
    private String codebookId;
    private String codebookLabel;
    private String codeId;
    private String codeLabel;
    private String net1Id;
    private String net1Label;
    private String net2Id;
    private String net2Label;
    private String net3Id;
    private String net3Label;
    private String historyAction;
    private String market;
    private Date actionDatetime;
    /** Creates a new instance of Change */
    public Change() {
    }
    public void setCodebookId(String codebookId)
    {
        this.codebookId = codebookId;
    }
    public String getCodebookId()
    {
        return this.codebookId;
    }
    public void setCodebookLabel(String codebookLabel)
    {
        this.codebookLabel = codebookLabel;
    }
    public String getCodebookLabel()
    {
        return this.codebookLabel;
    }
    public void setCodeId(String codeId)
    {
        this.codeId = codeId;
    }
    public String getCodeId()
    {
        return this.codeId;
    }
    public void setCodeLabel(String codeLabel)
    {
        this.codeLabel = codeLabel;
    }
    public String getCodeLabel()
    {
        return this.codeLabel;
    }
    public void setNet1Id(String net1Id)
    {
        this.net1Id = net1Id;
    }
    public String getNet1Id()
    {
        return this.net1Id;
    }
    public void setNet1Label(String net1Label)
    {
        this.net1Label = net1Label;
    }
    public String getNet1Label()
    {
        return this.net1Label;
    }
    public void setNet2Id(String net2Id)
    {
        this.net2Id = net2Id;
    }
    public String getNet2Id()
    {
        return this.net2Id;
    }
    public void setNet2Label(String net2Label)
    {
        this.net2Label = net2Label;
    }
    public String getNet2Label()
    {
        return this.net2Label;
    }
    public void setNet3Id(String net3Id)
    {
        this.net3Id = net3Id;
    }
    public String getNet3Id()
    {
        return this.net3Id;
    }
    public void setNet3Label(String net3Label)
    {
        this.net3Label = net3Label;
    }
    public String getNet3Label()
    {
        return this.net3Label;
    }
    public void setHistoryAction(String historyAction)
    {
        this.historyAction = historyAction;
    }
    public String getHistoryAction()
    {
        return this.historyAction;
    }
    public void setMarket(String market)
    {
        this.market = market;
    }
    public String getMarket()
    {
        return this.market;
    }
	public Date getActionDatetime()
	{
		return actionDatetime;
	}
	public void setActionDatetime(Date actionDatetime)
	{
		this.actionDatetime = actionDatetime;
	}
}
