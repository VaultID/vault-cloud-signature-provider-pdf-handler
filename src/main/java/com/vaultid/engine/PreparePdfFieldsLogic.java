/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is 'JSignPdf, a free application for PDF signing'.
 * 
 * The Initial Developer of the Original Code is Josef Cacek.
 * Portions created by Josef Cacek are Copyright (C) Josef Cacek. All Rights Reserved.
 * 
 * Contributor(s): Josef Cacek.
 * 
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License, version 2.1 (the  "LGPL License"), in which case the
 * provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 */
package com.vaultid.engine;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import static net.sf.jsignpdf.Constants.RES;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jsignpdf.types.HashAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseField;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfBorderDictionary;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfFileSpecification;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PushbuttonField;
import com.itextpdf.text.pdf.TextField;
import com.sun.java.swing.plaf.gtk.GTKConstants;
import java.util.ArrayList;
import java.lang.Integer;
import java.nio.file.Files;
import java.util.HashMap;
import net.sf.jsignpdf.BasicSignerOptions;

/**
 * Main logic of signer application. It uses iText to create signature in PDF.
 *
 * @author Josef Cacek
 */
public class PreparePdfFieldsLogic implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(PreparePdfFieldsLogic.class.getName());

    private final BasicSignerOptions options;

    private boolean autoFixDocument = true;

    private ArrayList fields;

    private ArrayList attachments;

    /**
     * Constructor with all necessary parameters.
     *
     * @param anOptions options of signer
     */
    public PreparePdfFieldsLogic(final BasicSignerOptions anOptions) {
        if (anOptions == null) {
            throw new NullPointerException("Options has to be filled.");
        }
        options = anOptions;
    }

    public void setAutofixDocument(boolean autoFix) {
        this.autoFixDocument = autoFix;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
     */
    public void run() {
        prepareFile();
    }

    public static class Status {

        private boolean success = false;
        private String errorMessage = "";

        public Status(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public void setFields(ArrayList fields) {
        this.fields = fields;
    }

    public void setAttachments(ArrayList attachments) {
        this.attachments = attachments;
    }

    /**
     * Signs a single file.
     *
     * @return true when signing is finished succesfully, false otherwise
     */
    public Status prepareFile() {

        final String outFile = options.getOutFileX();
        if (!validateInOutFiles(options.getInFile(), outFile)) {
            LOGGER.severe(RES.get("console.skippingSigning"));
            return new Status(false, "Can not access either input or output file");
        } else {
            LOGGER.info("both input and output files validated");
        }

        boolean finished = false;
        Throwable tmpException = null;
        FileOutputStream fout = null;
        String error = "";
        try {
            LOGGER.info(RES.get("console.createPdfReader", options.getInFile()));
            PdfReader.unethicalreading = true;
            PdfReader reader;
            try {
                reader = new PdfReader(options.getInFile(), options.getPdfOwnerPwdStrX().getBytes());
            } catch (Exception e) {
                try {
                    reader = new PdfReader(options.getInFile(), new byte[0]);
                } catch (Exception e2) {
                    // try to read without password
                    reader = new PdfReader(options.getInFile());
                }
            }

            LOGGER.info(RES.get("console.createOutPdf", outFile));
            fout = new FileOutputStream(outFile);

            final HashAlgorithm hashAlgorithm = options.getHashAlgorithmX();

            LOGGER.info(RES.get("console.createSignature"));
            char tmpPdfVersion = '\0'; // default version - the same as input,
            
            /**
             *It's validating in PHP. Because not found library that return correct pdf version
            */
            // if (reader.getPdfVersion() < hashAlgorithm.getPdfVersion()) {

            //     //Autofix, old version? version should be updated
            //     if (this.autoFixDocument) {
            //         options.setAppend(true);
            //     }

            //     // this covers also problems with visible signatures (embedded
            //     // fonts) in PDF 1.2, because the minimal version
            //     // for hash algorithms is 1.3 (for SHA1)
            //     if (options.isAppendX()) {
            //         // if we are in append mode and version should be updated
            //         // then return false (not possible)
            //         error = RES.get("console.updateVersionNotPossibleInAppendMode");
            //         LOGGER.severe(error);
            //         return new Status(false, error);
            //     }
            //     tmpPdfVersion = hashAlgorithm.getPdfVersion();
            //     LOGGER.info(RES.get("console.updateVersion", new String[]{String.valueOf(reader.getPdfVersion()),
            //         String.valueOf(tmpPdfVersion)}));
            // }

            //final PdfStamper stp = new PdfStamper(reader, fout);
            final PdfStamper stp = new PdfStamper(reader, fout, tmpPdfVersion, options.isAppendX());

            if (!options.isAppendX()) {
                // we are not in append mode, let's remove existing signatures
                // (otherwise we're getting to troubles)
                final AcroFields acroFields = stp.getAcroFields();
                @SuppressWarnings("unchecked")
                final List<String> sigNames = acroFields.getSignatureNames();
                for (String sigName : sigNames) {
                    acroFields.removeField(sigName);
                }
            }

            if (this.attachments != null) {
                for (int k = 0; k < this.attachments.size(); k++) {

                    HashMap<String, Object> attachment = (HashMap<String, Object>) attachments.get(k);
                    byte[] fileBytes = Files.readAllBytes(new File((String) attachment.get("file")).toPath());
                    PdfFileSpecification fs = PdfFileSpecification.fileEmbedded(stp.getWriter(), null, (String) attachment.get("file"), fileBytes);
                    stp.addFileAttachment((String) attachment.get("description"), fs);
                }
            }

            if (this.fields != null) {
                for (int i = 0; i < this.fields.size(); i++) {

                    HashMap<String, Object> field = (HashMap<String, Object>) fields.get(i);

                    int page = (Integer) field.get("page");
                    if (page < 1 || page > reader.getNumberOfPages()) {
                        page = reader.getNumberOfPages();
                    }

                    int x = (Integer) field.get("x");
                    float y = (Integer) field.get("y");
                    float width = (Integer) field.get("width");
                    float height = (Integer) field.get("height");
                    options.setPositionLLX(x);
                    options.setPositionLLY(reader.getPageSizeWithRotation(page).getHeight() - y - height);
                    options.setPositionURX(x + width);
                    options.setPositionURY(reader.getPageSizeWithRotation(page).getHeight() - y);


                    /**
                     * Check PDF BaseField Options
                     */
                    int pdfFieldsOptions = 0;
                    if (field.get("option_required") != null && (Boolean) field.get("option_required") == true) {
                        pdfFieldsOptions += BaseField.REQUIRED;
                    }

                    if (field.get("option_multiline") != null && (Boolean) field.get("option_multiline") == true) {
                        pdfFieldsOptions += BaseField.MULTILINE;
                    }

                    if (field.get("option_read_only") != null && (Boolean) field.get("option_read_only") == true) {
                        pdfFieldsOptions += BaseField.READ_ONLY;
                    }

                    if (field.get("option_do_not_scroll") != null && (Boolean) field.get("option_do_not_scroll") == true) {
                        pdfFieldsOptions += BaseField.DO_NOT_SCROLL;
                    }

                    if (field.get("option_password") != null && (Boolean) field.get("option_password") == true) {
                        pdfFieldsOptions += BaseField.PASSWORD;
                    }

                    /**
                     * Check Border Color
                     */
                    BaseColor borderColor = BaseColor.BLACK;

                    if (field.get("border_color") != null) {
                        if (field.get("border_color").equals("WHITE")) {
                            borderColor = BaseColor.WHITE;
                        } else if (field.get("border_color").equals("LIGHT_GRAY")) {
                            borderColor = BaseColor.LIGHT_GRAY;
                        } else if (field.get("border_color").equals("GRAY")) {
                            borderColor = BaseColor.GRAY;
                        } else if (field.get("border_color").equals("DARK_GRAY")) {
                            borderColor = BaseColor.DARK_GRAY;
                        } else if (field.get("border_color").equals("BLACK")) {
                            borderColor = BaseColor.BLACK;
                        } else if (field.get("border_color").equals("RED")) {
                            borderColor = BaseColor.RED;
                        } else if (field.get("border_color").equals("PINK")) {
                            borderColor = BaseColor.PINK;
                        } else if (field.get("border_color").equals("ORANGE")) {
                            borderColor = BaseColor.ORANGE;
                        } else if (field.get("border_color").equals("YELLOW")) {
                            borderColor = BaseColor.YELLOW;
                        } else if (field.get("border_color").equals("GREEN")) {
                            borderColor = BaseColor.GREEN;
                        } else if (field.get("border_color").equals("MAGENTA")) {
                            borderColor = BaseColor.MAGENTA;
                        } else if (field.get("border_color").equals("CYAN")) {
                            borderColor = BaseColor.CYAN;
                        } else if (field.get("border_color").equals("BLUE")) {
                            borderColor = BaseColor.BLUE;
                        }
                    }

                    /**
                     * Check Border Color
                     */
                    int borderStyle = PdfBorderDictionary.STYLE_SOLID;

                    if (field.get("border_style") != null) {
                        if (field.get("border_style").equals("SOLID")) {
                            borderStyle = PdfBorderDictionary.STYLE_SOLID;
                        } else if (field.get("border_style").equals("DASHED")) {
                            borderStyle = PdfBorderDictionary.STYLE_DASHED;
                        } else if (field.get("border_style").equals("BEVELED")) {
                            borderStyle = PdfBorderDictionary.STYLE_BEVELED;
                        } else if (field.get("border_style").equals("INSET")) {
                            borderStyle = PdfBorderDictionary.STYLE_INSET;
                        } else if (field.get("border_style").equals("UNDERLINE")) {
                            borderStyle = PdfBorderDictionary.STYLE_UNDERLINE;
                        }
                    }
                    
                    if (((String) field.get("type")).equals("static_text")) {
                        
                        int rotation = field.get("rotation") != null? (Integer) field.get("rotation"): 0;
                        
                        stp.setRotateContents(true);
                        PdfContentByte canvas = stp.getOverContent(page);
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase((String) field.get("value")),x , y, rotation);

                    }else if (((String) field.get("type")).equals("text")) {

                        Rectangle rec = new Rectangle(options.getPositionLLX(), options.getPositionLLY(), options.getPositionURX(), options.getPositionURY());
                        
                        rec.setRotation(0);
                        PdfWriter writer = stp.getWriter();
                        

                        TextField pdfField = new TextField(writer, rec, (String) field.get("name"));
//                        TextField pdfField = new TextField(writer, new Rectangle(options.getPositionLLX(), options.getPositionLLY(), options.getPositionURX(), options.getPositionURY(),90), (String) field.get("name"));
//                        TextField pdfField = new TextField(writer, new Rectangle(llx, lly, urx, ury), (String) field.get("name"));



                        //pdfField.getAppearance().drawTextField(options.getPositionLLX(), options.getPositionLLY(), options.getPositionURX(), options.getPositionURY());
                        //pdfField.setRotation(0);
                        //pdfField.setRotationFromPage(reader.getPageSizeWithRotation(page));
                        //pdfField.getTextField().setRotate(0);


                        pdfField.setOptions(pdfFieldsOptions);

                        //Border?
                        if (field.get("border_width") != null && (Integer) field.get("border_width") > 0) {
                            pdfField.setBorderColor(borderColor);
                            pdfField.setBorderStyle(borderStyle);
                        }
                        

                        PdfFormField personal_name = pdfField.getTextField();


                        stp.addAnnotation(personal_name, page);

                       

                    } else if (((String) field.get("type")).equals("image")) {
                        PushbuttonField pdfField = new PushbuttonField(stp.getWriter(), new Rectangle(options.getPositionLLX(), options.getPositionLLY(), options.getPositionURX(), options.getPositionURY()), (String) field.get("name"));
                        pdfField.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                        pdfField.setProportionalIcon(true);
                        pdfField.setImage(Image.getInstance((String) field.get("value")));
                        pdfField.setOptions(pdfFieldsOptions);

                        //Border?
                        if (field.get("border_width") != null && (Integer) field.get("border_width") > 0) {
                            pdfField.setBorderColor(borderColor);
                            pdfField.setBorderStyle(borderStyle);
                        }
                        stp.addAnnotation(pdfField.getField(), page);
                    } else {
                        throw new Exception("Invalid field type");
                    }
                }
            }
            
            stp.close();
            fout.close();
            fout = null;
            finished = true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, RES.get("console.exception"), e);
            error = e.getMessage();
        } catch (OutOfMemoryError e) {
            LOGGER.log(Level.SEVERE, RES.get("console.memoryError"), e);
            error = e.getMessage();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            LOGGER.info(RES.get("console.finished." + (finished ? "ok" : "error")));
            options.fireSignerFinishedEvent(tmpException);
            return new Status(finished, error);
        }
    }

    public static <V> String replace(Object source, Map<String, V> valueMap) {
        return (new StrSubstitutor(valueMap)).replace((Object) source);
    }

    /**
     * Validates if input and output files are valid for signing.
     *
     * @param inFile input file
     * @param outFile output file
     * @return true if valid, false otherwise
     */
    private boolean validateInOutFiles(final String inFile, final String outFile) {
        LOGGER.info(RES.get("console.validatingFiles"));
        if (StringUtils.isEmpty(inFile) || StringUtils.isEmpty(outFile)) {
            LOGGER.info(RES.get("console.fileNotFilled.error"));
            return false;
        }
        final File tmpInFile = new File(inFile);
        final File tmpOutFile = new File(outFile);
        if (!(tmpInFile.exists() && tmpInFile.isFile() && tmpInFile.canRead())) {
            LOGGER.info(RES.get("console.inFileNotFound.error"));
            return false;
        }
        if (tmpInFile.getAbsolutePath().equals(tmpOutFile.getAbsolutePath())) {
            LOGGER.info(RES.get("console.filesAreEqual.error"));
            return false;
        }
        return true;
    }

}
