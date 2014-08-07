/*
 * Product.java
 *
 * Created on August 20, 2009, 1:02 PM
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
public class Product  implements Serializable{
    
    /** Creates a new instance of Product */
    public Product() {}
    
    private String productId;
    private String productValue;
    
    public void setProductId(String productId)
    {
        this.productId = productId;
    }
    public String getProductId()
    {
        return this.productId;
    }
    public void setProductValue(String productValue)
    {
        this.productValue = productValue;
    }
    public String getProductValue()
    {
        return this.productValue;
    }
}