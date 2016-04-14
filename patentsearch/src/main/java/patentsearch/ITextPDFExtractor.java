/**
 * 
 */
package patentsearch;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

/**
 * @author dprakash
 * @param <ITextExtractionStrategy>
 *
 */
public class ITextPDFExtractor<ITextExtractionStrategy> {

	class FontRenderFilter extends RenderFilter {
        public boolean allowText(TextRenderInfo renderInfo) {
            String font = renderInfo.getFont().getPostscriptFontName();
            return font.endsWith("Bold") || font.endsWith("Oblique");
        }
    }
	
	/*
	 * Method to read properties file
	 */
	public String getFilePath() {
		
		Properties props = new Properties();
		String testPath = null;
		try {
			InputStream resourceStream = 
					   Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
			props.load(resourceStream);
			   
			testPath = (String) props.get("testpath");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return testPath;
	}
	
	/**
	 * Method to parse patent pdf for information
	 */
	
	public Map getPatentInfo(String fileName) {
		System.out.println("======inside getPatentInfo() ===============");
		String pubOrg = null;
		String pubNo = null;
		String pubDate = null;
		String title = null;
		String abs = null;
		String inventors = null;
		PdfReader reader;
		Map patInfo = new HashMap<String, String>();
		try {
			patInfo.put("filename", fileName);
			reader = new PdfReader(fileName); 
			System.out.println("pdf file length ==>" + reader.getFileLength());
			int fileContentSize = PdfTextExtractor.getTextFromPage(reader, 1).trim().length();
			System.out.println("pdf file content ==>" + fileContentSize);
			if(fileContentSize == 0) {
				System.err.println("pdf file is image based. Call OCR for text extraction .....");
				return null;
			} else {
				// location for Publication Organization
				Rectangle rectPubOrg = new Rectangle(30, 700, 330, 740);
		        RenderFilter pubOrgFilter = new RegionTextRenderFilter(rectPubOrg);
		        TextExtractionStrategy pubOrgStrategy 
		        			= new FilteredTextRenderListener(new LocationTextExtractionStrategy(), pubOrgFilter);
				
		        pubOrg = PdfTextExtractor.getTextFromPage(reader, 1, pubOrgStrategy);
		        patInfo.put("puborg", pubOrg);
		        System.out.println("Publication Org =>" + pubOrg);
				
				// location for Publication Number
				Rectangle rect = new Rectangle(380, 700, 600, 740);
		        RenderFilter regionFilter = new RegionTextRenderFilter(rect);
		        TextExtractionStrategy strategy 
		        			= new FilteredTextRenderListener(new LocationTextExtractionStrategy(), regionFilter);
				
		        pubNo = PdfTextExtractor.getTextFromPage(reader, 1, strategy);
		        patInfo.put("pubno", pubNo);
		        System.out.println("Publication No =>" + pubNo);
		        
		        // Location for Publication Date
				Rectangle rectPubDate = new Rectangle(380, 670, 600, 700);
		        RenderFilter pubDateFilter = new RegionTextRenderFilter(rectPubDate);
		        TextExtractionStrategy pubDateStrategy
		        			= new FilteredTextRenderListener(new LocationTextExtractionStrategy(), pubDateFilter);
	
		        pubDate = PdfTextExtractor.getTextFromPage(reader, 1, pubDateStrategy);
		        patInfo.put("pubdate", pubDate);
		        System.out.println("Publication Date ==>" + pubDate);
	
		        // Location for Publication Title
		     	Rectangle rectTitle = new Rectangle(30, 630, 300, 670);
		     	RenderFilter titleFilter = new RegionTextRenderFilter(rectTitle);
		     	TextExtractionStrategy titleStrategy
		     	      			= new FilteredTextRenderListener(new LocationTextExtractionStrategy(), titleFilter);
	
		     	title = PdfTextExtractor.getTextFromPage(reader, 1, titleStrategy);
		     	patInfo.put("pubtitle", title);
		     	System.out.println("Publication title ==>" + title);
		        
		        // Location for Inventor
				Rectangle rectInventor = new Rectangle(30, 590, 300, 630);
		        RenderFilter inventorFilter = new RegionTextRenderFilter(rectInventor);
		        TextExtractionStrategy inventorStrategy
		        			= new FilteredTextRenderListener(new LocationTextExtractionStrategy(), inventorFilter);
	
		        inventors = PdfTextExtractor.getTextFromPage(reader, 1, inventorStrategy);
		        patInfo.put("inventors", inventors);
		        System.out.println("Inventors ==>" + inventors);
		        
		        // Location for Abstracts
				Rectangle rectAbs = new Rectangle(340, 470, 600, 550);
		        RenderFilter absFilter = new RegionTextRenderFilter(rectAbs);
		        TextExtractionStrategy absStrategy
		        			= new FilteredTextRenderListener(new LocationTextExtractionStrategy(), absFilter);
	
		        abs = PdfTextExtractor.getTextFromPage(reader, 1, absStrategy);
		        patInfo.put("abstract", abs);
		        System.out.println("Abstract ==>" + abs);
		        
		        // store current datetime
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				patInfo.put("datetime", date.toString());
			}
		    reader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Content in PatInfo Map ofject ==>\n" + patInfo.toString());
		return patInfo;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ITextPDFExtractor iText = new ITextPDFExtractor();
		String testPath = iText.getFilePath();
		
		// pdf with text based
		Map PubInfo = iText.getPatentInfo(testPath+ "\\US20100129912.pdf");
		
		// pdf with image based
		//Map PubInfo1 = iText.getPatentInfo(testPath+ "\\US4829000(A).pdf");
		
	}

}
