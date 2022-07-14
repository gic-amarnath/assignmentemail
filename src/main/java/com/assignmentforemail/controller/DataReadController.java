package com.assignmentforemail.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignmentforemail.pojo.RootMail;
import com.assignmentforemail.pojo.RootMailConfig;
import com.assignmentforemail.repository.EmailRepository;
import com.assignmentforemail.service.EmailService;
import com.assignmentforemail.service.MailService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/data")
public class DataReadController {
	ObjectMapper mapper=new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	@Autowired
	EmailRepository emailrepo;
	@Autowired
	EmailService emailService;
	@Autowired
	private MailService notificationService;
	@PostMapping(path="/savemail")
	public RootMail saveJson(@RequestBody RootMail rootData) {
		JsonNode root1=null;
		try {
			String strin=mapper.writeValueAsString(rootData);
			root1=mapper.readValue(strin, JsonNode.class);
			//	mapper.createArrayNode()
			System.out.println("root1 :::: >> "+root1);
			List<JsonNode> emailToList = StreamSupport.stream(root1.get("emailTo").spliterator(), false).collect(Collectors.toList());
			//System.out.println("emailTo :::: >> "+emailToList);
			List<JsonNode> emailCCList = StreamSupport.stream(root1.get("emailCC").spliterator(), false).collect(Collectors.toList());
			//System.out.println("emailCC :::: >> "+emailCCList);
			List<JsonNode> emailBCCList = StreamSupport.stream(root1.get("emailBCC").spliterator(), false).collect(Collectors.toList());
			String holdsub=root1.get("subject").textValue();
			String emlbody=root1.get("emailBody").textValue();
			boolean retVal=validate(emailToList,holdsub,emlbody);
			if(retVal) {
				emailService.saveJson(root1);
				List toemail=new ArrayList<>();
				List toCc=new ArrayList<>();
				List bCc=new ArrayList<>();
				for(JsonNode emailToList1 : emailToList) {
					toemail.add(emailToList1);
				}
				for(JsonNode emailCCList1 : emailCCList) {
					toCc.add(emailCCList1);
				}
				for(JsonNode emailBCCList1 : emailBCCList) {
					bCc.add(emailBCCList);
				}
				rootData.setEmailTo(toemail);
				rootData.setEmailCC(toCc);
				rootData.setEmailBCC(bCc);
				rootData.setSubject(strin);
				rootData.setEmailBody(strin);
				notificationService.sendEmail(rootData);

			}

		}catch(Exception e) {
			e.printStackTrace();
		}
		//jsonServiceProvider.saveJson(root1);
		return rootData;
	}

	public boolean validate(List<JsonNode> emailToList,String holdsub,String emlbody) {
		boolean ret=false;
		if(emailToList.size()>0 && !holdsub.isEmpty() && !emlbody.isEmpty()) {
			ret=true;
		}
		return ret;
	}

}
