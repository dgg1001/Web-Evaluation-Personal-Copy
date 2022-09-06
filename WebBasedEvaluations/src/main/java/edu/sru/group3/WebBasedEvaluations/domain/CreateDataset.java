package edu.sru.group3.WebBasedEvaluations.domain;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import edu.sru.group3.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.group3.WebBasedEvaluations.evalform.Section;

/**
 * CreateDataset class creates datasets to be used in chart generation
 * @author antho
 *
 */
public class CreateDataset 
{
/**
 * static pie dataset for testing
 * @return dataset filled with test values
 */
	public DefaultPieDataset createTestPieDataset()
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Male", 10);
		dataset.setValue("Female", 5);
		return dataset;
	}
/**
 * static category data set for testing	
 * @return data set filled with test values
 */
	public DefaultCategoryDataset createTestDataset()
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.setValue(10, "test", "gggggg");
		dataset.setValue(5, "test 2", "test 2");
		return dataset;
	}
/**
 * Receives completed evaluations and generates a pie data set to be used for chart generation and PDF reports
 * @param completedEvals receives all completed evaluations for a specific reviewee
 * @return datasetFinal data set filled with average scores of each section
 */
	public DefaultPieDataset createPieDataset(List<Evaluation> completedEvals)
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		DefaultPieDataset datasetFinal = new DefaultPieDataset();
		
		Evaluation evalArray = completedEvals.get(0);
		
		Evaluation eval;
		
		int secCountArray = evalArray.getSectionCount();
		int numOfSec = 0;
		
		double[] sectionTotals = new double[8];
		double[] sectionTotalsFinal = new double[8];
		double[] sectionTotalsAverage = new double[8];
		String[] sectionName = new String[8];
		
		for (int x = 0; x < completedEvals.size(); x++)
		{
			eval = completedEvals.get(x);

		double totals = 0;
		double overallStatus = 0;
		int numOfScoreSec = 0;
	
		int secCount = eval.getSectionCount();
		int count = 0;

		
		while(count < secCount)
		{
			Section sec = eval.getSection(count);
			
			for (int i = 0; i < sec.getQuestionCount(); i++)
			{
				if(sec.getQuestion(i).getQResponseType().equals("COMPUTE"))
				{
					String name = sec.getSecName();
					
					if(name.contains("OVERALL:"))
					{
						name = name.replace("OVERALL: ", "");
						
						Section parent = eval.getSectionByName(name);
						
						if(parent.hasDropdownQuestions())
						{
							
						
							dataset.setValue(parent.getSecName(), parent.getSectionScore());
							
							numOfSec++;
							totals += parent.getSectionScore();
						}
		
					
					}
				}
					
			}
		
		
		count ++;
		
		}
		
		//Get array of all eval scores and add sections
		for(int i = 0; i < dataset.getItemCount(); i++)
				{
					
					sectionTotals[i] = (double) dataset.getValue(i);
					sectionTotalsFinal[i] = sectionTotalsFinal[i] + sectionTotals[i];
					
				}
		
		
		}
		
		//Get section names and place in array
		for(int i = 0; i < dataset.getItemCount(); i++)
		{
			
			sectionName[i] = (String) dataset.getKey(i);
		
		}
		
		
		//Find average of each section and create dataset
	for(int i = 0; i < sectionTotals.length; i++)
		{
			sectionTotalsAverage[i] = sectionTotalsFinal[i] / completedEvals.size();
			datasetFinal.setValue(sectionName[i], sectionTotalsAverage[i]);
		
		}
	
	
	
	return datasetFinal;
}
/**
 * Receives completed evaluations and generates a category data set to be used for chart generation and PDF reports
 * @param completedEvals receives all completed evaluations for a specific reviewee
 * @return datasetFinal data set filled with average scores of each section
 */
	public DefaultCategoryDataset createDefaultDataset(List<Evaluation> completedEvals)
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		DefaultCategoryDataset datasetFinal = new DefaultCategoryDataset();
		
		double[] sectionTotals = new double[8];
		double[] sectionTotalsFinal = new double[8];
		double[] sectionTotalsAverage = new double[8];
		String[] sectionName = new String[8];
		Evaluation eval;
		
		for (int x = 0; x < completedEvals.size(); x++)
		{
			eval = completedEvals.get(x);
		
		double totals = 0;
		double overallStatus = 0;
		int numOfScoreSec = 0;
		
		
		int secCount = eval.getSectionCount();
		int count = 0;
		
		while(count < secCount)
		{
			Section sec = eval.getSection(count);
			
			for (int i = 0; i < sec.getQuestionCount(); i++)
			{
				if(sec.getQuestion(i).getQResponseType().equals("COMPUTE"))
				{
					String name = sec.getSecName();
					
					if(name.contains("OVERALL:"))
					{
						name = name.replace("OVERALL: ", "");
						
						Section parent = eval.getSectionByName(name);
						
						if(parent.hasDropdownQuestions())
						{
							
							dataset.setValue(parent.getSectionScore(), parent.getSecName(), "");
							
							numOfScoreSec++;
							totals += parent.getSectionScore();
						}
		
					
					}
				}
					
			}
		
		
		count ++;
		
		}
		
		//Get section names and place in array
				for(int i = 0; i < dataset.getRowCount(); i++)
				{
					
					sectionName[i] = (String) dataset.getRowKey(i);
				
				}
		
		for(int i = 0; i < dataset.getRowCount(); i++)
		{
			
			sectionTotals[i] = (double) dataset.getValue(sectionName[i], "");
			sectionTotalsFinal[i] = sectionTotalsFinal[i] + sectionTotals[i];
			
		}
		
		//Find average of each section and create dataset
		for(int i = 0; i < sectionTotals.length; i++)
			{
				sectionTotalsAverage[i] = sectionTotalsFinal[i] / completedEvals.size();
				datasetFinal.setValue(sectionTotalsAverage[i], sectionName[i],"");
			
			}


}
		
		
		
		
	return datasetFinal;

}
/**
 * Receives a specific completed evaluation and generates a pie data set to be used for individual chart generation and PDF reports
 * @param completedEvals receives specific completed evaluation for a specific reviewee
 * @return dataset returns data set to be used in chart generation
 */
	public DefaultPieDataset createPieDatasetIndividual(List<Evaluation> completedEvals)
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		Evaluation eval;
		
		for (int x = 0; x < completedEvals.size(); x++)
		{
			eval = completedEvals.get(x);
		
		int secCount = eval.getSectionCount();
		int numOfSec = 0;
		int count = 0;
		System.out.println(eval.getSectionCount());
		
		while(count < secCount)
		{
			Section sec = eval.getSection(count);
			if(sec.getQuestionCount() > 0)
			{
				for(int i = 0; i < sec.getQuestionCount(); i++)
				{
					if(sec.getQuestion(i).getQResponseType().equalsIgnoreCase("COMPUTE"))
					{
						String name = sec.getSecName();
						
						if(name.contains("OVERALL:"))
						{
							name = name.replace("OVERALL: ", "");
							
							Section parent = eval.getSectionByName(name);
							
							if(parent.hasDropdownQuestions())
							{
								dataset.setValue(parent.getSecName(), parent.getSectionScore());
							}
						}
						
						
					}
				}

			}
			count++;
		}
		
		
		}
		
		
	
		return dataset;
		
	}
