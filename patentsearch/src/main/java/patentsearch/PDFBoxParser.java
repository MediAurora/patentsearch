package patentsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

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
             parser = new PDFParser(new FileInputStream(f));
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

     //Extracts text from a PDF Document and writes it to a text file
     public static void main(String args[]) {
   
         PDFBoxParser pdfTextParserObj = new PDFBoxParser();
         String pdfFile = "C:/DP/patents/Extractor.pdf";
         String txtFile = "C:/DP/patents/text/Extractor.txt";
         
         int result = pdfTextParserObj.PDFBoxExtractor(pdfFile, txtFile);
  
         if (result == 1) {
             System.out.println("PDF to Text Conversion failed.");
         } else {
             System.out.println("PDF to text conversion succeeded....");

         }
     }
 }