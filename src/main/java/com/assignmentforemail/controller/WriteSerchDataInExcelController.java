package com.assignmentforemail.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${rest.temp.url}")
	private String url;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	EmailService emailService1;
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;
	//String url = "http://localhost:8080/api/data/emailData/search";
	ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	@PostMapping(value = { "/writeResultInExlFile/search" }, produces = { "application/json" }, consumes = {"application/json" })
	public String listOfdata(@RequestBody MailSearchData emailSearchReqst)
			throws JsonMappingException, JsonProcessingException {
		if (emailSearchReqst.getEmailBCC() != null) {
			System.out.println("DDDAAATTAAA == " + emailSearchReqst.getEmailBCC());
		}

		String rewquestDTO = new String();
		try {
			rewquestDTO = (new ObjectMapper()).writeValueAsString(emailSearchReqst);
			//			System.out.println("rewquestDTO :::: " + rewquestDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<>(rewquestDTO, httpHeaders);
		RestTemplate restTemplate = restTemplateBuilder.build();
		String data=restTemplate.postForObject(url, requestEntity, String.class);
		ObjectMapper mapper = new ObjectMapper();
		//JsonNode actualObj = mapper.readTree(data);
		List<RootMailConfig> list = mapper.readValue(data, TypeFactory.defaultInstance().constructCollectionType(List.class, RootMailConfig.class));
		System.out.println("list.size() ::::: "+list.size());
		if(list.size()>0) {
			emailService1.writeDataToExcel(list);
		}
		return data;
	}


	ObjectMapper mapper1 = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	@PostMapping(value = { "/writeInPDFFile/search" })
	public String listOfDataForPDF(@RequestBody MailSearchData emailSearchReqst) throws JsonMappingException, JsonProcessingException {
		String rewquestDTO = new String();
		try {
			rewquestDTO = (new ObjectMapper()).writeValueAsString(emailSearchReqst);
			//			System.out.println("rewquestDTO :::: " + rewquestDTO);
		} catch (JsonProcessingException e) {
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
		System.out.println("list.size() ::::: "+list.size());
		if(list.size()>0) {
			emailService1.writeDataInToPDF(list);
		}
		return data;
	}


	ObjectMapper mapper3 = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	@PostMapping(value = { "/countByGivenFilter/search" })
	public String countMailByFilter(@RequestBody MailSearchData emailSearchReqst) throws JsonMappingException, JsonProcessingException {
		String rewquestDTO = new String();
		try {
			rewquestDTO = (new ObjectMapper()).writeValueAsString(emailSearchReqst);
			//			System.out.println("rewquestDTO :::: " + rewquestDTO);
		} catch (JsonProcessingException e) {
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
		System.out.println("list.size() ::::: "+list.size());
		String dataSize="0";
		if(list.size()>0) {
			dataSize =""+list.size();
			return "Found " +dataSize+ " data by search criteria";
		}else {
			return "Found " +dataSize+ " data by search criteria";
		}
	}
}
