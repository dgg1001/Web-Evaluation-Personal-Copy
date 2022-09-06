package edu.sru.group3.WebBasedEvaluations.service;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Provides automated email sending abilities.
 * Date: 4/21/2022
 * @author Dalton Stenzel
 *
 */
@Service
public class EmailSenderService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	
	/**Method for sending a simple email.
	 * @param email is a String value that holds the receiving email address.
	 * @param body is a String value that holds the body of the email.
	 * @param subject is a String value that holds the subject of the email.
	 * @throws MessagingException 
	 */
	public void sendSimpleEmail(String email, String body, String subject) throws MessagingException {
		
		
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom("spring.web.eval@gmail.com");
		message.setTo(email);
		message.setText(body);
		message.setSubject(subject);
		
		mailSender.send(message);
		
		//System.out.println("Mail send");
		/*
		MimeMessage message = mailSender.createMimeMessage();
	     
	    MimeMessageHelper helper = new MimeMessageHelper(message, true);
	    
	    helper.setFrom("spring.web.eval@gmail.com");
	    helper.setTo(email);
	    helper.setSubject(subject);
	    helper.setText(body);
	        
	    FileSystemResource file 
	      = new FileSystemResource(new File("src\\main\\resources\\logs\\logs.txt"));
	    helper.addAttachment("Invoice", file);*/

	    //mailSender.send(message);
		
		
	}

}
