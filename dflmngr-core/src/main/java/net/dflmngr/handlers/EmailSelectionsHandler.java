package net.dflmngr.handlers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;

public class EmailSelectionsHandler {
	private Logger logger;

	private String dflmngrEmailAddr;
	private String incomingMailHost;
	private String outgoingMailHost;
	private String outgoingMailPort;
	private String mailUsername;
	private String mailPassword;
	
	GlobalsService globalsService;
	
	Session mailSession;
	
	Map <String, Boolean> responses;
	
	public EmailSelectionsHandler() {
		
		MDC.put("online.name", "Selections");
		logger = LoggerFactory.getLogger("online-logger");
		
		try {
			globalsService = new GlobalsServiceImpl();
			
			Map<String, String> emailConfig = globalsService.getEmailConfig();
			
			this.dflmngrEmailAddr = emailConfig.get("dflmngrEmailAddr");
			this.incomingMailHost = emailConfig.get("incomingMailHost");
			this.outgoingMailHost = emailConfig.get("outgoingMailHost");
			this.outgoingMailPort = emailConfig.get("outgoingMailPort");
			this.mailUsername = emailConfig.get("mailUsername");
			this.mailPassword = emailConfig.get("mailPassword");
			
			logger.info("Email config: dflmngrEmailAddr={}; incomingMailHost={}; outgoingMailHost={}; outgoingMailHost={}; mailUsername={}; mailPassword={}",
					     dflmngrEmailAddr, incomingMailHost, outgoingMailHost, outgoingMailPort, mailUsername, mailPassword);
			
			configureMail();
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
			MDC.remove("online.name");
		}
	}
	
	public void execute() {
		try {
			this.responses = new HashMap<>();
			logger.info("Email Selections Handler is executing ....");
			processSelections();
			logger.info("Sending responses");
			sendResponses();
			
			globalsService.close();
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove("online.name");
		}
	}
	
