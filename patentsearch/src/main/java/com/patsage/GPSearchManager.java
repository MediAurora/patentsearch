package com.patsage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class GPSearchManager {
	
	public String formGPSearchURL(GPSearchObject obj, String key) {
		System.out.println("<============ inside formGPSearchURL()==============>");
		String url = "";
		
		if(obj == null) {
			url = null;
			return url;
		}
		String baseUrl = "https://patents.google.com/?q=";
		if(key != null) {
			url = baseUrl + key;
			System.out.println("url ==>" + url);
		} else {
			url = baseUrl;
		}
		if(obj.getBefore_field() !=null && obj.getBefore_val() != null){
			url = url + "&before=" + obj.getBefore_field() + ":" + obj.getBefore_val();
			System.out.println("url ==>" + url);
		}
		if(obj.getAssignee() != null) {
			url = url + "&assignee=" + obj.getAssignee();
			System.out.println("url ==>" + url);
		}
		if(obj.getAfter_field() !=null && obj.getAfter_val() != null){
			url = url + "&after=" + obj.getAfter_field() + ":" + obj.getAfter_val();
			System.out.println("url ==>" + url);
		}
		if(obj.getInventor() != null) {
			url = url + "&inventor=" + obj.getInventor();
			System.out.println("url ==>" + url);
		}
		if(obj.getCpc() != null) {
			url = baseUrl + "&cpc=" + obj.getCpc();
			System.out.println("url ==>" + url);
		}
		if(obj.getLinkNum() != 0) {
			url = url + "&num=" + String.valueOf(obj.getLinkNum());
			System.out.println("url ==>" + url);
		}
		System.out.println("url ==>" + url);
		return url + "";
	}
	
	/*
	 * method to save search. 
	 * result = 0 ; error
	 * result = 1 ; success
	 */
	public int saveSearch(GPSearchObject searchObj) {
		// TODO Auto-generated method stub
		System.out.println("<============ inside saveSearch()==============>");
		int result = 0;
		
    	MYSQLConnector mysql = new MYSQLConnector();
    	Connection conn = mysql.getmysqlConn();
    	PreparedStatement preparedStmt = null;
		if(searchObj == null) {
			result = 0;
			return result;
		}
		try {
	   		String sql = "INSERT INTO patentsearch.gp_searchkeyword " + 
							"(user_id, project_id, keywords, before_field, before_val, after_field, after_val, "
							+ "assignee, inventor, cpc, patent_office, language, filing_status, patent_type, "
							+ "citing_patent, active, linknum, search_url)" +
							"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	   		// create the mysql insert prepared statement
	   		preparedStmt = conn.prepareStatement(sql);
	   		
	   		String keywords = "";
	   		
			if(searchObj.getKeywords() != null) {
				ArrayList<String> key = searchObj.getKeywords();
				int size = key.size();
				for (int i=0; i < size; i++) {
					if(key.get(i) != null || key.get(i) != "") {
						keywords = keywords + "&q=" + key.get(i);
					}
				}
			}
			System.out.println("keywords======>" + keywords);
			
			String search_url = formGPSearchURL(searchObj, keywords);
			
			
			preparedStmt.setInt (1, searchObj.getUser_id());
			preparedStmt.setInt (2, searchObj.getProject_id());
			preparedStmt.setString (3, keywords);
			preparedStmt.setString (4, searchObj.getBefore_field());
			preparedStmt.setString (5, searchObj.getBefore_val());
			preparedStmt.setString (6, searchObj.getAfter_field());
			preparedStmt.setString (7, searchObj.getAfter_val());
			preparedStmt.setString (8, searchObj.getAssignee());
			preparedStmt.setString (9, searchObj.getInventor());
			preparedStmt.setString (10, searchObj.getCpc());
			preparedStmt.setString (11, searchObj.getPatent_office());
			preparedStmt.setString (12, searchObj.getLanguage());
			preparedStmt.setString (13, searchObj.getFiling_status());
			preparedStmt.setString (14, searchObj.getPatent_type());
			preparedStmt.setString (15, searchObj.getCiting_patent());
			preparedStmt.setInt (16, searchObj.getActive());
			preparedStmt.setInt (17, searchObj.getLinkNum());
			preparedStmt.setString (18, search_url);

			            
	  		// execute the preparedstatement
	  		preparedStmt.execute();
		} catch (SQLException ex) {
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
		
		return 1;
	}	
	
	/*
	 * method to fetch Search criterias for each module under a project
	 */
	public ArrayList<Object> getSearchCriteria() {

		System.out.println("<============ inside getSearchCriteria()==============>");
		ArrayList<Object> result = new  ArrayList<Object>();
		
    	MYSQLConnector mysql = new MYSQLConnector();
    	Connection conn = mysql.getmysqlConn();
    	Statement stmt = null;
    	ResultSet rs = null;
		try {
	   		String sql = "SELECT criteria_id, search_url FROM patentsearch.gp_searchkeyword where active = 1";
	   		
	   		// create mysql statement
	   		stmt = conn.createStatement();
	  		// execute the statement
	  		//stmt.executeQuery(sql);
	  	    // or alternatively, if you don't know ahead of time that
	  	    // the query will be a SELECT...

	  	    if (stmt.execute(sql)) {
	  	        rs = stmt.getResultSet();
	  	    }
	  	    if(rs != null) {
	  	    	while(rs.next()) {
	  	    		int searchid = rs.getInt("criteria_id");
	  	    		String url = rs.getString("search_url");
	  	    		System.out.println("search url =>\n" + url);
	  	    		HashMap<String, Object> tempHash = new HashMap<String, Object>();
	  	    		tempHash.put("searchid", searchid);
	  	    		tempHash.put("url", url);
	  	    		result.add(tempHash);
	  	    	}
	  	    }
		} catch (SQLException ex) {
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
    		if (conn != null) {
    			try {
    				conn.close();
    		    } catch (SQLException sqlEx) { } // ignore
    		        conn = null;
    		}
    	}
		return result;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// insert Search keywords
		GPSearchManager keywords = new GPSearchManager();
		GPSearchObject obj = new GPSearchObject();
		// save search
		// Catheter,ablation AND Assignee=Boston And filingDate after 2012-01-01
		obj.setBefore_field("filing");
		obj.setBefore_val("2012-01-01");
		obj.setAssignee("Boston");
		ArrayList<String> key = new ArrayList<String>(); 
		String key1 = "Catheter,ablation";
		key.add(key1);
		String key2 = "injectible";
		key.add(key2);
		obj.setKeywords(key);
		obj.setProject_id(1);
		obj.setUser_id(1);
		obj.setActive(1);
		obj.setLinkNum(10);
		//keywords.saveSearch(obj);
		ArrayList result = keywords.getSearchCriteria();
	}
}
