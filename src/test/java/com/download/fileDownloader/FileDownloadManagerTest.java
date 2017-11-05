package com.download.fileDownloader;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FileDownloadManagerTest {

	FileDownloadManager fileDownloadManager=null;
	
	String filePath="/";
	  
	/*@Test
	public void startdownloadTest() {
		fileDownloadManager.init();
	}*/
	  
  @Test
	public void FileDownloadManager() {
  		File fl=getFileObject();
         if(fl!=null)
        	 fl.delete();
		fileDownloadManager = new FileDownloadManager("http://www.tutorialspoint.com/java/java_tutorial.pdf", filePath);
	}



  @Test(dependsOnMethods={"FileDownloadManager"})
  public void startDownload() {
    fileDownloadManager.startDownload();
  }
  
  @Test(dependsOnMethods={"startDownload"})
  public void acceptNewCommand() {
	    fileDownloadManager.runCommandInterface();
  }
  
  @Test(dependsOnMethods={"acceptNewCommand"})
  public void pauseDownload() {
    Assert.assertTrue(fileDownloadManager.pauseDownload());
  }

  @Test(dependsOnMethods={"pauseDownload"})
  public void resumeDownload() {
	    Assert.assertTrue(fileDownloadManager.resumeDownload());
  }

 
  @Test(dependsOnMethods={"pauseDownload"})
  public void stopDownloadManager() {
	  fileDownloadManager.stopDownloadManager();
  }
  
  @Test(dependsOnMethods={"stopDownloadManager"})
  public void initTest() {
		File fl=getFileObject();
        if(fl!=null)
       	 fl.delete();
        
	  fileDownloadManager=new FileDownloadManager("http://www.tutorialspoint.com/java/java_tutorial.pdf",
				filePath);
    	fileDownloadManager.init();
  }
  
  @Test(dependsOnMethods={"initTest"})
  public void validateFileDate() {
    	File fl=getFileObject();
    	Assert.assertNotNull(fl.length());
  }
  
  private File getFileObject(){
	  return new File(filePath);
		
  }
  
}
