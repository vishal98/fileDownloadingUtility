package com.download.fileDownloader;

/*
 * *
 * This class manage download for file including status,progress and action 
 * init method can be used to start download,get download progress and allow stop and resume for dowmlaoad
 * FileDownload class take care of managing httpconnection and downloading actual file
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.download.IDownloadManager;

public class FileDownloadManager extends IDownloadManager {

	private String filepath;
	private URL url;
	private Thread downloadThread;
	private volatile EDownloadStates currentState;
	private int downloadedFileSize;
	private volatile List<Integer> progressList;

	public FileDownloadManager(String url, String fileDestinationPath) {
		try {
			this.url = new URL(url);
			// Get the file name from url path
			url = this.url.getFile();
			String fileName = url.substring(url.lastIndexOf('/') + 1);
			this.filepath = fileDestinationPath + fileName;
		} catch (MalformedURLException e) {
			System.out.println(e);
		}
		downloadThread = new Thread(new FileDownloadThread(), "downloadManager");

	}

	public enum EDownloadStates {
		PAUSE(0), RESUME(1), START(2), DOWNLOADING(3), ERROR(4), COMPLETED(5);
		int value;

		EDownloadStates(int value) {
			this.value = value;
		}
	};

	/*
	 * 
	 * @see com.download.fileDownloader.IDownLoadManager#init()
	 */

	@Override
	public boolean init() {
		try {
			startDownloadAsync();
			outer: while (currentState != EDownloadStates.DOWNLOADING) {
				if (currentState == EDownloadStates.COMPLETED || currentState == EDownloadStates.ERROR) {
					break outer;
				}
			}

			runCommandInterfaceAsync();
			stopDownloadManager();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.download.fileDownloader.IDownLoadManager#getCurrentState()
	 */
	@Override
	public EDownloadStates getCurrentState() {
		return currentState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.download.fileDownloader.IDownLoadManager#startDownload()
	 */
	@Override
	public boolean startDownloadAsync() {
		try {
			new Thread(downloadThread, "downloadThread").start();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.download.fileDownloader.IDownLoadManager#pauseDownload()
	 */
	@Override
	public boolean pauseDownload() {
		currentState = EDownloadStates.PAUSE;
		System.out.println("file download is paused");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.download.fileDownloader.IDownLoadManager#resumeDownload()
	 */
	@Override
	public boolean resumeDownload() {
		setCurrentState(EDownloadStates.DOWNLOADING);
		System.out.println("file download is resumed");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.download.fileDownloader.IDownLoadManager#getDownloadProgress()
	 */
	@Override
	public List<Integer> getDownloadProgress() {
		return progressList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.download.fileDownloader.IDownLoadManager#stopDownloadManager()
	 */
	@Override
	public boolean stopDownloadManager() {
		while (true) {
			if (currentState == EDownloadStates.COMPLETED || currentState == EDownloadStates.ERROR) {
				System.out.println("file is downloaded .press 4 to exit");
				return true;
			}
		}
	}

	/**
	 * 
	 * @author vishal.t
	 *
	 */
	class FileDownloadThread extends DownloadThread {

		private BufferedInputStream in = null;
		private FileOutputStream fos = null;
		private BufferedOutputStream bout;
		private File file = null;
		private int progress=0;

		@Override
		public void run() {
			try {
				int connectionLength = getConnectionLength(url);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				setRequestProperty(connection, connectionLength);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(100000);
				// Connect to server
				connection.connect();

				if (connection.getResponseCode() / 100 != 2)
					throw new IllegalArgumentException("Invalid response code!");
				else {
					downloadFile(connection);
				}
				currentState = EDownloadStates.COMPLETED;
				System.out.println("file downloaded finished with percent " + progress);
			} catch (Exception e) {
				e.printStackTrace();
				currentState = EDownloadStates.ERROR;
				System.out.println("download failed with percent " + progress);
			} finally {
				try {
					if (bout != null)
						bout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private int getConnectionLength(URL url) throws IOException {
			HttpURLConnection connection;
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				int connectionLength = connection.getContentLength();
				connection.disconnect();
				return connectionLength;
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}

		}

		private void setRequestProperty(URLConnection connection, int connectionLength) {
			file = new File(filepath);
			if (file.exists()) {// if file exist getting file length within range
				downloadedFileSize = (int) file.length();
				if (downloadedFileSize > 0)
					connection.setRequestProperty("Range", "bytes=" + (file.length()) + "-" + connectionLength);
			}
		}
        
		@Override
		public void downloadFile(URLConnection connection) {
			int fileLength = connection.getContentLength() + downloadedFileSize;
			try {
				in = new BufferedInputStream(connection.getInputStream());

				fos = (downloadedFileSize == 0) ? new FileOutputStream(filepath) : new FileOutputStream(filepath, true);
				bout = new BufferedOutputStream(fos, 102400);
				byte[] data = new byte[10240];
				int x = 0;
				System.out.println("download started");
				int prevProgress = 0;
				while ((x = in.read(data, 0, 10240)) >= 0) {
					while (currentState == EDownloadStates.PAUSE) {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							System.out.println("resume download");
						}
					}
					currentState = EDownloadStates.DOWNLOADING;
					bout.write(data, 0, x);
					downloadedFileSize += x;
					progress = (int) ((downloadedFileSize * 100) / fileLength);

					if (progress > prevProgress) {
						System.out.print(progress + " - ");
						prevProgress = progress;
						if(progressList==null){
							progressList=new ArrayList<>();
						}
						progressList.add(progress);
						updateProgress(progressList);
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.download.fileDownloader.IDownLoadManager#runCommandInterface()
	 */

	@Override
	public boolean runCommandInterfaceAsync() {

		Runnable r = () -> {
			try {
				Scanner sc = new Scanner(System.in);

				while (true) {
					int command = 0;
					System.out.println("press 0 to PauseDownload and 1 to Resume Download ");

					if (currentState == EDownloadStates.COMPLETED) {
						return;
					}
					command = sc.nextInt();
					if (currentState.value == command) {
						System.out.println("already state is " + currentState);
						continue;
					} else if (command == EDownloadStates.PAUSE.value) {
						pauseDownload();
						continue;
					} else if (command == EDownloadStates.RESUME.value) {
						resumeDownload();
						continue;
					} else if (command == 4) {
						System.out.println("exiting");
						return;
					} else if (command == 5) {
						System.out.println("Enter your username: ");
						String line = sc.nextLine();
						return;
					}
					sc.close();
					System.out.println("invalid command");
					continue;
				}
			} catch (Exception e) {
				System.out.println("error occur in commands");
				e.printStackTrace();
			}

		};

		try {
			new Thread(r, "command").start();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void updateProgress(List<Integer> progressList) {
		this.progressList=progressList;
	}
	
	@Override
	public void setCurrentState(EDownloadStates currentState){
			 this.currentState=currentState;
		  }

	
	public URL getUrl() {
		return this.url;
	}

	public String getfilePath() {
		return this.filepath;
	}

}
