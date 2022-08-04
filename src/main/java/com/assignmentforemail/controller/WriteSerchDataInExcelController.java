package com.assignmentforemail.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.assignmentforemail.pojo.MailSearchData;
import com.assignmentforemail.pojo.RootMailConfig;
import com.assignmentforemail.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/data")
public class WriteSerchDataInExcelController {
	// public ResponseEntity<RootMailConfig> listOfReqId
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	EmailService emailService1;
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	@PostMapping(value = { "/writeResultInFile/search" }, produces = { "application/json" }, consumes = {"application/json" })
	public String listOfdata(@RequestBody MailSearchData emailSearchReqst)
			throws JsonMappingException, JsonProcessingException {
		if (emailSearchReqst.getEmailBCC() != null) {
			System.out.println("DDDAAATTAAA == " + emailSearchReqst.getEmailBCC());
		}
		String url = "http://localhost:8080/api/data/emailData/search";
		String rewquestDTO = new String();
		try {
			rewquestDTO = (new ObjectMapper()).writeValueAsString(emailSearchReqst);
			System.out.println("rewquestDTO :::: " + rewquestDTO);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<>(rewquestDTO, httpHeaders);
		RestTemplate restTemplate = restTemplateBuilder.build();
		String data=restTemplate.postForObject(url, requestEntity, String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree(data);
		List<RootMailConfig> list = mapper.readValue(data, TypeFactory.defaultInstance().constructCollectionType(List.class, RootMailConfig.class));
		if(list.size()>0) {
		emailService1.writeDataToExcel(list);
		}
		return data;
	}

}
