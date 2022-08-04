package com.assignmentforemail.pojo;

import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class MailSearchData {
	private LocalDate createDate;
	private String subject;
	private JsonNode emailTo;
	private JsonNode emailCC;
	private JsonNode emailBCC;
}
