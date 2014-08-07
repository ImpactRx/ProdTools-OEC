/*
 * Market.java
 *
 * Created on August 19, 2009, 7:41 AM
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
public class Market implements Serializable {
    
    private String marketId;
    private String marketValue;
    
    /** Creates a new instance of Market */
    public Market() {
    }
    
    public void setMarketId(String marketId)
    {
        this.marketId = marketId;
    }
    public String getMarketId()
    {
        return this.marketId;
    }
    public void setMarketValue(String marketValue)
    {
        this.marketValue = marketValue;
    }
    public String getMarketValue()
    {
        return this.marketValue;
    }
}
