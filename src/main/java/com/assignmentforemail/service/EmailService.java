package com.assignmentforemail.service;


import com.assignmentforemail.pojo.RootMailConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.util.ArrayList;

public interface EmailService {
	public RootMailConfig saveJson(JsonNode jsonNode);
	public ArrayList<RootMailConfig> findByEmailId(String byEmailId);
}
