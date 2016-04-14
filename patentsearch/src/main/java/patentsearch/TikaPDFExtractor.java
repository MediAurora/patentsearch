package patentsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaPDFExtractor {
	
    // TikaPDFExtractor Constructor 
    public TikaPDFExtractor() {
    
    }
    
    public void TikaPDFParser (String pdfFile, String txtFile) {
    	
    }

   public static void main(final String[] args) throws IOException,TikaException {

	   FileInputStream inputstream = null;
	   try {
		   ContentHandler handler = new BodyContentHandler();
		   Metadata metadata = new Metadata();
		   inputstream = new FileInputStream(new File("C:/DP/patents/Extractor.pdf"));
		   ParseContext pcontext = new ParseContext();
      
		   //parsing the document using PDF parser
		   PDFParser pdfparser = new PDFParser(); 
		   pdfparser.parse(inputstream, handler, metadata, pcontext);
		   
		   //getting the content of the document
		   System.out.println("Contents of the PDF :" + handler.toString());
		   
		   //getting metadata of the document
		   System.out.println("Metadata of the PDF:");
		   String[] metadataNames = metadata.names();
		   for(String name : metadataNames) {
		         System.out.println(name+ " : " + metadata.get(name));
		      }
		   
	   } catch (SAXException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   } finally {
		   if (inputstream != null) inputstream.close();
	   }
      

   }
}
