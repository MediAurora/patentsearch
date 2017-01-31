package patentsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
public class GooglePatentParser {
		
	/*
	 * method to generate hashkey for google link
	 */
	
	public String makeSHA1Hash(String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException  {
		MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = input.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return hexStr;
	} 
	
	
	public int parseGooglePatent(String url, String keyword) {
		
		System.out.println("<=============== Inside parseGooglePatent()===============>");
		String patTitle = null;
		String abstracts = null;
		String patNumber = null;
		String assignee = null;
		String inventorsStr = "";
		String inventor = null;
		String publishDate = null;
		String filingDate = null;
		String priorityDate = null;	 
		String us_classificationStr = "";
		String int_classificationStr = "";
		String coop_classificationStr = "";
		Document doc;
		if(url == null || url =="") {
			System.err.println("url : " + url);
			return 1;
		}
		
 		System.out.println("url : " + url  + "\n");
 
 		//instantiate MySQL connection
    	MYSQLConnector mysql = new MYSQLConnector();
    	Connection conn = mysql.getmysqlConn();
    	PreparedStatement preparedStmtPat = null;
    	PreparedStatement preparedStmtInventor = null;
    	PreparedStatement preparedStmtClass = null;
		try {
	   		String patsql = "INSERT INTO patentsearch.patentinfo " + 
	    		   	" (PatentNumber, PatentOrg, Inventors, PatentTitle, PublishDate, PatentAbstract,"
	    		   	+ " PatentFileName, PriorityDate, GrantDate, Assignee, Keyword, patentsource, "
	    		   	+ " us_classifications, int_classifications, coop_classifications, inventor_title_id) " 
	    		   	+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	   		String inventorsql = "INSERT INTO patentsearch.patent_inventors " +
	   								"(patent_number, inventor) VALUES (?, ?)"; 
	   							
    		String classSql = "INSERT INTO patentsearch.patent_classification "
    							+ " (pat_classification_type, pat_number, pat_classification, classification_link)"
    							+ " VALUES (?, ?, ?, ?)";
	   		
	   		// create the mysql insert prepared statement
    		preparedStmtPat = conn.prepareStatement(patsql);
    		preparedStmtInventor = conn.prepareStatement(inventorsql);
    		preparedStmtClass = conn.prepareStatement(classSql);
    		
    		// need http protocol
			//doc = Jsoup.connect("http://circ.ahajournals.org/content/131/4/e29.full.pdf+html").userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
			doc = Jsoup.connect(url).userAgent("Mozilla").ignoreHttpErrors(true).timeout(5000).get();
			
			if(doc == null) {
				System.err.println("Document is null ...exiting...");
				return 1;
			} else {
				System.out.println("Document is valid ...proceeding with parsing the page...");
			}
			// get page title
			//String pageTitle = doc.title();
			//System.out.println("page title : " + pageTitle);
			if(!doc.select("meta[name=DC.title]").isEmpty() || 
					doc.select("meta[name=DC.title]") != null) {
				if(doc.select("meta[name=DC.title]").first() != null) {
					patTitle = doc.select("meta[name=DC.title]").first().attr("content");  
					System.out.println("title : " + patTitle  + "\n");
				} else {
					System.err.println("meta[name=DC.title].first() element is null !");
				}
			} else {
				System.err.println("meta[name=DC.title] element is null !");
			}
			if(!doc.select("meta[name=DC.description]").isEmpty()) {
				abstracts = doc.select("meta[name=DC.description]").get(0).attr("content");  
				System.out.println("Abstract : " + abstracts  + "\n");
			}
			if(!doc.select("meta[name=citation_patent_publication_number]").isEmpty()) {
				patNumber = doc.select("meta[name=citation_patent_publication_number]").get(0).attr("content");  
				System.out.println("Patent Number : " + patNumber  + "\n");
			}
			if(!doc.select("meta[name=citation_patent_number]").isEmpty()) {
				patNumber = doc.select("meta[name=citation_patent_number]").get(0).attr("content");  
				System.out.println("Patent Number : " + patNumber  + "\n");
			}
						
			Elements items = doc.select("tr");
			System.out.println("found table row =>" + items.size());
			for (Element item : items) {
				// scraping patent number and priority date
				Elements patentDataHeading = item.select("td[class=patent-bibdata-heading]");
				if(patentDataHeading != null && !patentDataHeading.isEmpty()) {
					if(patentDataHeading.get(0) != null && patentDataHeading.get(0).hasText()) {
						if (patentDataHeading.get(0).text().equals("Publication number")){
							Elements patentDataVal = item.select("td[class=single-patent-bibdata]");
							if(patentDataVal != null && !patentDataVal.isEmpty()) {
								if(patentDataVal.get(0) != null && patentDataVal.get(0).hasText()) {
									patNumber = patentDataVal.get(0).text();
									System.out.println("Publication Number : "+ patNumber + "\n");
								}
							}
						}else if(patentDataHeading.get(0).text().equals("Priority date")) {
							Elements patentDataVal = item.select("td[class=single-patent-bibdata]");
							if(patentDataVal != null && !patentDataVal.isEmpty()) {
								if(patentDataVal.get(0) != null && patentDataVal.get(0).hasText()) {
									priorityDate = patentDataVal.get(0).text();
									System.out.println("priorityDate : "+ priorityDate + "\n");
								}
							}
						}
					}
				}
				Elements classItems = item.getElementsContainingText("International Classification");
				//Elements classItems = item.getElementsByClass("patent-data-table-td");
				if(classItems != null && classItems.size() > 0) {
					System.out.println("found Classification table =>" + classItems.size());
					Elements spanItems = item.select("span[class=nested-value]");
					for(Element span : spanItems) {
						if(span.select("a[href]") != null) {
							String classUrl = span.select("a[href]").get(0).attr("href");
							String classTxt = span.select("a[href]").text();
							int_classificationStr = int_classificationStr + " ; " + classTxt;
							//System.out.println("Classification Type : "+ classItems.text() + "\n");
							//System.out.println("Classification Text : "+ classTxt + "\n");
							//System.out.println("Classification url : "+ classUrl + "\n");
							preparedStmtClass.setString(1, "International Classification");
							preparedStmtClass.setString(2, patNumber);
							preparedStmtClass.setString(3, classTxt);
							preparedStmtClass.setString(4, classUrl);
							preparedStmtClass.execute();
						}
					}
				}
				Elements usClassItems = item.getElementsContainingText("US Classification");
				//Elements classItems = item.getElementsByClass("patent-data-table-td");
				if(usClassItems != null && usClassItems.size() > 0) {
					System.out.println("found Classification table =>" + usClassItems.size());
					Elements usSpanItems = item.select("span[class=nested-value]");
					for(Element usSpan : usSpanItems) {
						if(usSpan.select("a[href]") != null) {
							String usclassUrl = usSpan.select("a[href]").get(0).attr("href");
							String usclassTxt = usSpan.select("a[href]").text();
							us_classificationStr = us_classificationStr + " ; " + usclassTxt;
							//System.out.println("Classification Type : "+ usClassItems.text() + "\n");
							//System.out.println("Classification Text : "+ usclassTxt + "\n");
							//System.out.println("Classification url : "+ usclassUrl + "\n");
							preparedStmtClass.setString(1, "US Classification");
							preparedStmtClass.setString(2, patNumber);
							preparedStmtClass.setString(3, usclassTxt);
							preparedStmtClass.setString(4, usclassUrl);
							preparedStmtClass.execute();
						}
					}
				}
				Elements corpClassItems = item.getElementsContainingText("Cooperative Classification");
				//Elements classItems = item.getElementsByClass("patent-data-table-td");
				if(corpClassItems != null && corpClassItems.size() > 0) {
					System.out.println("found Classification table =>" + corpClassItems.size());
					Elements corpSpanItems = item.select("span[class=nested-value]");
					for(Element corpSpan : corpSpanItems) {
						if(corpSpan.select("a[href]") != null) {
							String corpclassUrl = corpSpan.select("a[href]").get(0).attr("href");
							String corpclassTxt = corpSpan.select("a[href]").text();
							coop_classificationStr = coop_classificationStr + " ; " + corpclassTxt;
							//System.out.println("Classification Type : "+ corpClassItems.text() + "\n");
							//System.out.println("Classification Text : "+ corpclassTxt + "\n");
							//System.out.println("Classification url : "+ corpclassUrl + "\n");
							preparedStmtClass.setString(1, "Cooperative Classification");
							preparedStmtClass.setString(2, patNumber);
							preparedStmtClass.setString(3, corpclassTxt);
							preparedStmtClass.setString(4, corpclassUrl);
							preparedStmtClass.execute();
						}
					}
				}
			}

			System.out.println("International Classifications : " + int_classificationStr  + "\n"); 
			System.out.println("US Classifications : " + us_classificationStr  + "\n"); 
			System.out.println("Coorporate Classifications : " + coop_classificationStr  + "\n"); 
			
			//publishDate = doc.select("meta[name=DC.date]").get(0).attr("content"); 
			if(doc.select("meta[name=DC.date]") != null) {
				if(doc.select("meta[name=DC.date]").first() != null) {
					Element tempPubDate = doc.select("meta[name=DC.date]").get(0);
					if(tempPubDate != null) {
						publishDate = tempPubDate.attr("content");
					}
				}
			}
			System.out.println("Publish Date : " + publishDate  + "\n"); 

			//filingDate = doc.select("meta[name=DC.date]").get(1).attr("content");
			if(doc.select("meta[name=DC.date]") != null) {
				if(doc.select("meta[name=DC.date]").first() != null) {
					Element tempFilingDate = doc.select("meta[name=DC.date]").get(0);
					if(tempFilingDate != null) {
						filingDate = tempFilingDate.attr("content");
					}
				}
			}
			System.out.println("Filing Date : " + filingDate  + "\n"); 
			
			Elements inventors = doc.select("meta[name=DC.contributor]");
			for(Element tempInventor : inventors) {
				if(tempInventor != null) {
					String type = tempInventor.attr("scheme");
					if(type !=null && type.equals("inventor")) {
						inventor = tempInventor.attr("content");
						System.out.println("Inventor : " + inventor);
						inventorsStr = inventorsStr + " ; " +  inventor;
						preparedStmtInventor.setString(1, patNumber);
						preparedStmtInventor.setString(2, inventor);
						preparedStmtInventor.execute();
					}else if(type.equals("assignee")) {
						assignee = tempInventor.attr("content");
						System.out.println("Assignee : " + assignee);
					}
				}
			}
			String inventor_titleHash = makeSHA1Hash(inventorsStr+patTitle);
			
			// inserting into mysql DB
			preparedStmtPat.setString (1, patNumber);
	        preparedStmtPat.setString (2, null);
	        preparedStmtPat.setString (3, inventorsStr);
	        preparedStmtPat.setString (4, patTitle);
	        preparedStmtPat.setString (5, publishDate);
	        preparedStmtPat.setString (6, abstracts);
	        preparedStmtPat.setString (7, url);
	        preparedStmtPat.setString (8, priorityDate);
	        preparedStmtPat.setString (9, filingDate);
	        preparedStmtPat.setString (10, assignee);
	        preparedStmtPat.setString (11, keyword);
	        preparedStmtPat.setString (12, "GooglePatent");
	        preparedStmtPat.setString (13, us_classificationStr);
	        preparedStmtPat.setString (14, int_classificationStr);
	        preparedStmtPat.setString (15, coop_classificationStr);
	        preparedStmtPat.setString (16, inventor_titleHash);
	        // execute the prepared statement
	    	preparedStmtPat.execute();

	 
		} catch (IOException | SQLException | NoSuchAlgorithmException ex) {
			ex.printStackTrace();
    	    // handle any errors
    	    System.err.println("SQLException: " + ex.getMessage());
    	    System.err.println("SQLState: " + ((SQLException) ex).getSQLState());
    	    System.err.println("VendorError: " + ((SQLException) ex).getErrorCode());
		} finally {
    		if (preparedStmtPat != null) {
    			try {
    				preparedStmtPat.close();
    			} catch (SQLException sqlEx) { } // ignore
    				preparedStmtPat = null;
    		}
    		if (preparedStmtInventor != null) {
    			try {
    				preparedStmtInventor.close();
    			} catch (SQLException sqlEx) { } // ignore
    				preparedStmtInventor = null;
    		}
    		if (preparedStmtClass != null) {
    			try {
    				preparedStmtClass.close();
    			} catch (SQLException sqlEx) { } // ignore
    			preparedStmtClass = null;
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    		    } catch (SQLException sqlEx) { } // ignore
    		        conn = null;
    		}
    	}
		return 0;
	}	
	
