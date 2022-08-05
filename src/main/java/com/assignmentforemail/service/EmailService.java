package com.assignmentforemail.service;


import java.util.List;


import com.assignmentforemail.pojo.RootMailConfig;
import com.fasterxml.jackson.databind.JsonNode;

public interface EmailService {
	public RootMailConfig saveJson(JsonNode jsonNode);
	public  void writeDataToExcel(List<RootMailConfig> docList);
	public  void writeDataInToPDF(List<RootMailConfig> listForPdf);
	public  void countEmalByFilter(List<RootMailConfig> listForPdf);
	
	
}
