package com.vaultid.engine;

import com.itextpdf.text.pdf.security.PdfSignatureBuildProperties;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfDate;

/**
 * Implements the timestamp signature dictionary.
 * 
 * @see <https://raw.githubusercontent.com/itext/itextpdf/develop/itext/src/main/java/com/itextpdf/text/pdf/PdfSignature.java>
 * @author Paulo Filipe <paulo.santos@soluti.com.br>
 */
public class PdfTimestampSignature extends PdfDictionary {

	/** Creates new PdfSignature */
	public PdfTimestampSignature(PdfName filter, PdfName subFilter) {
		super(PdfName.DOCTIMESTAMP);
		put(PdfName.FILTER, filter);
		put(PdfName.SUBFILTER, subFilter);
	}

	public void setByteRange(int range[]) {
		PdfArray array = new PdfArray();
		for (int k = 0; k < range.length; ++k)
			array.add(new PdfNumber(range[k]));
		put(PdfName.BYTERANGE, array);
	}

	public void setContents(byte contents[]) {
		put(PdfName.CONTENTS, new PdfString(contents).setHexWriting(true));
	}

	public void setCert(byte cert[]) {
		put(PdfName.CERT, new PdfString(cert));
	}

	public void setName(String name) {
		put(PdfName.NAME, new PdfString(name, PdfObject.TEXT_UNICODE));
	}

	public void setDate(PdfDate date) {
		put(PdfName.M, date);
	}

	public void setLocation(String name) {
		put(PdfName.LOCATION, new PdfString(name, PdfObject.TEXT_UNICODE));
	}

	public void setReason(String name) {
		put(PdfName.REASON, new PdfString(name, PdfObject.TEXT_UNICODE));
	}

	/**
	 * Sets the signature creator name in the
	 * {@link PdfSignatureBuildProperties} dictionary.
	 * 
	 * @param name
	 */
	public void setSignatureCreator(String name) {
		if (name != null) {
			getPdfSignatureBuildProperties().setSignatureCreator(name);
		}
	}

	/**
	 * Gets the {@link PdfSignatureBuildProperties} instance if it exists, if
	 * not it adds a new one and returns this.
	 * 
	 * @return {@link PdfSignatureBuildProperties}
	 */
	PdfSignatureBuildProperties getPdfSignatureBuildProperties() {
		PdfSignatureBuildProperties buildPropDic = (PdfSignatureBuildProperties) getAsDict(PdfName.PROP_BUILD);
		if (buildPropDic == null) {
			buildPropDic = new PdfSignatureBuildProperties();
			put(PdfName.PROP_BUILD, buildPropDic);
		}
		return buildPropDic;
	}

	public void setContact(String name) {
		put(PdfName.CONTACTINFO, new PdfString(name, PdfObject.TEXT_UNICODE));
	}
}
