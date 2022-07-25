package com.assignmentforemail.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.regex.*;    
import java.util.*;  
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignmentforemail.pojo.RootMail;
import com.assignmentforemail.pojo.RootMailConfig;
import com.assignmentforemail.repository.EmailRepository;
import com.assignmentforemail.service.EmailService;
import com.assignmentforemail.service.MailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/data")
public class DataReadController {
	ObjectMapper mapper=new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	@Autowired
	EmailRepository emailrepo;
	@Autowired
	EmailService emailService;
	@Autowired
	EmailService ems;
	@Autowired
	private MailService notificationService;
	@PostMapping(path="/savemail")
	public RootMail saveJson(@RequestBody RootMail rootData) {
		String msg=null;
		JsonNode root1=null;
		try {
			String strin=mapper.writeValueAsString(rootData);
			root1=mapper.readValue(strin, JsonNode.class);
			//List<JsonNode> datasets = StreamSupport.stream(root1.get("emailTo").spliterator(), false).collect(Collectors.toList());
			ArrayList<String> emailTo1 =new ObjectMapper().convertValue(mapper.readTree(strin).get("emailTo"), ArrayList.class);
			ArrayList<String> emailCC1 =new ObjectMapper().convertValue(mapper.readTree(strin).get("emailCC"), ArrayList.class);
			ArrayList<String> emailBCC1 =new ObjectMapper().convertValue(mapper.readTree(strin).get("emailBCC"), ArrayList.class);
			String holdsub=root1.get("subject").textValue();
			String emlbody=root1.get("emailBody").textValue();
			boolean retVal=validate(emailTo1,holdsub,emlbody);
			System.out.println("emailTo1 :::: "+emailTo1);
			System.out.println("validate >>>>> "+retVal);
			//boolean emlVldt=validateEmailId(emailTo1,emailCC1,emailBCC1);
			if(retVal) {
				boolean emlVldt=validateEmailId(emailTo1,emailCC1,emailBCC1);
				if(emlVldt){
					emailService.saveJson(root1);
					//msg=notificationService.sendEmail(emailTo1,emailCC1,emailBCC1,holdsub,emlbody);
					notificationService.sendEmail(emailTo1,emailCC1,emailBCC1,holdsub,emlbody);
				}

			}

		}catch(Exception e) {
			e.printStackTrace();
		}
		return rootData;
	}

	//fetch Data based on emailCreation Date
	@GetMapping(path = "findByDate/{emailDate}")
	public ResponseEntity <List<RootMailConfig>> getDataByDate(@RequestParam(value = "emailDate") String emailDate) throws ParseException{
		System.out.println("------------In Api---emailDate--------- "+emailDate);
		LocalDate emailDate1 = LocalDate.parse(emailDate);
		return new ResponseEntity<List<RootMailConfig>>((List<RootMailConfig>) emailrepo.findByEmailDate(emailDate1), HttpStatus.OK);
	}

	//fetch Data based on subject
	@GetMapping(path = "findBySubject/{bySubject}")
	public ResponseEntity <List<RootMailConfig>> getDataBySubject(@RequestParam(name = "bySubject") String bySubjectText) throws ParseException{
		System.out.println("------------In Api--bySubject---------- "+bySubjectText);
		List<RootMailConfig> data=emailrepo.findBySubject(bySubjectText);
		System.out.println("data >>>> "+data);
		return new ResponseEntity<List<RootMailConfig>>((List<RootMailConfig>) emailrepo.findBySubject(bySubjectText), HttpStatus.OK);
	}

	@GetMapping(path = "findByEmail/{byEmail}")
	public ResponseEntity <List<RootMailConfig>> getDataByEmail(@RequestParam(name = "byEmail") String byEmail){
		System.out.println("------------In Api--byEmail---------- "+byEmail);
		List<RootMailConfig> data=(List<RootMailConfig>) emailService.findByEmailId(byEmail);
		return new ResponseEntity<List<RootMailConfig>>(data, HttpStatus.OK);
	}

	public boolean validate(List emailToList,String holdsub,String emlbody) {
		boolean ret=false;
		if(emailToList.size()>0 && !holdsub.isEmpty() && !emlbody.isEmpty()) {
			ret=true;
		}
		return ret;
	}

	public boolean validateEmailId(List<String> emailToList,List ccEmail,List bccEmail) {
		boolean retuVar=false;
		ArrayList<String> holdEmalLst=new ArrayList<String>();
		ArrayList<String> finlEmalLst=new ArrayList<String>();
		holdEmalLst.addAll(ccEmail);
		holdEmalLst.addAll(bccEmail);
		holdEmalLst.addAll(emailToList);
		System.out.println(holdEmalLst.size()+ " = holdEmalLst :: "+holdEmalLst.toString());
		String regex =  "^[A-Za-z0-9+_.-]+@(.+)$";
		Pattern pattern = Pattern.compile(regex);
		for(String email : holdEmalLst){
			Matcher matcher = pattern.matcher(email);  
			System.out.println(email +" : "+ matcher.matches()+"\n");  
			boolean b=matcher.matches();
			if(b) {
				finlEmalLst.add(email);
			}

		}
		System.out.println(finlEmalLst.size()+ " = finlEmalLst :: "+finlEmalLst.toString());
		if(holdEmalLst.equals(finlEmalLst)==true) {
			retuVar=true;
		}
		return retuVar;

	}

}
