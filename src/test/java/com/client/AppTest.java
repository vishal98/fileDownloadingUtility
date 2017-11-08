package com.client;

import org.testng.Assert;
import org.testng.TestNG;
import org.testng.annotations.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestNG
{
    
	App app =null;
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
	@Test
    public AppTest( String testName )
    {
        app=new App();
    }

 

	@Test
    public void testApp()
    {
    	Assert.assertNotNull(app.downloadFile());
    }
}
