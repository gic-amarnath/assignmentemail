package com.assignmentforemail.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

import com.assignmentforemail.pojo.MailSearchData;
import com.assignmentforemail.pojo.RootMail;
import com.assignmentforemail.pojo.RootMailConfig;
import com.assignmentforemail.repository.EmailRepository;
import com.assignmentforemail.service.EmailService;
import com.assignmentforemail.service.MailService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/data")
public class DataReadController {
	ObjectMapper mapper=new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	public static final String DATE_PATTERN="yyyy-MM-dd";
	@Autowired
	private EmailRepository emailRepository;
	@Autowired
	EmailService emailService1;
	@Autowired
	EmailService ems;
	@Autowired
	private MailService notificationService;
	@Autowired
	private EntityManager entityManager;
	@PostMapping(path="/savemail")
	public RootMail saveJson(@RequestBody RootMail rootData) {
		String msg=null;
		JsonNode root1=null;
		try {
			String strin=mapper.writeValueAsString(rootData);
			root1=mapper.readValue(strin, JsonNode.class);
			JsonNode valuesNode = mapper.readTree(strin).get("emailTo");
			List<String> emailTo = new ArrayList<>();
			for (JsonNode node : valuesNode) {
				emailTo.add(node.asText());
			}
			JsonNode valuesNode1 = mapper.readTree(strin).get("emailCC");
			List<String> emailCC = new ArrayList<>();
			for (JsonNode node : valuesNode1) {
				emailCC.add(node.asText());
			}
			JsonNode valuesNode2 = mapper.readTree(strin).get("emailBCC");
			List<String> emailBCC = new ArrayList<>();
			for (JsonNode node : valuesNode2) {
				emailBCC.add(node.asText());
			}
			String holdsub=root1.get("subject").textValue();
			String emlbody=root1.get("emailBody").textValue();
			boolean retVal=validate(emailTo,holdsub,emlbody);
			//			System.out.println("emailTo1 :::: "+emailTo);
			//			System.out.println("validate >>>>> "+retVal);
			if(retVal) {
				boolean emlVldt=validateEmailId(emailTo,emailCC,emailBCC);
				if(emlVldt){
					emailService1.saveJson(root1);//save data into Db
					notificationService.sendEmail(emailTo,emailCC,emailBCC,holdsub,emlbody);//send mail
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
		List<RootMailConfig> data= emailRepository.findByEmailDate(emailDate1);
		if(data.size()>0) {return new ResponseEntity<List<RootMailConfig>>(data, HttpStatus.OK);}else {
			return new ResponseEntity<List<RootMailConfig>>(data, HttpStatus.NOT_FOUND);
		}
	}

	//fetch Data based on subject
	@GetMapping(path = "findBySubject/{bySubject}")
	public ResponseEntity <List<RootMailConfig>> getDataBySubject(@RequestParam(name = "bySubject") String bySubjectText) throws ParseException{
		System.out.println("------------In Api--bySubject---------- "+bySubjectText);
		List<RootMailConfig> data=emailRepository.findBySubject(bySubjectText);
		System.out.println("data >>>> "+data);
		if(data.size()>0) {
			return new ResponseEntity<List<RootMailConfig>>(data, HttpStatus.OK);
		}else {
			return new ResponseEntity<List<RootMailConfig>>(data, HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/emailData/search")
	public ResponseEntity<List<RootMailConfig>> getAllMailDataSearch(@RequestBody MailSearchData emailSearchReqst) {
		System.out.println("--------------Multi Search API---------------------- ");
		boolean isFilter = false;
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<RootMailConfig> criteriaQuery = criteriaBuilder.createQuery(RootMailConfig.class);
		Root<RootMailConfig> itemRoot = criteriaQuery.from(RootMailConfig.class);
		List<Predicate> predicates = new ArrayList<>();
		List<RootMailConfig> rootMailConfig = new ArrayList<>();
		if (StringUtils.isNotBlank(emailSearchReqst.getSubject())) {
			predicates.add(criteriaBuilder.like(criteriaBuilder.lower(itemRoot.get("subject")),"%"+emailSearchReqst.getSubject().toLowerCase()+"%"));
			isFilter = true;
		}

		if (emailSearchReqst.getCreateDate()!= null) {
			System.out.println("DDDDDD = "+emailSearchReqst.getCreateDate());	
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			LocalDate ldate=emailSearchReqst.getCreateDate();
			Date todayWithZeroTime = null;
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(itemRoot.get("creationDate"), ldate));
			isFilter = true;
		}

		if (emailSearchReqst.getEmailTo()!=null && !emailSearchReqst.getEmailTo().isEmpty()) {
			System.out.println("emmll >>> "+emailSearchReqst.getEmailTo());
			String toEmlname=emailSearchReqst.getEmailTo().get(0).asText().trim();
			System.out.println("namenamename ::: "+toEmlname);
			if(StringUtils.isNotEmpty(toEmlname)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(itemRoot.get("emailTo")),"%"+toEmlname.toLowerCase()+"%"));
				isFilter = true;
			}
			//			predicates.add(criteriaBuilder.like(criteriaBuilder.lower(itemRoot.get("emailTo")),"%"+toEmlname.toLowerCase()+"%"));
			//			isFilter = true;
		}

		if (emailSearchReqst.getEmailCC()!=null && !emailSearchReqst.getEmailCC().isEmpty()) {
			System.out.println("toEmlCCname >>> "+emailSearchReqst.getEmailCC());
			String toEmlCCname=emailSearchReqst.getEmailCC().get(0).asText().trim();
			//			System.out.println("namenamename ::: "+toEmlCCname);
			if(StringUtils.isNotEmpty(toEmlCCname)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(itemRoot.get("emailCC")),"%"+toEmlCCname.toLowerCase()+"%"));
				isFilter = true;
			}
		}

		if (emailSearchReqst.getEmailBCC()!=null && !emailSearchReqst.getEmailBCC().isEmpty()) {
			System.out.println("toEmlBCCname >>> "+emailSearchReqst.getEmailBCC());
			String toEmlBCCname=emailSearchReqst.getEmailBCC().get(0).asText().trim();
			//			System.out.println("namenamename ::: "+toEmlBCCname);
			if(StringUtils.isNotEmpty(toEmlBCCname)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(itemRoot.get("emailBCC")),"%"+toEmlBCCname.toLowerCase()+"%"));
				isFilter = true;
			}
		}