	/*
	 *  method for parsing patent 
	 * 
	 */
	public int masterPatentParserMethod() {
		System.out.println("<=============== Inside masterPatentParserMethod()===============>");
		int result = 0;
		Document doc;
		//instantiate MySQL connection
    	MYSQLConnector mysql = new MYSQLConnector();
    	Connection conn = mysql.getmysqlConn();
    	Statement stmt = null;
    	ResultSet rs = null;
    	Date currDate = new Date();
    	Calendar cal = Calendar.getInstance();
    	cal.setTime (currDate); // convert your date to Calendar object
    	int daysToDecrement = 0;
    	// converting into yesterday's date (-1)
    	cal.add(Calendar.DATE, daysToDecrement);
    	cal.set(Calendar.HOUR, 00);
    	cal.set(Calendar.MINUTE, 00);
    	cal.set(Calendar.SECOND, 00);
    	Date yesterdayDate = cal.getTime();
    	System.out.println("yesterday's date ==> : " + yesterdayDate);
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	String dateStr = formatter.format(yesterdayDate);
    	System.out.println("yesterday's date in string ==> : " + dateStr);
		try {
	   		String sql = 
	    		   	"SELECT keyword, link FROM patentsearch.googlepatentresult where createdon >= '"+ dateStr +"'";
	   		System.out.println("sql qyery ===>\n" + sql);
	   		
	   		stmt = conn.createStatement();
	   		rs = stmt.executeQuery(sql);
	   		if(rs != null && rs.last()) {
	   			System.out.println("Number of links selected for parsing ===>" + rs.getRow());
	   			rs.beforeFirst();
	   		}
	   		while (rs.next()) {
	   			String keyword = rs.getString("keyword");
	   			String url = rs.getString("link");
	   			System.out.println("URL to be parsed : " + url);
	   			result = parseGooglePatent(url, keyword);
	   			System.out.println("Successfully parsed? ********>>>" + result);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		    // handle any errors
		    System.err.println("SQLException: " + e.getMessage());
		    System.err.println("SQLState: " + ((SQLException) e).getSQLState());
		    System.err.println("VendorError: " + ((SQLException) e).getErrorCode());
		} finally {
			if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException sqlEx) { } // ignore
				stmt = null;
				return 1;
			}
			if (rs != null) {
				try {
					rs.close();
			    } catch (SQLException sqlEx) { } // ignore
			        rs = null;
			        return 1;
			}
			if (conn != null) {
				try {
					conn.close();
			    } catch (SQLException sqlEx) { } // ignore
			        conn = null;
			        return 1;
			}
		}
		return result;
 
	}// close master method
	
	/*
	 * method to scrape google patent portal
	 */
	public int scrapeGooglePatentByURL(String url) {
		System.out.println("<=============== Inside parseGooglePatent()===============>");
		String patTitle = null;
		String abstracts = null;
		String patNumber = null;
		String assignee = null;
		String inventorsStr = "";
		String inventor = null;
		String publishDate = null;
		String filingDate = null;
		String priorityDate = null;	 
		String us_classificationStr = "";
		String int_classificationStr = "";
		String coop_classificationStr = "";
		String claims = null;
		Document doc = null;
		if(url == null || url =="") {
			System.err.println("url : " + url);
			return 1;
		}
		
 		System.out.println("url : " + url  + "\n");

		try {
			doc = Jsoup.connect(url).userAgent("Mozilla").ignoreHttpErrors(true).timeout(5000).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(doc == null) {
			System.err.println("Document is null ...exiting...");
			return 1;
		} else {
			System.out.println("Document is valid ...proceeding with parsing the page...");
		}
		// get page title
		//String pageTitle = doc.title();
		//System.out.println("page title : " + pageTitle);
		if(!doc.select("meta[name=DC.title]").isEmpty() || 
				doc.select("meta[name=DC.title]") != null) {
			if(doc.select("meta[name=DC.title]").first() != null) {
				patTitle = doc.select("meta[name=DC.title]").first().attr("content");  
				System.out.println("title : " + patTitle  + "\n");
			} else {
				System.err.println("meta[name=DC.title].first() element is null !");
			}
		} else {
			System.err.println("meta[name=DC.title] element is null !");
		}
		if(!doc.select("meta[name=DC.description]").isEmpty()) {
			abstracts = doc.select("meta[name=DC.description]").get(0).attr("content");  
			System.out.println("Abstract : " + abstracts  + "\n");
		}
		if(!doc.select("meta[name=citation_patent_publication_number]").isEmpty()) {
			patNumber = doc.select("meta[name=citation_patent_publication_number]").get(0).attr("content");  
			System.out.println("Patent Number : " + patNumber  + "\n");
		}
		if(!doc.select("meta[name=citation_patent_number]").isEmpty()) {
			patNumber = doc.select("meta[name=citation_patent_number]").get(0).attr("content");  
			System.out.println("Patent Number : " + patNumber  + "\n");
		}
		if(!doc.select("div[class=claim-text style-scope patent-text]").isEmpty()) {
			System.out.println("claims section is not null : \n");
			claims = doc.select("div[class=claim-text style-scope patent-text]").get(0).attr("content");  
			System.out.println("Claim # 1 : " + claims  + "\n");
		}

		return 0;
	}
	
	
	public static void main(String[] args) {
	 
		//String url = "https://www.google.com/patents/WO2015050398A1?cl=en&dq=3D+Bioprinting&hl=en&sa=X&ved=0ahUKEwi3sdC-qanNAhUPS2MKHU9UAasQ6AEIGjAC";
		//String url = "https://patents.google.com/patent/US7722604B2/en?q=ep+catheter&assignee=bard&country=US";
		//String url = "https://patents.google.com/patent/EP2013752781/en";
		String url = "https://patents.google.com/?q=EP2013752781&num=10";
		GooglePatentParser scrapper = new GooglePatentParser();

		//int result = scrapper.parseGooglePatent(url, "ep+Catheter+Bard");
		//System.out.println("patent parser result ==>" + result);
		int result = scrapper.masterPatentParserMethod();	
		//int result = scrapper.scrapeGooglePatentByURL(url);
	  }
 
}