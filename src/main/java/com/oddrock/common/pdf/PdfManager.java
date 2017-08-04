package com.oddrock.common.pdf;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.itextpdf.text.pdf.PdfReader;

public class PdfManager {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(PdfManager.class);

	public PdfManager() {

	}

	/**
	 * 获得PDF总页数
	 * @param pdfFilePath
	 * @return
	 * @throws IOException
	 */
	public int pdfPageCount(String pdfFilePath) throws IOException {
		PdfReader reader = new PdfReader(pdfFilePath);
		int pageCount = reader.getNumberOfPages();
		reader.close();
		return pageCount;
	}
}
