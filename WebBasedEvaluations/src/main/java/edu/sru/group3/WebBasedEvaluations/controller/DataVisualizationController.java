package edu.sru.group3.WebBasedEvaluations.controller;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import edu.sru.group3.WebBasedEvaluations.domain.CreateDataset;
import edu.sru.group3.WebBasedEvaluations.domain.EvalRole;
import edu.sru.group3.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.domain.Reviewee;
import edu.sru.group3.WebBasedEvaluations.domain.SelfEvaluation;
import edu.sru.group3.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.group3.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.group3.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;
import edu.sru.group3.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.group3.WebBasedEvaluations.repository.SelfEvaluationRepository;

/*Resources
 * https://stackoverflow.com/questions/36171659/cast-defaultcategorydataset-to-categorydataset
 * https://zetcode.com/articles/javaservletchart/
 * https://www.jfree.org/jfreechart/javadoc/org/jfree/data/category/CategoryDataset.html
 * https://www.tutorialspoint.com/h2_database/h2_database_jdbc_connection.htm
 * https://www.tutorialspoint.com/itext/itext_setting_position_of_image.htm
 * https://www.mysamplecode.com/2012/10/create-table-pdf-java-and-itext.html
 * https://www.baeldung.com/spring-thymeleaf-conditionals
 * https://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#removing-template-fragments
 * https://www.jfree.org/forum/viewtopic.php?t=13708
 * https://getbootstrap.com/docs/5.0/components/button-group/
 * https://blog.hubspot.com/website/html-dropdown
 * https://social.msdn.microsoft.com/Forums/en-US/79d6b97c-bb57-4735-ae3f-369febf89c9b/how-to-conditionally-enabledisable-button-control-using-javascript-?forum=asphtmlcssjavascript
 * https://www.baeldung.com/spring-data-jpa-query
 * https://www.baeldung.com/hibernate-select-all
 * https://www.jfree.org/forum/viewtopic.php?t=20190
 * 
 * 
 */



/**
 * Data visualization controller, maps all chart and report generating code
 * @author antho
 *
 */
@Controller
public class DataVisualizationController 
{
	

	private UserRepository userRepository;
	private EvaluationRepository evalFormRepo;
	private EvaluatorRepository evaluatorRepository;
	private EvaluationLogRepository evaluationLogRepository;
	private GroupRepository groupRepository;
	private RevieweeRepository revieweeRepository;
	private SelfEvaluationRepository selfEvalRepo;
	
	private Logger log = LoggerFactory.getLogger(DataVisualizationController.class);

	
public DataVisualizationController(UserRepository userRepository, EvaluationRepository evalFormRepo, EvaluatorRepository evaluatorRepository,EvaluationLogRepository evaluationLogRepository,GroupRepository groupRepository, EvalRoleRepository roleRepository,  RevieweeRepository revieweeRepository, SelfEvaluationRepository selfEvalRepo)
{
	this.evaluationLogRepository = evaluationLogRepository;
	this.groupRepository = groupRepository;
	this.revieweeRepository = revieweeRepository;
	this.selfEvalRepo = selfEvalRepo;
}
/**
 * showSQLPage receives the id path variable and maps to the charts html page. returns chartGeneration
 * @param id Path Variable receives group id to find all the users with that group id
 * @param model model attributes for reviewee repository, evaluation log, and role
 * @return /chartGeneration Returns the chart generation page where charts and pdf reports can be made from evaluations
 */

	@RequestMapping({"/charts/{id}"})
	public String showSQLPage(@PathVariable("id") long id, Model model)
	{
		Group group = groupRepository.findById(id);
		model.addAttribute("group", group);
		
		List<Reviewee> rev = revieweeRepository.findBygroup_Id(id);
		
		List<EvaluationLog> elog = (List<EvaluationLog>) evaluationLogRepository.findAll();
		EvalRole role = new EvalRole();
		
		
		model.addAttribute("role", role);
		model.addAttribute("rev", rev);
		model.addAttribute("elog", elog);
		
		
		return "chartGeneration";
	}

