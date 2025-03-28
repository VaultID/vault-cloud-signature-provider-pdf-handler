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

import static net.sf.jsignpdf.Constants.L2TEXT_PLACEHOLDER_CONTACT;
import static net.sf.jsignpdf.Constants.L2TEXT_PLACEHOLDER_LOCATION;
import static net.sf.jsignpdf.Constants.L2TEXT_PLACEHOLDER_REASON;
import static net.sf.jsignpdf.Constants.L2TEXT_PLACEHOLDER_SIGNER;
import static net.sf.jsignpdf.Constants.L2TEXT_PLACEHOLDER_TIMESTAMP;
import static net.sf.jsignpdf.Constants.RES;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jsignpdf.types.HashAlgorithm;
import net.sf.jsignpdf.utils.FontUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfIndirectReference;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignature;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfSignatureAppearanceSecundary;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PushbuttonField;
import com.itextpdf.text.pdf.TextField;
import com.vaultid.main.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;
import net.sf.jsignpdf.BasicSignerOptions;
import net.sf.jsignpdf.Constants;
import net.sf.jsignpdf.SecundarySignerOptions;
import org.apache.commons.io.IOUtils;

/**
 * Main logic of signer application. It uses iText to create signature in PDF.
 *
 * @author Josef Cacek
 */
