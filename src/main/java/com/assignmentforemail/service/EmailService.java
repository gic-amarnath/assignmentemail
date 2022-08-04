package com.assignmentforemail.service;


import java.util.List;


import com.assignmentforemail.pojo.RootMailConfig;
import com.fasterxml.jackson.databind.JsonNode;

public interface EmailService {
	public RootMailConfig saveJson(JsonNode jsonNode);
	//public void callAPIWriteDataInExcel(List<RootMailConfig> rootMailConfig);
	public  void writeDataToExcel(List<RootMailConfig> docList);
}
