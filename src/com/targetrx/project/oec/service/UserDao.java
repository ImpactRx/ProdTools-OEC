/*
 * UserDao.java
 *
 * Created on March 22, 2006, 1:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.targetrx.project.oec.service;

import java.util.List;
import java.util.Map;
import com.targetrx.project.oec.bo.User;
/**
 *
 * @author pkukk
 */
public interface UserDao {
    
   public User getUser(final String loginName); 
   
}