/**
 * Receives a specific completed evaluation and generates a category data set to be used for individual chart generation and PDF reports
 * @param completedEvals receives specific completed evaluation for a specific reviewee
 * @return dataset returns data set to be used in chart generation
 */
	public DefaultCategoryDataset createDefaultDatasetIndividual(List<Evaluation> completedEvals)
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		Evaluation eval;
		
		for (int x = 0; x < completedEvals.size(); x++)
		{
			eval = completedEvals.get(x);
		
		int secCount = eval.getSectionCount();
		int numOfSec = 0;
		int count = 0;
		System.out.println(eval.getSectionCount());
		
		while(count < secCount)
		{
			Section sec = eval.getSection(count);
			if(sec.getQuestionCount() > 0)
			{
				for(int i = 0; i < sec.getQuestionCount(); i++)
				{
					if(sec.getQuestion(i).getQResponseType().equalsIgnoreCase("COMPUTE"))
					{
						String name = sec.getSecName();
						
						if(name.contains("OVERALL:"))
						{
							name = name.replace("OVERALL: ", "");
							
							Section parent = eval.getSectionByName(name);
							
							if(parent.hasDropdownQuestions())
							{
								dataset.setValue(parent.getSectionScore(), parent.getSecName(), "");
							}
						}
						
						
					}
				}

			}
			count++;
		}
		
		
		}
		
		
	
		return dataset;	
	}
	
	
	
	/**
	 * Receives completed evaluations and generates a pie data set to be used for group chart generation and PDF reports
	 * @param completedEvals receives all evaluations for a group and finds the average
	 * @return datasetFinal returns data set to be used in chart and PDF generation
	 */
	
	public DefaultPieDataset createPieDatasetGroup(List<Evaluation> completedEvals)
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		DefaultPieDataset datasetFinal = new DefaultPieDataset();
		
		Evaluation eval;
		
		
		int numOfSec = 0;
		
		double[] sectionTotals = new double[8];
		double[] sectionTotalsFinal = new double[8];
		double[] sectionTotalsAverage = new double[8];
		String[] sectionName = new String[8];
		
		for (int x = 0; x < completedEvals.size(); x++)
		{
			eval = completedEvals.get(x);

		double totals = 0;
		double overallStatus = 0;
		int numOfScoreSec = 0;
	
		int secCount = eval.getSectionCount();
		int count = 0;

		
		while(count < secCount)
		{
			Section sec = eval.getSection(count);
			
			for (int i = 0; i < sec.getQuestionCount(); i++)
			{
				if(sec.getQuestion(i).getQResponseType().equals("COMPUTE"))
				{
					String name = sec.getSecName();
					
					if(name.contains("OVERALL:"))
					{
						name = name.replace("OVERALL: ", "");
						
						Section parent = eval.getSectionByName(name);
						
						if(parent.hasDropdownQuestions())
						{
							
						
							dataset.setValue(parent.getSecName(), parent.getSectionScore());
							
							numOfSec++;
							totals += parent.getSectionScore();
						}
		
					
					}
				}
					
			}
		
		
		count ++;
		
		}
		
		//Get array of all eval scores and add sections
		for(int i = 0; i < dataset.getItemCount(); i++)
				{
					
					sectionTotals[i] = (double) dataset.getValue(i);
					sectionTotalsFinal[i] = sectionTotalsFinal[i] + sectionTotals[i];
				}
		
		
		}
		
		//Get section names and place in array
		for(int i = 0; i < dataset.getItemCount(); i++)
		{
			
			sectionName[i] = (String) dataset.getKey(i);
		
		}
		
		
		//Find average of each section and create dataset
	for(int i = 0; i < sectionTotals.length; i++)
		{
			sectionTotalsAverage[i] = sectionTotalsFinal[i] / completedEvals.size();
			datasetFinal.setValue(sectionName[i], sectionTotalsAverage[i]);
		
		}
	
	
	
	return datasetFinal;
}
	
