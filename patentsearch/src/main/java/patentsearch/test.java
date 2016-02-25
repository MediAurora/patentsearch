/**
 * 
 */
package patentsearch;

import java.io.IOException;
import java.io.InputStream;
import java.text.Format.Field;
import java.util.Properties;

import org.apache.lucene.document.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.searchengine.lucene.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;

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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		test t = new test();
		t.pdfParser();
	}

}
