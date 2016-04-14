/**
 * 
 */
package patentsearch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

/**
 * @author dprakash
 *
 */
public class PatentParser {

	 /*
	  * Method to read Config properties file
	  */
	 public String getTextPath() {
		 Properties props = new Properties();
		 String textPath = null;
		 try {
			 InputStream resourceStream = 
					 Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
			 props.load(resourceStream);
			 textPath = (String) props.get("textpath");
			 System.out.println("text path is ==>" + textPath);
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
		return textPath;
	 }
	 
	 /*
	  * Method to read Schema properties file
	  */
	 public Map getSchemaInfo() {
		 Properties props = new Properties();
		 Map<String, String> schema = new HashMap();
		 try {
			 InputStream resourceStream = 
					 Thread.currentThread().getContextClassLoader().getResourceAsStream("schema.properties");
			 props.load(resourceStream);
			 schema.put("patorg1",(String) props.get("patorg1"));
			 schema.put("patorg2",(String) props.get("patorg2"));
			 schema.put("patorg3",(String) props.get("patorg3"));
			 schema.put("patorg4",(String) props.get("patorg4"));
			 schema.put("endpatorg1",(String) props.get("endpatorg1"));
			 schema.put("endpatorg2",(String) props.get("endpatorg2"));
			 schema.put("endpatorg3",(String) props.get("endpatorg3"));
			 schema.put("endpatorg4",(String) props.get("endpatorg4"));
			 schema.put("pubdate1",(String) props.get("pubdate1"));
			 schema.put("pubdate2",(String) props.get("pubdate2"));
			 schema.put("pubdate3",(String) props.get("pubdate3"));
			 schema.put("pubdate4",(String) props.get("pubdate4"));
			 schema.put("pubnum1",(String) props.get("pubnum1"));
			 schema.put("pubnum2",(String) props.get("pubnum2"));
			 schema.put("endpubnum1",(String) props.get("endpubnum1"));
			 schema.put("endpubnum2",(String) props.get("endpubnum2"));
			 schema.put("inventors1",(String) props.get("inventors1"));
			 schema.put("inventors2",(String) props.get("inventors2"));
			 schema.put("inventors3",(String) props.get("inventors3"));
			 schema.put("inventors4",(String) props.get("inventors4"));
			 schema.put("abstract1",(String) props.get("abstract1"));
			 schema.put("abstract2",(String) props.get("abstract2"));
			 schema.put("title1",(String) props.get("title1"));
			 schema.put("title2",(String) props.get("title2"));
			 schema.put("endtitle1",(String) props.get("endtitle1"));
			 schema.put("endtitle2",(String) props.get("endtitle2"));
			 schema.put("endpubdate1",(String) props.get("endpubdate1"));
			 schema.put("endpubdate2",(String) props.get("endpubdate2"));
			 schema.put("endinventors1",(String) props.get("endinventors1"));
			 schema.put("endinventors2",(String) props.get("endinventors2"));
			 schema.put("endinventors3",(String) props.get("endinventors3"));
			 schema.put("endinventors4",(String) props.get("endinventors4"));
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
		return schema;
	 }
	 

	 /*
	  * Method to read file content and store it in a string
	  * 
	  */
	 
	static String readFile(String path, Charset encoding) 
			  throws IOException 	{
		System.out.println("Inside readFile() method ------------------>" + path);
		File file = new File(path);
		
		byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
		return new String(encoded, encoding);
	}
	 

	/*
	 * Parse text file and store the values in MySQL Database
	 * 
	 */
	public Map parsePatent (String fileName) {
		System.out.println("Inside parsePatent() method ------------------>");
		String content = null;	
		Map schema = null;
		String patOrg = null;
		String patNum = null;
		String patPubDate = null;
		String inventors = null;
		String title = null;
		String abs = null;    	
		Map patInfo = new HashMap<String, String>();
		if(fileName != null) {
			schema = getSchemaInfo();
			
			try {
				content = readFile(fileName, Charset.defaultCharset());
				//System.out.println("File Content is ==>\n" + content);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(content != null) {
				//content = content.replaceAll("\\s+","");
				patInfo.put("filename", fileName);
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				patInfo.put("datetime", date.toString());
				try {
					// get publication Organization
					if(content.contains((String)schema.get("patorg1"))) {
						int begin = content.lastIndexOf((String)schema.get("patorg1"));
						int end = content.indexOf((String)schema.get("endpatorg1"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						patOrg = content.substring(begin + 4, end);
						System.out.println("Patent Organization ==>" + patOrg);
						patInfo.put("puborg", patOrg);
					}else if(content.contains((String)schema.get("patorg2"))) {
						int begin = content.lastIndexOf((String)schema.get("patorg2"));
						int end = content.indexOf((String)schema.get("endpatorg2"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						patOrg = content.substring(begin + 4, end);
						System.out.println("Patent Organization ==>" + patOrg);
						patInfo.put("puborg", patOrg);
					}else if(content.contains((String)schema.get("patorg3"))) {
						int begin = content.lastIndexOf((String)schema.get("patorg3"));
						int end = content.indexOf((String)schema.get("endpatorg3"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						patOrg = content.substring(begin + 4, end);
						System.out.println("Patent Organization ==>" + patOrg);
						patInfo.put("puborg", patOrg);
					}else if(content.contains((String)schema.get("patorg4"))) {
						int begin = content.lastIndexOf((String)schema.get("patorg4"));
						int end = content.indexOf((String)schema.get("endpatorg4"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						patOrg = content.substring(begin + 4, end);
						System.out.println("Patent Organization ==>" + patOrg);
						patInfo.put("puborg", patOrg);
					}
					
					// get publication title
					if(content.contains((String)schema.get("title1"))) {
						int begin = content.lastIndexOf((String)schema.get("title1"));
						int end = content.indexOf((String)schema.get("endtitle1"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						title = content.substring(begin + 4, end);
						System.out.println("title ==>" + title);
						patInfo.put("pubtitle", title);
					}else if(content.contains((String)schema.get("title2"))) {
						int begin = content.lastIndexOf((String)schema.get("title2"));
						int end = content.indexOf((String)schema.get("endtitle2"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						title = content.substring(begin + 4, end);
						System.out.println("title ==>" + title);
						patInfo.put("pubtitle", title);
					}
					
					// get publication date
					if(content.contains((String)schema.get("pubdate1"))) {
						int begin = content.lastIndexOf((String)schema.get("pubdate1"));
						int end = content.indexOf((String)schema.get("endpubdate1"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						if(end == -1) end = begin + 4 + 35;
						patPubDate = content.substring(begin + 4, end);
						System.out.println("publication date ==>" + patPubDate);
						patInfo.put("pubdate", patPubDate);
					}else if(content.contains((String)schema.get("pubdate2"))) {
						int begin = content.lastIndexOf((String)schema.get("pubdate2"));
						int end = content.indexOf((String)schema.get("endpubdate2"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						if(end == -1) end = begin + 4 + 35;
						patPubDate = content.substring(begin + 4, end);
						System.out.println("publication date ==>" + patPubDate);
						patInfo.put("pubdate", patPubDate);
					}else if(content.contains((String)schema.get("pubdate3"))) {
						int begin = content.lastIndexOf((String)schema.get("pubdate3"));
						int end = content.indexOf((String)schema.get("endpubdate1"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						if(end == -1) end = begin + 4 + 35;
						patPubDate = content.substring(begin + 4, end);
						System.out.println("publication date ==>" + patPubDate);
						patInfo.put("pubdate", patPubDate);
					}else if(content.contains((String)schema.get("pubdate4"))) {
						int begin = content.lastIndexOf((String)schema.get("pubdate4"));
						int end = content.indexOf((String)schema.get("endpubdate2"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						if(end == -1) end = begin + 4 + 35;
						patPubDate = content.substring(begin + 4, end);
						System.out.println("publication date ==>" + patPubDate);
						patInfo.put("pubdate", patPubDate);
					}
					
					// get publication number
					if(content.contains((String)schema.get("pubnum1"))) {
						int begin = content.lastIndexOf((String)schema.get("pubnum1"));
						System.out.println("begin==>" + begin);
						//int end = content.indexOf((String)schema.get("endpubnum1"), begin);
						int end = content.indexOf((String)schema.get("endpubnum1"));
						System.out.println("begin & end ==>" + begin + " , " + end);
						patNum = content.substring(begin + 4, end);
						System.out.println("publication number ==>" + patNum);
						patInfo.put("pubno", patNum);
					}else if(content.contains((String)schema.get("pubnum2"))) {
						int begin = content.lastIndexOf((String)schema.get("pubnum2"));
						int end = content.indexOf((String)schema.get("endpubnum2"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						patNum = content.substring(begin + 4, end);
						System.out.println("publication Number ==>" + patNum);
						patInfo.put("pubno", patNum);
					}
					
					// get Inventors
					if(content.contains((String)schema.get("inventors1"))) {
						int begin = content.lastIndexOf((String)schema.get("inventors1"));
						int end = content.indexOf((String)schema.get("endinventors1"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						inventors = content.substring(begin + 4, end);
						System.out.println("Inventors ==>" + inventors);
						patInfo.put("inventors", inventors);
					}else if(content.contains((String)schema.get("inventors2"))) {
						int begin = content.lastIndexOf((String)schema.get("inventors2"));
						int end = content.indexOf((String)schema.get("endinventors2"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						inventors = content.substring(begin + 4, end);
						System.out.println("Inventors ==>" + inventors);
						patInfo.put("inventors", inventors);
					}else if(content.contains((String)schema.get("inventors3"))) {
						int begin = content.lastIndexOf((String)schema.get("inventors3"));
						int end = content.indexOf((String)schema.get("endinventors3"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						inventors = content.substring(begin + 4, end);
						System.out.println("Inventors ==>" + inventors);
						patInfo.put("inventors", inventors);
					}else if(content.contains((String)schema.get("inventors4"))) {
						int begin = content.lastIndexOf((String)schema.get("inventors4"));
						int end = content.indexOf((String)schema.get("endinventors4"), begin);
						System.out.println("begin & end ==>" + begin + " , " + end);
						inventors = content.substring(begin + 4, end);
						System.out.println("Inventors ==>" + inventors);
						patInfo.put("inventors", inventors);
					}
					
					// get Abstract
					if(content.contains((String)schema.get("abstract1"))) {
						int begin = content.lastIndexOf((String)schema.get("abstract1"));
						//int end = content.indexOf((String)schema.get("endinventors1"), begin);
						System.out.println("begin & end ==>" + begin);
						abs = content.substring(begin + 4);
						System.out.println("abstract ==>" + abs);
						patInfo.put("abstract", abs);
					}else if(content.contains((String)schema.get("abstract2"))) {
						int begin = content.lastIndexOf((String)schema.get("abstract2"));
						//int end = content.indexOf((String)schema.get("endinventors2"), begin);
						System.out.println("begin & end ==>" + begin);
						abs = content.substring(begin + 4);
						System.out.println("abstract ==>" + abs);
						patInfo.put("abstract", abs);
					}
					
				}catch (Exception ex) {
					System.err.println("String parsing error !! ==>");
					ex.printStackTrace();
					return null;
				}
			} else {
				System.err.println("Patent text file is empty !! ==>" + fileName);
				return null;
			}
		}else {
			System.err.println("Patent file path is Null !!===>" + fileName);
			return null;
		}
		
		return patInfo;
	}
	
	public void parsePatentBatch() {
		
		System.out.println("Inside parsePatentBach() method ------------------>");
		String txtPath = getTextPath();
		if(txtPath == null) {
			System.err.println("TextPath is set as Null !!");
		} else {
			File[] files = new File(txtPath).listFiles();
	    	System.out.println("# of files found = " + files.length);
	    	if(files.length == 0) {
	    		System.out.println("No files found at ..: " + txtPath);
	    	} else {
		    	for (File file : files) {
		            if (!file.isDirectory()) {
		            	String fileName = file.getName();
		            	String baseName = FilenameUtils.getBaseName(fileName);
		            	System.out.println("\nFile base name is : " + baseName);
		            	System.out.println("\nFile name is : " + fileName);
		            	
		            	String tmpSrcPath = txtPath + "\\" + fileName;
		            	
		            	//String patText = NLPAPIController.getFileContents(tmpSrcPath);
		            	System.out.println("\nfull file path is : " + tmpSrcPath);
						
		            	// call the parser method
						parsePatent(tmpSrcPath);		            	
		           } else {
		                System.out.println("This is not a file but a directory ....: " + file.getName());
		                continue;
		            }
		        }
	    	}
		}
        
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		PatentParser parser = new PatentParser();
	
		//int result = parser.parsePatentBatch();
		Map patInfo =  parser.parsePatent("C:\\DP\\patents\\text\\US9115238(B2)_1.txt");
		System.out.println("parsing result ===>" + patInfo.toString());

		

	}

}