/**
 * Receives completed evaluation and generates a category data set to be used for group chart generation and PDF reports
 * @param completedEvals receives all evaluations for a group and finds the average
 * @return datasetFinal returns data set to be used in chart and PDF generation
 */
	public DefaultCategoryDataset createDefaultDatasetGroup(List<Evaluation> completedEvals)
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		DefaultCategoryDataset datasetFinal = new DefaultCategoryDataset();
		
		double[] sectionTotals = new double[8];
		double[] sectionTotalsFinal = new double[8];
		double[] sectionTotalsAverage = new double[8];
		String[] sectionName = new String[8];
		Evaluation eval;
		
		for (int x = 0; x < completedEvals.size(); x++)
		{
			eval = completedEvals.get(x);
		
		double totals = 0;
		double overallStatus = 0;
		int numOfScoreSec = 0;
		
		
		int secCount = eval.getSectionCount();
		int count = 0;
		
		while(count < secCount)
		{
			Section sec = eval.getSection(count);
			
			for (int i = 0; i < sec.getQuestionCount(); i++)
			{
				if(sec.getQuestion(i).getQResponseType().equals("COMPUTE"))
				{
					String name = sec.getSecName();
					
					if(name.contains("OVERALL:"))
					{
						name = name.replace("OVERALL: ", "");
						
						Section parent = eval.getSectionByName(name);
						
						if(parent.hasDropdownQuestions())
						{

							
							dataset.setValue(parent.getSectionScore(), parent.getSecName(), "");
							
							numOfScoreSec++;
							totals += parent.getSectionScore();

							
						}
		
					
					}
				}
					
			}
		
		
		count ++;
		
		}
		
		//Get section names and place in array
				for(int i = 0; i < dataset.getRowCount(); i++)
				{
					
					sectionName[i] = (String) dataset.getRowKey(i);
				
				}
		
		for(int i = 0; i < dataset.getRowCount(); i++)
		{
			
			sectionTotals[i] = (double) dataset.getValue(sectionName[i], "");
			sectionTotalsFinal[i] = sectionTotalsFinal[i] + sectionTotals[i];
			
		}
		
		//Find average of each section and create dataset
		for(int i = 0; i < sectionTotals.length; i++)
			{
				sectionTotalsAverage[i] = sectionTotalsFinal[i] / completedEvals.size();
				datasetFinal.setValue(sectionTotalsAverage[i], sectionName[i],"");
			
			}


}
		
		
		
		
	return datasetFinal;

}
	

/**
 * Receives completed self evaluation and generates a pie data set to be used for self evaluation chart generation and PDF reports
 * @param completedEvals receives completed self evaluation log 
 * @return data set returns a pie data set to be used in self evaluation chart generation
 */
	public DefaultPieDataset createPieDatasetSelf(List<Evaluation> completedEvals)
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		Evaluation eval;
		
		for (int x = 0; x < completedEvals.size(); x++)
		{
			eval = completedEvals.get(x);
		
		int secCount = eval.getSectionCount();
		int numOfSec = 0;
		int count = 0;
		System.out.println(eval.getSectionCount());
		
		while(count < secCount)
		{
			Section sec = eval.getSection(count);
			if(sec.getQuestionCount() > 0)
			{
				for(int i = 0; i < sec.getQuestionCount(); i++)
				{
					if(sec.getQuestion(i).getQResponseType().equalsIgnoreCase("COMPUTE"))
					{
						String name = sec.getSecName();
						
						if(name.contains("OVERALL:"))
						{
							name = name.replace("OVERALL: ", "");
							
							Section parent = eval.getSectionByName(name);
							
							if(parent.hasDropdownQuestions())
							{
								dataset.setValue(parent.getSecName(), parent.getSectionScore());
							}
						}
						
						
					}
				}

			}
			count++;
		}
		
		
		}
		
		
	
		return dataset;
		
	}
	
	
	
}
