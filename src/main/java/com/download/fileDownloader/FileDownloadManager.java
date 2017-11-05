package com.download.fileDownloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class FileDownloadManager implements IFileDownloadManager {

	private String filepath;
	private URL url;
	private Thread downloadThread ;
	private volatile downloadStates currentState;
	private int downloadedFileSize;
	private Thread commandThread ;
    private volatile int progress;
    private List<Integer> progressList;

	public enum downloadStates {
		 PAUSE(0), RESUME(1),START(2), DOWNLOADING(3), ERROR(4), COMPLETED(5);
		int value;

		downloadStates(int value) {
			this.value = value;
		}
	};

	public FileDownloadManager(String url, String fileDestinationPath)  {
		try {
			this.url = new URL(url);
			// Get the file name from url path
			 url = this.url.getFile();
			String fileName = url.substring(url.lastIndexOf('/') + 1);
			this.filepath = fileDestinationPath+fileName;
		} catch (MalformedURLException e) {
			System.out.println(e);
		}
		downloadThread = new Thread(new FileDownload(),"downloadManager");
		
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		FileDownloadManager fl=new FileDownloadManager("http://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf","D://test.pdf");
		fl.startDownload();
	}
    
	@Override
	public void init(){
		try {
			startDownload();
			while (currentState != downloadStates.DOWNLOADING ) {
				if (currentState == downloadStates.COMPLETED || currentState == downloadStates.ERROR) {
					return;
				}
			}		
			showDownloadProgress();
			runCommandInterface();
			stopDownloadManager();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public downloadStates getCurrentState() {
		return currentState;
	}
	
	public boolean startDownload(){
		downloadThread.start();
		return true;

	}
	
	public boolean pauseDownload() {
		currentState = downloadStates.PAUSE;
		System.out.println("file download is paused");
		return true;
	}

	public boolean resumeDownload() {
		currentState = downloadStates.DOWNLOADING;
		downloadThread.interrupt();
		System.out.println("file download is resumed");
		return true;
	}

	public void showDownloadProgress() {
		progressList=new ArrayList<>();
		Runnable r = () -> {
			int prevProgress = 0;
			while (true) {
				if(currentState == downloadStates.COMPLETED || currentState == downloadStates.ERROR){
					return;
				}
				if (progress > prevProgress) {
					System.out.print(progress +" - ");
					prevProgress = progress;
					progressList.add(progress);
				}
			}

		};
		new Thread(r, "progressThread").start();
	}
	
	

	public List<Integer> getDownloadProgress() {
	         return progressList;	
	}
	
	
	public void stopDownloadManager() {
		while (true) {
			if (currentState == downloadStates.COMPLETED || currentState == downloadStates.ERROR) {
			    System.out.println("file is downloaded .press 4 to exit");
				return;
			}
		}
	}
	
	/**
	 * 
	 * @author vishal.t
	 *
	 */
	class FileDownload implements Runnable {
		BufferedInputStream in = null;
		FileOutputStream fos = null;
		private BufferedOutputStream bout;
		File file = null;

		@Override
		public void run() {
			try {
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				int connectionLength = connection.getContentLength();
				connection.disconnect();
				
				connection = (HttpURLConnection) url.openConnection();
				
					file = new File(filepath);
					if (file.exists()) {//if file exist getting file length within range
						downloadedFileSize = (int) file.length();
						if ( downloadedFileSize > 0)
							connection.setRequestProperty("Range", "bytes=" + (file.length()) + "-" + connectionLength);
					}
				
				
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(100000);
				// Connect to server
				connection.connect();
				
				if (connection.getResponseCode() / 100 != 2)
					throw new IllegalArgumentException("Invalid response code!");
				else {
					int fileLength = connection.getContentLength() + downloadedFileSize;
					in = new BufferedInputStream(connection.getInputStream());
					fos = (downloadedFileSize == 0) ? new FileOutputStream(filepath)
							: new FileOutputStream(filepath, true);
					bout = new BufferedOutputStream(fos, 102400);
					byte[] data = new byte[10240];
					int x = 0;
					while ((x = in.read(data, 0, 10240)) >= 0) {
						while (currentState == downloadStates.PAUSE) {
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
								System.out.println("resume download");
							}
						}
						currentState = downloadStates.DOWNLOADING;
						bout.write(data, 0, x);
						downloadedFileSize += x;
						progress = (int) ((downloadedFileSize * 100) / fileLength);
					}
				}
				currentState = downloadStates.COMPLETED;
				System.out.println("file downloaded finished with percent "+progress);
			} catch (Exception e) {
				e.printStackTrace();
				currentState = downloadStates.ERROR;
				System.out.println("download failed with percent "+progress);
			} finally {
				try {
					if(bout!=null)
					bout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void runCommandInterface() {

		Runnable r = () -> {
			try {
				Scanner sc = new Scanner(System.in);
				
				
				while (true) {
					int command = 0;
					System.out.println("press 0 to PauseDownload and 1 to Resume Download ");
					 
					if (currentState == downloadStates.COMPLETED) {
						return;
					}
					command = sc.nextInt();
					if (currentState.value == command) {
						System.out.println("already state is " + currentState);
						continue;
					}else if (command == downloadStates.PAUSE.value) {
						pauseDownload();
						continue;
					}else if (command == downloadStates.RESUME.value) {
						resumeDownload();
						continue;
					}else if (command == 4) {
						System.out.println("exiting");
						return;
					}else if (command == 5) {
						System.out.println("Enter your username: ");
                        String line =sc.nextLine();
						return;
					}
					sc.close();
					System.out.println("invalid command");
					continue;
				}
			} catch (Exception e) {
				System.out.println("error occur in commands" );
				e.printStackTrace();
			}

		};
		
		commandThread = new Thread(r,"commandThread");
		commandThread.start();
	}

}
