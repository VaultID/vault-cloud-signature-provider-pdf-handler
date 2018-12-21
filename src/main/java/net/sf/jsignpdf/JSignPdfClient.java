package net.sf.jsignpdf;

import java.io.File;
import java.io.IOException;

public class JSignPdfClient {

    public static void main(String[] args) throws IOException {

        //File file = new File("/home/paulo/Área de Trabalho/pdfexemplo.pdf");
        //System.out.println("exists =" + file.exists() + " can read= " + file.canRead() + " isfile=" + file.isFile());
        

        BasicSignerOptions options = new BasicSignerOptions();
        
        options.setInFile("/home/paulo/Área de Trabalho/pdfexemplo.pdf");
        
        //Append document
        options.setAppend(true);
        
        //Set visible signature
        options.setVisible(true);
        options.setBgImgPath("/home/paulo/Área de Trabalho/signature.png");
        options.setPage(0);
        
        options.setPositionLLX(355); //X 
        options.setPositionLLY(782); //Y
        options.setPositionURX(230); //Width
        options.setPositionURY(50); //Height
        options.setL2Text("");
        
        //Set pdf
        options.setReason("Set reason");
		options.setLocation("Set location");
		options.setContact("Set Contact");

        final PreparePdfToSignLogic signer = new PreparePdfToSignLogic(options);
        
        signer.setSignerName("PAULO FILIPE MACEDO DOS SANTOS:04660457192");
        signer.setSubfilter("adbe.pkcs7.detached");
        signer.setAutofixDocument(true); //Enable update document before sign (if needed)
        
        signer.signFile();
        
    }
}
