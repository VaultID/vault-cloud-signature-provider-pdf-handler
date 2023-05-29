package com.itextpdf.text.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import static net.sf.jsignpdf.Constants.RES;
import net.sf.jsignpdf.SecundarySignerOptions;

/**
 *
 */
public class PdfSignatureAppearanceSecundary {
    
    private final static Logger LOGGER = Logger.getLogger(PdfSignatureAppearanceSecundary.class.getName());

    private PdfStamperImp writer;
    private String name;
    private PdfIndirectReference refSig;
    private PdfSigLockDictionary fieldLock;
    private PdfTemplate appearance;

    private ArrayList<SecundarySignerOptions> secundarySignatures = new ArrayList<SecundarySignerOptions>();

    void setWriter(PdfStamperImp writer) {
        this.writer = writer;
    }

    void setName(String string) {
        this.name = name;
    }

    void setRefSign(PdfIndirectReference refSig) {
        this.refSig = refSig;
    }

    void setFieldLock(PdfSigLockDictionary fieldLock) {
        this.fieldLock = fieldLock;
    }

    public void setSecundarySignatureOptions(ArrayList<SecundarySignerOptions> secundarySignatures) {
        LOGGER.info("PdfSignatureAppearanceSecundary - setSecundarySignatureOptions");
        this.secundarySignatures = secundarySignatures;
    }

    public void setAppearance(PdfTemplate appearance) {
        LOGGER.info("PdfSignatureAppearanceSecundary - setSecundarySignatureOptions");
        this.appearance = appearance;
    }

    public void run() throws IOException, DocumentException {
        
        LOGGER.info("PdfSignatureAppearanceSecundary - run");
        
        int i = 1;
        
        for (SecundarySignerOptions option : this.secundarySignatures) {
            
            LOGGER.info("PdfSignatureAppearanceSecundary - option");
            
            Rectangle rect = new Rectangle(option.getPositionLLX(), option.getPositionLLY(), option.getPositionURX(), option.getPositionURY());
            PdfFormField sigFieldRepeat = PdfFormField.createSignature(writer);
            i += 1;
            sigFieldRepeat.setFieldName(name + Float.toString(option.getPositionLLX()) + Float.toString(option.getPositionURX()) + Integer.toString(option.getPage()) + Integer.toString(i));
            sigFieldRepeat.put(PdfName.V, refSig);
            sigFieldRepeat.setFlags(PdfAnnotation.FLAGS_PRINT | PdfAnnotation.FLAGS_LOCKED);
            if (this.fieldLock != null) {
                sigFieldRepeat.put(PdfName.LOCK, writer.addToBody(this.fieldLock).getIndirectReference());
                fieldLock = this.fieldLock;
            }
            
//            PdfTemplate appearance = new PdfTemplate(writer);;
//            appearance.setBoundingBox(rect);
//            writer.addDirectTemplateSimple(appearance, new PdfName("n2"));
//            System.out.println("Get Image: " + option.getBgImgPath());
//            final Image image = Image.getInstance(option.getBgImgPath());
//            if (image != null) {
//                appearance.addImage(image, rect.getWidth(), 0, 0, rect.getHeight(), 0, 0);
//            }

            final Image image = Image.getInstance(option.getBgImgPath());            
            this.appearance.addImage(image, this.appearance.getBoundingBox().getWidth(), 0, 0, this.appearance.getBoundingBox().getHeight(), 0, 0);
            
            sigFieldRepeat.setWidget(rect, null);
            sigFieldRepeat.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, this.appearance);
            sigFieldRepeat.setPage(option.getPage());
            writer.addAnnotation(sigFieldRepeat, option.getPage());
        }
    }
}
