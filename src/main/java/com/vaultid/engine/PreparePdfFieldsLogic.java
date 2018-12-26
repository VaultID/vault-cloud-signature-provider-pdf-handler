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
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.TextField;
import java.awt.Color;
import net.sf.jsignpdf.BasicSignerOptions;

/**
 * Main logic of signer application. It uses iText to create signature in PDF.
 * 
 * @author Josef Cacek
 */
public class PreparePdfFieldsLogic implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(PreparePdfFieldsLogic.class.getName());

	private final BasicSignerOptions options;
    
    private String signerName = ""; //Common name
    
    private String subfilter = "adbe.pkcs7.detached"; //Acroform Signature subfilter
    
    private boolean autoFixDocument = true;

	/**
	 * Constructor with all necessary parameters.
	 * 
	 * @param anOptions
	 *            options of signer
	 */
	public PreparePdfFieldsLogic(final BasicSignerOptions anOptions) {
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
		String error ="";
		try {
			LOGGER.info(RES.get("console.createPdfReader", options.getInFile()));
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
			if (reader.getPdfVersion() < hashAlgorithm.getPdfVersion()) {
                
                //Autofix, old version? version should be updated
                if(this.autoFixDocument){
                    options.setAppend(true);
                }
                
				// this covers also problems with visible signatures (embedded
				// fonts) in PDF 1.2, because the minimal version
				// for hash algorithms is 1.3 (for SHA1)
				if (options.isAppendX()) {
					// if we are in append mode and version should be updated
					// then return false (not possible)
					error = RES.get("console.updateVersionNotPossibleInAppendMode");
					LOGGER.severe(error);
					return new Status(false, error);
				}
				tmpPdfVersion = hashAlgorithm.getPdfVersion();
				LOGGER.info(RES.get("console.updateVersion", new String[] { String.valueOf(reader.getPdfVersion()),
						String.valueOf(tmpPdfVersion) }));
			}
            
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
            
            int page = options.getPage();
            if (page < 1 || page > reader.getNumberOfPages()) {
                page = reader.getNumberOfPages();
            }

            float x = options.getPositionLLX(); 
            float y = options.getPositionLLY();
            float width = options.getPositionURX(); //Width
            float height = options.getPositionURY(); //Height
            options.setL2Text("");//Set empty text

            options.setPositionLLX(x);
            options.setPositionLLY(reader.getPageSize(page).getHeight() - y - height);

            options.setPositionURX(x + width);
            options.setPositionURY(reader.getPageSize(page).getHeight() - y);

            Font font = FontFactory.getFont("/usr/share/fonts/truetype/freefont/FreeSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 0.8f, Font.NORMAL, Color.BLACK);
            BaseFont baseFont = font.getBaseFont();
            PdfFormField personal = PdfFormField.createEmpty(stp.getWriter());
            personal.setFieldName("personal");
            TextField name = new TextField(stp.getWriter(), new Rectangle(options.getPositionLLX(), options.getPositionLLY(), options.getPositionURX(),
                                                                    options.getPositionURY()), "name");
            name.setFont(baseFont);
            name.setText("Meu valor padrão");
            //name.setOptions(TextField.READ_ONLY); //TextField.MULTILINE
            PdfFormField personal_name = name.getTextField();
            personal.addKid(personal_name);                       
            //TextField password = new TextField(writer, new Rectangle(150, 760, 450, 790), "password");
            //PdfFormField personal_password = password.getTextField();
            //personal.addKid(personal_password);
            stp.addAnnotation(personal, page);
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
		return (new StrSubstitutor(valueMap)).replace((Object)source);
	}


	/**
	 * Validates if input and output files are valid for signing.
	 * 
	 * @param inFile
	 *            input file
	 * @param outFile
	 *            output file
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
