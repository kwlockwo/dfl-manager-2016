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

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.freeutils.tnef.Attachment;
import net.freeutils.tnef.TNEFInputStream;

public class EmailSelectionsHandler {
	
	private LoggingUtils loggerUtils;

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
		
		loggerUtils = new LoggingUtils("online-logger", "online.name", "Selections");
		
		try {
			globalsService = new GlobalsServiceImpl();
			
			Map<String, String> emailConfig = globalsService.getEmailConfig();
			
			this.dflmngrEmailAddr = emailConfig.get("dflmngrEmailAddr");
			this.incomingMailHost = emailConfig.get("incomingMailHost");
			this.outgoingMailHost = emailConfig.get("outgoingMailHost");
			this.outgoingMailPort = emailConfig.get("outgoingMailPort");
			this.mailUsername = emailConfig.get("mailUsername");
			this.mailPassword = emailConfig.get("mailPassword");
						
			loggerUtils.log("info", "Email config: dflmngrEmailAddr={}; incomingMailHost={}; outgoingMailHost={}; outgoingMailHost={}; mailUsername={}; mailPassword={}",
						dflmngrEmailAddr, incomingMailHost, outgoingMailHost, outgoingMailPort, mailUsername, mailPassword);
			
			configureMail();
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	public void execute() {
		try {
			this.responses = new HashMap<>();

			loggerUtils.log("info", "Email Selections Handler is executing ....");

			processSelections();

			loggerUtils.log("info", "Sending responses");
			
			sendResponses();
			
			globalsService.close();
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
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
		
		Folder inbox = store.getFolder("Selections");
		inbox.open(Folder.READ_WRITE);
		
		Message[] messages = inbox.getMessages();
		
		loggerUtils.log("info", "Opended inbox: messages={}", messages.length);
		
		for(int i = 0; i < messages.length; i++) {
			
			loggerUtils.log("info", "Handling message {}", i);
			
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
								loggerUtils.log("info", "Message from {}, has selection attachment", from);
								String teamCode = handleSelectionFile(part.getInputStream());
								String key = from + ";" + teamCode;
								this.responses.put(key, true);
								loggerUtils.log("info", "Message from {} ... SUCCESS!", from);
							} else if (attachementName.equalsIgnoreCase("WINMAIL.DAT") || attachementName.equalsIgnoreCase("ATT00001.DAT")) {
								loggerUtils.log("info", "Message from {}, is a TNEF message", from);
								String teamCode = handleTNEFMessage(part.getInputStream(), from);
								String key = from + ";" + teamCode;
								this.responses.put(key, true);
								loggerUtils.log("info", "Message from {} ... SUCCESS!", from);
							}
						}
							
					}
				}
				if(this.responses.size() == currentSize) {
					this.responses.put(from, false);
					loggerUtils.log("info", "Message from {} ... FAILURE!", from);
				}
			} catch (Exception ex) {
				loggerUtils.log("error", "Error in ... ", ex);
				try {
					String from =  InternetAddress.toString(messages[i].getFrom());
					this.responses.put(from, false);
					loggerUtils.log("info", "Message from {} ... FAILURE with EXCEPTION!", from);
				} catch (MessagingException ex2) {
					loggerUtils.log("error", "Error in ... ", ex2);
				}
			}
		}
		
		loggerUtils.log("info", "Moving messages to Processed folder");
		Folder processedMessages = store.getFolder("Processed");
		inbox.copyMessages(messages, processedMessages);
		
		for(int i = 0; i < messages.length; i++) {
			messages[i].setFlag(Flags.Flag.DELETED, true);
		}
		
		inbox.expunge();
		
		inbox.close(true);
		store.close();
	}
	
	private String handleTNEFMessage(InputStream inputStream, String from) throws Exception {

		String teamCode = "";
		
		TNEFInputStream tnefInputSteam = new TNEFInputStream(inputStream);
		net.freeutils.tnef.Message message = new net.freeutils.tnef.Message(tnefInputSteam);

		for (Attachment attachment : message.getAttachments()) {
			if (attachment.getNestedMessage() == null) {
				String filename = attachment.getFilename();

				if (filename.equals("selections.txt")) {
					loggerUtils.log("info", "Message from {}, has selection attachment", from);
					teamCode = handleSelectionFile(attachment.getRawData());
				}
			} 
		}
		
		message.close();
		
		return teamCode;
	}
	
	private String handleSelectionFile(InputStream inputStream) throws Exception {
		
		String line = "";
		String teamCode = "";
		int round = 0;
		List<Integer> ins = new ArrayList<>();
		List<Integer> outs = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		
		loggerUtils.log("info", "Moving messages to Processed folder");
		
		while((line = reader.readLine()) != null) {
			
			if(line.equalsIgnoreCase("[team]")) {
				line = reader.readLine();
				teamCode = line;
				loggerUtils.log("info", "Selections for team: {}", teamCode);
			}
			
			if(line.equalsIgnoreCase("[round]")) {
				line = reader.readLine();
				round = Integer.parseInt(line);
				loggerUtils.log("info", "Selections for round: {}", round);
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
				loggerUtils.log("info", "Selection in: {}", ins);
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
				loggerUtils.log("info", "Selection out: {}", outs);
			}		
		}
		
		TeamSelectionLoaderHandler selectionsLoader = new TeamSelectionLoaderHandler();
		selectionsLoader.execute(teamCode, round, ins, outs);
		
		return teamCode;
	}
	
	private void sendResponses() throws Exception {
		
		
		for (Map.Entry<String, Boolean> response : this.responses.entrySet()) {
			
			String key = response.getKey();
			
			String to = "";
			String teamCode = "";
			
			if(key.contains(";")) {
				to = key.split(";")[0];
				teamCode = key.split(";")[1];
			} else {
				to = key;
			}
			
			MimeMessage message = new MimeMessage(this.mailSession);
			message.setFrom(new InternetAddress(this.dflmngrEmailAddr));
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			
			loggerUtils.log("info", "Creating response message: to={}; from={};", to, this.dflmngrEmailAddr);
			
			boolean isSuccess = response.getValue();
			
			if(isSuccess) {
				if(teamCode != null && !teamCode.equals("")) {
					String teamTo = globalsService.getTeamEmail(teamCode);
					loggerUtils.log("info", "Team email: {}", teamTo);
					if(!to.equalsIgnoreCase(teamTo)) {
						loggerUtils.log("info", "Adding team email");
						message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(teamTo));
					}
				}
				loggerUtils.log("info", "Message is for SUCCESS");
				setSuccessMessage(message);
			} else {
				loggerUtils.log("info", "Message is for FAILURE");
				setFailureMessage(message);
			}
						
			loggerUtils.log("info", "Sending message");
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
