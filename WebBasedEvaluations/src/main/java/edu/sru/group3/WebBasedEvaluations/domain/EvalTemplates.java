package edu.sru.group3.WebBasedEvaluations.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;


/**
 * Class used to hold uploaded Evaluation Templates
 * 
 * @author Logan Racer, Tanuj Rane
 */
@Entity
public class EvalTemplates {

	@Id
	String name;
	@Lob
	byte[] eval;
	@Lob
	byte[] excelFile;
	
	// Constructors
	public EvalTemplates() {
		this.name = null;
		this.eval = null;
		this.excelFile = null;
	}
	
	public EvalTemplates(String name, byte[] eval, byte[] excelFile) {
		this.name = name;
		this.eval = eval;
		this.excelFile = excelFile;
	}
	
	public EvalTemplates(String name, byte[] eval) {
		this.name = name;
		this.eval = eval;
		this.excelFile = null;
	}

	// Setters and Getters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getEval() {
		return eval;
	}

	public void setEval(byte[] eval) {
		this.eval = eval;
	}
	
	public byte[] getExcelFile() {
		return this.excelFile;
	}

	public void setExcelFile(byte[] excelFile) {
		this.excelFile = excelFile;
	}
	
}
