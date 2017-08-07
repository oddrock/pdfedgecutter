package com.oddrock.common.pdf;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;

public class PdfManager {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(PdfManager.class);

	public PdfManager() {}

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
	
	public PdfSize pdfSize(String pdfFilePath) throws IOException{
		PdfReader reader = new PdfReader(pdfFilePath);
		int pageCount = reader.getNumberOfPages();
		PdfSize pdfSize = new PdfSize();
		Rectangle rec;
		for(int i=1; i<=pageCount; i++){
			rec = reader.getPageSize(i);
			pdfSize.addPageSize(i,rec.getWidth(), rec.getHeight());
		}
		return pdfSize;
	}
	
	public void showPdfSize(String pdfFilePath) throws IOException{
		PdfSize pdfSize =  pdfSize(pdfFilePath);
		Integer pageNum;
		java.text.DecimalFormat   df   =  new   java.text.DecimalFormat("#.00");  
		for(Object o : pdfSize.getMap().keySet()){
			pageNum = (Integer)o;
			System.out.println(String.valueOf(pageNum) 
					+ ":" + df.format(pdfSize.getPageWidthInch(pageNum))
					+ ":" + df.format(pdfSize.getPageHeightInch(pageNum)));
		}
	}
	
	/**
	 * 是否加密
	 * @param pdfFilePath
	 * @return
	 * @throws IOException
	 */
	public boolean isEncrypted(String pdfFilePath) throws IOException{
		PdfReader pr = new PdfReader(pdfFilePath);
		return pr.isEncrypted();
	}
	
	/**
	 * 是否可以转换
	 * @param pdfFilePath
	 * @return
	 * @throws IOException
	 */
	public boolean canCutPage(String pdfFilePath) throws IOException{
		if(isEncrypted(pdfFilePath)){
			logger.warn("文件【"+pdfFilePath+"】已加密，无法转换");
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) throws IOException{
		String pdfFilePath = "C:\\Users\\oddro\\Desktop\\pdf测试\\456.pdf";
		PdfReader pr = new PdfReader(pdfFilePath);
		System.out.println(pr.isEncrypted());
		System.out.println(pr.is128Key());
		pdfFilePath = "C:\\Users\\oddro\\Desktop\\pdf测试\\flume - 副本.pdf";
		pr = new PdfReader(pdfFilePath);
		System.out.println(pr.isEncrypted());
		System.out.println(pr.is128Key());
	}
}