		if (isFilter) {
			criteriaQuery.where(predicates.toArray(new Predicate[0]));
			rootMailConfig = entityManager.createQuery(criteriaQuery).getResultList();
			//			ems.callAPIWriteDataInExcel(rootMailConfig);
		}
		System.out.println("Size=== " + rootMailConfig.size());

		return new ResponseEntity<>(rootMailConfig, HttpStatus.OK);
	}


	public boolean validate(List<String> emailToList,String holdsub,String emlbody) {
		boolean ret=false;
		if(emailToList.size()>0 && !holdsub.isEmpty() && !emlbody.isEmpty()) {
			ret=true;
		}
		return ret;
	}

	public boolean validateEmailId(List<String> emailToList,List<String> ccEmail,List<String> bccEmail) {
		boolean retuVar=false;
		ArrayList<String> holdEmalLst=new ArrayList<String>();
		ArrayList<String> finlEmalLst=new ArrayList<String>();
		holdEmalLst.addAll(ccEmail);
		holdEmalLst.addAll(bccEmail);
		holdEmalLst.addAll(emailToList);
		//		System.out.println(holdEmalLst.size()+ " = holdEmalLst :: "+holdEmalLst.toString());
		String regex =  "^[A-Za-z0-9+_.-]+@(.+)$";
		Pattern pattern = Pattern.compile(regex);
		for(String email : holdEmalLst){
			Matcher matcher = pattern.matcher(email);  
			//			System.out.println(email +" : "+ matcher.matches()+"\n");  
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
