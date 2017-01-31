package com.patsage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;

 
public class GPLinkManager {
	
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
	
	/*
	 *  The method collects patent links from Google Patent Search
	 *  based on search key words configured in patentsearch.gp_searchkeyword
	 *  result = 0; error
	 *  result = 1; success
	 */
	public int getGPSearchResults() {
		System.out.println("<================== inside getGPSearchResults()==========================>");
		Document doc;
		GPSearchManager searchMgr = new GPSearchManager();
		ArrayList<Object> key = searchMgr.getSearchCriteria();
		if(key == null | key.isEmpty()) {
			System.err.println("----- search keywords map is null or empty----");
			return 0;
		}
		int size = key.size();
		//instantiate MySQL connection
    	MYSQLConnector mysql = new MYSQLConnector();
    	Connection conn = mysql.getmysqlConn();
    	PreparedStatement preparedStmt = null;
    	
		try {
			for(int i=0; i < size; i++) {
				HashMap<String, Object> hash = (HashMap)key.get(i);
				int searchid = (int) hash.get("searchid");
				String url = (String) hash.get("url");
	   			String sql = "INSERT INTO patentsearch.gp_searchresult " + 
							"(criteria_id, search_url, result_link, linkhash)" +
							"VALUES (?, ?, ?, ?)";

	   			// create the mysql insert prepared statement
	   			preparedStmt = conn.prepareStatement(sql);
		 		System.out.println("url : " + url);
		 		if (url == null) {
		 			System.err.println("Search url is null....");
		 			return 0;
		 		}
		 		URL myUrl = new URL(url); 
		 		// need http protocol
				doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0")
										.ignoreHttpErrors(true).timeout(5000).get();
		 		//doc = Jsoup.parse(myUrl, 5000);
		 		//doc = getGPSearchResultPage(url);
		 		if(doc == null) {
		 			System.err.println("Document is null....");
		 			return 0;
		 		}
		 		// get page title
				String pageTitle = doc.title();
				System.out.println("page title : " + pageTitle);

				//Elements searchResultItems = doc.select("section[class=style-scope search-results]");
				//Elements searchResultItems = doc.select("a[href]");
				Elements searchResultItems = doc.getElementsByTag("a");

				System.out.println("Element  : " + searchResultItems);
				if(searchResultItems != null) {
					System.out.println("Element size : " + searchResultItems.size() + " : " + searchResultItems.text());
					
					for(Element searchItem : searchResultItems) {
				       	String href = null;
				       	if(searchItem != null) {
				       		String hrefLink = searchItem.attr("href");
				       		System.out.println("link : " + hrefLink);
				       		//System.out.println("Body: "+body + "\n");
				       		//preparedStmt.setString (1, keyword);
				       		//preparedStmt.setString (2, title);
				       		//preparedStmt.setString (3, body);
				       		//preparedStmt.setString (4, href);
				       		//String linkhash = makeSHA1Hash(href);
				       		//System.out.println("url : " + linkhash);
				       		//preparedStmt.setString (5, linkhash);					
					   		
				       		// execute the prepared statement
				       		//preparedStmt.execute();
						}
					}
				}
			}
		} catch (IOException | SQLException ex) {
			ex.printStackTrace();
    	    // handle any errors
    	    System.err.println("SQLException: " + ex.getMessage());
    	    System.err.println("SQLState: " + ((SQLException) ex).getSQLState());
    	    System.err.println("VendorError: " + ((SQLException) ex).getErrorCode());
		} finally {
    		if (preparedStmt != null) {
    		try {
    			preparedStmt.close();
    		} catch (SQLException sqlEx) { } // ignore
    			preparedStmt = null;
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
	 * Method to fetch search criteria for a user.
	 */
	public ArrayList<Map<String, Object>> getSearchCriteria(String userid) {
		
		System.out.println("<================== inside getSearchCriteria()==========================>");
		
 		//instantiate MySQL connection
    	if(userid == null || userid =="") {
    		System.err.println(" ----- UserId is null or blank! ------ ");
    		return null;
    	}
		ArrayList keyList = new ArrayList();
		MYSQLConnector mysql = new MYSQLConnector();
    	Connection conn = mysql.getmysqlConn();
    	Statement stmt = null;
    	ResultSet rs = null;
    	
    	try {
	   		String sql = 
	    		   	"Select userid, keyword, linknum, source from patentsearch.searchkeyword where userid = '"
	    		   	+ userid + "' and active = 0";

    		// create the mysql insert prepared statement
    		stmt = conn.createStatement();
    		rs = stmt.executeQuery(sql);
    		while (rs.next()) {
    			String user = rs.getString("userid");
    			String keyword = rs.getString("keyword");
    			Integer linknum = new Integer(rs.getInt("linknum"));
    			Integer sourceId = new Integer(rs.getInt("source"));
    			System.out.println("resultset : " + user + " : " + keyword + " : "+ linknum );
    			Map<String, Object> keys = new HashMap<String, Object> ();
    			keys.put("user", user);
    			keys.put("keyword", keyword);
    			keys.put("linknum", linknum);
    			keys.put("source", sourceId);
    			keyList.add(keys);
    		}
    		
		} catch (SQLException  ex) {
			ex.printStackTrace();
    	    // handle any errors
    	    System.err.println("SQLException: " + ex.getMessage());
    	    System.err.println("SQLState: " + ((SQLException) ex).getSQLState());
    	    System.err.println("VendorError: " + ((SQLException) ex).getErrorCode());
		} finally {
    		if (stmt != null) {
    		try {
    			stmt.close();
    		} catch (SQLException sqlEx) { } // ignore
    			stmt = null;
    		}
    		if (rs != null) {
    			try {
    				rs.close();
    		    } catch (SQLException sqlEx) { } // ignore
    		        rs = null;
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    		    } catch (SQLException sqlEx) { } // ignore
    		        conn = null;
    		}
    	}
		return keyList;
	}
	
	
	/*
	 * method to search Google Patent site using PatentNumber
	 * the method creates links for google patent site using patent number and
	 * store it back into googlepatentresult table
	 */
	public void prepareGooglePatentLinkUsingPatentNo(String user) {
		System.out.println("<================== inside prepareGooglePatentLinkUsingPatentNo()==========================>");
		MYSQLConnector mysql = new MYSQLConnector();
    	Connection conn = mysql.getmysqlConn();
    	Statement stmt = null;
    	ResultSet rs = null;
    	PreparedStatement preparedStmt = null;
    	try {
	   		String sql = 
	    		   	"Select projectid, patentnumber from patentsearch.patentlist";

    		// create the mysql insert preparedstatement
    		stmt = conn.createStatement();
    		rs = stmt.executeQuery(sql);
    		while (rs.next()) {
    			Integer projectid = new Integer(rs.getInt("projectid"));
    			String patnum = rs.getString("patentnumber");
    			String patentLink = "https://patents.google.com/patent/" + patnum + "/en";
    			System.out.println("resultset : " + projectid + " : " + patnum + " : " + patentLink);
    			String insertsql = "INSERT INTO patentsearch.googlepatentresult " + 
									"(keyword, link, user, active, projectid)" + 
									"VALUES (?, ?, ?, ?, ?)";
	   			// create the mysql insert prepared statement
	   			preparedStmt = conn.prepareStatement(insertsql);
	            preparedStmt.setString (1, patnum);
	            preparedStmt.setString (2, patentLink);
	            preparedStmt.setString (3, user);
	            preparedStmt.setInt (4, 1);
	            preparedStmt.setInt (5, 1);
	    		// execute the preparedstatement
	    		preparedStmt.execute();
    		}
    		
		} catch (SQLException  ex) {
			ex.printStackTrace();
    	    // handle any errors
    	    System.err.println("SQLException: " + ex.getMessage());
    	    System.err.println("SQLState: " + ((SQLException) ex).getSQLState());
    	    System.err.println("VendorError: " + ((SQLException) ex).getErrorCode());
		} finally {
    		if (stmt != null) {
    		try {
    			stmt.close();
    		} catch (SQLException sqlEx) { } // ignore
    			stmt = null;
    		}
    		if (rs != null) {
    			try {
    				rs.close();
    		    } catch (SQLException sqlEx) { } // ignore
    		        rs = null;
    		}
    		if (conn != null) {
    			try {
    				conn.close();
    		    } catch (SQLException sqlEx) { } // ignore
    		        conn = null;
    		}
    	}
	}
	
	/*
	 * use this mthod to open Google Patent in firefox browser and store the page source 
	 * as DOM to parse further using JSoup.
	 */
	public org.w3c.dom.Document getGPSearchResultPage(String url) {
		/*
		// Selenium
		System.setProperty("webdriver.chrome.driver", "C:/Softwares/Chrome Driver/chromedriver.exe");
		WebDriver driver = new ChromeDriver();

		driver.get("http://www.google.com");
		//driver.get(url);  
		String html_content = driver.getPageSource();
		driver.close();

		// Jsoup makes DOM here by parsing HTML content
		Document doc = Jsoup.parse(html_content);
		*/
        HtmlPage page = null;
		final WebClient webClient = new WebClient();

		try {
			webClient.setWebConnection(
			        new WebConnectionWrapper(webClient) {
			            public WebResponse getResponse(WebRequest request) throws IOException {
			                WebResponse response = super.getResponse(request);
			                String content = response.getContentAsString("UTF-8");
			                if(content != null) {
			                    if(!content.contains("<body>") && content.contains("</head>")) {
			                        content = content.replace("</head>", "</head>\n<body>");
			                        if(!content.contains("</body>") && content.contains("</html>")) {
			                            content = content.replace("</html>", "</body>\n</html>");
			                        }
			                    }
			                }
			                System.out.println("response: {}" +  content);
			                WebResponseData data = new WebResponseData(content.getBytes("UTF-8"),
			                        response.getStatusCode(), response.getStatusMessage(), response.getResponseHeaders());
			                response = new WebResponse(data, request, response.getLoadTime());
			                return response;
			            }
			        });
			page = webClient.getPage(url);
			
		} catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        org.w3c.dom.Document doc = page.getOwnerDocument();
        System.out.println("html page source code =====>\n" + doc);

        webClient.close();
		

		// OPERATIONS USING DOM TREE
		return doc;
	}
	
	public static void main(String[] args) {
	 
		//String keys = "3D+Bioprinting";
		//String pageitem = "10";
		String userid = "divyap";
		ArrayList<Map<String, Object>> keyList = new ArrayList<Map<String, Object>>();
		GPLinkManager linkMgr = new GPLinkManager();

		//load patentlink in googlepatentresult
		//scrapper.prepareGooglePatentLinkUsingPatentNo("divyap");

		//run keyword search on Google Patent and store result links.
		linkMgr.getGPSearchResults();
		/*
		ClassLoader classLoader = GPLinkManager.class.getClassLoader();
		URL resource = classLoader.getResource("org.apache.http.impl.client.HttpClientBuilder.dnsResolver");
		System.out.println(resource);
		
		String url = "https://patents.google.com/?q=&q=Catheter,ablation&q=injectible&before=filing:2012-01-01&assignee=Boston&num=10";
		org.w3c.dom.Document doc = linkMgr.getGPSearchResultPage(url);
		*/
	  }
 
}