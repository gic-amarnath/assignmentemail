package com.assignmentforemail.service;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMessage;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.assignmentforemail.pojo.RootMail;
@Service
public class MailService {
	@Value("${spring.mail.username}")
	private String fromRootData;
	//	private JavaMailSender javaMailSender;
	//	@Autowired
	@Autowired
	private JavaMailSender mailSender;
	//	public MailService(JavaMailSender javaMailSender) {
	//		this.javaMailSender = javaMailSender;
	//	}

	public void sendEmail(RootMail rootMail) {
		ObjectMapper mapper=new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			List<String> holdtoEmail = new ArrayList<>();
			System.out.println("rootMailrootMail == "+rootMail);
//			System.out.println("==== "+rootMail.getEmailTo());
//			List<String> dataToml=rootMail.getEmailTo();
//			List<String> dataToml1=rootMail.getEmailTo();
//			String strin=mapper.writeValueAsString(rootMail.emailTo);
//			System.out.println("strinstrin == "+strin);
//			String result = strin.replaceAll("\\[", " ");
//			String result1 = result.replaceAll("\\]", " ");
//			if(result1.contains(",")) {
//				String arr[]=result1.toString().split(",");
//				for (String string : arr) {
//					holdtoEmail.add(string.replaceAll("''",""));
//					System.out.println("aaaaaaaaa == "+string.replaceAll("[^a-zA-Z0-9]", " "));
//				}
//
//			}
//			System.out.println(result1+" == result == "+result);
//			System.out.println("dataToml8888 >>> "+dataToml1);
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			//System.out.println("lstTo1== ::: "+lstTo1);
			holdtoEmail.add("kumaamar888@gmail.com");
			System.out.println("lstTolstTo ::: "+holdtoEmail);
			if (CollectionUtils.isNotEmpty(holdtoEmail)) {
				helper.setTo(holdtoEmail.stream().toArray(String[]::new));
			}
			helper.setFrom(fromRootData);
			helper.setSubject(rootMail.getSubject());
			helper.setText(rootMail.getEmailBody());
			mailSender.send(message);
		}catch(Exception ec) 
		{
			ec.printStackTrace();
		}
	}
}