public class PreparePdfToSignLogic implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(PreparePdfToSignLogic.class.getName());

    private final BasicSignerOptions options;

    private String signerName = ""; //Common name

    private String subfilter = "adbe.pkcs7.detached"; //Acroform Signature subfilter
    
    private PdfName filter = PdfName.ADOBE_PPKLITE;

    private String type = "PdfSignature"; //PdfSignature OR PdfTimestampSignature

    private boolean autoFixDocument = true;

    private ArrayList fields;
    
    private ArrayList extraInfo;

    /**
     * Constructor with all necessary parameters.
     *
     * @param anOptions options of signer
     */
    public PreparePdfToSignLogic(final BasicSignerOptions anOptions) {
        if (anOptions == null) {
            throw new NullPointerException("Options has to be filled.");
        }
        options = anOptions;
    }

    public void setSignerName(String signerName) {
        this.signerName = signerName;
    }

    public void setSubfilter(String subfilter) {
        this.subfilter = subfilter;
    }
    
    public void setType(String type) {
        this.type = type;
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
        signFile();
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
    
    public void setExtraInfo(ArrayList extraInfo) {
        this.extraInfo = extraInfo;
    }
    
    public void setFilter(PdfName filter) {
        this.filter = filter;
    }

    public PdfName getFilter() {
        return filter;
    }
    
    /**
     * Signs a single file.
     *
     * @return true when signing is finished succesfully, false otherwise
     */
    public Status signFile() {
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
            char tmpPdfVersion = '\0'; // default version - the same as input
            
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
            
            final PdfStamper stp = PdfStamper.createSignature(reader, fout, tmpPdfVersion, null, options.isAppendX());
            
            final AcroFields acroFields = stp.getAcroFields();
         
//            if (!options.isAppendX()) {;;
//                // we are not in append mode, let's remove existing signatures
//                // (otherwise we're getting to troubles)
//                @SuppressWarnings("unchecked")
//                final List<String> sigNames = acroFields.getSignatureNames();
//                for (String sigName : sigNames) {
//                    acroFields.removeField(sigName);
//                }
//            }
            
            Set<String> fldNames = acroFields.getFields().keySet();
            for (String fldName : fldNames) {
                System.out.println(fldName + ": " + acroFields.getField(fldName));
            }
            
            if(this.fields != null){
                for (int i = 0; i < this.fields.size(); i++) {
                    HashMap<String, Object> field = (HashMap<String, Object>) fields.get(i);
                    if (((String) field.get("type")).equals("text") || ((String) field.get("type")).equals("static_text")) {

                        //Replace fields form values

                        acroFields.setField((String) field.get("name"), (String) field.get("value"));
                        acroFields.setField("form." + (String) field.get("name"), (String) field.get("value"));
                       
//                       TextField txf = acroFields.getFieldCache().getOrDefault((String) field.get("name"), null);
//                        
//                        txf.setRotation(90);
//                        acroFields.regenerateField((String) field.get("name"));
                        
                        try {
                            if (((String) field.get("readonly")).equals("true")) {
                                acroFields.setFieldProperty((String) field.get("name"), "setfflags", PdfFormField.FF_READ_ONLY, null);
                            }
                        } catch( NullPointerException e ) {
                            // Parâmetro "readonly" não informado
                        }
                    } else if (((String) field.get("type")).equals("image")) {
                        PushbuttonField ad = acroFields.getNewPushbuttonFromField((String) field.get("name"));
                        ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                        ad.setProportionalIcon(true);
                        ad.setImage(Image.getInstance((String) field.get("value")));
                        acroFields.replacePushbuttonField((String) field.get("name"), ad.getField());
                    }
                }
            }
            

            if(this.extraInfo != null){
                Map<String, String> newInfo;
                newInfo = new HashMap();
                for (int i = 0; i < this.extraInfo.size(); i++) {
                    HashMap<String, Object> field = (HashMap<String, Object>) extraInfo.get(i);
                    newInfo.put((String) field.get("name"), (String) field.get("value"));
                }
                stp.setMoreInfo(newInfo);
            }
            
            
            
            
            final PdfSignatureAppearance sap = stp.getSignatureAppearance();
            LOGGER.info("PdfSignatureAppearanceSecundary");
            final PdfSignatureAppearanceSecundary sap2 = new PdfSignatureAppearanceSecundary();
            
            final String reason = options.getReason();
            if (StringUtils.isNotEmpty(reason)) {
                LOGGER.info(RES.get("console.setReason", reason));
                sap.setReason(reason);
            }
            final String location = options.getLocation();
            if (StringUtils.isNotEmpty(location)) {
                LOGGER.info(RES.get("console.setLocation", location));
                sap.setLocation(location);
            }
            final String contact = options.getContact();
            if (StringUtils.isNotEmpty(contact)) {
                LOGGER.info(RES.get("console.setContact", contact));
                sap.setContact(contact);
            }
            LOGGER.info(RES.get("console.setCertificationLevel"));
            sap.setCertificationLevel(options.getCertLevelX().getLevel());

            if (options.isVisible()) {
                // visible signature is enabled
                LOGGER.info(RES.get("console.configureVisible"));
                LOGGER.info(RES.get("console.setAcro6Layers", Boolean.toString(options.isAcro6Layers())));
                sap.setAcro6Layers(options.isAcro6Layers());

                final String tmpImgPath = options.getImgPath();
                if (tmpImgPath != null) {
                    LOGGER.info(RES.get("console.createImage", tmpImgPath));
                    final Image img = Image.getInstance(tmpImgPath);
                    LOGGER.info(RES.get("console.setSignatureGraphic"));
                    sap.setSignatureGraphic(img);
                }
                final String tmpBgImgPath = options.getBgImgPath();
                if (tmpBgImgPath != null) {
                    LOGGER.info(RES.get("console.createImage", tmpBgImgPath));
                    final Image img = Image.getInstance(tmpBgImgPath);
                    LOGGER.info(RES.get("console.setImage"));
                    sap.setImage(img);
                }
                LOGGER.info(RES.get("console.setImageScale"));
                sap.setImageScale(options.getBgImgScale());
                LOGGER.info(RES.get("console.setL2Text"));

                final String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z").format(sap.getSignDate()
                        .getTime());

                if (options.getL2Text() != null) {
                    final Map<String, String> replacements = new HashMap<String, String>();
                    replacements.put(L2TEXT_PLACEHOLDER_SIGNER, StringUtils.defaultString(this.signerName));
                    replacements.put(L2TEXT_PLACEHOLDER_TIMESTAMP, timestamp);
                    replacements.put(L2TEXT_PLACEHOLDER_LOCATION, StringUtils.defaultString(location));
                    replacements.put(L2TEXT_PLACEHOLDER_REASON, StringUtils.defaultString(reason));
                    replacements.put(L2TEXT_PLACEHOLDER_CONTACT, StringUtils.defaultString(contact));
                    final String l2text = StrSubstitutor.replace(options.getL2Text(), replacements);
                    sap.setLayer2Text(l2text);
                } else {
                    final StringBuilder buf = new StringBuilder();
                    buf.append(RES.get("default.l2text.signedBy")).append(" ").append(this.signerName).append('\n');
                    buf.append(RES.get("default.l2text.date")).append(" ").append(timestamp);
                    if (StringUtils.isNotEmpty(reason)) {
                        buf.append('\n').append(RES.get("default.l2text.reason")).append(" ").append(reason);
                    }
                    if (StringUtils.isNotEmpty(location)) {
                        buf.append('\n').append(RES.get("default.l2text.location")).append(" ").append(location);
                    }
                    sap.setLayer2Text(buf.toString());
                }
                if (FontUtils.getL2BaseFont() != null) {
                    sap.setLayer2Font(new Font(FontUtils.getL2BaseFont(), options.getL2TextFontSize()));
                }
                LOGGER.info(RES.get("console.setL4Text"));
                sap.setLayer4Text(options.getL4Text());
                LOGGER.info(RES.get("console.setRender"));
                //sap.setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION);
                LOGGER.info(RES.get("console.setVisibleSignature"));
                
                int page = options.getPage();
                
                if(page == -100){
                   sap.setSignAllPages(true);
                }
             
                if (page < 1 || page > reader.getNumberOfPages()) {
                    page = reader.getNumberOfPages();
                }

                float x = options.getPositionLLX();
                float y = options.getPositionLLY();
                float width = options.getPositionURX(); //Width
                float height = options.getPositionURY(); //Height
                options.setL2Text("");//Set empty text
                float yLimit = reader.getPageSizeWithRotation(page).getHeight() - height;
                if (y > yLimit) {
                    y = yLimit;
                }

                options.setPositionLLX(x);
                options.setPositionLLY(reader.getPageSizeWithRotation(page).getHeight() - y - height);

                options.setPositionURX(x + width);
                options.setPositionURY(reader.getPageSizeWithRotation(page).getHeight() - y);
                
                if(options.getVisibleSignatureField() != null){
                    sap.setVisibleSignature(options.getVisibleSignatureField()); //03_Signature Emitente
                }
                else{
                    
                    sap.setVisibleSignature(
                        new Rectangle(options.getPositionLLX(), options.getPositionLLY(), options.getPositionURX(),
                                options.getPositionURY()), page, null);  
                }
            }

            LOGGER.info(RES.get("console.processing"));
            
            if(this.type.equals("PdfSignature")){
                final PdfSignature dic = new PdfSignature(this.filter, new PdfName(this.subfilter));
                if (!StringUtils.isEmpty(reason)) {
                    dic.setReason(sap.getReason());
                }
                if (!StringUtils.isEmpty(location)) {
                    dic.setLocation(sap.getLocation());
                }
                if (!StringUtils.isEmpty(contact)) {
                    dic.setContact(sap.getContact());
                }
                dic.setDate(new PdfDate(sap.getSignDate()));
                sap.setCryptoDictionary(dic);
            }
            else{
                final PdfTimestampSignature dic = new PdfTimestampSignature(this.filter, new PdfName(this.subfilter));
                if (!StringUtils.isEmpty(reason)) {
                    dic.setReason(sap.getReason());
                }
                if (!StringUtils.isEmpty(location)) {
                    dic.setLocation(sap.getLocation());
                }
                if (!StringUtils.isEmpty(contact)) {
                    dic.setContact(sap.getContact());
                }
                dic.setDate(new PdfDate(sap.getSignDate()));
                sap.setCryptoDictionary(dic);
            }
            
            final int contentEstimated = (int) (Constants.DEFVAL_SIG_SIZE + 2L);
            final HashMap<PdfName, Integer> exc = new HashMap<PdfName, Integer>();
            exc.put(PdfName.CONTENTS, new Integer(contentEstimated * 2 + 2));
            
            for(SecundarySignerOptions secundarySignatureOption: options.getSecundarySignatures()){
                float x = secundarySignatureOption.getPositionLLX();
                float y = secundarySignatureOption.getPositionLLY();
                float width = secundarySignatureOption.getPositionURX(); //Width
                float height = secundarySignatureOption.getPositionURY(); //Height

                secundarySignatureOption.setPositionLLX(x);
                secundarySignatureOption.setPositionLLY(reader.getPageSizeWithRotation(secundarySignatureOption.getPage()).getHeight() - y - height);

                secundarySignatureOption.setPositionURX(x + width);
                secundarySignatureOption.setPositionURY(reader.getPageSizeWithRotation(secundarySignatureOption.getPage()).getHeight() - y);
            }
            
            LOGGER.info("setSecundarySignatureOptions");
            sap2.setSecundarySignatureOptions(options.getSecundarySignatures());
            sap2.setAppearance(sap.getAppearance());

            sap.preClose(exc, sap2);
            
            Calendar cal = Calendar.getInstance();

            byte[] encodedSig = {};

            if (contentEstimated + 2 < encodedSig.length) {
                System.err.println("SigSize - contentEstimated=" + contentEstimated + ", sigLen=" + encodedSig.length);
                throw new Exception("Not enough space");
            }

            byte[] paddedSig = new byte[contentEstimated];
            System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);

            PdfDictionary dic2 = new PdfDictionary();
            
            dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
            LOGGER.info(RES.get("console.closeStream"));
            sap.close(dic2);
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
