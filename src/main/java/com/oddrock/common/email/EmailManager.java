package com.oddrock.common.email;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
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
	    if(emailSent.getSmtpPort()!=null){
	    	props.setProperty("mail.smtp.port", emailSent.getSmtpPort()); 
	    }
	    Session session = null;
	    if(emailSent.isSmtpAuth()){
	    	final String userName = emailSent.getSenderAccount();
            final String password = emailSent.getSenderPasswd();
	    	Authenticator authenticator = new Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(userName, password);
	            }
	        };
	        session = Session.getInstance(props, authenticator);
	    }else{
	    	session = Session.getDefaultInstance(props); 
	    } 
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
			String recverAccounts, String subject, String content, boolean smtpAuth, String smtpPort) 
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
		emailSent.setSmtpAuth(smtpAuth);
		if(smtpPort!=null){
			emailSent.setSmtpPort(smtpPort);
		}
		sendEmail(emailSent);
	}
	
	public static void sendEmailFast(String senderAccount, String senderPasswd, 
			String recverAccounts, String subject) 
			throws UnsupportedEncodingException, MessagingException{
		sendEmailFast(senderAccount, senderPasswd, recverAccounts, subject, subject, false, null);
	}
	
	public static void sendEmailFastByAuth(String senderAccount, String senderPasswd, 
			String recverAccounts, String subject, String smtpPort) 
			throws UnsupportedEncodingException, MessagingException{
		sendEmailFast(senderAccount, senderPasswd, recverAccounts, subject, subject, true, smtpPort);
	}
	
	public static void sendQQEmail() throws MessagingException{
        // 创建Properties 类用于记录邮箱的一些属性
        final Properties props = new Properties();
        // 表示SMTP发送邮件，必须进行身份验证
        props.put("mail.smtp.auth", "true");
        //此处填写SMTP服务器
        props.put("mail.smtp.host", "smtp.qq.com");
        //端口号，QQ邮箱给出了两个端口，但是另一个我一直使用不了，所以就给出这一个587
        props.put("mail.smtp.port", "587");
        // 此处填写你的账号
        props.put("mail.user", "oddrock@qq.com");
        // 此处的密码就是前面说的16位STMP口令
        props.put("mail.password", "asfazgavqzmcbibi");
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress form = new InternetAddress(props.getProperty("mail.user"));
        message.setFrom(form);
        // 设置收件人的邮箱
        InternetAddress to = new InternetAddress("13856980838@139.com");
        message.setRecipient(RecipientType.TO, to);
        // 设置邮件标题
        message.setSubject("测试邮件");
        // 设置邮件的内容体
        message.setContent("这是一封测试邮件", "text/html;charset=UTF-8");
        // 最后当然就是发送邮件啦
        Transport.send(message);
	}
	
	public static void main(String[] args) throws MessagingException{
		sendQQEmail();
	}
}