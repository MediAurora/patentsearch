package patentsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.sql.PreparedStatement;

/**
 * @author divyaprakash
 * This controller class will check for PDF for text content and image.
 * It will parse the text and image pdfs into Text file
 * It will also make a call to Alchemy API to generate Keywords extraction and stores in MySQL
 */
public class PDFExtractController {

   
   public PDFExtractController() {
    	// constructor
   }
   
   /*
    * Method to read properties file
    */
   public Map<String, String> getFilesPath() {
	   Properties props = new Properties();
	   String pdfPath = null;
	   String imagePath = null;
	   String textPath = null;
	   String ocrPath = null;
	   Map<String, String> paths = new HashMap<String, String>();
	   try {
		   InputStream resourceStream = 
				   Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
		   props.load(resourceStream);
		   
		   pdfPath = (String) props.get("pdfpath");
		   paths.put("pdfpath", pdfPath);
		   imagePath = (String) props.get("imagepath");
		   paths.put("imagepath", imagePath);
		   textPath = (String) props.get("textpath");
		   paths.put("textpath", textPath);
		   ocrPath = (String) props.get("ocrpath");
		   paths.put("ocrpath", ocrPath);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   return paths;
	}
   
  
    /*
     * This method does step by step processing.
     * Step 1 : extract text from PDF using PDFBOX Parser. This will parse text based pdfs.
     * Step 2 : If step 1 yield no result, it will call PDF-Image Extractor.
     * Step 3 : Calls TesseractOCR to extract text from image.
     * it will call OCR method to extract text from Image. 
     */
    public int pdfExtractor(String pdfPath, String textPath, String imgPath, String OCRPath) {
    	
    	System.out.println("========inside pdfExtractor () ========== ");
    	System.out.println("starting parsing for pdf===>" + pdfPath);
    	
    	ITextPDFExtractor<?> iText = new ITextPDFExtractor<Object>();
    	Map<String, String> patInfoText = iText.getPatentInfo(pdfPath);

    	if (patInfoText == null) {
    		System.out.println("The source PDF has Image, extracting image ...... ");
    		// Call Image extractor
    		PDFImageExtractor imgExtract = new PDFImageExtractor();
    		try {
    			//imgResult ==0; image extraction is successful
    			//imgResult ==1; image extraction failed.
				String imgResult = imgExtract.getPDFImage(pdfPath, imgPath);
				if(imgResult == null) {
					System.err.println("Image to Text OCR conversion failed ....");
					return 1;
				} else{
					//Step 3 : call tesseractOCR
					TesseractOCR tess = new TesseractOCR ();
					String tmpImg = imgPath + "\\" + imgResult + "_" + "1.tiff";
					
					String tmpTxt = textPath + "\\" + imgResult +  "_1";
					System.out.println("\n OCR path for processing ==>" + OCRPath);
					System.out.println("\n Image path for processing ==>" + tmpImg);
					System.out.println("\n Text path for processing ==>" + tmpTxt);
					int tessOutput = tess.runTessOCRFromWindows(OCRPath, tmpImg, tmpTxt);
					if(tessOutput == 1) {
						System.err.println("Image to Text OCR conversion failed ....");
						return tessOutput;
					}else {
						System.out.println("Image to Text OCR is successfull..!!..");
						// Extracting Patent Info from Text now...
						PatentParser parser = new PatentParser();
						patInfoText =  parser.parsePatent(tmpTxt+".txt");
						System.out.println("PDF Image after parsing ========>\n" + patInfoText.toString());
						// code to store the patent info
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 1;
			}
    	}else {
    		System.out.println("the Pdf text is parsed successfully=======>\n" + patInfoText.toString());
     	}// end else
    	
       	//instantiate MySQL connection
    	MYSQLConnector mysql = new MYSQLConnector();
    	Connection conn = mysql.getmysqlConn();
    	PreparedStatement preparedStmt = null;
    	try {
    		String sql = 
    		   	"INSERT INTO patentsearch.patentinfo " + 
    		   	"(PatentNumber, PatentOrg, Inventors, PatentTitle, PublishDate, PatentAbstract, BatchDateTime, PatentFileName)" +
    		    "VALUES (?, ?, ?, ?, ?,?, ?, ?)";
    		    
    		// create the mysql insert preparedstatement
    		preparedStmt = conn.prepareStatement(sql);
    		preparedStmt.setString (1, (String)patInfoText.get("pubno"));
    		preparedStmt.setString (2, (String)patInfoText.get("puborg"));
    		preparedStmt.setString (3, (String)patInfoText.get("inventors"));
    		preparedStmt.setString (4, (String)patInfoText.get("pubtitle"));
    		preparedStmt.setString (5, (String)patInfoText.get("pubdate"));
    		preparedStmt.setString (6, (String)patInfoText.get("abstract"));
    		preparedStmt.setString (7, (String)patInfoText.get("datetime"));
    		preparedStmt.setString (8, (String)patInfoText.get("filename"));
    		
    		System.out.println("Publication Number ==>" + (String)patInfoText.get("pubno"));
    		System.out.println("Publication Date   ==>" + (String)patInfoText.get("pubdate"));
    		// execute the preparedstatement
    		preparedStmt.execute();
    		
    	}catch (SQLException ex){
    	    // handle any errors
    	    System.err.println("SQLException: " + ex.getMessage());
    	    System.err.println("SQLState: " + ex.getSQLState());
    	    System.err.println("VendorError: " + ex.getErrorCode());
    	}finally {
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

    	// process is successful !
		return 0;    	
    } //end pdfExtractor()
    
    /*
     * This is master method to control the workflow
     *  This method loops through files in source PDF folder and convert into text file 
     */
    public int extractTxtManager() {
    	System.out.println("===============inside extractTxtManager () ================");
    	int output = 0;
    	//read source pdf files and destination location from properties.
    	Map<String, String> filePaths = getFilesPath();
    	String srcPath = (String) filePaths.get("pdfpath");
    	String imgPath = (String) filePaths.get("imagepath");
    	String txtPath = (String) filePaths.get("textpath");
    	String ocrPath = (String) filePaths.get("ocrpath");
    	//get the file counts, for every file, call the PDFTextExtractor
    	File[] files = new File(srcPath).listFiles();
    	System.out.println("# of pdf files found = " + files.length);
    	if(files.length == 0) {
    		System.out.println("No files found at ..: " + srcPath);
    		return 1;
    	}
    	
    	for (File file : files) {
            if (!file.isDirectory()) {
            	String fileName = file.getName();
            	String baseName = FilenameUtils.getBaseName(fileName);
            	System.out.println("File  base name is : " + baseName);
            	String tmpSrcPath = srcPath + "\\" + fileName;
            	//String tmpTxtPath = txtPath + "\\" + baseName + ".txt";
            	int result = this.pdfExtractor(tmpSrcPath, txtPath, imgPath, ocrPath);
            	if(result == 1) {
            		System.err.println("PDF File parser failed to extract text! ...");
            	} else {
            		System.out.println(" PDF extraction is successful !");
            	}
           } else {
                System.out.println("This is not a file but a directory ....: " + file.getName());
                continue;
            }
        }
    	
		return output;
    }
      
    public static void main(String[] args) {
        
		PDFExtractController pdfExt = new PDFExtractController();
		
		// Call extractManager method
		pdfExt.extractTxtManager();
	}
   
}
