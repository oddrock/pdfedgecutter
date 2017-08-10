package com.oddrock.pdf.pdfedgecutter;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.oddrock.common.email.EmailManager;

public class PdfEdgeCutTimer {
	private static Logger logger = Logger.getLogger(PdfEdgeCutTimer.class);
	private long start;
	private long end;
	private int pageCount;
	private int pdfCount;
	public void start(){
		start = System.currentTimeMillis();
		end = -1;
		pageCount = 0;
	}
	public void end(){
		end = System.currentTimeMillis();
	}
	public void countPages(){
		pageCount++;
	}
	public void countPdf(){
		pdfCount++;
	}
	public void countPages(int count){
		pageCount = pageCount + count;
	}
	public double speedPer100Pages(){
		long nowEnd = System.currentTimeMillis();
		if(end!=-1){
			nowEnd = end;
		}
		if(pageCount!=0){
			return (double)(nowEnd-start)/(double)1000/(double)pageCount*100;
		}else{
			return 0;
		}
	}
	public double speedPerPdf(){
		long nowEnd = System.currentTimeMillis();
		if(end!=-1){
			nowEnd = end;
		}
		if(pageCount!=0){
			return (double)(nowEnd-start)/(double)1000/(double)pdfCount;
		}else{
			return 0;
		}
	}
	public void showSpeed(){
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String message = "共切白边"+pdfCount+"本，每本耗时"+ df.format(speedPerPdf())+"秒，共切白边"+pageCount+"页，每100页耗时"+df.format(speedPer100Pages())+"秒";
		logger.warn(message);
	}
	public void showSpeedPer100Pages(){
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String message = "共切白边"+pageCount+"页，每100页耗时"+df.format(speedPer100Pages())+"秒";
		logger.warn(message);
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException, MessagingException {
		String senderAccount = Prop.get("mail.sender.account");
		String senderPasswd = Prop.get("mail.sender.passwd");
		String recverAccounts = Prop.get("mail.recver.accounts");
		EmailManager.sendEmailFast(senderAccount, senderPasswd, recverAccounts, "测试短信：PDF切白边已完成007");
	}

}
