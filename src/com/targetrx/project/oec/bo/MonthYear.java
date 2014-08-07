/*
 * MonthYear.java
 *
 * Created on August 7, 2007, 6:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.bo;

/**
 *
 * @author pkukk
 */
public class MonthYear {
    private String month;
    private String year;
    private String fullDate;
    private String displayDate;
    /** Creates a new instance of MonthYear */
    public MonthYear() {
    }
    /**
     * @return String
     */
    public String getMonth()
    {
        return this.month;
    }
    /**
     * @param String
     */
    public void setMonth(String month)
    {
        this.month = month;        
    }
    /**
     * @return String
     */
    public String getYear()
    {
        return this.year;
    }
    /**
     * @param String
     */
    public void setYear(String year)
    {
        this.year = year;
    }
    /**
     * @return String
     */
    public String getFullDate()
    {
        return this.fullDate;
    }
    /**
     * @param String
     */
    public void setFullDate(String fullDate)
    {
        this.fullDate = fullDate;
    }
	public String getDisplayDate()
	{
		return displayDate;
	}
	public void setDisplayDate(String displayDate)
	{
		this.displayDate = displayDate;
	}
}
