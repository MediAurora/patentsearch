package patentsearch;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
 
public class GoogleScraperMaster implements Job {
	

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		// TODO Auto-generated method stub
		
		JobKey jobKey = context.getJobDetail().getKey();
		
		// Run google Search based on user defined keywords
		//GoogleSearchParser googleSearch = new GoogleSearchParser();
		//googleSearch.masterMethod("divyap");
		System.out.println("Hi there-- i am running !!!!!");
		System.out.println("SimpleJob says: " + jobKey + " executing at " + new Date());
		//Run google patent parser for links fetched from yesterday
				
	}
	
	public static void main(String[] args) {
		// Run google Search based on user defined keywords
		GoogleSearchParser googleSearch = new GoogleSearchParser();
		googleSearch.masterMethod("divyap");
		
		//Run google patent parser for links fetched from yesterday
		GooglePatentParser parsePatent = new GooglePatentParser();
		
		int result = parsePatent.masterPatentParserMethod();
		System.out.println("Result ====>" + result);
	}
}