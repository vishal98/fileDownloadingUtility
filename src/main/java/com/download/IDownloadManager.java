package com.download;

import java.net.URLConnection;
import java.util.List;

import com.download.fileDownloader.FileDownloadManager.EDownloadStates;

/**
 * Manage where file download status
 * @author vishal.t
 *
 */
public abstract class IDownloadManager {

	/**
	 * This method return current state of any file download
	 * act as facade
	 * @return download
	 */
	public abstract boolean init();

	/**
	 * This method return current state of any file download
	 * @return download
	 */
	public abstract EDownloadStates getCurrentState();

	/**
	 * This method will be start any file download in async manner
	 */
	public abstract boolean startDownloadAsync();

	/**
	 * This method will be pause running file download
	 * @return boolean
	 */
	public abstract boolean pauseDownload();

	/**
	 * This method will be resume file download if we have partially downloaded
	 * file
	 */
	public abstract boolean resumeDownload();


	/**
	 * This method will return download progress object
	 * file
	 * @return boolean
	 */
	public abstract List<Integer> getDownloadProgress();

	/**
	 * This method stop download manager instance and its related processes
	 */
	public abstract boolean stopDownloadManager();

	/**
	 * This method run thread for accept download stop/start command in background
	 * 
	 * @return boolean
	 */
	public abstract boolean runCommandInterfaceAsync();
	
	/**
	 * class handling http connection and file download
	 * 
	 * @return boolean
	 */
	
   public abstract class  DownloadThread implements  Runnable {
	
	public abstract void downloadFile(URLConnection connection) ;
}

   /**
    * set current file download status
    * @param currentState
    */
	public abstract void setCurrentState(EDownloadStates currentState) ;

}