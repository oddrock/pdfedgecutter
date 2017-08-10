package com.oddrock.common.email;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailManager {
	public static void sendEmail(EmailSent emailSent) 
			throws UnsupportedEncodingException, MessagingException{
		Properties props = new Properties();                    
	    props.setProperty("mail.transport.protocol", emailSent.getProtocol());   
	    props.setProperty("mail.smtp.host", emailSent.getSmtpHost());  
	    props.setProperty("mail.smtp.auth", String.valueOf(emailSent.isSmtpAuth()));           
	    Session session = Session.getDefaultInstance(props);    
	    session.setDebug(emailSent.isDebug());
	    MimeMessage message = new MimeMessage(session);
	    message.setFrom(new InternetAddress(emailSent.getSenderAccount(),emailSent.getSenderName()));
	    InternetAddress[] addresses = new InternetAddress[emailSent.getRecverAccounts().size()];
	    int i = 0;
	    for (String recv: emailSent.getRecverAccounts()){
	    	addresses[i++] = new InternetAddress(recv);	
	    }
	    message.setRecipients(MimeMessage.RecipientType.TO, addresses);
	    message.setSubject(emailSent.getSubject());	
	    message.setContent(emailSent.getContent(), emailSent.getContentEncoding());
	    message.setSentDate(emailSent.getSendTime());	
	    message.saveChanges();	
	    Transport transport = session.getTransport();
	    transport.connect(emailSent.getSenderAccount(), emailSent.getSenderPasswd());
	    transport.sendMessage(message, message.getAllRecipients());
	    transport.close();
	}
	
	public static void sendEmailFast(String senderAccount, String senderPasswd, 
			String recverAccounts, String subject, String content) 
			throws UnsupportedEncodingException, MessagingException{
		String[] recvers = recverAccounts.split(",");
		EmailSent emailSent = new EmailSent();
		for(String recver : recvers){
			if(recver!=null && recver.trim().length()>0){
				emailSent.addrecverAccount(recver.trim());
			}
		}
		emailSent.setSubject(subject);
		emailSent.setContent(content);
		emailSent.setSenderAccount(senderAccount);
		emailSent.setSenderPasswd(senderPasswd);
		sendEmail(emailSent);
	}
	
	public static void sendEmailFast(String senderAccount, String senderPasswd, 
			String recverAccounts, String subject) 
			throws UnsupportedEncodingException, MessagingException{
		String[] recvers = recverAccounts.split(",");
		EmailSent emailSent = new EmailSent();
		for(String recver : recvers){
			if(recver!=null && recver.trim().length()>0){
				emailSent.addrecverAccount(recver.trim());
			}
		}
		emailSent.setSubject(subject);
		emailSent.setSenderAccount(senderAccount);
		emailSent.setSenderPasswd(senderPasswd);
		sendEmail(emailSent);
	}
}