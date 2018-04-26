package patentsearch;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class PDFBoxParser {

     PDFParser parser;
     String parsedText;
     PDFTextStripper pdfStripper;
     PDDocument pdDoc;
     COSDocument cosDoc;
     PDDocumentInformation pdDocInfo;

     // PDFTextParser Constructor 
     public PDFBoxParser() {
     
     }

     // Extract text from PDF Document
     String pdfToText(String fileName) {
     	System.out.println("inside pdfToText method...... ");    	 
    	 
        File f = new File(fileName);

   
        if (!f.isFile()) {
             System.out.println("File " + fileName + " does not exist.");
             return null;
        }
   
        try {
            RandomAccessRead source = new RandomAccessBufferedFileInputStream(f);
            parser = new PDFParser(source);
        } catch (Exception e) {
            System.out.println("Unable to open PDF Parser.--->" +fileName);
            e.printStackTrace();
            return null;
        }
  
        try {
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(3);
            parsedText = pdfStripper.getText(pdDoc);
        } catch (Exception e) {
        	System.out.println("An exception occured in parsing the PDF Document.-->" + fileName);
            e.printStackTrace();
            try {
            	if (cosDoc != null) cosDoc.close();
                if (pdDoc != null) pdDoc.close();
            } catch (Exception e1) {
            	e1.printStackTrace();
            }
            return null;
        }finally {
        	try {
            	if (cosDoc != null) cosDoc.close();
                if (pdDoc != null) pdDoc.close();
            } catch (Exception e2) {
            	e2.printStackTrace();
            }
        }

        System.out.println("PDF text extraction is Done......" + fileName);
         return parsedText;
     }

     // Write the parsed text from PDF to a file
     void writeTextToFile(String pdfText, String fileName) {
    	 
    	 System.out.println("inside writeTextToFile method...... " + fileName);
    	 try {
             PrintWriter pw = new PrintWriter(fileName);
             pw.print(pdfText);
             pw.close();  
         } catch (Exception e) {
             System.out.println("An exception occured in writing the pdf text to file.");
             e.printStackTrace();
         }
         System.out.println("writeTextToFile completed successfully.......");
     }
     
    public int PDFBoxExtractor(String pdfFile, String txtFile) {
    	
    	System.out.println("inside PDFBoxExtractor method...... ");
    	String pdfToText = null;
    	 if(pdfFile != null) {
             pdfToText = this.pdfToText(pdfFile);		 
    	 } else {
    		 System.out.println("Path to PDF file is Null!......");
    		 return 1;
    	 }

         if (pdfToText == null) {
             System.out.println("PDF to Text Conversion failed.");
             return 1;
         } else {
        	 pdfToText = pdfToText.trim();
        	 if(pdfToText.length() == 0) { 
        		 System.out.println("PDF contains image not Text !!");
        		 return 2;
        	 } else {
        		 this.writeTextToFile(pdfToText, txtFile);
        	 }
         }
		return 0;       
     }
    
    /*
     * Extract pdf text by area
     * 
     */
    
    public void extractPDFTextByArea(String filepath, String textPath) {
    	try {
    		BufferedWriter bw = null;
    		FileWriter fw = null;
    		File textFile = new File(textPath);
    		if (!textFile.exists()) {
    			textFile.createNewFile();
			}
    		PDDocument document = PDDocument.load(new File(filepath));
    		PDFTextStripperByArea stripper = new PDFTextStripperByArea();
    		stripper.setSortByPosition( true );
    	    Rectangle rect = new Rectangle( 10, 10, 220, 520 );
    	    stripper.addRegion( "class1", rect );
    	    StringBuffer strbuffer = new StringBuffer();
    	    
    	    for(int i=0; i < 53; i++) {
    	    	PDPage firstPage = document.getPage(i);
    	    	stripper.extractRegions( firstPage );
    	    	//System.out.println( "Text in the area: \n" + rect );
    	    	//System.out.println( stripper.getTextForRegion( "class1" ) );
    	    	System.out.println( "extracting PDF Page # ==>" + i );
    	    	strbuffer.append(stripper.getTextForRegion( "class1" ));
    	    }
    	    String remove = "\\n";
    	    String content = (String)strbuffer.toString();
    	    content = content.replaceAll("\\r|\\n", " ");
    	    //content = content.replaceAll(System.getProperty("line.separator"), " ");
    	    
    	    this.writeTextToFile(content, textPath);

    	} catch (Exception e){
    		System.out.println("error while processing the pdf doc");
    		e.printStackTrace();
    	}
    }

     //Extracts text from a PDF Document and writes it to a text file
     public static void main(String args[]) {
   
         PDFBoxParser pdfTextParserObj = new PDFBoxParser();
         String pdfFile = "C:/DP/patents/ContractTemplate.pdf";
         String txtFile = "C:/DP/patents/text/ContractTemplate.txt";
         pdfTextParserObj.extractPDFTextByArea(pdfFile, txtFile);
         /*
         int result = pdfTextParserObj.PDFBoxExtractor(pdfFile, txtFile);
         
         if (result == 1) {
             System.out.println("PDF to Text Conversion failed.");
         } else {
             System.out.println("PDF to text conversion succeeded....");

         }*/
     }
 }