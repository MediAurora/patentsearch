package patentsearch;

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

 
public class GoogleSearchParser {
	
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
	 *  Collect data from Google Advance Patent Search
	 */
	public int getGoogleSearchResults(ArrayList key) {
		System.out.println("<================== inside getGoogleSearchResults()==========================>");
		Document doc;

		if(key == null | key.isEmpty()) {
			System.err.println("----- search keywords map is null or empty----");
			return 1;
		}
		int size = key.size();
		//instantiate MySQL connection
    	MYSQLConnector mysql = new MYSQLConnector();
    	Connection conn = mysql.getmysqlConn();
    	PreparedStatement preparedStmt = null;
    	
		try {
			for(int i=0; i < size; i++) {
				HashMap<String, Object> tempMap = (HashMap)key.get(i);
				String userid = (String) tempMap.get("userid");
				String keyword = (String) tempMap.get("keyword");
				Integer sourceId = (Integer) tempMap.get("source");
				int item = (Integer)tempMap.get("linknum");
				String url = null;

	   			String sql = "INSERT INTO patentsearch.googlepatentresult " + 
							"(keyword, title, body, link, linkhash)" +
							"VALUES (?, ?, ?, ?, ?)";

	   			// create the mysql insert prepared statement
	   			preparedStmt = conn.prepareStatement(sql);

				if(sourceId == 1) {
					//Google search
					url = "https://www.google.com/search?tbo=p&tbm=pts&hl=en&q="+ keyword + "&num=" + item ;
				
				} else if(sourceId == 0) {
					// Google Patent Search
					url = "https://patents.google.com/?q="+ keyword + "&num=" + item ;
				}
		 		System.out.println("url : " + url);
		 		URL myUrl = new URL(url); 
		 		// need http protocol
				doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0")
										.ignoreHttpErrors(true).timeout(5000).get();
		 		//doc = Jsoup.parse(myUrl, 5000);
				
		 		// get page title
				String pageTitle = doc.title();
				System.out.println("page title : " + pageTitle);
				
				System.out.println("SourceId : "+ sourceId + "\n");
				if(sourceId == 1) {
					// get all links
					Elements links = doc.select("div[class=g]");
					//Elements links = doc.select("a[href]");
					if(links != null)
						System.out.println("Element size : " + links.size());			        
					
					for (Element link : links) {
		
			        	Elements hrefLink = link.select("a[href]");
			        	String href = null;
			        	if(hrefLink != null)
			        		href = hrefLink.get(0).attr("href");
			        	
			        	Elements titles = link.select("h3[class=r]");
			            String title = titles.text();
			
			            Elements bodies = link.select("span[class=st]");
			            String body = bodies.text();
			
			            System.out.println("Title: "+ title + "\n");
			            System.out.println("link : "+ href + "\n");
			            System.out.println("Body: "+body + "\n");
			            preparedStmt.setString (1, keyword);
			            preparedStmt.setString (2, title);
			            preparedStmt.setString (3, body);
			            preparedStmt.setString (4, href);
			            String linkhash = makeSHA1Hash(href);
			            System.out.println("url : " + linkhash);
			            preparedStmt.setString (5, linkhash);
			            
			    		// execute the preparedstatement
			    		preparedStmt.execute();
			        }
				} else if(sourceId == 0) {
					Elements searchResultItems = doc.select("section[class=style-scope search-results]");
					//Element resultContainer = doc.getElementById("leftBar");
					
					System.out.println("Element  : " + searchResultItems);
					if(searchResultItems != null) {
						//System.out.println("Element size : " + resultContainer.text());
						System.out.println("Element size : " + searchResultItems.size() + " : " + searchResultItems.text());
						/*
						for(Element searchItem : searchResultItems) {
				        	String href = null;
				        	if(searchItem != null) {
				        		href = searchItem.data();	
				        		System.out.println("href : " + href);
				        		//System.out.println("href : " +searchItem.getElementsByAttribute("href"));
					            //System.out.println("Title: "+ title + "\n");
					            //System.out.println("link : "+ href + "\n");
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
						}*/
					}
				}
			}
		} catch (IOException | SQLException | NoSuchAlgorithmException ex) {
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
	 * Method to fetch serarch criteria for a user.
	 */
	public ArrayList<Map<String, Object>> getSearchCriteria(String userid) {
		
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

    		// create the mysql insert preparedstatement
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
	
	public void masterMethod (String userid) {
		ArrayList<Map<String, Object>> keyList = new ArrayList<Map<String, Object>>();
		GoogleSearchParser scrapper = new GoogleSearchParser();
		keyList = scrapper.getSearchCriteria(userid) ;
		scrapper.getGoogleSearchResults(keyList);
	}
	
	public static void main(String[] args) {
	 
		//String keys = "3D+Bioprinting";
		//String pageitem = "10";
		String userid = "divyap";
		ArrayList<Map<String, Object>> keyList = new ArrayList<Map<String, Object>>();
		GoogleSearchParser scrapper = new GoogleSearchParser();
		keyList = scrapper.getSearchCriteria(userid) ;
		scrapper.getGoogleSearchResults(keyList);
	 
	  }
 
}