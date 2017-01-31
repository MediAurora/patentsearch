package patentsearch;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.node.ObjectNode;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.naming.*;
import javax.naming.directory.*;

/**
 * @author Divya Prakash
 * 
 */

@Path("/patent")
public class PatentAPI {

	/*
	 * API to call patent search results
	 */
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/getResult")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPatentResult(@QueryParam("batchdate") String batchdate) {
		System.out.println("<============getPatentResult API ==========>");
		System.out.println("batchdate ===>" + batchdate);
		String jsonString = null;
		Connection conn = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		Map<Integer,Object> resultJSON = new HashMap<Integer, Object>();

		//Create DBConnection
		MYSQLConnector myDB = new MYSQLConnector();		
		ObjectMapper mapper = new ObjectMapper();

		//String sql = "{ call sjhsSSRS.dbo.SJHSApp_Patient_Census_ministry(?) }";
		String sql = "SELECT * FROM patentsearch.patentinfo where BatchDateTime > ? ";
		System.out.println("Prepated sql call ===>" + sql);
		try {
			conn = myDB.getmysqlConn();
			cs = conn.prepareCall(sql);
			cs.setString(1, batchdate);
			rs = cs.executeQuery();
			while (rs.next()) {
				Map<String, String> tempMap = new HashMap<String, String>();
				Integer id = new Integer(rs.getInt("PatentInfoId"));
				String patNum = rs.getString("PatentNumber");
				tempMap.put("patnum", patNum);
				String patOrg = rs.getString("PatentOrg");
				tempMap.put("patorg", patOrg);
				String inventors = rs.getString("Inventors");
				tempMap.put("inventors", inventors);
				String patTitle = rs.getString("PatentTitle");
				tempMap.put("pattitle", patTitle);
				String pubDate = rs.getString("PublishDate");
				tempMap.put("patdate", pubDate);
				String abs = rs.getString("PatentAbstract");
				tempMap.put("patabs", abs);
				String date = rs.getString("BatchDateTime");
				tempMap.put("batchdate", date);
				String link = rs.getString("PatentFileName");
				tempMap.put("link", link);

				resultJSON.put(id, tempMap);
			}
			jsonString = mapper.writeValueAsString(resultJSON);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs!=null) {
					rs.close();
				}else { 
					resultJSON = new HashMap<Integer, Object>();
					resultJSON.put(1, "ERROR: ResultSet is null!");
					jsonString = mapper.writeValueAsString(resultJSON);
					return jsonString;
				}
				if(cs!= null) {
					cs.close();
				}else { 
					resultJSON = new HashMap<Integer, Object>();
					resultJSON.put(2, "ERROR: CallableStatement is null!");
					jsonString = mapper.writeValueAsString(resultJSON);
					return jsonString;
				}
				if(conn!= null) {
			        try {
			            conn.close();
			        } catch (SQLException sqlEx) { } // ignore
			        conn = null;
				}else {
					resultJSON = new HashMap<Integer, Object>();
					resultJSON.put(3, "ERROR: Connection Object is null!");
					jsonString = mapper.writeValueAsString(resultJSON);
					return jsonString;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonGenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("JSON String Result ==>" + jsonString);

		return jsonString;
	}

	
	public static void main(String[] args) {
		
		PatentAPI res = new PatentAPI();
		String result = res.getPatentResult("'" + "2016-04-09" + "'");
		System.out.println("user authenticated and Authorized ? ==>" + result);
	}
	
}

