/**
 * 
 */
package patentsearch;

import java.io.IOException;
import java.io.InputStream;
import java.text.Format.Field;
import java.util.Properties;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.apache.lucene.document.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.searchengine.lucene.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.pdfbox.PDFToImage;


/**
 * @author divyaprakash
 *
 */
public class PDFImageExtractor {

	/**
	 * 
	 */
	public PDFImageExtractor() {
		// TODO Auto-generated constructor stub
	}
	
	String key = null;
	String filePath = null;
	
	public void getProperties() {
		Properties props = new Properties();
		try(InputStream resourceStream = 
				Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
		    props.load(resourceStream);
		    key = (String) props.get("key");
		    filePath = (String)props.get("filePath");
		    System.out.println(key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getPDFImage(String sourceDir, String destinationDir)
				throws Exception {
		System.out.println("inside getPDFImage method...... ");
		String fileName = null;
		try {
			System.out.println("PDF file to be extracted ==>...... " + sourceDir);
		    File oldFile = new File(sourceDir);
		    if (oldFile.exists()){
		    	PDDocument document = PDDocument.load(sourceDir);
		    	//Extract only page 1 & Page 2 of PDF image.
		        List<PDPage> list =   document.getDocumentCatalog().getAllPages();
		        fileName = oldFile.getName().replace(".pdf", "");
		        int totalImages = 1;
		        int pageCount = 1;
		        destinationDir = destinationDir + "\\" + fileName;
		        for (PDPage page : list) {
		        	PDResources pdResources = page.getResources();
		            Map pageImages = pdResources.getXObjects();
		            if (pageImages != null){
		            	System.out.println("Pdf page has image...... ");
		            	Iterator imageIter = pageImages.keySet().iterator();
		                while (imageIter.hasNext()){
		                	String key = (String) imageIter.next();
		                    Object obj = pageImages.get(key);

		                    if(obj instanceof PDXObjectImage) {
		                    	PDXObjectImage pdxObjectImage = (PDXObjectImage) obj;
		                    	String destination = destinationDir + "_" + totalImages;
		                    	System.out.println("destination for PDFImage.==>" + destination);
		                    	pdxObjectImage.write2file(destination);
		                    	System.out.println("Pdf image written to image file...... ");
		                    	totalImages++;
		                    }
		                }
		            }else {
				    	System.err.println("Source PDF File does not have any image! ...");
				    	return null;
		            }
		            if(pageCount != 3) {
		            	pageCount++;
		            } else {
		            	break;
		            }
		            
		        }
		    }  else {
		    	System.err.println("Source PDF File does not exist! ...");
		    	return null;
		    }  
		} catch (Exception e){
		    System.err.println(e.getMessage());
		}
		return fileName;
	}
	
	//Tesseract call using Tess4J
	
	public void callTess4J() {
        File imageFile = new File("C:/DP/patents/image/9183764.tif");
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping

        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		PDFImageExtractor t = new PDFImageExtractor();
		try {
			String sourceDir = "C:/DP/patents/9183764.pdf";
		    String destinationDir = "C:/DP/patents/image";
		    
		    //t.callTess4J();
		  
		    t.getPDFImage(sourceDir,destinationDir);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
