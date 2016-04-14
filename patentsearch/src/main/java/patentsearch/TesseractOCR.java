package patentsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


public class TesseractOCR {
	/*
	static {
	    try {
	    	System.load("C:/Softwares/Tesseract-ocr/Tess4J-3.0-src/Tess4J/lib/win32-x86-64/liblept172.dll");
	    	System.load("C:/Softwares/Tesseract-ocr/Tess4J-3.0-src/Tess4J/lib/win32-x86-64/gsdll64.dll");
	    	System.load("C:/Softwares/Tesseract-ocr/Tess4J-3.0-src/Tess4J/lib/win32-x86-64/libtesseract304.dll");
	    } catch (UnsatisfiedLinkError e) {
	      System.err.println("Native code library failed to load.\n" + e);
	      System.exit(1);
	    }
	  }
	*/
	public int runTessOCRFromWindows(String OCRpath, String imgPath, String txtPath) {
		
		System.out.println(" Inside runTessOCRFromWindows method ...... ");
		
		try {
			Process process = new ProcessBuilder(OCRpath, imgPath, txtPath).start();
			try {
				int result = process.waitFor();
				System.out.println("Tess OCR Process completed...=>" + result);
				return result;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public void tessOCR() {
		
		File imageFile = new File("C:/DP/patents/image/9183764.tiff");
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        instance.setDatapath("tessdata/eng.traineddata");
        instance.setLanguage("eng");
        System.out.println("Does input file exist? =>" +imageFile.exists());
        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }

	}
	
	public static void main(String[] args) {
    
		TesseractOCR exp = new TesseractOCR();
		//exp.tessOCR();
		String ocrPath = "C:\\Program Files (x86)\\Tesseract-OCR\\tesseract.exe";
		String param1 = "C:\\DP\\patents\\image\\9183764_1.tiff";
		String param2 = "C:\\DP\\patents\\text\\9183764.text";
		int result = exp.runTessOCRFromWindows(ocrPath, param1, param2);
		System.out.println("TessractOCR result =>" + result);
	}

}
