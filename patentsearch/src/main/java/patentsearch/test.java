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
public class test {

	/**
	 * 
	 */
	public test() {
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

	
	
	public void pdfParser() {
		
		PDFManager pdfManager = new PDFManager();
		pdfManager.setFilePath(filePath);
	       try {
			System.out.println(pdfManager.ToText());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		//Convert PDF into Text
		String contents = "";
		PDDocument doc = null;
		try {
			
		    doc = PDDocument.load("");
		    PDFTextStripper stripper = new PDFTextStripper();

		    stripper.setLineSeparator("\n");
		    stripper.setStartPage(1);
		    stripper.setEndPage(2);// this mean that it will index the first 5 pages only
		    contents = stripper.getText(doc);
		    System.out.println(contents);

		} catch(Exception e){
		    e.printStackTrace();
		}
		/*
	   //index the pdf using lucene document
		luceneDoc.add(new Field(CONTENT_FIELD, allContents.toString(), Field.Store.NO, Field.Index.TOKENIZED));
		Document luceneDocument = LucenePDFDocument.getDocument(new File(path));
		org.apache.lucene.document.Field contents = luceneDocument.getField("contents");
		System.out.println(contents.stringValue());
		
		PDDocument doc = PDDocument.load(path);
		PDFTextStripper stripper = new PDFTextStripper();
		String text = stripper.getText(doc);
		System.out.println(text);
		doc.close();
		*/
	}
	
	public void getPDFImage()
				throws Exception {

		try {
			String sourceDir = "C:/DP/patents/9183764.pdf";
		    String destinationDir = "C:/DP/patents/";
		    File oldFile = new File(sourceDir);
		    if (oldFile.exists()){
		    	PDDocument document = PDDocument.load(sourceDir);
		        List<PDPage> list =   document.getDocumentCatalog().getAllPages();
		        String fileName = oldFile.getName().replace(".pdf", "_cover");
		        int totalImages = 1;
		        for (PDPage page : list) {
		        	PDResources pdResources = page.getResources();
		            Map pageImages = pdResources.getXObjects();
		            if (pageImages != null){
		            	Iterator imageIter = pageImages.keySet().iterator();
		                while (imageIter.hasNext()){
		                	String key = (String) imageIter.next();
		                    Object obj = pageImages.get(key);

		                    if(obj instanceof PDXObjectImage) {
		                    	PDXObjectImage pdxObjectImage = (PDXObjectImage) obj;
		                    	pdxObjectImage.write2file(destinationDir + fileName+ "_" + totalImages);
		                    	totalImages++;
		                    }
		                }
		            }
		        }
		    }  else {
		    	System.err.println("File not exist");
		    }  
		}
		catch (Exception e){
		    System.err.println(e.getMessage());
		}
	}
	
	//Tesseract call using Tess4J
	
	public void callTess4J() {
        File imageFile = new File("eurotext.tif");
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
		
		test t = new test();
		try {
			t.getPDFImage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
