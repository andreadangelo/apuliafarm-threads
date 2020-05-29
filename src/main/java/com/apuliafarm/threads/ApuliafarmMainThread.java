package com.apuliafarm.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApuliafarmMainThread {
	private  static final Logger logger = LoggerFactory.getLogger(ApuliafarmMainThread.class);	
	public static void main(String[] args) {		
		try (InputStream input = new FileInputStream("target/classes/apuliafarm-thread.properties")) {
			Properties prop = new Properties();
			prop.load(input);
			File folder = new File(prop.getProperty("products.dir.path"));
			String backupPath = prop.getProperty("products.backudir.path");
			String restURL = prop.getProperty("productcontroller.addFromCsv");
			for (File f : folder.listFiles()) {
				System.out.println(f.getAbsolutePath());
				if (f.isFile()) {
					new Thread(new WorkCvsThread(f,backupPath, restURL )).run();
				}
			}
		} catch (IOException e) {
			logger.error("Error", e);
		}		
	}
}
