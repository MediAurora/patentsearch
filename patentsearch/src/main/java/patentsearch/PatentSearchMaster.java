/**
 * 
 */
package patentsearch;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

/**
 * @author dprakash
 *
 */
public class PatentSearchMaster {
	
	/*
	 * This is master Controller method to carry out backend processing
	 */
	public void masterControl () {
		
		//Step 1: call PDFExtractController to extract PDF into text file
		PDFExtractController extractPDF = new PDFExtractController();
		extractPDF.extractTxtManager();
		
		//Step 2: parse text files and store patent info
		PatentParser parser = new PatentParser ();
		parser.parsePatentBatch();
		
		//Step 3: Call NLP API to gather keywords for all text files in daily batch
		/*NLPAPIController api = new NLPAPIController ();
		
		try {
			api.connectNLPServices();
		} catch (XPathExpressionException | IOException | SAXException
				| ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PatentSearchMaster patSearch = new PatentSearchMaster();
		patSearch.masterControl();
	}

}
