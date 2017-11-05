package com.download.fileDownloader;

import org.testng.Assert;
import org.testng.TestNG;

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
    public AppTest( String testName )
    {
        app=new App();
    }

 

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	app.fileInputs();
    	Assert.assertTrue(true);
    }
}
