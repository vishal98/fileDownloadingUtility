package com.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.download.IDownloadManager;
import com.download.fileDownloader.FileDownloadManager;


public class App {
	
	
	public static void main(String[] args) throws FileNotFoundException {
		App app = new App();
		app.downloadFile();
	}

	
	public File downloadFile() {
		Scanner sc = null;
		try {
			String url = null;
			String filePath = null;
			String command = null;
			System.out.println("Enter filename and location with spaces");
			sc = new Scanner(System.in);
			String input = sc.nextLine();
			if (input != null && input.length() > 0) {
				String[] data = input.split("\\s+");
				url = data[0];
				if (data.length > 1) {
					filePath = data[1];
				} else {
					System.out.println("file path not specified");
				   main(data);
				}
				
			}

			IDownloadManager downloadManager = new FileDownloadManager(url, filePath);
			downloadManager.init();
			File fl=new File(filePath);
			return fl;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			sc.close();
		}
		return null;

	}

}
