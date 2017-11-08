package com.download.fileDownloader;

import java.io.File;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.download.IDownloadManager;
import com.download.fileDownloader.FileDownloadManager.EDownloadStates;

public class FileDownloadManagerTest {

	private IDownloadManager fileDownloadManager=null;
	private List<Integer> progress =null;
	private String filePath="/";
	  
	/*@Test
	public void startdownloadTest() {
		fileDownloadManager.init();
	}*/
/*
 * create filedownloader object	  
 */
  @Test
	public void FileDownloadManager() {
  		File fl=getFileObject();
         if(fl!=null)
        	 fl.delete();
		fileDownloadManager = new FileDownloadManager("http://www.tutorialspoint.com/java/java_tutorial.pdf", filePath);
	}



  @Test(dependsOnMethods={"FileDownloadManager"})
  public void startDownloadAsync() {
    Assert.assertTrue(fileDownloadManager.startDownloadAsync());
  }
  
  @Test(dependsOnMethods={"startDownloadAsync"})
	public void pauseDownload() throws InterruptedException {
		downloadBarier();
		progress = fileDownloadManager.getDownloadProgress();
		Assert.assertTrue(fileDownloadManager.pauseDownload());
			Thread.sleep(5000);
		List<Integer> progress2 = fileDownloadManager.getDownloadProgress();
		Assert.assertEquals(progress.get(progress.size() - 1), progress2.get(progress2.size() - 1));

	}
  
  @Test(dependsOnMethods={"pauseDownload"})
  public void resumeDownloadTest() {
    Assert.assertTrue(fileDownloadManager.resumeDownload());
       try {
		Thread.sleep(5000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    List<Integer> progress2 =fileDownloadManager.getDownloadProgress();
     if(progress==null||progress2==null){
    	 return;
     }
    
  }
  
  @Test(dependsOnMethods={"pauseDownload"})
  public void acceptNewCommand() {
	    Assert.assertTrue(fileDownloadManager.runCommandInterfaceAsync());
  }
  
/**
 * checking whether file got downloaded or not
 */
  @Test(dependsOnMethods={"acceptNewCommand"})
  public void stopDownloadManager() {
	  Assert.assertTrue(fileDownloadManager.stopDownloadManager());
	  Assert.assertNotNull(getFileObject());
  }
  
  @Test(dependsOnMethods={"stopDownloadManager"})
  public void initTest() {
		File fl=getFileObject();
        if(fl!=null)
       	 fl.delete();
        
	  fileDownloadManager=new FileDownloadManager("http://www.tutorialspoint.com/java/java_tutorial.pdf",
				filePath);
	  Assert.assertTrue(fileDownloadManager.init());
  }
  
  @Test(dependsOnMethods={"initTest"})
  public void validateFile() {
    	File fl=getFileObject();
    	Assert.assertNotNull(fl.length());
  }
  
  private File getFileObject(){
	  return new File(filePath);	
  }
  
  public void downloadBarier(){
	  while(progress==null||progress.isEmpty()){
          if(fileDownloadManager.getCurrentState()==EDownloadStates.COMPLETED||fileDownloadManager.getCurrentState()==EDownloadStates.ERROR){
        	  return;
          }
		  progress=fileDownloadManager.getDownloadProgress();	  	  

	  }
  }
  
  
}
