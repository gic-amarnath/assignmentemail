package com.assignmentforemail.pojo;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;

@Data
@Entity
@Table(name="email_data")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class RootMailConfig {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String subject;
	private String emailBody;
	private String emailTo;
	private String emailCC;
	private String emailBCC;
	@CreationTimestamp
	private Date creationDate;//= new Date();
}
