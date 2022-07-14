package com.assignmentforemail.service;

import com.assignmentforemail.pojo.RootMailConfig;
import com.fasterxml.jackson.databind.JsonNode;

public interface EmailService {
	public RootMailConfig saveJson(JsonNode jsonNode);
}
