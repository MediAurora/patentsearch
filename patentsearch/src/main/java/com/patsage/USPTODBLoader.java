/**
 * 
 */
package com.patsage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author dprakash
 *
 */
public class USPTODBLoader {
	
	private String mysqlloadpath = null;
	private String usptofoldername = null;
	private int filelimit = 0;
	private String parentfile = null;
		
	/**
	 * Constructor
	 */
	USPTODBLoader() {
		// TODO Auto-generated constructor stub
		Properties props = new Properties();

		 try {
			 InputStream resourceStream = 
					 Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
			 props.load(resourceStream);
			 this.mysqlloadpath = (String) props.get("mysqlloadpath");
			 this.usptofoldername = (String) props.get("usptofoldername");
			 this.filelimit = Integer.parseInt((String)props.get("filelimit"));
			 this.parentfile = (String) props.get("parentfile");

		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	 }
 
	/*
	 * count the number of records in file
	 */
	public static int countLines(String filename) throws IOException {
	    BufferedInputStream is = new BufferedInputStream (new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}

	
	/*
	 * Method to chunk the parent file into 50,000 records files
	 */
	
	public static List<File> splitFiles(File file, int sizeOfFileInMB) throws IOException {
	
		int counter = 1;
	    List<File> files = new ArrayList<File>();
	    int sizeOfChunk = 1024 * 1024 * sizeOfFileInMB;
	    String eof = System.lineSeparator();
	    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	        String name = file.getName();
	        String line = br.readLine();
	        while (line != null) {
	            File newFile = new File(file.getParent(), name + "."
	                    + String.format("%03d", counter++));
	            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(newFile))) {
	                int fileSize = 0;
	                while (line != null) {
	                    byte[] bytes = (line + eof).getBytes(Charset.defaultCharset());
	                    if (fileSize + bytes.length > sizeOfChunk)
	                        break;
	                    out.write(bytes);
	                    fileSize += bytes.length;
	                    line = br.readLine();
	                }
	            }
	            files.add(newFile);
	        }
	    }
	    return files;
		
	}

	/*
	 * Read and Write method
	 */
	
	static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if(val != -1) {
            bw.write(buf);
        }
	}

	/*
	 * Method to load multiple data files into MySQL table.
	 * returns 1 for success
	 * returns 0 for failure
	 */
	public int loadUSPTOdata() {
		int result = 0;
		int fileCnt = 0;
    	MYSQLConnector mysql = new MYSQLConnector();
    	Connection conn = mysql.getmysqlConn();
    	PreparedStatement preparedStmt = null;
		
		String filePath = this.mysqlloadpath +"\\" + this.usptofoldername;
		File fileFolder = new File(filePath);
		File[] fileList = fileFolder.listFiles();
		if(fileList != null)
			fileCnt = fileList.length;
		
		for(int i=0; i <fileCnt; i++) {
			
		}
		
		
		return result;
	}
	
	/*
	 *  Load single file into database
	 */
	public void loadDataFile(File file, Connection conn) {
		
	}
	
	/*
	 * Master method
	 */
	public void masterLoad() {
		int count = 0;
		List<File> fileList = new ArrayList<File>();
		String parentNameFull = this.mysqlloadpath +"\\" + this.parentfile;
		File parentFile = new File(parentNameFull);
		try {
			//count = countLines(parentNameFull);
			fileList = splitFiles(parentFile, 100);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("file record count ==> " + count);
		System.out.println("file count ==> " + fileList.size());
	}
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		USPTODBLoader dbLoader = new USPTODBLoader();
		dbLoader.masterLoad();
	}

}
