package net.dflmngr.utils;

import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;

public class EmailUtils {
	
	private static GlobalsService globalsService = new GlobalsServiceImpl();
	private static String incomingMailHost = globalsService.getEmailConfig().get("incomingMailHost");
	private static String outgoingMailHost = globalsService.getEmailConfig().get("outgoingMailHost");
	private static String outgoingMailPort = globalsService.getEmailConfig().get("outgoingMailPort");
	private static String mailUsername = globalsService.getEmailConfig().get("mailUsername");;
	private static String mailPassword = globalsService.getEmailConfig().get("mailPassword");;
	
	public static void send(List<String> to, String from, String subject, String body, List<String> attachments) throws Exception {
	
		MimeMessage message = new MimeMessage(getMailSession());
		message.setFrom(new InternetAddress(from));
		
		InternetAddress[] toAddresses = new InternetAddress[to.size()]; 
		
		for(int i = 0; i < to.size(); i++) {
			toAddresses[i] = new InternetAddress(to.get(i));
		}
		
		message.setRecipients(Message.RecipientType.TO, toAddresses);
		message.setSubject(subject);

		
		if(attachments != null && !attachments.isEmpty()) {
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);

			Multipart multipart = new MimeMultipart("mixed");
			multipart.addBodyPart(messageBodyPart);

			for(String attachment : attachments) {
				messageBodyPart = new MimeBodyPart();
				
				DataSource source = new FileDataSource(attachment);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(source.getName());
				multipart.addBodyPart(messageBodyPart);
			}
			
			message.setContent(multipart);
		} else {
			message.setContent(body, "text/plain");
		}

		Transport.send(message);
	}
	
	private static Session getMailSession() {
	
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", outgoingMailHost);
		properties.setProperty("mail.smtp.port", outgoingMailPort);
		properties.setProperty("mail.smtp.auth", "true");
		//properties.setProperty("mail.imap.ssl.enable", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.store.protocol", "imaps");
				
		Session mailSession = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailUsername, mailPassword);
			}
		});
		
		return mailSession;
	}

}
