package com.assignmentforemail.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignmentforemail.pojo.RootMailConfig;
import com.assignmentforemail.repository.EmailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Service
public class EmailErviceImpl  implements EmailService{
	ObjectMapper mapper=new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	@Autowired
	EmailRepository emailrepo;
	@Override
	public RootMailConfig saveJson(JsonNode jsonNode) {
		RootMailConfig inputData=new RootMailConfig();
		// TODO Auto-generated method stub
		//System.out.println("jsonNode ::: "+jsonNode);

		inputData.setEmailBCC(jsonNode.requiredAt("/emailBCC"));
		inputData.setEmailCC(jsonNode.requiredAt("/emailCC"));
		inputData.setEmailTo(jsonNode.requiredAt("/emailTo"));
		inputData.setSubject(jsonNode.get("subject").textValue());
		inputData.setEmailBody(jsonNode.get("emailBody").textValue());
		return emailrepo.save(inputData);
	}
	@Override
	public ArrayList<RootMailConfig> findByEmailId(String byEmailId) {
		System.out.println("byEmailId ::: "+byEmailId);
		ArrayList<RootMailConfig> retLst=new ArrayList();
		List<RootMailConfig> emlList=emailrepo.findAll();
		if(!emlList.isEmpty()) {
			for (RootMailConfig rootMailConfig : emlList) {
				Iterable<JsonNode> jnod=rootMailConfig.getEmailTo();
				String dd=jnod.toString();
				if(dd.contains(byEmailId)) {
					retLst.add(rootMailConfig);
				}
			}
			System.out.println("find by email list Size ::: "+retLst.size());
		}
		
		return retLst;
	}

}
