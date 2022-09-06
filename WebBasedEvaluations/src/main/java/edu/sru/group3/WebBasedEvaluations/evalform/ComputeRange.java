package edu.sru.group3.WebBasedEvaluations.evalform;

import java.io.Serializable;

/**
 * Contains a range of min and max values which correspond to a given range name.
 * Used in an Evaluation for determining what grade to give for a score.
 * 
 * @author Logan Racer
 */
public class ComputeRange implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String rangeName;
	private double rangeValMin;
	private double rangeValMax;
	
	// Constructors
	public ComputeRange() {
		this.rangeName = "";
		this.rangeValMin = 0.0;
		this.rangeValMax = 1.0;
	}
	
	public ComputeRange(double min, double max, String name) {
		this.rangeName = name;
		this.rangeValMin = min;
		this.rangeValMax = max;
	}

	//Setters and getters
	public String getRangeName() {
		return this.rangeName;
	}

	public void setRangeName(String rangeName) {
		this.rangeName = rangeName;
	}

	public double getRangeValMin() {
		return this.rangeValMin;
	}

	public void setRangeValMin(double rangeValMin) {
		this.rangeValMin = rangeValMin;
	}

	public double getRangeValMax() {
		return this.rangeValMax;
	}

	public void setRangeValMax(double rangeValMax) {
		this.rangeValMax = rangeValMax;
	}
}