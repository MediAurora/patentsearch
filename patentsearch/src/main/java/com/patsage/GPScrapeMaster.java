package com.patsage;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
 
public class GPScrapeMaster {
	
	public void GPScrapeMasterMethod () {
		
		// insert Search keywords
		GPSearchManager keywords = new GPSearchManager();
		GPSearchObject obj = new GPSearchObject();
		keywords.saveSearch(obj);
		
		//Run search on Google Patent(GP) using keywords and store GP Links.
		
		
		//Run GoogleScraper for each GP Link and store Patent Info.
		
		
	}
	
	public static void main(String[] args) {
		// Run google Search based on user defined keywords
		GPLinkManager googleSearch = new GPLinkManager();
		//googleSearch.masterMethod("divyap");
		
		//Run google patent parser for links fetched from yesterday
		GooglePatentParser parsePatent = new GooglePatentParser();
		
		int result = parsePatent.masterPatentParserMethod();
		System.out.println("Result ====>" + result);
	}
}