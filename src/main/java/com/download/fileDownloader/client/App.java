package com.download.fileDownloader.client;

import java.io.FileNotFoundException;
import java.util.Scanner;

import com.download.fileDownloader.FileDownloadManager;
import com.download.fileDownloader.IFileDownloadManager;


public class App {
	
	
	public static void main(String[] args) throws FileNotFoundException {
		App app = new App();
		app.fileInputs();
	}

	
	public void fileInputs() {
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

			IFileDownloadManager fl = new FileDownloadManager(url, filePath);
			fl.init();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			sc.close();
		}

	}

}