	private void configureMail() {
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", this.outgoingMailHost);
		properties.setProperty("mail.smtp.port", this.outgoingMailPort);
		properties.setProperty("mail.smtp.auth", "true");
		//properties.setProperty("mail.imap.ssl.enable", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.store.protocol", "imaps");
		//properties.setProperty("mail.store.protocol", "pop3s");
		
		//this.mailSession = Session.getInstance(properties);
				
		this.mailSession = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailUsername, mailPassword);
			}
		});
				
	}
	
	private void processSelections() throws Exception {
		
		Store store = this.mailSession.getStore("imaps");
		store.connect(this.incomingMailHost, this.mailUsername, this.mailPassword);
		
		Folder inbox = store.getFolder("Inbox");
		inbox.open(Folder.READ_WRITE);
		
		Message[] messages = inbox.getMessages();
		
		logger.info("Opended inbox: messages={}", messages.length);
		
		for(int i = 0; i < messages.length; i++) {
			
			logger.info("Handling message {}", i);
			
			int currentSize = this.responses.size();
			
			try {
				
				String from = InternetAddress.toString(messages[i].getFrom());
				String contentType = messages[i].getContentType();
				
				if (contentType.contains("multipart")) {
					Multipart multipart = (Multipart) messages[i].getContent();
								
					for(int j = 0; j < multipart.getCount(); j++) {
						MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(j);
											
						String disposition = part.getDisposition();
		
						if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
							String attachementName = part.getFileName();
							if(attachementName.equals("selections.txt")) {
								logger.info("Message from {}, has selection attachment", from);
								handleSelectionFile(part.getInputStream());
								this.responses.put(from, true);
								logger.info("Message from {} ... SUCCESS!", from);
							}
						}
							
					}
				}
				if(this.responses.size() == currentSize) {
					this.responses.put(from, false);
					logger.info("Message from {} ... FAILURE!", from);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				try {
					String from =  InternetAddress.toString(messages[i].getFrom());
					this.responses.put(from, false);
					logger.info("Message from {} ... FAILURE with EXCEPTION!", from);
				} catch (MessagingException ex2) {
					ex2.printStackTrace();
				}
			}
		}
		
		logger.info("Moving messages to Processed folder");
		Folder processedMessages = store.getFolder("Processed");
		inbox.copyMessages(messages, processedMessages);
		
		for(int i = 0; i < messages.length; i++) {
			messages[i].setFlag(Flags.Flag.DELETED, true);
		}
		
		inbox.expunge();
		
		inbox.close(true);
		store.close();
	}
	
	private void handleSelectionFile(InputStream inputStream) throws Exception {
		
		String line = "";
		String teamCode = "";
		int round = 0;
		List<Integer> ins = new ArrayList<>();
		List<Integer> outs = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		
		logger.info("Reading selections file ...");
		
		while((line = reader.readLine()) != null) {
			
			if(line.equalsIgnoreCase("[team]")) {
				line = reader.readLine();
				teamCode = line;
				logger.info("Selections for team: {}", teamCode);
			}
			
			if(line.equalsIgnoreCase("[round]")) {
				line = reader.readLine();
				round = Integer.parseInt(line);
				logger.info("Selections for round: {}", round);
			}
			
			if(line.equalsIgnoreCase("[in]")) {
				while(reader.ready()) {
					line = reader.readLine();
					if(line.equalsIgnoreCase("[out]")) {
						break;
					} else if(line.equalsIgnoreCase("")) {
						// ignore blank lines
					} else {
						ins.add(Integer.parseInt(line));
					}
				}
				logger.info("Selection in: {}", ins);
			}
			
			if(line.equalsIgnoreCase("[out]")) {
				while(reader.ready()) {
					line = reader.readLine();
					if(line.equalsIgnoreCase("[in]")) {
						break;
					} else if(line.equalsIgnoreCase("")) {
						// ignore blank lines
					} else {
						outs.add(Integer.parseInt(line));
					}
				}
				logger.info("Selection out: {}", outs);
			}		
		}
		
		TeamSelectionLoaderHandler selectionsLoader = new TeamSelectionLoaderHandler();
		selectionsLoader.execute(teamCode, round, ins, outs);
	}
	
	private void sendResponses() throws Exception {
		
		
		for (Map.Entry<String, Boolean> response : this.responses.entrySet()) {
		
			MimeMessage message = new MimeMessage(this.mailSession);
			message.setFrom(new InternetAddress(this.dflmngrEmailAddr));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(response.getKey()));
			
			logger.info("Creating response message: to={}; from={};", response.getKey(), this.dflmngrEmailAddr);
			
			boolean isSuccess = response.getValue();
			
			if(isSuccess) {
				logger.info("Message is for SUCCESS");
				setSuccessMessage(message);
			} else {
				logger.info("Message is for FAILURE");
				setFailureMessage(message);
			}
			
			logger.info("Sending message");
			Transport.send(message);
		}
	}
	
	private void setSuccessMessage(Message message) throws Exception {
		message.setSubject("Selections received - SUCCESS!");
		message.setContent("Coach, \n\n Your selections have been stored in the database .... have a nice day. \n\n DFL Manager Admin", "text/plain");
	}
	
	private void setFailureMessage(Message message) throws Exception {
		message.setSubject("Selections received - FAILED!");
		
		String messageBody = "Coach,\n\n" +
							 "Your selections have not been stored in the database .... The reasons for this may be:\n" +
							 "\t- You sent the email with no selections.txt\n" +
							 "\t- You made a mistake in the selections.txt\n" +
							 "\t- Shitty programming\n" +
							 "\t- Just bad luck\n\n" +
							 "Certainly it is one of the first 2 options, so please check your selections.txt file and try again.  " +
							 "If it fails again, send an email to the google group and maybe if you are lucky someone will sort it out.\n\n" +
							 "DFL Manager Admin";
		
		message.setContent(messageBody, "text/plain");
	}
	
	
	// internal testing
	public static void main(String[] args) {
		
		EmailSelectionsHandler testing = new EmailSelectionsHandler();

		try {
			testing.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
