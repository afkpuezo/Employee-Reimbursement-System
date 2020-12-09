/**
 * This class contains some (very simple) tests for the BackEndUtil class.
 */
package com.revature.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestBEU {
    
    @Test
    public void testSingleton(){

        ServiceFront sf0 = BackEndUtil.getBackEnd();
        ServiceFront sf1 = BackEndUtil.getBackEnd();
        
        assertTrue(sf0 == sf1);
    }
}
