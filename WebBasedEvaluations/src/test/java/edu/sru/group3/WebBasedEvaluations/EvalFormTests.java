package edu.sru.group3.WebBasedEvaluations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.sru.group3.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.group3.WebBasedEvaluations.evalform.Question;
import edu.sru.group3.WebBasedEvaluations.evalform.Section;
import edu.sru.group3.WebBasedEvaluations.evalform.ComputeRange;

public class EvalFormTests {
	
	private Evaluation eval;
	private int questions;
	
	@BeforeEach
	public void initialize() {
		this.questions = 0;
		
		this.eval = new Evaluation();
		this.eval.setTitle("Title");
		this.eval.setEvalID("EVAL-001");
		this.eval.addComputeRange(new ComputeRange(1.00,1.99,"Rank A"));
		this.eval.addComputeRange(new ComputeRange(2.00,2.99,"Rank B"));
		this.eval.addComputeRange(new ComputeRange(3.00,3.99,"Rank C"));
		this.eval.addComputeRange(new ComputeRange(4.00,4.99,"Rank D"));
		this.eval.addComputeRange(new ComputeRange(5.00,5.00,"Rank E"));
		
		for (int i = 0; i < 5; i++) {
			
			Section section = new Section();
			
			if (i == 0) {
				section.setSecName("Section-Preload");
				
				for (int j = 0; j < 4; j++) {
					Question question = new Question();
					question.setQText("Pre-Load-Question" + j);
					question.setQResponseType("PRE-LOAD");
					section.addQuestion(question);
					this.questions++;
				}
				
			} else {
				
				if(i%2 == 1) {
					section.setSecName("Section Name -" + i);
					section.setSecDescription("This is the section description.");
					section.setSecToolTipVal("This is the section tooltip message.");

					for (int j = 0; j < 4; j++) {
						Question question = new Question();
						question.setQText("Question Text - " + j);
						question.setQResponseType("DROPDOWN");
						question.addOption("3 - OPT C");
						question.addOption("5 - OPT E");
						question.addOption("1 - OPT A");
						question.addOption("4 - OPT D");
						question.addOption("2 - OPT B");
						
						section.addQuestion(question);
						this.questions++;
					}
				} else {
					int num = i - 1;
					section.setSecName("OVERALL: Section Name -" + num);
					Question question = new Question();
					question.setQResponseType("COMPUTE");
					question.setQResponse("SCORING INFORMATION NOT AVAILABLE");
					question.setComputeRanges(this.eval.getComputeRanges());
					section.addQuestion(question);
					this.questions++;
				}
			}
			this.eval.addSection(section);
		}
	}
	
	@Test
	public void testEvalSectionCount() {
		assertEquals(5, this.eval.getSectionCount());
	}
	
