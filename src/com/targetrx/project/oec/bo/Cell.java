/*
 * CellCode.java
 *
 * Created on October 19, 2009, 8:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.bo;

import java.io.Serializable;
/**
 *
 * @author pkukk
 */
public class Cell implements Serializable {
    private String cellCode;
    private String cellDesc;
    /** Creates a new instance of CellCode */
    public Cell() {
    }
    
    public String getCellCode()
    {
        return this.cellCode;
    }
    public void setCellCode(String cellCode)
    {
        this.cellCode = cellCode;
    }
    public String getCellDesc()
    {
        return this.cellDesc;
    }
    public void setCellDesc(String cellDesc)
    {
        this.cellDesc = cellDesc;
    }
}