	/**
	 * Receives the reviewee id as path variable and finds all logs of all completed evaluations for that reviewee, then generates pie charts using the average for that reviewee	
	 * @param id Path Variable id receives reviewee id to find the completed logs linked to that id
	 * @param response HttpServletResponse needed to display charts on webpage
	 * @throws IOException provides information on various IO exceptions produced by failed or interrupted IO operations
	 * @throws SQLException provides information on a database access error or other SQL errors
	 */

	@RequestMapping({"/piechartGenerator/{id}"})
	public void handlePieChartSQL(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
		//Get chart name
		String chartName = revieweeRepository.findNameById(id);
		
		CreateDataset dataset = new CreateDataset();
		DefaultPieDataset pieDataset = new DefaultPieDataset();
	
		//Get Completed Eval
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		
		new ArrayList<EvaluationLog>();
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		for (int i = 0; i < evalLogs.size(); i++)
		{
			if(evalLogs.get(i).getReviewee().getId() == id && evalLogs.get(i).getCompleted())
			{
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				System.out.println(completeEval.getEvalID());
	
					completedEvals.add(completeEval);			
			}
		}
		
		
			pieDataset = dataset.createPieDataset(completedEvals);
		
		
		
		JFreeChart pieChart = ChartFactory.createPieChart(chartName + " Evaluation" , pieDataset);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, pieChart, 600, 600);	
		
	
		
		 
	}

