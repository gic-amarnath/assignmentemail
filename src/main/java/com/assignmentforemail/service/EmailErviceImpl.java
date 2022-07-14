package com.assignmentforemail.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignmentforemail.pojo.RootMailConfig;
import com.assignmentforemail.repository.EmailRepository;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class EmailErviceImpl  implements EmailService{
	@Autowired
	EmailRepository emailrepo;
	@Override
	public RootMailConfig saveJson(JsonNode jsonNode) {
		RootMailConfig inputData=new RootMailConfig();
		// TODO Auto-generated method stub
		//System.out.println("jsonNode ::: "+jsonNode);
		inputData.setCreationDate(new Date());
		inputData.setEmailBCC(jsonNode.requiredAt("/emailBCC"));
		inputData.setEmailCC(jsonNode.requiredAt("/emailCC"));
		inputData.setEmailTo(jsonNode.requiredAt("/emailTo"));
		inputData.setSubject(jsonNode.get("subject").textValue());
		inputData.setEmailBody(jsonNode.get("emailBody").textValue());
		return emailrepo.save(inputData);
	}

}
