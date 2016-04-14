/**
 * 
 */
package patentsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
/**
 * @author dprakash
 *
 */
public class NLPAPIController {
	
	private String apiURL;
	private String textSource;
		
	/*
	 * Method to read properties file
	 */
	public String getProperties() {
		Properties props = new Properties();
		String url = null;
		
		try {
			InputStream resourceStream = 
					Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
			props.load(resourceStream);
			url = (String) props.get("url");
			this.apiURL = url;
			this.textSource = (String) props.get("textpath");
			System.out.println("AlchemyAPI is ==>" + url);
			System.out.println("text Path is ==>" + textSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return textSource;
	}
	
	/*
	 * Main method to call AlchemyAPI
	 */
	
	public void connectNLPServices () throws IOException, SAXException,
    ParserConfigurationException, XPathExpressionException {
		
		Document doc = null;
		String textPath = this.getProperties();
		
        // Create an AlchemyAPI object.
        AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromFile("api_key.txt");

        // Extract topic keywords for a web URL.
        //Document urlDoc = alchemyObj.URLGetRankedKeywords("http://www.techcrunch.com/");
        //System.out.println(getStringFromDocument(urlDoc));

        // Extract topic keywords for a text string.
        /*textDoc = alchemyObj.TextGetRankedKeywords(
            "Hello there, my name is Bob Jones.  I live in the United States of America.  " +
            "Where do you live, Fred?");
        System.out.println(getStringFromDocument(doc));
		*/
        File[] files = new File(textSource).listFiles();
    	System.out.println("# of pdf files found = " + files.length);
    	if(files.length == 0) {
    		System.out.println("No files found at ..: " + textSource);
    	}
    	
    	for (File file : files) {
            if (!file.isDirectory()) {
            	String fileName = file.getName();
            	String baseName = FilenameUtils.getBaseName(fileName);
            	System.out.println("File  base name is : " + baseName);
            	System.out.println("File name is : " + fileName);
            	String tmpSrcPath = textSource + "\\" + fileName;
            	//String tmpTxtPath = txtPath + "\\" + baseName + ".txt";
            	String tmpText = getFileContents(tmpSrcPath);
            	Document tmpDoc = alchemyObj.TextGetRankedKeywords(tmpText);
            	System.out.println(getStringFromDocument(tmpDoc));
            	
           } else {
                System.out.println("This is not a file but a directory ....: " + file.getName());
                continue;
            }
        }
        
        // Load a HTML document to analyze.
        //String htmlDoc = getFileContents("testdir/data/example.html");

        // Extract topic keywords for a HTML document.
        //doc = alchemyObj.HTMLGetRankedKeywords(htmlDoc, "http://www.test.com/");
        //System.out.println(getStringFromDocument(doc));

	}

	/*
	 * Utility method to fetch File contents
	 */
    static String getFileContents(String filename)
        throws IOException, FileNotFoundException
    {
        File file = new File(filename);
        StringBuilder contents = new StringBuilder();

        BufferedReader input = new BufferedReader(new FileReader(file));

        try {
            String line = null;

            while ((line = input.readLine()) != null) {
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
        } finally {
            input.close();
        }

        return contents.toString();
    }

    /*
     * Utility method for getting String from Document
     */
    private static String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		NLPAPIController api = new NLPAPIController ();
		
		try {
			api.connectNLPServices();
		} catch (XPathExpressionException | IOException | SAXException
				| ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