/**
 * Receives the reviewee id as path variable and finds all logs of all completed evaluations for that reviewee, then generates ring charts using the average for that reviewee	
 * @param id Path Variable id receives reviewee id to find the completed logs linked to that id
 * @param response HttpServletResponse needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by failed or interrupted IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
 
	@RequestMapping({"/ringchartGenerator/{id}"})
	public void handleRingChartSQL(@PathVariable("id") long id, HttpServletResponse response) throws IOException, SQLException
	{
		String chartName = revieweeRepository.findNameById(id);
		CreateDataset dataset = new CreateDataset();
		DefaultPieDataset pieDataset = new DefaultPieDataset();
	
		//Get Completed Eval
				List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
				
				new ArrayList<EvaluationLog>();
				
				List<Evaluation> completedEvals = new ArrayList<Evaluation>();
				
				byte[] evalLogByte = null;
				
				for (int i = 0; i < evalLogs.size(); i++)
				{
					if(evalLogs.get(i).getReviewee().getId() == id && evalLogs.get(i).getCompleted())
					{
						evalLogByte = evalLogs.get(i).getPath();
						Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
						System.out.println(completeEval.getEvalID());
			
							completedEvals.add(completeEval);			
					}
				}
	
			pieDataset = dataset.createPieDataset(completedEvals);
		
		
		JFreeChart ringChart = ChartFactory.createRingChart(chartName + " Evaluation" ,pieDataset, false, false, false);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, ringChart, 600, 600);	
	}
/**
 * Receives the reviewee id as path variable and finds all logs of all completed evaluations for that reviewee, then generates bar charts using the average for that reviewee	
 * @param id Path Variable id receives reviewee id to find the completed logs linked to that id
 * @param response HttpServletResponse needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by failed or interrupted IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/barchartGenerator/{id}"})
	public void handlebarChartSQL(@PathVariable("id") long id, HttpServletResponse response) throws IOException, SQLException
	{
		String chartName = revieweeRepository.findNameById(id);
		
		CreateDataset dataset = new CreateDataset();
		DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
	
		//Get Completed Eval
				List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
				
				new ArrayList<EvaluationLog>();
				
				List<Evaluation> completedEvals = new ArrayList<Evaluation>();
				
				byte[] evalLogByte = null;
				
				for (int i = 0; i < evalLogs.size(); i++)
				{
					if(evalLogs.get(i).getReviewee().getId() == id && evalLogs.get(i).getCompleted())
					{
						evalLogByte = evalLogs.get(i).getPath();
						Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
						System.out.println(completeEval.getEvalID());
			
							completedEvals.add(completeEval);			
					}
				}
		
			categoryDataset = dataset.createDefaultDataset(completedEvals);

		JFreeChart barChart = ChartFactory.createBarChart(chartName + " Evaluation" ,"Category", "Score", categoryDataset);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, barChart, 600, 600);	
	}
/**
 *Receives the reviewee id as path variable and finds all logs of all completed evaluations for that reviewee, then generates area charts using the average for that reviewee	
 * @param id Path Variable id receives reviewee id to find the completed logs linked to that id
 * @param response HttpServletResponse needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by failed or interrupted IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/areachartGenerator/{id}"})
	public void handleareaChartSQL(@PathVariable("id") long id,HttpServletResponse response) throws IOException, SQLException
	{
		String chartName = revieweeRepository.findNameById(id);
		CreateDataset dataset = new CreateDataset();
		DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
	
		//Get Completed Eval
				List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
				
				new ArrayList<EvaluationLog>();
				
				List<Evaluation> completedEvals = new ArrayList<Evaluation>();
				
				byte[] evalLogByte = null;
				
				for (int i = 0; i < evalLogs.size(); i++)
				{
					if(evalLogs.get(i).getReviewee().getId() == id && evalLogs.get(i).getCompleted())
					{
						evalLogByte = evalLogs.get(i).getPath();
						Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
						System.out.println(completeEval.getEvalID());
			
							completedEvals.add(completeEval);			
					}
				}
		
	
			categoryDataset = dataset.createDefaultDataset(completedEvals);

		
		JFreeChart areaChart = ChartFactory.createAreaChart(chartName + " Evaluation" + " Evaluation" ,"Category", "Score", categoryDataset);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, areaChart, 600, 600);	
	}

/**
 * Receives the log id as path variable and finds all logs of all completed evaluations for that reviewee, then generates pie charts for that reviewee	
 * @param id Path variable Id receives log id to find specific completed evaluations of that user
 * @param response HttpServletResponse is needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by failed or interrupted IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/piechartGeneratorIndividual/{id}"})
	public void handlePieChartSQLIndividual(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
	
		CreateDataset dataset = new CreateDataset();
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		String chartName = null;
		
	
		//Get Completed Eval
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		for (int i = 0; i < evalLogs.size();i++) 
		{
			if(evalLogs.get(i).getCompleted() && evalLogs.get(i).getId() == id)
			{
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				completedEvals.add(completeEval);
				
				 chartName = evalLogs.get(i).getReviewee().getName();
				
			}
		}
		
		
		
		pieDataset = dataset.createPieDatasetIndividual(completedEvals);
	
		JFreeChart pieChart = ChartFactory.createPieChart(chartName + " Evaluation" , pieDataset);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, pieChart, 600, 600);	
		
	}
/**
 * Receives the log id as path variable and finds all logs of all completed evaluations for that reviewee, then generates ring charts for that reviewee	
 * @param id Path variable Id receives log id to find specific completed evaluations of that user
 * @param response HttpServletResponse is needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by failed or interrupted IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
 
	@RequestMapping({"/ringchartGeneratorIndividual/{id}"})
	public void handleRingChartSQLIndividual(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
	
		CreateDataset dataset = new CreateDataset();
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		String chartName = null;
		
	
		//Get Completed Eval
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		for (int i = 0; i < evalLogs.size();i++) 
		{
			if(evalLogs.get(i).getCompleted() && evalLogs.get(i).getId() == id)
			{
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				
				completedEvals.add(completeEval);
				chartName = evalLogs.get(i).getReviewee().getName();
				
			}
		}
		
		
		
		pieDataset = dataset.createPieDatasetIndividual(completedEvals);
	
	
		JFreeChart pieChart = ChartFactory.createRingChart(chartName + " Evaluation" ,pieDataset, false, false, false);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, pieChart, 600, 600);	
		
	
		
		 
	}
/**
 *Receives the log id as path variable and finds all logs of all completed evaluations for that reviewee, then generates bar charts for that reviewee	
 * @param id Path variable Id receives log id to find specific completed evaluations of that user
 * @param response HttpServletResponse is needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by failed or interrupted IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/barchartGeneratorIndividual/{id}"})
	public void handleBarChartSQLIndividual(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
	
		DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
		CreateDataset dataset = new CreateDataset();
		
		
		String chartName = null;
		
	
		//Get Completed Eval
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		for (int i = 0; i < evalLogs.size();i++) 
		{
			if(evalLogs.get(i).getCompleted() && evalLogs.get(i).getId() == id)
			{
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				
				completedEvals.add(completeEval);
				chartName = evalLogs.get(i).getReviewee().getName();
				
			}
		}
		
		
		
		barDataset = dataset.createDefaultDatasetIndividual(completedEvals);
		
	
		JFreeChart pieChart = ChartFactory.createBarChart(chartName + " Evaluation" , "","", barDataset);
		
		
	
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, pieChart, 600, 600);	
		
		
		 
	}
/**
 * Receives the log id as path variable and finds all logs of all completed evaluations for that reviewee, then generates area charts for that reviewee	
 * @param id Path variable Id receives log id to find specific completed evaluations of that user
 * @param response HttpServletResponse is needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by failed or interrupted IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/areachartGeneratorIndividual/{id}"})
	public void handleBarAreaSQLIndividual(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
	
		DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
		CreateDataset dataset = new CreateDataset();
		
		String chartName = null;
		
	
		//Get Completed Eval
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		for (int i = 0; i < evalLogs.size();i++) 
		{
			if(evalLogs.get(i).getCompleted() && evalLogs.get(i).getId() == id)
			{
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				
				completedEvals.add(completeEval);
				chartName = evalLogs.get(i).getReviewee().getName();
				
			}
		}
		
		
		
		barDataset = dataset.createDefaultDatasetIndividual(completedEvals);
		
	
		JFreeChart pieChart = ChartFactory.createAreaChart(chartName + " Evaluation" ,"Category", "Score", barDataset);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, pieChart, 600, 600);
		
		
	}
	
/**
 * Receives the group id as path variable and finds all logs of completed evaluations for every group member and finds the average score of the group to display in a pie chart
 * @param id Path Variable id receives group id to find logs of all completed evaluations in the group
 * @param response HttpServletResponse is needed to display the charts on the webpage
 * @throws IOException provides information on various IO exceptions produced by interrupted or failed IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/piechartGeneratorGroup/{id}"})
	public void handlePieChartGroupSQL(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
		//Get chart name
		String chartName = revieweeRepository.findGroupById(id);
		long groupNum = 0;
	
		
		CreateDataset dataset = new CreateDataset();
		DefaultPieDataset pieDataset = new DefaultPieDataset();


		//Get Completed Eval
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		
		new ArrayList<EvaluationLog>();
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		
		
		
		byte[] evalLogByte = null;
		
		//find groupNum
		for (int i = 0; i < evalLogs.size(); i++)
		{
			if(evalLogs.get(i).getReviewee().getId() == id && evalLogs.get(i).getCompleted())
			{
				groupNum = evalLogs.get(i).getReviewee().getGroup().getId();
			}
		}
		
		
		
		//Find all completed evals in groupNum
		for (int i = 0; i < evalLogs.size(); i++)
		{

			if(evalLogs.get(i).getReviewee().getGroup().getId() == groupNum && evalLogs.get(i).getCompleted())
			{
				
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				completedEvals.add(completeEval);
							
			}
		}
		
		for(int i = 0; i < completedEvals.size(); i++)
		{
			System.out.println(completedEvals.get(i));
		}
	
			pieDataset = dataset.createPieDatasetGroup(completedEvals);
			
		JFreeChart pieChart = ChartFactory.createPieChart("Group " + chartName + " Evaluation" , pieDataset);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, pieChart, 600, 600);	
	
	}

/**
 *Receives the group id as path variable and finds all logs of completed evaluations for every group member and finds the average score of the group to display in a ring chart
 * @param id Path Variable id receives group id to find logs of all completed evaluations in the group
 * @param response HttpServletResponse is needed to display the charts on the webpage
 * @throws IOException provides information on various IO exceptions produced by interrupted or failed IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/ringchartGeneratorGroup/{id}"})
	public void handleRingChartGroupSQL(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
		//Get chart name
		String chartName = revieweeRepository.findGroupById(id);
		long groupNum = 0;
	
		
		CreateDataset dataset = new CreateDataset();
		DefaultPieDataset pieDataset = new DefaultPieDataset();


		//Get Completed Eval
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		
		new ArrayList<EvaluationLog>();
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		//find groupNum
		for (int i = 0; i < evalLogs.size(); i++)
		{
			if(evalLogs.get(i).getReviewee().getId() == id && evalLogs.get(i).getCompleted())
			{
				groupNum = evalLogs.get(i).getReviewee().getGroup().getId();
			}
		}
		
		
		//Find all completed evals in groupNum
		for (int i = 0; i < evalLogs.size(); i++)
		{

			if(evalLogs.get(i).getReviewee().getGroup().getId() == groupNum && evalLogs.get(i).getCompleted())
			{
				
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				completedEvals.add(completeEval);
							
			}
		}
		
		for(int i = 0; i < completedEvals.size(); i++)
		{
			System.out.println(completedEvals.get(i));
		}
	
			pieDataset = dataset.createPieDatasetGroup(completedEvals);
			
		JFreeChart ringChart = ChartFactory.createRingChart("Group " + chartName + " Evaluation" ,pieDataset, false, false, false);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, ringChart, 600, 600);	
	 
	}
	
/**
 * Receives the group id as path variable and finds all logs of completed evaluations for every group member and finds the average score of the group to display in a bar chart
 * @param id Path Variable id receives group id to find logs of all completed evaluations in the group
 * @param response HttpServletResponse is needed to display the charts on the webpage
 * @throws IOException provides information on various IO exceptions produced by interrupted or failed IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/barchartGeneratorGroup/{id}"})
	public void handleBarChartGroupSQL(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
		//Get chart name
		String chartName = revieweeRepository.findGroupById(id);
		long groupNum = 0;
	
		
		CreateDataset dataset = new CreateDataset();
		DefaultCategoryDataset barDataset = new DefaultCategoryDataset();


		//Get Completed Eval
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		
		new ArrayList<EvaluationLog>();
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		//find groupNum
		for (int i = 0; i < evalLogs.size(); i++)
		{
			if(evalLogs.get(i).getReviewee().getId() == id && evalLogs.get(i).getCompleted())
			{
				groupNum = evalLogs.get(i).getReviewee().getGroup().getId();
			}
		}
		
		
		//Find all completed evals in groupNum
		for (int i = 0; i < evalLogs.size(); i++)
		{

			if(evalLogs.get(i).getReviewee().getGroup().getId() == groupNum && evalLogs.get(i).getCompleted())
			{
				
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				completedEvals.add(completeEval);
							
			}
		}
		
		for(int i = 0; i < completedEvals.size(); i++)
		{
			System.out.println(completedEvals.get(i));
		}
	
			barDataset = dataset.createDefaultDatasetGroup(completedEvals);
			
		JFreeChart areaChart = ChartFactory.createBarChart("Group " + chartName + " Evaluation" , "","", barDataset);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, areaChart, 600, 600);	
	 
	}
/**
 * Receives the group id as path variable and finds all logs of completed evaluations for every group member and finds the average score of the group to display in a area chart
 * @param id Path Variable id receives group id to find logs of all completed evaluations in the group
 * @param response HttpServletResponse is needed to display the charts on the webpage
 * @throws IOException provides information on various IO exceptions produced by interrupted or failed IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/areachartGeneratorGroup/{id}"})
	public void handleareaChartGroupSQL(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
		//Get chart name
		String chartName = revieweeRepository.findGroupById(id);
		long groupNum = 0;
	
		
		CreateDataset dataset = new CreateDataset();
		DefaultCategoryDataset barDataset = new DefaultCategoryDataset();


		//Get Completed Eval
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		
		new ArrayList<EvaluationLog>();
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		//find groupNum
		for (int i = 0; i < evalLogs.size(); i++)
		{
			if(evalLogs.get(i).getReviewee().getId() == id && evalLogs.get(i).getCompleted())
			{
				groupNum = evalLogs.get(i).getReviewee().getGroup().getId();
			}
		}
		
		
		//Find all completed evals in groupNum
		for (int i = 0; i < evalLogs.size(); i++)
		{

			if(evalLogs.get(i).getReviewee().getGroup().getId() == groupNum && evalLogs.get(i).getCompleted())
			{
				
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				completedEvals.add(completeEval);
							
			}
		}
		
		for(int i = 0; i < completedEvals.size(); i++)
		{
			System.out.println(completedEvals.get(i));
		}
	
			barDataset = dataset.createDefaultDatasetGroup(completedEvals);
			
		JFreeChart areaChart = ChartFactory.createAreaChart("Group " + chartName + " Evaluation" + " Evaluation" ,"Category", "Score", barDataset);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, areaChart, 600, 600);	
	 
	}

/**
 * Receives the revieweeId and pulls the self evaluation logs for that reviewee and generates pie charts
 * @param id Path Variable id receives revieweeId to find self evaluation logs for completed self evaluations
 * @param response HttpServletResponse is needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by interrupted or failed IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/piechartGeneratorSelf/{id}"})
	public void handlePieChartSelf(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{

		CreateDataset dataset = new CreateDataset();
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		String chartName = selfEvalRepo.findByRevieweeUser_Id(id).getReviewee().getName();
		

		//Get Completed Eval and Deserealize
		SelfEvaluation selfLogs = selfEvalRepo.findByRevieweeUser_Id(id);
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		evalLogByte = selfLogs.getPath();
		
		Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
		
		completedEvals.add(completeEval);
			
		pieDataset = dataset.createPieDatasetIndividual(completedEvals);
		
		JFreeChart pieChart = ChartFactory.createPieChart(chartName + " Self Evaluation" , pieDataset, true, true, true);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, pieChart, 600, 600);	
	}

/**
 * Recieves the revieweeId and pulls the self evaluation logs for that reviewee and generates ring charts
 * @param id Path Variable id receives revieweeId to find self evaluation logs for completed self evaluations
 * @param response HttpServletResponse is needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by interrupted or failed IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/ringchartGeneratorSelf/{id}"})
	public void handleRingChartSelf(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{

		CreateDataset dataset = new CreateDataset();
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		String chartName = selfEvalRepo.findByRevieweeUser_Id(id).getReviewee().getName();
		

		//Get Completed Eval and Deserealize
		SelfEvaluation selfLogs = selfEvalRepo.findByRevieweeUser_Id(id);
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		evalLogByte = selfLogs.getPath();
		
		Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
		
		completedEvals.add(completeEval);
			
		pieDataset = dataset.createPieDatasetIndividual(completedEvals);
		
		JFreeChart pieChart = ChartFactory.createRingChart(chartName + " Self Evaluation" , pieDataset, true, true, true);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, pieChart, 600, 600);	
	}
/**
 * Recieves the revieweeId and pulls the self evaluation logs for that reviewee and generates bar charts	
 * @param id Path Variable id receives revieweeId to find self evaluation logs for completed self evaluations
 * @param response HttpServletResponse is needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by interrupted or failed IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/barchartGeneratorSelf/{id}"})
	public void handleBarChartSelf(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
	
		DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
		CreateDataset dataset = new CreateDataset();
		
		String chartName = selfEvalRepo.findByRevieweeUser_Id(id).getReviewee().getName();
		
	
		//Get Completed Eval
		SelfEvaluation selfLogs = selfEvalRepo.findByRevieweeUser_Id(id);
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		evalLogByte = selfLogs.getPath();
		
		Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
		
		completedEvals.add(completeEval);
		
		barDataset = dataset.createDefaultDatasetIndividual(completedEvals);
		
	
		JFreeChart pieChart = ChartFactory.createBarChart(chartName + " Self Evaluation" , "","", barDataset);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, pieChart, 600, 600);	
		
	}
/**
 * Recieves the revieweeId and pulls the self evaluation logs for that reviewee and generates area charts	
 * @param id Path Variable id receives revieweeId to find self evaluation logs for completed self evaluations
 * @param response HttpServletResponse is needed to display charts on webpage
 * @throws IOException provides information on various IO exceptions produced by interrupted or failed IO operations
 * @throws SQLException provides information on a database access error or other SQL errors
 */
	@RequestMapping({"/areachartGeneratorSelf/{id}"})
	public void handleAreaChartSelf(@PathVariable("id") long id, HttpServletResponse response, Model model) throws IOException, SQLException
	{
	
		DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
		CreateDataset dataset = new CreateDataset();
		
		String chartName = selfEvalRepo.findByRevieweeUser_Id(id).getReviewee().getName();
		
	
		//Get Completed Eval
		SelfEvaluation selfLogs = selfEvalRepo.findByRevieweeUser_Id(id);
		
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		evalLogByte = selfLogs.getPath();
		
		Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
		
		completedEvals.add(completeEval);
		
		barDataset = dataset.createDefaultDatasetIndividual(completedEvals);
		
	
		JFreeChart pieChart = ChartFactory.createAreaChart(chartName + " Self Evaluation" , "","", barDataset);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		ChartUtilities.writeChartAsJPEG(out, pieChart, 600, 600);	
		
	}
	
	
	/**
	 * Receives the group id and pulls logs for every level of evaluation for every reviewee and generates a pdf report saved to the project folder based on the group averages
	 * @param id Path Variable id receives reviewee id  
	 * @param response HttpServletResponse is needed to display on webpage
	 * @param request HttpServletRequest is needed to redirect back to current group page
	 * @return returns responseEntity<>
	 * @throws IOException provides information on various IO exceptions produced by interrupted or failed IO operations
	 * @throws SQLException provides information on a database access error or other SQL errors
	 */
	@GetMapping({"/generatePdf/{id}"})
	public ResponseEntity<Resource> handlePdfReports(@PathVariable("id") long id, HttpServletResponse response, Model model, HttpServletRequest request) throws IOException, SQLException
	{

		//Get chart name
		String chartName = revieweeRepository.findGroupById(id);
		long groupNum = 0;


		CreateDataset dataset = new CreateDataset();
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();


		//Get Completed Eval
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();

		new ArrayList<EvaluationLog>();

		List<Evaluation> completedEvals = new ArrayList<Evaluation>();




		byte[] evalLogByte = null;

		//find groupNum
		for (int i = 0; i < evalLogs.size(); i++)
		{
			if(evalLogs.get(i).getReviewee().getId() == id && evalLogs.get(i).getCompleted())
			{
				groupNum = evalLogs.get(i).getReviewee().getGroup().getId();
			}
		}



		//Find all completed evals in groupNum
		for (int i = 0; i < evalLogs.size(); i++)
		{

			if(evalLogs.get(i).getReviewee().getGroup().getId() == groupNum && evalLogs.get(i).getCompleted())
			{

				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				completedEvals.add(completeEval);

			}
		}

		for(int i = 0; i < completedEvals.size(); i++)
		{
			System.out.println(completedEvals.get(i));
		}

		pieDataset = dataset.createPieDatasetGroup(completedEvals);

		categoryDataset = dataset.createDefaultDatasetGroup(completedEvals);

		JFreeChart areaChart = ChartFactory.createAreaChart("Group " + chartName + " Evaluation"  , "","", categoryDataset);	

		JFreeChart barChart = ChartFactory.createBarChart("Group " + chartName + " Evaluation"  , "","", categoryDataset);	

		JFreeChart pieChart = ChartFactory.createPieChart("Group " + chartName + " Evaluation" , pieDataset);

		JFreeChart ringChart = ChartFactory.createRingChart("Group " + chartName + " Evaluation"  , pieDataset, true, true, true);



		final String TEMP_FILES_PATH = "src\\main\\resources\\temp\\";
		final String FILE_NAME = "Group " + chartName  + " Report" + ".pdf";


		try {
			
			Files.createDirectories(Paths.get(TEMP_FILES_PATH));
			FileUtils.cleanDirectory(new File(TEMP_FILES_PATH));
			
			Document document=new Document(); /* Create a New Document Object */
			/* Create PDF Writer Object that will write the chart information for us */
			PdfWriter writer=PdfWriter.getInstance(document,new FileOutputStream(TEMP_FILES_PATH + FILE_NAME));



			/* Open the Document Object for adding contents */
			document.open();

			/* Get Direct Content of the PDF document for writing */
			PdfContentByte cb = writer.getDirectContent();
			float width = PageSize.A4.getWidth();
			float height = PageSize.A4.getHeight();

			float halfWidth = width/2;
			float halfHeight = height/2;

			document.addTitle("Group " + chartName  + " Report");

			/* Create a template using the PdfContent Byte object */
			PdfTemplate pie = cb.createTemplate(halfWidth,halfHeight);
			/* Create a 2D graphics object to write on the template */
			Graphics2D g2d1 = pie.createGraphics(halfWidth, halfHeight, new DefaultFontMapper());

			Rectangle2D r2d1 = new Rectangle2D.Double(0,0,halfWidth,halfHeight);

			pieChart.draw(g2d1,r2d1);  
			g2d1.dispose();

			cb.addTemplate(pie,halfWidth,halfHeight);

			PdfTemplate bar = cb.createTemplate(halfWidth, halfHeight);
			Graphics2D g2d2 = bar.createGraphics(halfWidth, halfHeight, new DefaultFontMapper());
			Rectangle2D r2d2 = new Rectangle2D.Double(0, 0, halfWidth, halfHeight);

			barChart.draw(g2d2, r2d2);
			g2d2.dispose();
			cb.addTemplate(bar, 0, halfHeight);

			PdfTemplate ring = cb.createTemplate(halfWidth, halfHeight);
			Graphics2D g2d3 = ring.createGraphics(halfWidth, halfHeight, new DefaultFontMapper());
			Rectangle2D r2d3 = new Rectangle2D.Double(0, 0, halfWidth, halfHeight);

			ringChart.draw(g2d3, r2d3);
			g2d3.dispose();
			cb.addTemplate(ring, halfWidth, 0);

			PdfTemplate area = cb.createTemplate(halfWidth, halfHeight);
			Graphics2D g2d4 = area.createGraphics(halfWidth, halfHeight, new DefaultFontMapper());
			Rectangle2D r2d4 = new Rectangle2D.Double(0, 0, halfWidth, halfHeight);

			areaChart.draw(g2d4, r2d4);
			g2d4.dispose();
			cb.addTemplate(area, 0, 0);

			document.newPage();

			PdfPTable table = new PdfPTable(2); // 2 columns.

			table.setWidthPercentage(90f);

			PdfPCell header1 = new PdfPCell(new Paragraph("Section Titles"));
			PdfPCell header2 = new PdfPCell(new Paragraph("Section Scores(Group Average)"));

			table.addCell(header1);
			table.addCell(header2);

			for(int i = 0; i < pieDataset.getItemCount(); i++)
			{          	
				PdfPCell section = new PdfPCell(new Paragraph(pieDataset.getKey(i).toString()));
				table.addCell(section);

				PdfPCell value = new PdfPCell(new Paragraph(pieDataset.getValue(i).toString()));
				table.addCell(value);
			}

			document.add(table);
			document.close();
		}

		catch (Exception i)
		{
			System.out.println(i);
		}
		//String referer = request.getHeader("Referer");
		//return "redirect:"+ referer;
		
		log.info("File '" + FILE_NAME + "' requested for download.");
		
		//Download the file
		FileSystemResource resource = new FileSystemResource(TEMP_FILES_PATH + FILE_NAME);
		MediaType mediaType = MediaTypeFactory
				.getMediaType(resource)
				.orElse(MediaType.APPLICATION_OCTET_STREAM);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		ContentDisposition disposition = ContentDisposition
				.attachment()
				.filename(resource.getFilename())
				.build();
		headers.setContentDisposition(disposition);
		
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
}
