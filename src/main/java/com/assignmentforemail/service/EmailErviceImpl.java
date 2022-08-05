package com.assignmentforemail.service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.assignmentforemail.pojo.RootMailConfig;
import com.assignmentforemail.repository.EmailRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class EmailErviceImpl  implements EmailService{
	ObjectMapper mapper=new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	@Autowired
	EmailRepository emailrepo;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${excelfile.save.path}")
	private String excelfilePath;
	@Value("${pdf.file.save.path}")
	private String pDFFilePath;
	@Override
	public RootMailConfig saveJson(JsonNode jsonNode) {
		RootMailConfig inputData=new RootMailConfig();
		String delim = ",";
		// TODO Auto-generated method stub
		//inputData.setEmailBCC(jsonNode.requiredAt("/emailBCC"));

		ArrayNode toBCCjn=(ArrayNode)jsonNode.requiredAt("/emailBCC");
		String toemlBCC=null;
		List<String> toEmlBCC=new ArrayList<>() ;
		for (JsonNode jsonNode4 : toBCCjn) {
			try {
				if(jsonNode4!=null) {
					toemlBCC=jsonNode4.asText();
					toEmlBCC.add(toemlBCC);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String resToBCC = String.join(delim, toEmlBCC);
		inputData.setEmailBCC(resToBCC);

		ArrayNode toCCjn=(ArrayNode)jsonNode.requiredAt("/emailCC");
		String toemlCC=null;
		List<String> toEmlCC=new ArrayList<>() ;
		for (JsonNode jsonNode3 : toCCjn) {
			try {
				if(jsonNode3!=null) {
					toemlCC=jsonNode3.asText();
					toEmlCC.add(toemlCC);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String resToCC = String.join(delim, toEmlCC);
		inputData.setEmailCC(resToCC);
		//inputData.setEmailCC(jsonNode.requiredAt("/emailCC"));
		ArrayNode jn=(ArrayNode)jsonNode.requiredAt("/emailTo");
		String toeml=null;
		List<String> toEmllstt=new ArrayList<>() ;
		for (JsonNode jsonNode2 : jn) {
			try {
				if(jsonNode2!=null) {
					toeml=jsonNode2.asText();
					toEmllstt.add(toeml);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String res = String.join(delim, toEmllstt);
		inputData.setEmailTo(res);
		//inputData.setEmailTo(jsonNode.requiredAt("/emailTo"));
		inputData.setSubject(jsonNode.get("subject").textValue());
		inputData.setEmailBody(jsonNode.get("emailBody").textValue());
		inputData.setCreationDate(new Date());
		return emailrepo.save(inputData);
	}
	@Override
	public void writeDataToExcel(List<RootMailConfig> docList) {
		// TODO Auto-generated method stub
		XSSFWorkbook workbook = new XSSFWorkbook(); 

		//Create a blank sheet
		XSSFSheet sheet = workbook.createSheet("Search_Data_Sheet");
		Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
		int i=0;
		try {
			data.put(0, new Object[] {"ID","SUBJECT", "EMAIL_BODY", "EMAIL_TO", "EMAIL_CC", "EMAIL_BCC", "CREATION_DATE"});

			for (RootMailConfig rootMailConfig : docList) {
				int ID = ++i;
				String _id =""+rootMailConfig.getId();
				data.put(ID, new Object[] {rootMailConfig.getId(),rootMailConfig.getSubject(),rootMailConfig.getEmailBody(),rootMailConfig.getEmailTo(),rootMailConfig.getEmailCC(),rootMailConfig.getEmailBCC(),rootMailConfig.getCreationDate()});
			}
			Set<Integer> keyset = data.keySet();
			int rownum = 0;
			for (Integer key : keyset)
			{
				Row row = sheet.createRow(rownum++);
				Object [] objArr = data.get(key);
				int cellnum = 0;
				for (Object obj : objArr)
				{
					Cell cell = row.createCell(cellnum++);
					if(obj instanceof String)
						cell.setCellValue((String)obj);
					else if(obj instanceof Integer)
						cell.setCellValue((Integer)obj);
					else if(obj instanceof Date)
						cell.setCellValue(obj.toString());
				}
			}

			try
			{
				FileOutputStream out = new FileOutputStream(new File(excelfilePath));
				workbook.write(out);
				out.close();
				workbook.close();
				System.out.println("----Write Data in Excel Completed---------- ");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Override
	public void writeDataInToPDF(List<RootMailConfig> listForPdf) {
		// TODO Auto-generated method stub
		//String path1 = "D:\\ExcelFileWrite\\SearchDataInPdf.pdf";
		Document document = new Document();
		List<RootMailConfig> listForPdfdub=listForPdf;
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pDFFilePath));
			document.open();
			PdfPTable table = new PdfPTable(7);
			table.setWidthPercentage(100); //Width 100%
			table.setSpacingBefore(10f); //Space before table
			table.setSpacingAfter(10f);
			addTableHeader(table);
			addRows(table,listForPdfdub);
			//addCustomRows(table);
			document.add(table);
			document.close();
		} catch (FileNotFoundException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void addTableHeader(PdfPTable table) {
		Stream.of("ID","SUBJECT", "EML_BODY", "EMAIL_TO", "EMAIL_CC", "EMAIL_BCC", "DATE")
		.forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.RED);
			header.setBorderWidth(1);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});
	}

	private void addRows(PdfPTable table,List<RootMailConfig> listForPdfdub) {
		//table.addCell("row 1, col 1,row 1, col 1,row 1, col 1,row 1 ");
		PdfPCell cell=null;
		for (RootMailConfig listpdf: listForPdfdub) {
			//PdfPCell cell;
			cell = new PdfPCell(new Phrase(""+listpdf.getId()));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(listpdf.getSubject()));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(listpdf.getEmailBody()));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(listpdf.getEmailTo()));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(listpdf.getEmailCC()));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(listpdf.getEmailBCC()));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+listpdf.getCreationDate()));
			table.addCell(cell);
			//table.addCell(cell);
		}
		table.addCell(cell);
	}
	@Override
	public void countEmalByFilter(List<RootMailConfig> listForPdf) {
		// TODO Auto-generated method stub
		
	}


}
