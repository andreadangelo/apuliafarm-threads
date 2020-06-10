package com.apuliafarm.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.apuliafarm.entities.Category;
import com.apuliafarm.entities.Product;
import com.apuliafarm.entities.Seller;

public class WorkCvsThread implements Runnable {
	private  static final Logger logger = LoggerFactory.getLogger(WorkCvsThread.class);	
	private String backupPath = null;
	private String restURL = null;
	private File file = null;
	
	public WorkCvsThread(File file, String backupPath, String restURL){
		this.backupPath = backupPath;
		this.file = file;
		this.restURL = restURL;
	}
	public WorkCvsThread(){
		super();
	}
	
	public void run() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			try {
			    String line;
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			    while ((line = br.readLine()) != null) {
			        String arr[] = line.split(";");
			        //Avogado;AVG_DST;3.5;100;FRU;2020-06-10;2020-06-10;DST
			        Seller seller = new Seller(arr[7]);				        
			        Category category = new Category(arr[4]);
			        try{
				        Product product = new Product(arr[0], arr[1], Float.parseFloat(arr[2]), Float.parseFloat(arr[3]), category, seller, sdf.parse(arr[5]), sdf.parse(arr[6]));			       
				        product = new RestTemplate().postForObject(restURL, product, Product.class);
					} catch (ParseException e) {
						logger.error("Error" , e);					
			        }catch(Exception e ){
			        	logger.error("Error: " + line  , e);
			        }				        
			    }
			}catch(org.springframework.web.client.ResourceAccessException e){
				logger.error("Error" , e);
			}catch(java.net.ConnectException e){	
				logger.error("Error" , e);
			} catch (IOException e) {
				logger.error("Error" , e);					
			} finally {
			    try {
					br.close();						
				} catch (IOException e) {
					logger.error("Error" , e);
				}				    
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSSS");
				String backupFile = backupPath +file.getName() +  "_bck_"+ sdf.format(new Date())+ ".CSV";					
			    try {
					Files.copy(file.toPath(), new File(backupFile).toPath(),  StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					logger.error("Error" , e);
				}				    
			    file.delete();		    
			}
		} catch (FileNotFoundException e) {
			logger.error("Error" , e);
		}catch(NullPointerException e){
			logger.error("Error" , e);
		}
		file = null;
	}
}