	@Test
	public void testEvalQuestionCount() {
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			if (this.eval.getSection(i).getSecName().startsWith("OVERALL: ")) {
				assertEquals(1, this.eval.getSection(i).getQuestionCount());
			} else {
				assertEquals(4, this.eval.getSection(i).getQuestionCount());
			}
		}
	}
	
	@Test
	public void testEvalTotalQuestionCount() {
		int totalQuestions = 0;
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			totalQuestions += this.eval.getSection(i).getQuestionCount();
		}
		assertEquals(this.questions, totalQuestions);
	}
	
	@Test
	public void testSectionHasDropdownQuestions() {
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			
			int dropdowns = 0;
			for (int j = 0; j < this.eval.getSection(i).getQuestionCount(); j++) {
				if (this.eval.getSection(i).getQuestion(j).getQResponseType().equals("DROPDOWN")) {
					dropdowns++;
				}
			}
			if(dropdowns > 0) {
				assertTrue(this.eval.getSection(i).hasDropdownQuestions());
			} else {
				assertFalse(this.eval.getSection(i).hasDropdownQuestions());
			}
			
		}
	}
	
	@Test
	public void testComputeRanges() {
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			for (int j = 0; j < this.eval.getSection(i).getQuestionCount(); j++) {
				if (this.eval.getSection(i).getQuestion(j).getQResponseType().equals("COMPUTE")) {
					assertEquals("NO SCORE AVAILABLE",this.eval.getSection(i).getQuestion(j).computeResultString(0.0));
					assertEquals("NO SCORE AVAILABLE",this.eval.getSection(i).getQuestion(j).computeResultString(-1.0));
					assertEquals("NO SCORE AVAILABLE",this.eval.getSection(i).getQuestion(j).computeResultString(-999999.0));
					assertEquals("NO SCORE AVAILABLE",this.eval.getSection(i).getQuestion(j).computeResultString(0.99));
					assertEquals("Rank A", this.eval.getSection(i).getQuestion(j).computeResultString(1.00));
					assertEquals("Rank A", this.eval.getSection(i).getQuestion(j).computeResultString(1.99));
					assertEquals("Rank B", this.eval.getSection(i).getQuestion(j).computeResultString(2.00));
					assertEquals("Rank B", this.eval.getSection(i).getQuestion(j).computeResultString(2.99));
					assertEquals("Rank C", this.eval.getSection(i).getQuestion(j).computeResultString(3.00));
					assertEquals("Rank C", this.eval.getSection(i).getQuestion(j).computeResultString(3.99));
					assertEquals("Rank D", this.eval.getSection(i).getQuestion(j).computeResultString(4.00));
					assertEquals("Rank D", this.eval.getSection(i).getQuestion(j).computeResultString(4.99));
					assertEquals("Rank E", this.eval.getSection(i).getQuestion(j).computeResultString(5.00));
					assertEquals("NO SCORE AVAILABLE", this.eval.getSection(i).getQuestion(j).computeResultString(5.01));
					assertEquals("NO SCORE AVAILABLE", this.eval.getSection(i).getQuestion(j).computeResultString(9999999));
				}
			}
		}
	}
	
	@Test
	public void testDropdownOptionsMaxPoints() {
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			for (int j = 0; j < this.eval.getSection(i).getQuestionCount(); j++) {
				if (this.eval.getSection(i).getQuestion(j).getQResponseType().equals("DROPDOWN")) {
					assertEquals(5, this.eval.getSection(i).getQuestion(j).getQuestionMaxPoints());
				}
			}
		}
	}
	
	@Test
	public void testDropdownOptionsEarnedPoints() {
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			for (int j = 0; j < this.eval.getSection(i).getQuestionCount(); j++) {
				if (this.eval.getSection(i).getQuestion(j).getQResponseType().equals("DROPDOWN")) {
					
					this.eval.getSection(i).getQuestion(j).setQResponse("1 - OPT A");
					assertEquals(1, this.eval.getSection(i).getQuestion(j).getQuestionEarnedPoints());
					
					this.eval.getSection(i).getQuestion(j).setQResponse("2 - OPT B");
					assertEquals(2, this.eval.getSection(i).getQuestion(j).getQuestionEarnedPoints());
					
					this.eval.getSection(i).getQuestion(j).setQResponse("3 - OPT C");
					assertEquals(3, this.eval.getSection(i).getQuestion(j).getQuestionEarnedPoints());
					
					this.eval.getSection(i).getQuestion(j).setQResponse("4 - OPT D");
					assertEquals(4, this.eval.getSection(i).getQuestion(j).getQuestionEarnedPoints());
					
					this.eval.getSection(i).getQuestion(j).setQResponse("5 - OPT E");
					assertEquals(5, this.eval.getSection(i).getQuestion(j).getQuestionEarnedPoints());
					
					this.eval.getSection(i).getQuestion(j).setQResponse("N/A");
					assertEquals(0, this.eval.getSection(i).getQuestion(j).getQuestionEarnedPoints());
					
					this.eval.getSection(i).getQuestion(j).setQResponse("");
					assertEquals(0, this.eval.getSection(i).getQuestion(j).getQuestionEarnedPoints());
				}
			}
		}
	}
	
	@Test
	public void testSectionScoringWithDropdownsQResponse3() {
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			for (int j = 0; j < this.eval.getSection(i).getQuestionCount(); j++) {
				if (this.eval.getSection(i).getQuestion(j).getQResponseType().equals("DROPDOWN")) {
					this.eval.getSection(i).getQuestion(j).setQResponse("3 - OPT C");
				}
			}
		}
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			if (this.eval.getSection(i).getSecName().startsWith("OVERALL: ")) {
				assertEquals("SCORING INFORMATION NOT AVAILABLE" , this.eval.getSection(i).getQuestion(0).getQResponse());
			}
		}
		
		this.eval.updateCompute();
		
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			if (this.eval.getSection(i).getSecName().startsWith("OVERALL: ")) {
				assertEquals("<span>Points Earned: 12.0/20.0</span><br><span> Resulting Grade: 3.0 - Rank C</span>" , this.eval.getSection(i).getQuestion(0).getQResponse());
			}
		}
	}
	
	@Test
	public void testSectionScoringWithDropdownsQResponseNA() {
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			for (int j = 0; j < this.eval.getSection(i).getQuestionCount(); j++) {
				if (this.eval.getSection(i).getQuestion(j).getQResponseType().equals("DROPDOWN")) {
					this.eval.getSection(i).getQuestion(j).setQResponse("N/A");
				}
			}
		}
		
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			if (this.eval.getSection(i).getSecName().startsWith("OVERALL: ")) {
				assertEquals("SCORING INFORMATION NOT AVAILABLE" , this.eval.getSection(i).getQuestion(0).getQResponse());
			}
		}
		
		this.eval.updateCompute();
		
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			if (this.eval.getSection(i).getSecName().startsWith("OVERALL: ")) {
				assertEquals("<span>Points Earned: 0.0/0.0</span><br><span> Resulting Grade: 0.0 - NO SCORE AVAILABLE</span>" , this.eval.getSection(i).getQuestion(0).getQResponse());
			}
		}
	}
	
	@Test
	public void testSectionScoringWithDropdownsValueInvalidQResponse() {
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			for (int j = 0; j < this.eval.getSection(i).getQuestionCount(); j++) {
				if (this.eval.getSection(i).getQuestion(j).getQResponseType().equals("DROPDOWN")) {
					this.eval.getSection(i).getQuestion(j).setQResponse("6 - OPT F");
				}
			}
		}
		
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			if (this.eval.getSection(i).getSecName().startsWith("OVERALL: ")) {
				assertEquals("SCORING INFORMATION NOT AVAILABLE" , this.eval.getSection(i).getQuestion(0).getQResponse());
			}
		}
		
		this.eval.updateCompute();
		
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			if (this.eval.getSection(i).getSecName().startsWith("OVERALL: ")) {
				assertEquals("<span>Points Earned: 24.0/20.0</span><br><span> SCORING ERROR: More Points earned than possbile </span>" , this.eval.getSection(i).getQuestion(0).getQResponse());
			}
		}
	}
	
	@Test
	public void testSectionTooltipProcessing() {
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			if(this.eval.getSection(i).hasDropdownQuestions()) {
				assertEquals("This is the section description.", this.eval.getSection(i).getSecDescription());
				this.eval.getSection(i).setSecToolTip("section");
				assertEquals("This is the section description.", this.eval.getSection(i).getSecDescription());
			}
		}
		
		this.eval.processToolTips();
		
		for (int i = 0; i < this.eval.getSectionCount(); i++) {
			if(this.eval.getSection(i).hasDropdownQuestions()) {
				assertEquals("This is the <span class=\"dashed-underline\" title=\"This is the section tooltip message.\">section</span> description.", this.eval.getSection(i).getSecDescription());
			}
		}
	}
}