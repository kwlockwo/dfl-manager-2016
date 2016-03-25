package net.dflmngr.clients;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import net.dflmngr.webservice.CallDflmngrWebservices;

public class LoadSelectionsFromMail {

	String dflEmailAddr;
	String incomingMailHost;
	String outgoingMailHost;
	String mailUsername;
	String mailPassword;
	
	Session mailSession;
	
	public LoadSelectionsFromMail() {
		configureMail();
	}
	
	private void configureMail() {
		Properties properties = System.getProperties();
		properties.setProperty("mail.smpt.host", this.incomingMailHost);
		properties.put("mail.smpt.auth", "true");
		
		this.mailSession = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailUsername, mailPassword);
			}
		});		
	}
	
	private void processSelections() throws Exception {
				
		Store store = this.mailSession.getStore("pop3");
		store.connect(this.incomingMailHost, this.mailUsername, this.mailPassword);
		
		Folder inbox = store.getFolder("Inbox");
		inbox.open(Folder.READ_WRITE);
		
		Message[] messages = inbox.getMessages();
		
		for(int i = 0; i < messages.length; i++) {
			Multipart multipart = (Multipart) messages[i].getContent();
						
			for(int j = 0; j < multipart.getCount(); j++) {
				Part part = multipart.getBodyPart(i);
				
				String disposition = part.getDisposition();

				if (disposition != null && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))) {
					String attachementName = part.getFileName();
					if(attachementName.equals("selections.txt")) {
						handleSelectionFile(part.getInputStream());
					}
				}
			}
		}
		
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		
		while(reader.ready()) {
			line = reader.readLine();
			
			if(line.equalsIgnoreCase("[team]")) {
				line = reader.readLine();
				teamCode = line;
			}
			
			if(line.equalsIgnoreCase("[round]")) {
				line = reader.readLine();
				round = Integer.parseInt(line);
			}
			
			if(line.equalsIgnoreCase("[ins]")) {
				while(reader.ready()) {
					line = reader.readLine();
					if(line.equalsIgnoreCase("[outs]")) {
						break;
					} else if(line.equalsIgnoreCase("")) {
						// ignore blank lines
					} else {
						ins.add(Integer.parseInt(line));
					}
				}
			}
			
			if(line.equalsIgnoreCase("[outs]")) {
				while(reader.ready()) {
					line = reader.readLine();
					if(line.equalsIgnoreCase("[ins]")) {
						break;
					} else if(line.equalsIgnoreCase("")) {
						// ignore blank lines
					} else {
						outs.add(Integer.parseInt(line));
					}
				}				
			}		
		}
		
		CallDflmngrWebservices.loadSelections(teamCode, round, ins, outs, null);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
