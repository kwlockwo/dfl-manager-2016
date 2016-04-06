package net.dflmngr.handlers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FilenameUtils;

import net.dfl.schema.EmailerType;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.utils.EmailUtils;

public class EmailerHandler {
	
	GlobalsService globalsService;
	
	public EmailerHandler() {
		globalsService = new GlobalsServiceImpl();
	}
	
	public void execute() throws Exception {
		
		String emailerRoot = globalsService.getEmailerRoot();
		
		File emailerRootFolder = new File(emailerRoot);
		File[] emailerFiles = emailerRootFolder.listFiles();
		
		File handledDir = new File(emailerRoot + "/sent");
		
		Thread.sleep(5000);
		
		for(File fileEntry : emailerFiles) {
			
			if(fileEntry.isFile()) {
				if(FilenameUtils.isExtension(fileEntry.getName(),"xml")) {
					processEmailerRequest(fileEntry);
					Files.move(fileEntry.toPath(), handledDir.toPath(), StandardCopyOption.ATOMIC_MOVE);
				}
			}	
		}
	}
	
	private void processEmailerRequest(File xmlRequestFile) throws Exception {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(EmailerType.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		EmailerType emailRequest = (EmailerType) jaxbUnmarshaller.unmarshal(xmlRequestFile);
		
		List<String> to = emailRequest.getTo().getAddress();
		String from = emailRequest.getFrom();
		String subject = emailRequest.getSubject();
		String body = emailRequest.getBody();
		List<String> attachments = emailRequest.getAttachments().getAttachement();
		
		
		EmailUtils.sendTextEmail(to, from, subject, body, attachments);	
	}

}
