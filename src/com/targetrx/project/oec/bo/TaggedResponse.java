/*
 * TaggedResponse.java
 *
 * Created on May 10, 2006, 9:05 AM
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
public class TaggedResponse implements Serializable {
    private String tagTypeCode;
    private String count;
    
    /** Creates a new instance of TaggedResponse */
    public TaggedResponse() {
    }
    
    public void setTagTypeCode(String tagTypeCode)
    {
        this.tagTypeCode = tagTypeCode;
    }
    public String getTagTypeCode()
    {
        return this.tagTypeCode;
    }
    public void setCount(String count)
    {
        this.count = count;
    }
    public String getCount()
    {
        return this.count;
    }
    
}
